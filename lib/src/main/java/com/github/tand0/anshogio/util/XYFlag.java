package com.github.tand0.anshogio.util;


/**
 * x,y,flagの情報を得る
 */
public class XYFlag {
    /** 移動先x */
	private final int x;
	/** 移動先 y */
	private final int y;
	/** 複数移動する場合true (香,角,飛が対応) */
	private final boolean flag;
	/**
	 * @param x 移動先X
	 * @param y 移動先Y
	 * @param flag 複数移動するか？
	 */
	public XYFlag(int x, int y, boolean flag) {
		this.x = x;
		this.y = y;
		this.flag = flag;
	}
    /** 移動先x */
	public int getX() {
		return this.x;
	}
    /** 移動先y */
	public int getY(int teban) {
		return (teban == 0) ? y : -y;
	}
	/** 複数移動するなら true */
	public boolean getFlag() {
		return flag;
	}
}
