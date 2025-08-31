package com.github.tand0.anshogio.util;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 盤面情報の実態。
 * こちらは評価値まわりを分離している
 */
public class BanmenNextEval extends BanmenNextBase {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(BanmenNextEval.class);
    
    /** PnDn値 */
    private PnDn[] pndn = null;

    /** コンストラクタ
     * @param key キー
     */
    protected BanmenNextEval(BanmenKey key) {
        super(key);
    }
    
    /** 入玉勝ちならtrue, 入玉勝ちでなければfalse, 未チェックならnu;; */
    public Boolean kingWin = null;
    
    /** 評価値 */
    private Float eval = null;
    
    /** 強制のときはtrue */
    private boolean forceFlag = false;

    /** 強制フラグを取得 */
    @Override   
    public boolean getForceFlag() {
        return forceFlag;
    }
    
    /** 評価値を入れる
     * @param force 強制更新 (postgresが優先される)
     * @param eval 値を計算する
     */
    @Override
    public void setEval(boolean force, float eval) {
        if (force) {
            this.forceFlag = true;
            this.eval = eval;
        } else {
            if (! this.forceFlag) {
                this.eval = eval;
            }
        }
    }
    /** 評価値を返す */
    @Override
    public Float getEvel() {
        if (pndn != null) {
            int myTeban = this.getMyKey().getTeban();
            if ((pndn[myTeban] != null) && (pndn[myTeban].pn == 0)) {
                // 自身のが詰んでいる場合 (1.1 or -1.1にする
                return (float)(1.1f - (myTeban *2.2f));
            }
            if ((pndn[1 - myTeban] != null) && (pndn[1 - myTeban].pn == 0)) {
                // 自分が詰まされている場合(-1.1 or 1.1にする)
                return (float)((myTeban *2.2f) - 1.1f);
            }
        }
        //
        // 評価値を返す
        return this.eval;
    }

    @Override
    public int getEvelTe(BanmenFactory factory,LinkedList<BanmenKey> banmenList) {
        BanmenKey te = null;
        //
        int teban = this.getMyKey().getTeban();
        float value = (teban == 0) ? Float.MIN_VALUE : Float.MAX_VALUE;
        boolean forceFlagTarget = false;
        for (BanmenKey teKey : this.getChild()) {
            if (te == null) {
                te = teKey; // デフォルトを入れる
            }
            if ((this.getMyKey().getTeban() == 0)  && banmenList.contains(teKey)) {
                // 千日手を回避するため、"先手の時は"同じ局面にはしない
                continue;
            }
            BanmenNext next = factory.create(teKey);
            Float winLossValue = next.getEvel();
            if (winLossValue == null) {
                logger.error("winLossValue == null");
                continue;
            }
            boolean childForceFlag = next.getForceFlag();
            if (forceFlagTarget && (!childForceFlag)) {
                // forceFlagTarget が true で、
                // child の forceFlag が falseの場合は
                // 無視する(postgreの指し手を優先する)
                continue;
            }
            if ((!forceFlagTarget) && childForceFlag) {
                // forceFlagTarget が false (つまり、childのforceFlagが一度もtrueになったことがない)で
                // でかつ、childForceFlag が true の場合は
                // forceFlagTarget を true にする
                forceFlagTarget = true;
                // 初期値を与えて繰り返す
                te = teKey;
                continue;
                // childForceFlagがfalseの場合は続行
            }
            //
            // forceFlagTargetがfalseの場合
            // つまり、childのforceFlagが一度もtrueになったことがない場合、か、
            // childのforceFlagがtrueの場合に来る
            if (teban == 0) { // 先手の場合、一番高手を選ぶ
                if (value < winLossValue) {
                    value = winLossValue;
                    te = teKey;
                }
            } else {
                if (winLossValue < value) {
                    value = winLossValue;
                    te = teKey;
                }
            }
        }
        if (te == null) {
            return -2;
        }
        logger.debug("te={}",te);
        return this.getMyKey().createKeyToTe(te);
    }

    /** pndn値を返す
     */
    @Override
    public PnDn[] getPnDn() {
        return this.pndn;
    }
    /** pndn値を返す
     * 
     * @return PnDn値
     */
    @Override
    public PnDn[] createPnDn() {
        int teban = this.getMyKey().getTeban();
        if (this.pndn == null) {
            this.pndn = new PnDn[2];
            if (this.isKingWin()) { // もしも入玉勝ちなら
                this.pndn[teban  ] = new PnDn(0, Integer.MAX_VALUE);
                this.pndn[1-teban] = new PnDn(Integer.MAX_VALUE, 0);
            } else {
                this.pndn[0] = new PnDn(1,1);
                this.pndn[1] = new PnDn(1,1);                
            }
        }
        return this.getPnDn();
    }
    
