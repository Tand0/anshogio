package com.github.tand0.anshogio.etc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.util.BanmenDefine;
import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenNext;
import com.github.tand0.anshogio.util.BanmenOnly;


/** ポスグレと接続するためのコード
 * デフォルトアカウント：
 * 
 * postgres をインストールしたあとに、以下を実行してDBを作らないとDBにログインできない
 * CREATE DATABASE anshogio; 
 * 
 * C:\Program Files\PostgreSQL\17\bin>psql -d anshogio -U postgres
 * ユーザー postgres のパスワード:
 *
 * psql (17.5)
 * "help"でヘルプを表示します。
 *
 * anshogio=#
 */
public class ANPostgreO implements Runnable {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANPostgreO.class);

    /** 設定 */
    private final JSONObject setting;
    
    /** 合計値 */
    private int sum = Integer.MAX_VALUE;

    /** テーブル上の年 */
    private int oldYear = 1900;

    /** テーブル上の月 */
    private int oldMonth = 1;

    /** テーブル上の日 */
    private int oldDate = 1;
    		
    /** 親データ */
    public ANPostgreO(JSONObject setting) {
    	this.setting = setting;
    }
    
    /** win と loss の戻り値 */
    public class ReslutWinLoss {
        
        /** win 値 */
    	public int win;

    	/** loss 値 */
    	public int loss;
    	
    	/** コンストラクタ
    	 * @param win win値
    	 * @param loss loss値
    	 */
    	public ReslutWinLoss(int win, int loss) {
    		this.win = win;
    		this.loss = loss;
    	}
    }
    public class ResultKey {
    	public String key;
    	public int win;
    	public int loss;
    	public ResultKey(String key, int win, int loss) {
    		this.key = key;
    		this.win = win;
    		this.loss = loss;
    	}
    }
    
    /** postgres のコネクション */
    private Connection conn = null;

    /** 接続状態か確認する */
    public boolean isAlive() {
    	return conn != null;
    }

    /** チェックする拡張子 */
	public String getExtention() {
		return ".csa";
	}
	/** ログフォルダにあるファイルを読み込んでDBに押し込む */
	public void run() {
		logger.debug("run start!");
		BanmenFactory factory = new BanmenFactory();
		try {
			//
			oldYear = setting.getInt("targetYearStart");
			//
			connect();
			init();
			//
	        // スタートを設定
	        Calendar oldCalendar = Calendar.getInstance();
	        oldCalendar.set(Calendar.YEAR,oldYear);
	        oldCalendar.set(Calendar.MONTH,oldMonth -1);
	        oldCalendar.set(Calendar.DATE,oldDate);
	        //
	        // 現在の日を取得
	        Calendar nowCalendar = Calendar.getInstance();
	        nowCalendar.add(Calendar.DAY_OF_MONTH, -2); // 2日前にする
			//
			// ダウンロード数
			this.sum = setting.getInt("postgres.sum");
			if (sum == 0) {
				sum = Integer.MAX_VALUE;// 実質無制限
			}
			//
			// 棋譜データのダウンロード元
			String downloadDir = setting.getString("downloadDir");
			while (oldCalendar.before(nowCalendar)) {
				//
				// 日付を１日増やす
				oldCalendar.add(Calendar.DAY_OF_MONTH,1);
				//
	            oldYear = oldCalendar.get(Calendar.YEAR);
	            oldMonth = oldCalendar.get(Calendar.MONTH) + 1;
	            oldDate = oldCalendar.get(Calendar.DATE);
				//
				File file = new File(downloadDir);
				file = new File(file,String.format(Locale.JAPANESE,"%04d/%02d/%02d", oldYear,oldMonth,oldDate));
				runFile(factory, file);
				//
				// 日付をアップデートする
				updateDate();
			}
			//
			//
		} catch (SQLException e){
			logger.error("e=" + e.getMessage());
			close();
			sum = 0;
		} catch (IOException e) {
			logger.error("e=" + e.getMessage());
			sum = 0;
		}
		logger.debug("run over!");	
	}
	/** 指定されたデータの出力 */
	public void runFile(BanmenFactory factory, File dir) throws SQLException, IOException {
		if (sum <= 0) {
			return; // ダウンロード数が完了した
		}
		if (dir.isDirectory()) {
			for (File nextFile : dir.listFiles()) {
				runFile(factory, nextFile);
			}
			return; //フォルダチェック語は終了
		}
		String extension = dir.getName();
		extension = extension.substring(extension.lastIndexOf("."));
		if (!extension.equals(getExtention())) {
			return; // 拡張子が違う
		}
		//
		// ファイル１個分の処理
		runFileOne(factory,dir);
    	sum--;
	}
	/** ファイル１個分の処理 */
	public BanmenNext runFileOne(BanmenFactory factory,File file) throws SQLException, IOException {
		//
		// ハッシュをすべてクリアする
		factory.clearAllHash();
		//
		BanmenNext only;
		logger.debug(file.getAbsolutePath().toString());
	    try (FileReader fr = new FileReader(file);
		    	BufferedReader br = new BufferedReader(fr)) {
	    	LinkedList<String> strLine = new LinkedList<>();
	    	String str;
	    	while ((str = br.readLine()) != null) {
		    	strLine.add(str);   			
		    }
	    	only = readCSAText(factory,strLine);
		}
	    return only;
	}
	/** １局分のデータを読み込み */
	private BanmenNext readCSAText(BanmenFactory factory,LinkedList<String> strLine) throws SQLException {
		BanmenOnly only = new BanmenOnly(null, 0);
		boolean knownError = false; // タイムアップ、千日手など分かっている無視する手順
		int sennichte = 0;
		boolean summary = false;
		String senteName = "working1";
		String goteName = "working2";
		String winner = "working3";
		//
		//
		LinkedList<BanmenNext> nextList = new LinkedList<>();
		int tesuu = 0;
		for (String str: strLine) {
			char ch = str.charAt(0);
    		if (str.trim().length() <= 0) {
    			// EMPTY
    		} else if (0 <= str.indexOf("N+")) {
    			senteName = str.substring(2);	
    		} else if (0 <= str.indexOf("N-")){
    			goteName = str.substring(2);
    		} else if (only.setForCSAProtocol(str)) {
    			if ((ch == '+') || (ch == '-')) {
    				BanmenNext next = factory.create(null, only);
    				nextList.addLast(next);
    			}
    		} else if (0 == str.indexOf("\'summary:")) {
    			//
    			summary = true;
    			//
    			String working = getWinnerOrLooser(str, "win:");
    			if (working != null) {
    				winner = working;
    			} else {
    				working = getWinnerOrLooser(str, "lose:");
    				if (working != null) {
    					if (working.equals(senteName)) {
    						winner = goteName;
    					} else if (working.equals(goteName)) {
    						winner = senteName;
    					} else {
    						logger.error("unkown working=" + working + " sente=" + senteName + " gote=" + goteName);
    						knownError = true;
    					}
    				} else {
        				working = getWinnerOrLooser(str, "draw:");
        				if (working != null) {
        					winner = goteName; // ドローなら後手勝ちにする
        				} else {
        					logger.error("unkown win/lose str=" + str);
        				}
    				}
    			}
    		} else if ((ch == 'V') || (ch == '\'') || (ch == '$') || (ch == 'T')) {
    			// EMPTY
    		} else if ((ch == '+') || (ch == '-')) {
    			//
    			// 手数に+1する
				tesuu++;
				//
    			int te = BanmenDefine.changeTeStringToInt(str);
    			BanmenNext next = nextList.getLast().decisionTe(factory, te);
    			only = next.getMyKey().createBanmenOnly(); // 最終盤面
    			nextList.addLast(next); //打った手を保存
    		} else if (str.equals("%TIME_UP")) {
    			knownError = true;
    		} else if (str.equals("%SENNICHITE")) {
    			sennichte = 1;
    		} else if (str.equals("%TORYO")) {
    			// EMPTY
    		} else if (0 <= str.indexOf("%KACHI")) {
    			// EMPTY
    		} else {
    			logger.error("unkown=(" + str + ")");
    			break;
    		}
		}
		// 後手勝ちでtrue, 先手勝ちでfalse, 千日手は後手勝ち
		int winLoss = 1; // 後手勝ちで1, 先手勝ちで0, 千日手は後手勝ち1
		if (sennichte != 0) {
			winLoss = 1; // 千日手なら後手勝ち		
		} else if (winner.equals(senteName)) {
			winLoss = 0;
		} else if (winner.equals(goteName)) {
			winLoss = 1;
		} else {
			logger.error("unkown Winnder=" + winner + " sente=" + senteName + " gote=" + goteName);
			knownError = true;
		}
		//
		if (knownError || (tesuu < 20) || (!summary)) {
			if (nextList.size() < 0) {
	            return null;
			}
            return nextList.getLast(); // knownErrorは無視する。20手より少ないのは無視する、サマリーがなければ無視する
		}
		//
		//
		for (BanmenNext nextd : nextList) {
			//
			// DBへ保存
			// 後手勝ちでtrue, 先手勝ちでfalse, 千日手は後手勝ち
			int winInt =  winLoss;
			int lossInt = (1 - winLoss) | sennichte;
			addKey(nextd.getMyKey().toString(), winInt, lossInt);
			//
			// ログ表示
			//logger.debug(nextd.toString());
			//logger.debug(nextd.getMyKey().toString());
		}
		logger.trace("win=" + winLoss);
		return nextList.getLast();
	}
	/** 文字を取る */
	public String getWinnerOrLooser(String str,String target) {
		int index = str.indexOf(target);
		String winner = null;
		if (0 < index) {
			winner = str.substring(index + target.length());
			index = winner.indexOf(" ");
			if (0 < index) {
				winner = winner.substring(0, index);
			}
		}
		return winner;
	}
	
	/** postgresへの接続 */
	public void connect() throws SQLException {
		logger.debug("connect");
		//
		//接続文字列
		String url;
		String user;
		String password;
		try {
			 url = setting.getString("postgres.url");
			 user = setting.getString("postgres.user");
			 password = setting.getString("postgres.passwd");
		} catch (JSONException e) {
			// 初期設定情報がないのでで終了
			logger.error(e.getMessage());
			return;
		}
		if (! isAlive()) {
			// 接続していなかったら接続するようにする
			this.conn = DriverManager.getConnection(url, user, password);
		}
		//自動コミットON
		this.conn.setAutoCommit(true);
		//
	}
	
	/** 初期化する */
	public void init() throws SQLException  {
		try (Statement stmt = conn.createStatement()) {
			boolean flag = false;
			// テーブルの存在チェック
			String sql = "SELECT * FROM information_schema.tables WHERE table_name='keytable';";
			logger.debug("sql=" + sql);
			//
			try (ResultSet rset = stmt.executeQuery(sql)) {
				while (rset.next()) {
					flag = true;
					break;
				}
			}
			/*
			if (flag) {
				// 存在しているので削除を走らせる
				//
				// DELETE文の実行
				sql = "DROP TABLE keytable;";
				logger.debug("sql=" + sql);
				stmt.executeUpdate(sql);
				//
				// DROPしたので存在しなくなった
				flag = false;
			}
			*/
			if (!flag) {
				//
				// 存在していないので作る
				// テーブルの作成
				sql = "CREATE TABLE keytable (key varchar(64) PRIMARY KEY,win integer,loss integer);";
				logger.debug("sql=" + sql);
				stmt.executeUpdate(sql);
			}
			//
			// テーブルの存在チェック
			sql = "SELECT * FROM information_schema.tables WHERE table_name='datetable';";
			logger.debug("sql=" + sql);
			flag = false;
			try (ResultSet rset = stmt.executeQuery(sql)) {
				while (rset.next()) {
					flag = true;
					break;
				}
			}
			if (!flag) {
                // 存在していないので作る
			    // テーブルの作成
			    sql = "CREATE TABLE datetable (year integer, month integer,date integer);";
			    logger.debug("sql=" + sql);
			    stmt.executeUpdate(sql);
				//
				// 存在しなければinsertを実行
				final String sqlInsert = "INSERT INTO datetable VALUES ('" + oldYear + "'," + oldMonth + "," + oldDate + ");";
				logger.trace("sql=" + sqlInsert);
				stmt.executeUpdate(sqlInsert);
			} else {
				// 存在していれればoldを更新
				sql = "SELECT * FROM datetable limit 1;";
				logger.trace("sql=" + sql);
				try (ResultSet result = stmt.executeQuery(sql)) {
					while (result.next()) {
						oldYear = result.getInt("year");
						oldMonth = result.getInt("month");
						oldDate = result.getInt("date");
						break;
					}
				}
			}
		}
		//
	}
	/** キーのテーブルへの追加
	 * @param key キー値
	 * @param win 勝った数の追加分
	 * @param loss 負けた数の追加分
	 * @throws SQLException
	 */
	public void addKey(String key,int win,int loss) throws SQLException {
		try (Statement stmt = conn.createStatement()) {
			//
			ReslutWinLoss readResult = readKey(key);
			if (readResult == null) {
				//
				// 存在しなければinsertを実行
				final String sqlInsert = "INSERT INTO keytable VALUES ('" + key + "'," + win + "," + loss + ");";
				logger.trace("sql=" + sqlInsert);
				stmt.executeUpdate(sqlInsert);
			} else {
				//
				// readした値を更新
				win += readResult.win;
				loss += readResult.loss;
				//
				// 存在すればupdateを実行
				final String sqlUpdate = "UPDATE keytable SET win=" + win + ",loss=" + loss + " WHERE key='" + key + "';";
				logger.trace("sql=" + sqlUpdate);
				stmt.executeUpdate(sqlUpdate);
			}
		}
	}
	/** 日付の更新
	 */
	public void updateDate() throws SQLException {
		try (Statement stmt = conn.createStatement()) {
			//
			// 存在すればupdateを実行
			final String sqlUpdate = "UPDATE datetable SET year=" + oldYear + ",month=" + oldMonth + ", date='" + oldDate + "';";
			logger.trace("sql=" + sqlUpdate);
			stmt.executeUpdate(sqlUpdate);
		}
	}
	/**
	 * キー情報のテーブルからの取得
	 * @param key キー値
	 * @return 勝った数と負けた数
	 */
	public ReslutWinLoss readKey(String key) throws SQLException {
		final String sql = "SELECT * FROM keytable WHERE key='" + key + "';";
		logger.trace("sql=" + sql);
		try (Statement stmt = conn.createStatement()) {
			try (ResultSet rset = stmt.executeQuery(sql)) {
				while (rset.next()) {
					ReslutWinLoss reslut = new ReslutWinLoss(0,0);
					reslut.win = reslut.win + rset.getInt("win");
					reslut.loss = reslut.loss + rset.getInt("loss");
					return reslut;
				}
				return null;
			}
		}
	}
	/**
	 * テーブルの長さの取得
	 * @return テーブル長
	 * @throws SQLException
	 */
	public int getKeyLen() throws SQLException {
		final String sql = "SELECT count(*) FROM keytable;";
		logger.debug("sql=" + sql);
		try (Statement stmt = conn.createStatement()) {
			try (ResultSet rset = stmt.executeQuery(sql)) {
				while (rset.next()) {
					return rset.getInt("count");
				}
			}
		} catch(SQLException e) {
			logger.debug(e.getMessage());
			return 0;
		}
		return 0;
	}
	/** インデクスを指定したデータの返信
	 * @param index インデックス
	 * @return 戻り値
	 * @throws SQLException
	 */
	public ResultKey readKey(int index) throws SQLException {
		final String sql = "SELECT * FROM keytable OFFSET " + index + " LIMIT 1;";
		logger.debug("sql=" + sql);
		try (Statement stmt = conn.createStatement()) {
			try (ResultSet rset = stmt.executeQuery(sql)) {
				while (rset.next()) {
					String key = rset.getString("key");
					int win = rset.getInt("win");
					int loss = rset.getInt("loss");
					return new ResultKey(key,win,loss);
				}
			}
		}
		return null;
	}
	
	/** 切断する */
	public void close() {
		logger.debug("close");
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e){
				logger.debug(e.getMessage());				
			}
			conn = null;
		}
	}
}
