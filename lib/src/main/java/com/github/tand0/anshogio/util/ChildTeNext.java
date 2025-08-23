package com.github.tand0.anshogio.util;


/**
 * 手と盤面の情報
 */
public class ChildTeNext {

    /** 指し手 */
	private final int te;

	/** 盤面データ */
	private final BanmenNext next;

	/** コンストラクタ
	 * @param te 指し手
	 * @param next 盤面
	 */
	public ChildTeNext(int te,BanmenNext next) {
		this.te = te;
		this.next = next;
	}

	/**
	 * 指し手の取得 
	 * @return 指し手
	 */
    public int getTe() {
        return this.te;
    }

    /**
     * 盤面の取得
     * @return 盤面
     */
	public BanmenNext getNext() {
		return this.next;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj instanceof ChildTeNext) {
	        return next.equals(((ChildTeNext)obj).getNext());
        } else if (obj instanceof BanmenNext) {
            return next.equals((BanmenNext)obj);
        } else if (obj instanceof Integer) {
            return ((Integer) obj) == (Integer)te;
	    }
	    return false;
	}
}
