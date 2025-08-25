package com.github.tand0.anshogio.etc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.util.BanmenDefine;
import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNext;
import com.github.tand0.anshogio.util.BanmenOnly;


/** データベースをアップグレードする
 * @author A.N.DB アップグレード王
 */
public class ANDbUpgradeO extends ANPostgreO {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANDbUpgradeO.class);


    /** 合計値 */
    private int sum = Integer.MAX_VALUE;
    		
    /** コンストラクタ
     * 
     * @param setting 設定ファイル
     */
    public ANDbUpgradeO(JSONObject setting) {
        super(setting);
    }
    
    /** チェックする拡張子 */
    @Override
	public String getExtention() {
		return ".csa";
	}
	/** ログフォルダにあるファイルを読み込んでDBに押し込む */
	@Override
	public void run() {
		logger.debug("run start!");
		BanmenFactory factory = new BanmenFactory();
		try {
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
			this.sum = getSetting().getInt("postgres.sum");
			if (sum == 0) {
				sum = Integer.MAX_VALUE;// 実質無制限
			}
			//
			// 棋譜データのダウンロード元
			String downloadDir = getSetting().getString("download.dir");
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
		} catch (IOException e) {
			logger.error("e=" + e.getMessage());
		} finally {
            close();
            sum = 0;
		}
        //
		logger.debug("run over!");	
	}
	/** 指定されたデータの出力
	 * 
	 * @param factory 工場
	 * @param dir フォルダ
	 * @throws SQLException 例外
	 * @throws IOException 例外
	 */
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
        if (!dir.isFile()) {
            logger.debug("file not found dir={}",dir);
            return; // ファイルなし
        }
		String extension = dir.getName();
        logger.debug("dir={} extension={}",dir, extension);
        int index = extension.lastIndexOf(".");
        if (index <= 0) {
            logger.debug("not extention dir={}",dir);
            return; // 拡張子なし
        }
		extension = extension.substring(index);
		if (!extension.equals(getExtention())) {
			return; // 拡張子が違う
		}
		//
		// ファイル１個分の処理
		runFileOne(factory,dir);
    	sum--;
	}
	/** ファイル１個分の処理 
	 * 
	 * @param factory 工場
	 * @param file ファイル
	 * @return 盤面情報
	 * @throws SQLException 例外
	 * @throws IOException 例外
	 */
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
	/** １局分のデータを読み込み
	 * 
	 * @param factory 工場
	 * @param strLine ファイルか抽出したテキストのリスト
	 * @return 盤面情報
	 * @throws SQLException 例外
	 */
	private BanmenNext readCSAText(BanmenFactory factory,LinkedList<String> strLine) throws SQLException {
		BanmenOnly only = new BanmenOnly();
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
    			    BanmenKey key = new BanmenKey(only);
    				BanmenNext next = factory.create(null, key);
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
			if (nextList.size() <= 0) {
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
			int joseki = 0; // 定跡ではない
			addKey(nextd.getMyKey().toString(), winInt, lossInt,joseki);
			//
			// ログ表示
			//logger.debug(nextd.toString());
			//logger.debug(nextd.getMyKey().toString());
		}
		logger.trace("win=" + winLoss);
		return nextList.getLast();
	}
	/** 勝者を変える
	 * 
	 * @param str 対象文字列
	 * @param target 勝ちか負けがどちらか
	 * @return 勝ちか負けのオーナー情報
	 */
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

}
