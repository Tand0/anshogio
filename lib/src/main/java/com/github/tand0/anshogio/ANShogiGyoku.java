package com.github.tand0.anshogio;

import java.io.IOException;

/** 将棋用メイン処理 */
public class ANShogiGyoku {

	/** 設定ファイル名 */
	private static final String[] SETTING_FILE_VS = {"settingGyoku.json"};
	
	/** コンストラクタ */
	public ANShogiGyoku() {
	}
	
	/** メイン処理
	 * 
	 * @param argc メイン引数
	 * @throws IOException IO例外時は終了
	 */
	public static final void main(String[] argc) throws IOException {
		ANShogiO.main(SETTING_FILE_VS);
	}
}
