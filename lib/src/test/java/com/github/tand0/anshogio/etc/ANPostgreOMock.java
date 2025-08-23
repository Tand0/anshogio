package com.github.tand0.anshogio.etc;


import java.sql.SQLException;

import org.json.JSONObject;


/** ポスグレと接続するためのコードのMock用クラス
 * postgresに接続せず、動作確認をする。
 * 
 * postgres をインストールしたあとに、以下を実行してDBを作らないとDBにログインできない
 * CREATE DATABASE anshogio; 
 */
public class ANPostgreOMock extends ANDbUpgradeO {

    /** テスト用のkey値 */
	public String testKey = null;

	/** テスト用の勝ち数 */
	public int testWin;
	
	/** テスト用の負け数 */
	public int testLoss;
	
	
    /**
     * コンストラクタ
     * @param setting 設定
     */
    public ANPostgreOMock(JSONObject setting) {
    	super(setting);
    }
	
    @Override
    public boolean isAlive() {
    	return true; // 評価用に強制敵にtrueにする
    }
    
    @Override
    public String getExtention() {
        return ".txt";
    }
    @Override
	public void run() {
	}
	@Override
	public void connect() throws SQLException {
	}
	@Override
	public void init() throws SQLException  {
	}
	@Override
	public void addKey(String key,int win,int loss) throws SQLException {
		testKey = key;
		testWin = win;
		testLoss = loss;
	}
	@Override
    public void updateDate() throws SQLException {        
    }
    @Override
    public int getKeyLen() throws SQLException {
        return 0;
    }
	@Override
	public ReslutWinLoss readKey(String key) throws SQLException {
		return null;
	}
	@Override
	public ResultKey readKey(int index) throws SQLException {
		return null;
	}
	@Override
	public void close() {
	}
}