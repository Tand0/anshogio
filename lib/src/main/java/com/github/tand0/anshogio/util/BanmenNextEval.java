package com.github.tand0.anshogio.util;


import static com.github.tand0.anshogio.util.BanmenDefine.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 盤面情報の実態。
 * こちらは評価値まわりを分離している
 */
public class BanmenNextEval extends BanmenNextBase {
    /** PnDn値 */
    private final PnDn[] pndn = {new PnDn(1,1),new PnDn(1,1)};

    /** コンストラクタ
     * @param key キー
     */
    protected BanmenNextEval(BanmenKey key) {
        super(key);
    }
    
    /** 入玉勝ちならtrue, 入玉勝ちでなければfalse, 未チェックならnu;; */
    public Boolean kingWin = null;
    
    /**
     * 入玉勝ちチェック
     * @return 入玉勝ちの場合 true
     */
    @Override
    public boolean isKingWin() {
        return isKingWin(this.createBanmenOnly());
    }
    
    /**
     * 入玉勝ちチェック
     * @param banmen 盤面
     * @return 入玉勝ちの場合 true
     */
    public boolean isKingWin(BanmenOnly banmen) {
        if (kingWin == null) {
            // kingWin チェックをしていないのでこれを実行する
            int myOuX;
            int myOuY;
            if (this.getMyKey().getTeban()==0) { // 先手
                myOuX = banmen.getSenteOuX();
                myOuY = banmen.getSenteOuY();
            } else {
                myOuX = banmen.getGoteOuX();
                myOuY = banmen.getGoteOuY();
            }
            kingWin = isKingWin(myOuX,myOuY, banmen);
        }
        return kingWin;
    }

    /**
     * keyから盤面を作る
     * @return 盤面
     */
    public BanmenOnly createBanmenOnly() {
        return this.getMyKey().createBanmenOnly();
    }

    @Override
    public List<ChildTeNext> getChild(BanmenFactory factory) {
        BanmenOnly banmenOnly = createBanmenOnly();
        List<ChildTeNext> childMap = this.getChildGetGouhou(factory,banmenOnly);
        int teban = this.getMyKey().getTeban();
        if (this.isKingWin(banmenOnly)) { // もしも入玉勝ちなら
            pndn[teban  ].pn = 0;
            pndn[teban  ].dn = Integer.MAX_VALUE;
            pndn[1-teban].pn = Integer.MAX_VALUE;
            pndn[1-teban].dn = 0;
        } else if (childMap.size() == 0) { // 詰まされている
            pndn[teban  ].pn = Integer.MAX_VALUE;
            pndn[teban  ].dn = 0;
            pndn[1-teban].pn = 0;
            pndn[1-teban].dn = Integer.MAX_VALUE;
        } else {
            pndn[teban  ].pn = 1; //min 初回なので子供は全て1/1のはず
            pndn[teban  ].dn = childMap.size(); // sum 
            pndn[1-teban].pn = childMap.size(); // sum
            pndn[1-teban].dn = 1; // min
        }
        return childMap;
    }

