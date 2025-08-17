package com.github.tand0.anshogio;

import org.json.JSONObject;

public interface CSAWorker2 extends CSAWorker {
    /** ダウンロードを実施する */
	public void doProcessFlag(int i);

	/** ダウンロード中か？ */
	public boolean getProcessFlag();
	
    /** フラットゲート代替サーバを起動する */
	public void doServerFlag();

	/** フラットゲート代替サーバが起動しているか */
	public boolean getServerFlag();
	
	/** 接続する */
	public void doConnect(boolean stopFlag);
	
	/** 連戦しないようにする */
	public void setStop(boolean stopFlag);
	
	/** 状態表示 */
	public JSONObject getDisplayStatus();
}
