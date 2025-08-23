package com.github.tand0.anshogio;

/** ステータス情報 */
public enum ANStatus {
    /** 開始時 */
	START,
	/** 接続中 */
	BEGIN_CONNECT,
	/** ログイン中 */
	BEGIN_LOGIN,
	/** 開始時のサマリ処理中 */
	BIGIN_GAME_SUMMARY,
	/** 開始時の対戦情報 */
	BIGIN_GAME_NOW,
	/** 開始時の時間情報 */
	BEGIN_TIME,
	/** 開始時の位置情報 */
	BEGIN_POSITION,
	/** 開始時のスタート情報 */
	BEIGN_START,
	/** 対戦中 */
	FIGHT,
	/** 対戦終わり */
	END,
	/** 異常な状態 */
	ERROR
}
