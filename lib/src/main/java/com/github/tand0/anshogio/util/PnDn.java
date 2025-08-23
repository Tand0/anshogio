package com.github.tand0.anshogio.util;

/** PNDN値を保持する */
public class PnDn {
    /** Pn値 */
	public int pn;
	/** Dn値 */
	public int dn;
	
	/**
	 * コンストラクタ
	 * @param pn 初期pn値
	 * @param dn 初期dn値
	 */
	public PnDn(int pn,int dn) {
		this.pn = pn;
		this.dn = dn;
	}
	
	/**
	 * 最小のPnを求める
	 * @param target 子供のPnDn値
	 */
	public void minPn(PnDn target) {
		pn = Math.min(pn, target.pn);
	}
	/**
	 * Σpn を実現する
	 * @param target 子供のPnDn値
	 */
	public void sumPn(PnDn target) {
		if ((pn == Integer.MAX_VALUE) || (target.pn == Integer.MAX_VALUE)) {
			pn = Integer.MAX_VALUE; // 無限大から加算するとオーバーフローするので対応する
		} else {
			pn = pn + target.pn;
		}
	}

	/**
	 * pndnを差し替える
	 * @param minus 変更前の子供のPnDn値
	 * @param pluss 変更後の子供のPnDn値
	 */
	public void diffPn(PnDn minus, PnDn pluss) {
		if ((pn == Integer.MAX_VALUE) || (minus.pn == Integer.MAX_VALUE) || (pluss.pn == Integer.MAX_VALUE)) {
			pn = Integer.MAX_VALUE; // 無限大から無限大を引いても無限大とみなす
		} else {
			pn = Math.max(pn - minus.pn + pluss.pn, 0); // 負数にはしない
		}
	}
	/**
	 * 最小のDnを求める
	 * @param target 子供のPnDn値
	 */
	public void minDn(PnDn target) {
		dn = Math.min(dn, target.dn);
	}
	
	/**
	 * Σdn を実現する
	 * @param target 子供のPnDn値
	 */
	public void sumDn(PnDn target) {
		if ((dn == Integer.MAX_VALUE) || (target.dn == Integer.MAX_VALUE)) {
			dn = Integer.MAX_VALUE;
		} else {
			dn = dn + target.dn;
		}
	}

	/**
	 * pndnを差し替える
	 * @param minus 変更前の子供のPnDn値
	 * @param pluss 変更後の子供のPnDn値
	 */
	public void diffDn(PnDn minus, PnDn pluss) {
		if ((dn == Integer.MAX_VALUE) || (minus.dn == Integer.MAX_VALUE) || (pluss.dn == Integer.MAX_VALUE)) {
			dn = Integer.MAX_VALUE; // 無限大から無限大を引いても無限大とみなす
		} else {
			dn = Math.max(dn - minus.dn + pluss.dn, 0); // 負数にはしない
		}
	}
	
	/**
	 * コピーを作る
	 * @return コピー情報
	 */
	public PnDn copy() {
		return new PnDn(pn,dn);
	}
}