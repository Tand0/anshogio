package com.github.tand0.anshogio.eval;

import org.json.JSONObject;

/** tensor のサーバにアクセスしてキー値から値を得る */
public abstract class ANModel {
    
    /** 設定情報 */
    private final JSONObject setting;
	
    /** 一度でも接続失敗したら以後は見ない */
    private boolean alive;
	
    /** 生存確認
     * 
     * @return 生きているならtrue
     */
    public boolean isAlive() {
    	return this.alive;
    }
    /** 死の設定
     */
    protected void setDead() {
        this.alive = false;
    }
    /** 設定の取得
     * @return 設定
     */
    protected JSONObject getSetting() {
        return this.setting;
    }
    /**
     * コンストラクタ
     * @param setting 設定
     */
	public ANModel(JSONObject setting) {
		this.alive = true;
		this.setting = setting;
	}

	/**
	 * key値から評価値を取得
	 * @param key key値
	 * @return 評価値
	 */
	public abstract Float getKey(String key);
}