    /** 最大でチェックする階層深度 */
    private static final int MAX_CHECK = 20;

    /** 詰めろをチェックする
     * @param factory 工場
     * @param seme 0:先手が攻める, 1:後手が攻める
     * @param level どの枝まで探索するか、0なら何もしない,1なら1階層まで、2なら2階層まで、xならx階層まで探索する
     * @param route 過去のルート情報(重複はチェックしないようにするため)
     * @return 最後まで検索するか、メモリ不足の場合 true、それ以外の時はfalse
     */
    @Override
    public boolean executePnDn(BanmenFactory factory,int seme, int level, HashSet<BanmenKey> route) {
        // 使用ヒープサイズがxx%を超えたら計算できないことにする
        if (MAX_CHECK <= level) {
            return true; // 最も深いレベルまで来たので終了
        }
        // 展開されているか、どうかをチェックする
        boolean expand = this.isExpandChild();
        //
        // 子供を取得しに一手 pndnを更新
        List<BanmenKey> keyList = this.getChild();
        this.createPnDn(); // pndnがなければcreate
        int size = this.getChild().size();
        if (size == 0) { // 詰まされている
            int teban = this.getMyKey().getTeban();
            this.pndn[teban  ].pn = Integer.MAX_VALUE;
            this.pndn[teban  ].dn = 0;
            this.pndn[1-teban].pn = 0;
            this.pndn[1-teban].dn = Integer.MAX_VALUE;
            //logger.debug("level={} pn={} dn={}",level,this.pndn[seme].pn,this.pndn[seme].dn);
            return false;
        }
        //
        if (!expand) {
            return false; // 一度戻る
        }
        if ((this.pndn[0].pn == 0) || (this.pndn[1].pn == 0)) {
            return false; // すでに詰み or 詰まされ状態でなければ、チェック不要
        }
        //logger.debug("level={} pn={} dn={}",level,this.pndn[seme].pn,this.pndn[seme].dn);

        //
        BanmenNext target;
        if (seme == this.getMyKey().getTeban()) {
            target = executePnDnSeme(factory, seme, level, keyList, route);
        } else {
            target = executePnDnUke(factory, seme, level, keyList, route);
        }
        if (target == null) {
            return false; // 次の階層の枝がない
        }
        // 詰めろエンジンを動かす前に、過去のtargetのPnDnを覚えておく
        PnDn[] old = new PnDn[2];
        old[0] = target.getPnDn()[0].copy();
        old[1] = target.getPnDn()[1].copy();
        //
        route.add(target.getMyKey());// ルートに追加
        boolean limit = target.executePnDn(factory,seme, level + 1, route);
        route.remove(target.getMyKey());// ルートから削除
        if (seme == this.getMyKey().getTeban()) {
            updateMyPnDnSeme(this.getPnDn()[seme],old[seme],target.getPnDn()[seme]);
        } else {
            updateMyPnDnUke(this.getPnDn()[seme],old[seme],target.getPnDn()[seme]);     
        }
        return limit;
    }
    
