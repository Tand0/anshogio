package com.github.tand0.anshogio;

import org.json.JSONObject;

/** 社畜用の設定 */
public interface CSAWorker {
    /** ステータスの設定
     * 
     * @param status ステータス
     */
    public void setStatus(ANStatus status);
    
    /** ステータスの取得
     * 
     * @return ステータス
     */
    public ANStatus getStatus();
    
    /** 自分の手番
     * 
     * @param senteName 先手の名前
     * @param goteName 後手の名前
     * @param myTurn 自身が先手番か？ 0先手,1後手
     * @param totalTime トータルタイム
     * @param byoyomiTime 秒読み時間
     * @param delayTime 遅延時間
     * @param incrementTime 1手毎の加算時間
     */
    public void start(
        String senteName,
        String goteName,
    	int myTurn,
    	Integer totalTime,
    	Integer byoyomiTime,
    	Integer delayTime,
    	Integer incrementTime);

    /** 次の処理を促す
     * 
     * @param nextMove 次の動き
     */
    public void setNextMove(String nextMove);

    /** 設定ファイルの取得
     * 
     * @return 設定ファイル
     */
    public JSONObject getSetting();
    
    /** 処理を実施する
     * 
     * @param i プロセスフラグ
     */
    public void doProcessFlag(int i);

    /** 処理中か？
     * 
     * @return trueなら処理中
     */
    public boolean getProcessFlag();
    
    /** フラットゲート代替サーバを起動する
     */
    public void doServerFlag();

    /** フラットゲート代替サーバが起動しているか
     * 
     * @return trueなら起動している
     */
    public boolean getServerFlag();
    
    /** 接続する
     * 
     * @param stopFlag trueなら停止要求
     */
    public void doConnect(boolean stopFlag);
    
    /** 連戦しないようにする
     * 
     * @param stopFlag trueなら停止要求
     */
    public void setStop(boolean stopFlag);
    
    /** 状態表示
     * 
     * @return HTTPで表示させるときのREST情報
     */
    public JSONObject getDisplayStatus();
}
