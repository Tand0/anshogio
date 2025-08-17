package com.github.tand0.anshogio.util;


/** 固定値の定義 */
public interface BanmenDefine {
    
	/** 成りフラグ */
	byte NARI = 0x10; // 0b00010000

	/** 後手フラグ */
	byte ENEMY = 0x20; // 0b00100000
	
	/** 空白 */
	byte pNull = 0;
	/** 歩 */
	byte pP = 1; // 歩
	/** 香 */
	byte pL = 2; // 香
	/** 桂馬 */
	byte pN = 3; // 桂
	/** 銀 */
	byte pS = 4; // 銀
	/** 金 */
	byte pG = 5; // 金
	/** 角 */
	byte pB = 6; // 角
	/** 飛車 */
	byte pR = 7; // 飛
	/** 王 */
	byte pK = 8; // 玉
	//
	/** と */
	byte ppP = NARI | pP; // と
	/** 竜 */
	byte ppR = NARI | pR; // 竜
	/** 馬 */
	byte ppB = NARI | pB; // 馬
	/** 成り銀 */
	byte ppS = NARI | pS; // 銀成
	/** 成り桂 */
	byte ppN = NARI | pN; // 桂成
	/** 成り香 */
	byte ppL = NARI | pL; // 香成

	/** 後手 歩 */
	byte pp = ENEMY | pP; // 歩
	/** 後手 香車 */
	byte pl = ENEMY | pL; // 香
	/** 後手 桂馬 */
	byte pn = ENEMY | pN; // 桂
	/** 後手 銀 */
	byte ps = ENEMY | pS; // 銀
	/** 後手 角 */
	byte pb = ENEMY | pB; // 角
	/** 後手 飛車 */
	byte pr = ENEMY | pR; // 飛
	/** 後手 金 */
	byte pg = ENEMY | pG; // 金
	/** 後手 玉 */
	byte pk = ENEMY | pK; // 玉

	/** 盤面の最大値 */
	int B_MAX = 9;

	/** moveで持ちコマを打った時のolxXY値 */
	int BEAT = 9;

	/** コマを CSA文字に変換 */
	static String getKeyToString(int key) {
		key = key & (~ENEMY);
		String keyString;
		switch (key) {
			case pP: keyString = "FU"; break;// 歩
			case pL: keyString = "KY"; break;// 香
			case pN: keyString = "KE"; break;// 桂
			case pS: keyString = "GI"; break;// 銀
			case pG: keyString = "KI"; break;// 金
			case pB: keyString = "KA"; break;// 角
			case pR: keyString = "HI"; break;// 飛
			case pK: keyString = "OU"; break;// 玉
			case ppP: keyString = "TO"; break;// と
			case ppL: keyString = "NY"; break;// 香成
			case ppN: keyString = "NK"; break;// 桂成
			case ppS: keyString = "NG"; break;// 銀成
			case ppB: keyString = "UM"; break;// 馬
			case ppR: keyString = "RY"; break;// 竜
			default: keyString = "* "; break;// 空白
		}
		return keyString;
	}
	/** CSA文字をコマに変換 */
	static byte getStringToKey(String keyString) {
		byte key;
		switch (keyString.toUpperCase()) {
            case "FU": key = pP; break;
    		case "KY": key = pL; break;// 香
    		case "KE": key = pN; break;// 桂
    		case "GI": key = pS; break;// 銀
    		case "KI": key = pG; break;// 金
    		case "KA": key = pB; break;// 角
    		case "HI": key = pR; break;// 飛
    		case "OU": key = pK; break;// 玉
    		case "TO": key = ppP; break;// と
    		case "NY": key = ppL; break;// 香成
    		case "NK": key = ppN; break;// 桂成
    		case "NG": key = ppS; break;// 銀成
    		case "UM": key = ppB; break;// 馬
    		case "RY": key = ppR; break;// 竜
    		default: key = pNull; break;
    	}
		return key;
	}
	/** 変更する
	 *
	 * @param te 手
	 * @return CSA文字
	 */
	static String changeTeIntToString(int te) {
		if (te==0) { // 次に指す手がない
			return "none";
		}
		if (te<0) { // 入玉勝ち
			return "win";
		}
		StringBuilder buff = new StringBuilder();
		int key = (te >> (6 * 4)) & 0x3F;
		buff.append(((key & ENEMY) == 0) ? '+' : '-');
		int oldY = te & 0x1F;
		int oldX = (te >> 6) & 0x1F;
		if (oldY == BEAT) { // 打った
			buff.append('0');
			buff.append('0');
		} else {
			buff.append((char) ('1' + oldX));
			buff.append((char) ('1' + oldY));
		}
		int newY = (te >> (6 * 2)) & 0x1F;
		int newX = (te >> (6 * 3)) & 0x1F;
		buff.append((char) ('1' + newX));
		buff.append((char) ('1' + newY));
		buff.append(getKeyToString(key));
		return buff.toString();
	}