    /**
     * 攻めのPnDnを実行する
     * @param factory 工場
     * @param seme 先手攻めなら0、後手攻めなら1
     * @param level 探索階層
     * @param keyList 子供
     * @param route 過去の盤面(千日手防止用)
     * @return 盤面
     */
    protected BanmenNext executePnDnSeme(BanmenFactory factory,int seme,int level, List<BanmenKey> keyList, HashSet<BanmenKey> route) {
        BanmenNext target = null;
        this.pndn[seme].pn = Integer.MAX_VALUE;
        this.pndn[seme].dn = 0;
        for (BanmenKey key : keyList) {
            BanmenNext next = factory.create(key);
            PnDn childPnDn = next.createPnDn()[seme];
            if (route.contains(key)
                    || (! next.isMyOute())
                    || (next.getPnDn()[seme].dn == 0)) {
                continue; // ルート重複か、敵に王手が掛かっていないか 不詰め確定なら無視する
            }
            if (next.getPnDn()[seme].pn == 0) {
                // 一つでも詰み手順があるならその手を指すのでもう探索不要
                //
                // 詰みである
                this.pndn[seme].pn = next.getPnDn()[seme].pn;
                this.pndn[seme].dn = next.getPnDn()[seme].dn;
                break;
            }
            //
            // 次の階層の枝(target)を更新する
            if (target == null) {
                target = next;
            } else {
                if ((childPnDn.pn < this.pndn[seme].pn)
                        || ((childPnDn.pn == this.pndn[seme].pn) 
                                && (childPnDn.dn < this.pndn[seme].dn))) {
                    // 新しいpnの方が小さいか、pnが同じでdnが少なければ
                    target = next; // 次の階層の枝をpn最小値にする
                }
            }
            this.pndn[seme].minPn(childPnDn);
            this.pndn[seme].sumDn(childPnDn);
        }
        if (this.pndn[seme].pn != 0) {
            this.pndn[seme].pn += (level*350);
        }
        //logger.debug("seme level={} pn={} dn={}",level,this.pndn[seme].pn,this.pndn[seme].dn);
        return target;
    }
    /**
     * 受けのPnDnを実行する
     * @param factory 工場
     * @param seme 先手攻めなら0、後手攻めなら1
     * @param level 探索階層
     * @param keyList 子供
     * @param route 過去の盤面(千日手防止用)
     * @return 盤面
     */
    protected BanmenNext executePnDnUke(BanmenFactory factory, int seme,int level,List<BanmenKey> keyList, HashSet<BanmenKey> route) {
        BanmenNext target = null;
        this.pndn[seme].pn = 0;
        this.pndn[seme].dn = Integer.MAX_VALUE;
        for (BanmenKey teKey : keyList) {
            BanmenNext next = factory.create(teKey);
            PnDn childPnDn = next.createPnDn()[seme];
            if (route.contains(next.getMyKey())
                    || (next.getPnDn()[seme].pn == 0)) {
                continue; // 重複や詰む手は無視する
            } else if (next.getPnDn()[seme].dn == 0) {
                // 一つでも不詰み手順があるならその手を指すのでもう探索不要
                //
                // 自局面は不詰みである
                this.pndn[seme].pn = next.getPnDn()[seme].pn;
                this.pndn[seme].dn = next.getPnDn()[seme].dn;
                break;
            }
            //
            // 次の階層の枝(target)を更新する
            if (target == null) {
                target = next;
            } else {
                if ((childPnDn.dn <= this.pndn[seme].dn)
                        || ((childPnDn.dn == this.pndn[seme].dn) 
                            && (childPnDn.pn < this.pndn[seme].pn))) {
                    // 新しいdnの方が小さいか、dnが同じでdnが少なければ
                    target = next; // 次の階層の枝をdn最小値にする
                }            
            }
            this.pndn[seme].sumPn(childPnDn);
            this.pndn[seme].minDn(childPnDn);
        }
        if (this.pndn[seme].dn != 0) {
            this.pndn[seme].dn += (level*350);
        }
        //logger.debug("uke- level={} pn={} dn={}",level,this.pndn[seme].pn,this.pndn[seme].dn);
        return target;
    }
    /**
     * 自分のPnDnを更新する(攻め)
     * @param myPnDn 自分のpndn
     * @param oldPnDn 次の指し手の以前のPnDn
     * @param newPnDn 次の指し手の更新語のPnDn
     */
    protected void updateMyPnDnSeme(PnDn myPnDn ,PnDn oldPnDn,PnDn newPnDn) {
        // 更新後の詰み先の情報で更新する
        //logger.debug("seme now={}/{} old={}/{} new={}/{}",myPnDn.pn,myPnDn.dn,oldPnDn.pn,oldPnDn.dn,newPnDn.pn,newPnDn.dn);
        myPnDn.minPn(newPnDn);
        myPnDn.diffDn(oldPnDn, newPnDn);
    }
    /**
     * 自分のPnDnを更新する(受け)
     * @param myPnDn 自分のpndn
     * @param oldPnDn 次の指し手の以前のPnDn
     * @param newPnDn 次の指し手の更新語のPnDn
     */
    protected void updateMyPnDnUke(PnDn myPnDn ,PnDn oldPnDn,PnDn newPnDn) {
        // 更新後の詰み先の情報で更新する
        //logger.debug("uke- now={}/{} old={}/{} new={}/{}",myPnDn.pn,myPnDn.dn,oldPnDn.pn,oldPnDn.dn,newPnDn.pn,newPnDn.dn);
        myPnDn.diffPn(oldPnDn, newPnDn);
        myPnDn.minDn(newPnDn);
    }

}
