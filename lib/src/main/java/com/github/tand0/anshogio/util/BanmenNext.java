package com.github.tand0.anshogio.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/** 木構造付きの情報 */
public interface BanmenNext extends Comparable<BanmenNext> {
    /**
     * 自分のキーを渡す
     * @return key値
     */
    public BanmenKey getMyKey();
    

	/**
	 * 子供のリストが展開されいるか？（PnDnで展開チェックに使う
	 * @return 子供のリストが展開されていたらtrue
	 */
	public boolean isExpandChild();
	
	/**
	 * 子供のリストを取得する
	 * @return 子供
	 */
	public List<BanmenKey> getChild();

	/**
	 * hash code
	 * @return hash
	 */
	public long hashCodeLong();

	/** hash code */
	@Override
    public int hashCode();
	
	/**
	 * hash code
	 * @return hash
	 */
    public int hashCodeInt();

    /**
     * hash code
     * @return hash
     */
    public short hashCodeShort();
    
    /**
     * hash code
     * @return hash
     */
    public byte hashCodeByte();
    
	/**
	 * 入玉勝ちチェック
	 * @return 入玉勝ちの場合 true
	 */
	public boolean isKingWin();
	
	/**
	 * 強制フラグを取得する
	 * @return 強制フラグ
	 */
	public boolean getForceFlag();
	
	/**
	 * 評価値をセットする
	 * @param force trueなら強制
	 * @param eval 評価値
	 */
	public void setEval(boolean force,float eval);
	
	/** 評価値を取得する
	 * 
	 * @return 評価値。評価していない場合は null
	 */
	public Float getEvel();
	
	/**
	 * 最善手を返す
	 * @param factory 工場
	 * @param banmenList 過去に打った盤面のリスト(千日手防止用)
	 * @return 打つべき手
	 */
	public int getEvelTe(BanmenFactory factory,LinkedList<BanmenKey> banmenList);

	/**
	 * PnDn を返す
	 * @return PnDn値
	 */
	public PnDn[] getPnDn();

	/** pndn値の初期値を返す
     * 
     * @return PnDn値
     */
    public PnDn[] createPnDn();

    /**
	 * 詰めろをチェックする
	 * @param factory 工場
	 * @param seme 先手が攻めなら0、先手が受けなら1
	 * @param level 探索レベル
	 * @param route 過去に打った盤面のリスト(千日手防止用)
	 * @return もう検索できないならtrue
	 */
	public boolean executePnDn(BanmenFactory factory,int seme, int level, HashSet<BanmenKey> route);

	/** 王手にする */
	public void setEnemyOute();

    /**
     * 王手情報
     * @return 敵に王手をしているならtrue
     */
    public boolean isMyOute();
}
