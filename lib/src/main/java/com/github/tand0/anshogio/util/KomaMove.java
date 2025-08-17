package com.github.tand0.anshogio.util;


import static com.github.tand0.anshogio.util.BanmenDefine.*;

import java.util.Arrays;
import java.util.List;

/** コマの利きをチェックするクラス */
public class KomaMove {
    /** x,y,flagの情報 (x軸, y軸, 飛車のように先も見るならflagはtrue) */
	public final XYFlag xYFlag;
	/** コマの種類 */
	public final int[] koma;

	/** 合法手を作るときに使う配列(上左右に移動できるコマ) */
	public final static int[] CHECKLOW_up =   { pG, pS, ppS, ppL, ppN, ppP, ppR, pK };
	/** 合法手を作るときに使う配列(真上に移動できるコマ) */
	public final static int[] CHECKLOW_upUp = { pG, pS, ppS, ppL, ppN, ppP, ppB, pK, pP };
	/** 合法手を作るときに使う配列(横に移動できるコマ) */
	public final static int[] CHECKLOW_yoko = { pG,     ppS, ppL, ppN, ppP, ppB, pK };
	/** 合法手を作るときに使う配列(下横に移動できるコマ) */
	public final static int[] CHECKLOW_downYoko = { pS, pK, ppR };
	/** 合法手を作るときに使う配列(桂馬) */
	public final static int[] CHECKLOW_kei = { pN };
	/** 合法手を作るときに使う配列(上に複数移動できるコマ) */
	public final static int[] CHECKLOW_kyo = { pL, pR, ppR };
	/** 合法手を作るときに使う配列(飛車) */
	public final static int[] CHECKLOW_hisya = { pR, ppR };
	/** 合法手を作るときに使う配列(角) */
	public final static int[] CHECKLOW_kaku = { pB, ppB };
    
    /** 移動先が合法手である手を探す用(8方向用)
     * 飛角香車は別でやるので、成ったときだけ入れておく
     */
    public final static List<KomaMove> movers = Arrays.asList(
            // 8方向
            new KomaMove( 1,-1,CHECKLOW_up,false),
            new KomaMove(-1,-1,CHECKLOW_up,false),
            new KomaMove( 0,-1,CHECKLOW_upUp,false),
            new KomaMove( 1, 0,CHECKLOW_yoko,false),
            new KomaMove(-1, 0,CHECKLOW_yoko,false),
            new KomaMove( 1, 1,CHECKLOW_downYoko,false),
            new KomaMove(-1, 1,CHECKLOW_downYoko,false),
            new KomaMove( 0, 1,CHECKLOW_yoko,false),
            //
            // 桂馬チェック
            new KomaMove( 1,-2,CHECKLOW_kei,false),
            new KomaMove(-1,-2,CHECKLOW_kei,false),
            //
            // 複数移動チェック(香角飛)
            new KomaMove( 0,-1,CHECKLOW_kyo,true),
            new KomaMove( 0, 1,CHECKLOW_hisya,true),
            new KomaMove( 1, 0,CHECKLOW_hisya,true),
            new KomaMove(-1, 0,CHECKLOW_hisya,true),
            new KomaMove( 1,-1,CHECKLOW_kaku,true),
            new KomaMove( 1, 1,CHECKLOW_kaku,true),
            new KomaMove(-1,-1,CHECKLOW_kaku,true),
            new KomaMove(-1, 1,CHECKLOW_kaku,true)
    );
    /** コンストラクタ
     * @param x 移動先のx位置
     * @param y 移動先のy位置
     * @param koma コマの種類
     * @param flag 複数移動する場合true (香,角,飛が対応)
     */
    public KomaMove(int x,int y,int[] koma,boolean flag) {
        this.xYFlag = new XYFlag(x,y,flag);
        this.koma = koma;
    }
}
