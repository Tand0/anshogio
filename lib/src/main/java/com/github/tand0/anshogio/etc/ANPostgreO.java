package com.github.tand0.anshogio.etc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ポスグレと接続するためのコード<br />
 * 注意： postgres をインストールしたあとに、以下を実行してDBを作らないとDBにログインできない<br />
 * <code>CREATE DATABASE anshogio;</code>
 * 
 * <pre>
 * C:\Program Files\PostgreSQL\17\bin>psql -d anshogio -U postgres
 * ユーザー postgres のパスワード:
 *
 * psql (17.5)
 * "help"でヘルプを表示します。
 *
 * anshogio=#
 * </pre>
 */
public abstract class ANPostgreO implements Runnable {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANPostgreO.class);

    /** 設定 */
    private final JSONObject setting;

    /** テーブル上の年 */
    protected int oldYear;

    /** テーブル上の月 */
    protected int oldMonth;

    /** テーブル上の日 */
    protected int oldDate;
    		
    /** コンストラクタ
     * 
     * @param setting 設定
     */
    public ANPostgreO(JSONObject setting) {
    	this.setting = setting;
        //
        // 年情報の取得
        if (this.getSetting().has(ANDownloadO.DOWNLLOAD_YEAR_START)) {
            this.oldYear = getSetting().getInt(ANDownloadO.DOWNLLOAD_YEAR_START);
        } else {
            Calendar nowCalendar = Calendar.getInstance();
            this.oldYear = nowCalendar.get(Calendar.YEAR);
        }
        this.oldMonth = 1;
        this.oldDate = 1;
    }
    
    /** 設定を取得する
     * 
     * @return 設定
     */
    public JSONObject getSetting() {
        return this.setting;
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
    
    /** 結果配置用のクラス */
    public class ResultKey {
        /** key値 */
    	public final String key;
    	/** 勝った数 */
    	public final int win;
    	/** 負けた数 */
    	public final int loss;
    	/**
    	 * コンストラクタ
    	 * @param key 局面のkey値
    	 * @param win この局面で先手が勝った数
    	 * @param loss この局面で先手が負けた数
    	 */
    	public ResultKey(String key, int win, int loss) {
    		this.key = key;
    		this.win = win;
    		this.loss = loss;
    	}
    }
    
    /** postgres のコネクション */
    private Connection conn = null;

    /** 接続状態か確認する
     * 
     * @return 生存している
     */
    public boolean isAlive() {
    	return conn != null;
    }

    /** チェックする拡張子
     * 
     * @return 拡張子
     */
	public String getExtention() {
		return ".csa";
	}

	
	/** postgresへの接続
	 * 
	 * @throws SQLException 例外
	 */
	public void connect() throws SQLException {
		logger.debug("connect");
		//
		//接続文字列
		String url;
		String user;
		String password;
		try {
			 url = getSetting().getString("postgres.url");
			 user = getSetting().getString("postgres.user");
			 password = getSetting().getString("postgres.passwd");
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
	/** 初期化する
	 * 
	 * @throws SQLException 例外
	 */
    public void dbClear() throws SQLException  {
        logger.debug("dbClear");
        try (Statement stmt = conn.createStatement()) {
            String tableName = "keytable";
            boolean flag = findTable(stmt, tableName);
            if (flag) {
                dropTable(stmt, tableName);
            }
            tableName = "datetable";
            flag = findTable(stmt, tableName);
            if (flag) {
                dropTable(stmt, tableName);
            }
        }
    }
    /**
     * テーブルが存在するか確認する
     * @param stmt postgresのステートメント
     * @param tableName テーブル名
     * @return 存在している場合 true
     * @throws SQLException 例外
     */
    protected boolean findTable(Statement stmt, String tableName) throws SQLException {
        boolean flag = false;
        // テーブルの存在チェック
        final String sql = "SELECT * FROM information_schema.tables WHERE table_name='" + tableName + "';";
        logger.debug("sql=" + sql);
        //
        try (ResultSet rset = stmt.executeQuery(sql)) {
            while (rset.next()) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    /**
     * テーブルをクリアする
     * @param stmt postgresのステートメント
     * @param tableName テーブル名
     * @throws SQLException 例外
     */
    protected void dropTable(Statement stmt, String tableName) throws SQLException {
        // DELETE文の実行
        final String sql = "DROP TABLE " + tableName + ";";
        logger.debug("sql=" + sql);
        stmt.executeUpdate(sql);
    }
	/** 初期化する
	 * 
	 * @throws SQLException 例外
	 */
	public void init() throws SQLException  {
		try (Statement stmt = conn.createStatement()) {
            String tableName = "keytable";
            boolean flag = findTable(stmt, tableName);
			if (!flag) {
				//
				// 存在していないので作る
				// テーブルの作成
				final String sql = "CREATE TABLE keytable (key varchar(64) PRIMARY KEY,win integer,loss integer);";
				logger.debug("sql=" + sql);
				stmt.executeUpdate(sql);
			}
			//
            tableName = "datetable";
            flag = findTable(stmt, tableName);
			if (!flag) {
                // 存在していないので作る
			    // テーブルの作成
			    final String sql = "CREATE TABLE datetable (year integer, month integer,date integer);";
			    logger.debug("sql=" + sql);
			    stmt.executeUpdate(sql);
				//
				// 存在しなければinsertを実行
				final String sqlInsert = "INSERT INTO datetable VALUES ('" + oldYear + "'," + oldMonth + "," + oldDate + ");";
				logger.trace("sql=" + sqlInsert);
				stmt.executeUpdate(sqlInsert);
			} else {
				// 存在していれればoldを更新
			    final String sql = "SELECT * FROM datetable limit 1;";
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
	 * @throws SQLException 例外
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
	 * 
	 * @throws SQLException 例外
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
	 * @throws SQLException 例外
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
	 * @throws SQLException 例外
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
	 * @throws SQLException 例外
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