	/** 変更する
	 *
	 * @param string CSA文字
	 * @return 手
	 */
	static int changeTeStringToInt(String string) {
		if (string.length() < 7) {
			return 0;
		}
		int teban = string.charAt(0) == '+' ? 0 : 1;
		String keyString = string.substring(5, 7);
		int oldX = string.charAt(1);
		int oldY = string.charAt(2);
		if (oldY == '0') {
			oldX = BEAT;
			oldY = BEAT;
		} else {
			oldX = oldX - '1';
			oldY = oldY - '1';
		}
		int newX = string.charAt(3) - '1';
		int newY = string.charAt(4) - '1';
		byte key = getStringToKey(keyString);
		key = (byte) (key | (teban * ENEMY));
		return changeTeToInt(key, oldX, oldY, newX, newY);
	}

	/** 変更する
	 *
	 * @param key コマ
	 * @param oldX 移動前x、打つ場合は BEAT
	 * @param oldY 移動前y
	 * @param newX 移動後x
	 * @param newY 移動後y
	 * @return 手
	 */
	static int changeTeToInt(byte key, int oldX, int oldY, int newX, int newY) {
		return oldY | (oldX << 6) | (newY << (6 * 2)) | (newX << (6 * 3)) | (key << (6 * 4));
	}

	/** 成りや打ちを管理する
	 */
	public class CheckLow {
	    /** kyouseiNari 成ることが強制される位置。敵なら 8-xする(桂,香,銀,以外) */
		public final int kyouseiNari;
		/** kyouseiNari 成ることが強制される位置。敵なら 8-xする(桂,香,以外) */
		public final int uchiKinshi;
		/** 成れる場合、ならない手を合法にするか？ (桂,香,以外) */
		public final boolean narazuOK;
		/** 成れるコマか？ (金,王,以外) */
		public final boolean narenai;
		/**チェック用配列
		 * 
		 * @param kyouseiNari 成ることが強制される位置。敵なら 8-xする
		 * @param uchiKinshi 打つことができない位置。敵なら 8-xする。どこでも打てるなら0
		 * @param narazuOK 成れる場合、ならない手を合法にするか？
		 * @param narenai 成らず
		 */
		public CheckLow(int kyouseiNari,int uchiKinshi,boolean narazuOK,boolean narenai) {
			this.kyouseiNari = kyouseiNari;
			this.uchiKinshi = uchiKinshi;
			this.narazuOK = narazuOK;
			this.narenai = narenai;
		}
		/**
		 * 成りを強制する
		 * @param teban 手番
		 * @param y 位置
		 * @return 成りを強制する必要がある場合はtrue
		 */
		public boolean getKyouseiNari(int teban,int y) {
			return (teban == 0) ? (y < kyouseiNari) : ((8 - kyouseiNari) < y);
		}
		/** 敵陣ならばtrue
		 * 
		 * @param teban 手番
		 * @param y 位置
		 * @return 敵陣ならば true
		 */
		public boolean isTekijin(int teban,int y) {
			return (teban == 0) ? (y <= 2) : (6 <= y);
		}
		/** 
		 * 打ち込み禁止位置に打ち込んでいないか？
		 * @param teban 手番
		 * @param y 位置
		 * @return 打ち込めないならtrue
		 */
		public boolean getUchiKinshi(int teban,int y) {
			if (uchiKinshi == 0) {
				return false; // 0ならどこでも打てる
			}
			return (teban == 0) ? (y < uchiKinshi) : ((8 - uchiKinshi) < y);
		}
		public boolean isNarazuOK() {
			return narazuOK;
		}
		public boolean isNarenai() {
			return narenai;
		}
	}
	
	public static final CheckLow[] checkLow = {
			new CheckLow( 8,  0, false, true), //pNull 空白
			new CheckLow(+1, +1, false, false ), // pP 歩
			new CheckLow(+2, +1, true,  false ), // pL 香
			new CheckLow(+2, +2, true,  false ), // pN 桂
			new CheckLow( 0,  0, true,  false ), // pS 銀
			new CheckLow( 8,  0, false, true  ), // pG 金
			new CheckLow( 0,  0, false, false ), // pB 角
			new CheckLow( 0,  0, false, false ), // pR 飛
			new CheckLow( 8,  0, false, true  )  //pK  王
	};
}
