package com.github.tand0.anshogio.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/** 木構造付きの情報 */
public interface BanmenNext extends Comparable<BanmenNext> {

	/** 子供のリストが展開されていたらtrue */
	public boolean isExpandChild();
	
	/** 子供のリストを取得する */
	public HashMap<Integer,BanmenNext> getChild(BanmenFactory factory);

    /** 子供に手を追加する */
    public BanmenNext addTe(BanmenFactory factory, int te);
    
    /** 手を決定する(ほかの手は消す) */
    public BanmenNext decisionTe(BanmenFactory factory,int te);
	
    /** 盤面を取得する
	 */
	public BanmenOnly getBanmen();
	
	/** 自分のキーを渡す */
	public BanmenKey getMyKey();
	
	/**
	 * 使用中が１つ増える
	 */
	public void createDown(BanmenFactory factory);

	/** 全消しする */
	public void clearAllHash();

	/** hash code */
	public long hashCodeLong();

	/** hash code */
	@Override
    public int hashCode();
	
	/** hash code */
    public int hashCodeInt();

	/** hash code */
    public short hashCodeShort();
    
	/** hash code */
    public byte hashCodeByte();
    
	/**
	 * 入玉勝ちチェック
	 * @return 入玉勝ちの場合 true
	 */
	public boolean isKingWin();
	
	/** 強制フラグを取得する */
	public boolean getForceFlag();
	
	/** 評価値をセットする */
	public void setEval(boolean force,float eval);
	
	/** 評価値を取得する */
	public Float getEvel();
	
	/** 最善手を返す */
	public int getEvelTe(BanmenFactory factory,LinkedList<BanmenNext> banmenList);

	/** DnPnを返す */
	public PnDn getPnDn(int teban);
	
	/** 詰めろをチェックする */
	public boolean executePnDn(BanmenFactory factory,int seme, int level, HashSet<BanmenNext> route);

	/** 王手にする */
	public void setEnemyOute();

    /** 敵に王手をしているならtrue
     */
    public boolean isEnemyOute();
}
