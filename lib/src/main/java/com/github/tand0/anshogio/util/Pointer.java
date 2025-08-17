package com.github.tand0.anshogio.util;

/** keyを作成するときのポインターの設定 */
public class Pointer {
	public int index = 0;
	public int pos = 0;
	public void clear() {
		index = 0;
		pos = 0;
	}
    /** 持ち駒超過チェック */
    public void checkSum() {
    	boolean flag =  (pPSum <= 18) && (pLSum <= 4) && (pNSum <= 4) && (pSSum <= 4) && (pGSum <= 4)
    			&& (pRSum <= 2) && (pBSum <= 2) && (pKSum <= 2);
    	if (! flag) {
    		throw new java.lang.UnsupportedOperationException(
    			"NG p=" + pPSum + " l=" + pLSum + " n=" + pNSum +
    			" s=" + pSSum + " g=" + pGSum + " r=" + pRSum + " b=" + pBSum + " k=" + pKSum);
    	}
    }
    /** すべての駒を打ったチェック */
    public boolean okSum() {
    	return (pPSum == 18) && (pLSum == 4) && (pNSum == 4) && (pSSum == 4) && (pGSum == 4)
    			&& (pRSum == 2) && (pBSum == 2) && (pKSum == 2);
    }
    
	int pKSum = 0;
	int pPSum = 0;
	int pLSum = 0;
	int pNSum = 0;
	int pSSum = 0;
	int pGSum = 0;
	int pRSum = 0;
	int pBSum = 0;

}