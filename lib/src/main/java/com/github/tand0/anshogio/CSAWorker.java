package com.github.tand0.anshogio;

import org.json.JSONObject;

public interface CSAWorker {
    /** ステータスの設定 */
    void setStatus(ANStatus status);
    
    /** ステータスの取得 */
    ANStatus getStatus();
    
    /** 自分の手番 */
    void start(
        String senteName,
        String goteName,
    	int myTurn,
    	Integer totalTime,
    	Integer byoyomiTime,
    	Integer delayTime,
    	Integer incrementTime);

    /** 次の処理を促す */
    void setNextMove(String nextMove);

    /** 設定ファイルの取得 */
    public JSONObject getSetting();
}
