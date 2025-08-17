package com.github.tand0.anshogio;

import java.io.IOException;

/** 将棋用メイン処理 */
public class ANShogiGyoku {

	/** 設定ファイル名 */
	private static final String[] SETTING_FILE_VS = {"settingGyoku.json"};
	
	/** メイン処理 */
	public static final void main(String[] argc) throws IOException {
		ANShogiO.main(SETTING_FILE_VS);
	}
}
