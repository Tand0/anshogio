package com.github.tand0.anshogio.util;

/** PNDN値を保持する */
public class PnDn {
	public int pn = 1;
	public int dn = 1;
	public PnDn(int pn,int dn) {
		this.pn = pn;
		this.dn = dn;
	}
	public boolean anyMatch() {
		return (pn == 0) || (dn == 0);
	}
	/**
	 * 最小のPnを求める
	 * @param target
	 */
	public void minPn(PnDn target) {
		pn = Math.min(pn, target.pn);
	}
	/**
	 * Σpn を実現する
	 * @param target
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
	 * @param minus
	 * @param pluss
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
	 * @param target
	 */
	public void minDn(PnDn target) {
		dn = Math.min(dn, target.dn);
	}
	
	/**
	 * Σdn を実現する
	 * @param target
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
	 * @param minus
	 * @param pluss
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
	 * @return
	 */
	public PnDn copy() {
		return new PnDn(pn,dn);
	}
}