    /**
     * 入玉勝ちチェック
     * @param myOuX 自分の王x
     * @param myOuY 自分の王y
     * @param banmenOnly 盤面
     * @return 入玉勝ちならtrue
     */
    protected boolean isKingWin(int myOuX,int myOuY,BanmenOnly banmenOnly) {
        int teban = this.getMyKey().getTeban(); // 先手-1,後手1に変換
        int range0;
        if ((teban == 0) && (myOuY <= 2)) {
            range0 = 0;
        } else if ((teban != 0) && (6 <= myOuY)) {
            range0 = 6;
        } else {
            return false; // 入玉していない
        }
        if (banmenOnly.checkSelfMate(teban,myOuX,myOuY)) {
            return false;// 大手を掛けられていない
        }
        
        //
        // 盤面上の敵陣のコマを数える
        int sum = 0;
        int value = 0;
        for (int x = 0 ; x < BanmenDefine.B_MAX ; x++) {
            for (int y = range0 ; y <= (range0 +2) ; y++) {
                byte koma = banmenOnly.getKoma(x, y);
                if (koma == BanmenDefine.pNull) {
                    continue;
                }
                if (((koma & BanmenDefine.ENEMY)/BanmenDefine.ENEMY) == teban) {
                    if ((koma & 0b111) != BanmenDefine.pK) {
                        sum++;
                        if (((koma & 0b111) == BanmenDefine.pB)
                                && ((koma & 0b111) == BanmenDefine.pR)){
                            value = value + 5; // 大コマは５点
                        } else {
                            value = value + 1; // 小コマは１点
                        }
                    }
                }
            }
        }
        if (sum < 10) {
            return false; // 王を除く自コマが10枚より少ない
        }
        
        // 手持ちの小コマの数を数える
        for (byte i = 0 ; i < BanmenDefine.pB ; i++) {
            value = value + banmenOnly.getTegoma(i, teban);
        }
        // 手持ちの大コマの数を数える
        for (byte i = pB ; i <= BanmenDefine.pR ; i++) {
            value = value + banmenOnly.getTegoma(i, teban) * 5;
        }
        // 先手は28枚、後手は27枚以上ある
        return (27 + (1-teban)) <= value;
    }
    
    
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
            if (this.forceFlag) {
                this.eval = eval;
            }
        }
    }
    /** 評価値を返す */
    @Override
    public Float getEvel() {
        int myTeban = this.getMyKey().getTeban();
        if ((pndn[myTeban] != null) && (pndn[myTeban].pn == 0)) {
            // 自身のが詰んでいる場合 (1.1 or -1.1にする
            return (float)(1.1f - (myTeban *2.2f));
        }
        if ((pndn[1 - myTeban] != null) && (pndn[1 - myTeban].pn == 0)) {
            // 自分が詰まされている場合(-1.1 or 1.1にする)
            return (float)((myTeban *2.2f) - 1.1f);
        }
        //
        // 評価値を返す
        return this.eval;
    }

    @Override
    public int getEvelTe(BanmenFactory factory,LinkedList<BanmenNext> banmenList) {
        int te = -2;
        //
        int teban = this.getMyKey().getTeban();
        float value = (teban == 0) ? Float.MIN_VALUE : Float.MAX_VALUE;
        boolean forceFlagTarget = false;
        for (ChildTeNext teNext : this.getChild(factory)) {
            if (banmenList.contains(teNext.getNext())) {
                // 千日手を回避するため、同じ局面にはしない
                continue;
            }
            Float winLossValue = teNext.getNext().getEvel();
            if (winLossValue == null) {
                if (te < 0) {
                    te = teNext.getTe();
                }
            } else {
                if (!forceFlagTarget) {
                    // forceFlagTargetがfalseの場合
                    // つまり、childのforceFlagが一度もtrueになったことがない場合、
                    forceFlagTarget = teNext.getNext().getForceFlag();
                    if (forceFlagTarget) {
                        // これまでに来た値はpostgreの値でないので書き換える
                        te = teNext.getTe();
                        continue;
                    }
                    // forceFlagTargetがfalseの場合は続行
                } else if (!teNext.getNext().getForceFlag()) {
                    // forceFlagTargetがtrueで、
                    // childのforceFlagがfalseの場合は無視する(postgreの指し手を優先する)
                    continue;
                }
                //
                // forceFlagTargetがfalseの場合
                // つまり、childのforceFlagが一度もtrueになったことがない場合、か、
                // childのforceFlagがtrueの場合に来る
                if (teban == 0) { // 先手の場合、一番高手を選ぶ
                    if (value < winLossValue) {
                        value = winLossValue;
                        te = teNext.getTe();
                    }
                } else {
                    if (winLossValue < value) {
                        value = winLossValue;
                        te = teNext.getTe();
                    }
                }
            }
        }
        return te;
    }

    /** pndn値を返す
     */
    @Override
    public PnDn getPnDn(int teban) {
        if (pndn == null) {
            return null;
        }
        return pndn[teban];
    }
    
    /** 詰めろをチェックする
     * @param factory 工場
     * @param seme 0:先手が攻める, 1:後手が攻める
     * @param level どの枝まで探索するか、0なら何もしない,1なら1階層まで、2なら2階層まで、xならx階層まで探索する
     * @param route 過去のルート情報(重複はチェックしないようにするため)
     * @return 最後まで検索するか、メモリ不足の場合 true、それ以外の時はfalse
     */
    @Override
    public boolean executePnDn(BanmenFactory factory,int seme, int level, HashSet<BanmenNext> route) {
        // 使用ヒープサイズがxx%を超えたら計算できないことにする
        if (factory.checkMemory()) {
            return true;
        }
        if (level == 0) {
            return true; // 最も深いレベルまで来たので終了
        }
        if ((pndn[0].pn == 0) || (pndn[1].pn == 0)) {
            return false; // すでに詰み or 詰まされ状態でなければ、チェック不要
        }
        // 展開されているか、どうかをチェックする
        boolean expand = this.isExpandChild();
        //
        // 子供を取得しに一手 pndnを更新
        List<ChildTeNext> childSet = this.getChild(factory);
        //
        // childを取ることで中身が展開されて自身のpndn値が更新されることがある場合対策
        if ((pndn[0].pn == 0) || (pndn[1].pn == 0) || (!expand)) {
            return false; // すでに詰み or 詰まされ状態 or 展開されていなければ次ループへ
        }
        BanmenNext target;
        if (seme == this.getMyKey().getTeban()) {
            target = executePnDnSeme(seme,childSet, route);
        } else {
            target = executePnDnUke(seme,childSet, route);
        }
        if (target == null) {
            return false; // 次の階層の枝がない
        }
        // 詰めろエンジンを動かす前に、過去のtargetのPnDnを覚えておく
        PnDn old = target.getPnDn(seme).copy();
        //
        route.add(target);// ルートに追加
        boolean limit = target.executePnDn(factory,seme, level - 1, route);
        route.remove(target);// ルートから削除
        if (seme == this.getMyKey().getTeban()) {
            updateMyPnDnSeme(seme,target,old);
            updateMyPnDnUke(seme,target,old);       
        } else {
            updateMyPnDnUke(seme,target,old);       
            updateMyPnDnSeme(1- seme,target,old);
        }
        return limit;
    }
    
    /**
     * 攻めのPnDnを実行する
     * @param seme 先手攻めなら0、後手攻めなら1
     * @param teNextList 子供
     * @param route 過去の盤面(千日手防止用)
     * @return 盤面
     */
    protected BanmenNext executePnDnSeme(int seme,List<ChildTeNext> teNextList, HashSet<BanmenNext> route) {
        BanmenNext target = null;
        pndn[seme].pn = Integer.MAX_VALUE;
        pndn[seme].dn = 0;
        for (ChildTeNext teNext : teNextList) {
            if (route.contains(teNext.getNext())
                    || (! teNext.getNext().isEnemyOute())
                    || (teNext.getNext().getPnDn(seme).dn == 0)) {
                continue; // ルート重複か、敵に王手が掛かっていないか 不詰め確定なら無視する
            }
            if (teNext.getNext().getPnDn(seme).pn == 0) {
                break; // 一つでも詰み手順があるならその手を指すのでもう探索不要
            }
            PnDn childPnDn = teNext.getNext().getPnDn(seme);
            //
            // 次の階層の枝(target)を更新する
            if (target == null) {
                target = teNext.getNext();
            } else {
                if ((childPnDn.pn < pndn[seme].pn)
                        || ((childPnDn.pn == pndn[seme].pn) 
                                && (childPnDn.dn < pndn[seme].dn))) {
                    // 新しいpnの方が小さいか、pnが同じでdnが少なければ
                    target = teNext.getNext(); // 次の階層の枝をpn最小値にする
                }
            }
            pndn[seme].minPn(childPnDn);
            pndn[seme].sumDn(childPnDn);
        }
        return target;
    }
    /**
     * 受けのPnDnを実行する
     * @param seme 先手攻めなら0、後手攻めなら1
     * @param teNextList 子供
     * @param route 過去の盤面(千日手防止用)
     * @return 盤面
     */
    protected BanmenNext executePnDnUke(int seme,List<ChildTeNext> teNextList, HashSet<BanmenNext> route) {
        BanmenNext target = null;
        pndn[seme].pn = 0;
        pndn[seme].dn = Integer.MAX_VALUE;
        for (ChildTeNext teNext : teNextList) {
            if (route.contains(teNext.getNext())
                    || (teNext.getNext().getPnDn(seme).pn == 0)) {
                continue; // 重複や詰む手は無視する
            } else if (teNext.getNext().getPnDn(seme).dn == 0) {
                break; // 一つでも不詰み手順があるならその手を指すのでもう探索不要
            }
            PnDn childPnDn = teNext.getNext().getPnDn(seme);
            //
            // 次の階層の枝(target)を更新する
            if (target == null) {
                target = teNext.getNext();
            } else {
                if ((childPnDn.dn <= pndn[seme].dn)
                        || ((childPnDn.dn == pndn[seme].dn) 
                            && (childPnDn.pn < pndn[seme].pn))) {
                    // 新しいdnの方が小さいか、dnが同じでdnが少なければ
                    target = teNext.getNext(); // 次の階層の枝をdn最小値にする
                }            
            }
            pndn[seme].sumPn(childPnDn);
            pndn[seme].minDn(childPnDn);
        }
        return target;
    }
    /**
     * 自分のPnDnを更新する(攻め)
     * @param seme  先手攻めなら0、後手攻めなら1
     * @param target 盤面
     * @param old 更新前のPnDn
     */
    protected void updateMyPnDnSeme(int seme,BanmenNext target,PnDn old) {
        // 更新後の詰み先の情報で更新する
        PnDn targetPnDn = target.getPnDn(seme);
        pndn[seme].minPn(targetPnDn);
        pndn[seme].diffDn(old, targetPnDn);
    }
    /**
     * 自分のPnDnを更新する(受け)
     * @param seme  先手攻めなら0、後手攻めなら1
     * @param target 盤面
     * @param old 更新前のPnDn
     */
    protected void updateMyPnDnUke(int seme,BanmenNext target,PnDn old) {
        // 更新後の詰み先の情報で更新する
        PnDn targetPnDn = target.getPnDn(seme);
        pndn[seme].diffPn(old, targetPnDn);
        pndn[seme].minDn(targetPnDn);
    }
    

}
