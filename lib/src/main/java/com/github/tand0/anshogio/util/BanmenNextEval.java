package com.github.tand0.anshogio.util;


import static com.github.tand0.anshogio.util.BanmenDefine.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/** 木構造付きの情報 */
public class BanmenNextEval extends BanmenNextBase {
    
    /** コンストラクタ
     * @param key キー
     * @param bannmen 盤面
     */
    protected BanmenNextEval(BanmenKey key, BanmenOnly bannmen) {
        super(key, bannmen);
    }
    
    /** 入玉勝ちならtrue, 入玉勝ちでなければfalse, 未チェックならnu;; */
    public Boolean kingWin = null;
    
    /**
     * 入玉勝ちチェック
     * @return 入玉勝ちの場合 true
     */
    @Override
    public boolean isKingWin() {
        if (kingWin == null) {
            // kingWin チェックをしていないのでこれを実行する
            int myOuX;
            int myOuY;
            if (getBanmen().getTeban()==0) { // 先手
                myOuX = getBanmen().getSenteOuX();
                myOuY = getBanmen().getSenteOuY();
            } else {
                myOuX = getBanmen().getGoteOuX();
                myOuY = getBanmen().getGoteOuY();
            }
            kingWin = isKingWin(myOuX,myOuY);
        }
        return kingWin;
    }

    /** 子づくりする */
    @Override
    public HashMap<Integer, BanmenNext> getChild(BanmenFactory factory) {
        HashMap<Integer, BanmenNext> childMap = this.getChildGetGouhou(factory);
        int teban = this.getBanmen().getTeban();
        if (this.isKingWin()) { // もしも入玉勝ちなら
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
     * @return 入玉勝ちの場合 true
     */
    protected boolean isKingWin(int myOuX,int myOuY) {
        int teban = this.getBanmen().getTeban(); // 先手-1,後手1に変換
        int range0;
        if ((teban == 0) && (myOuY <= 2)) {
            range0 = 0;
        } else if ((teban != 0) && (6 <= myOuY)) {
            range0 = 6;
        } else {
            return false; // 入玉していない
        }
        if (this.getBanmen().checkSelfMate(teban,myOuX,myOuY)) {
            return false;// 大手を掛けられていない
        }
        
        //
        // 盤面上の敵陣の駒を数える
        int sum = 0;
        int value = 0;
        for (int x = 0 ; x < BanmenDefine.B_MAX ; x++) {
            for (int y = range0 ; y <= (range0 +2) ; y++) {
                byte koma = this.getBanmen().getKoma(x, y);
                if (koma == BanmenDefine.pNull) {
                    continue;
                }
                if (((koma & BanmenDefine.ENEMY)/BanmenDefine.ENEMY) == teban) {
                    if ((koma & 0b111) != BanmenDefine.pK) {
                        sum++;
                        if (((koma & 0b111) == BanmenDefine.pB)
                                && ((koma & 0b111) == BanmenDefine.pR)){
                            value = value + 5; // 大駒は５点
                        } else {
                            value = value + 1; // 小駒は１点
                        }
                    }
                }
            }
        }
        if (sum < 10) {
            return false; // 王を除く自駒が10枚より少ない
        }
        
        // 手持ちの小駒の数を数える
        for (byte i = 0 ; i < BanmenDefine.pB ; i++) {
            value = value + this.getBanmen().getTegoma(i, teban);
        }
        // 手持ちの大駒の数を数える
        for (byte i = pB ; i <= BanmenDefine.pR ; i++) {
            value = value + this.getBanmen().getTegoma(i, teban) * 5;
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
        int myTeban = this.getBanmen().getTeban();
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

    /** 最善手を返す */
    public int getEvelTe(BanmenFactory factory,LinkedList<BanmenNext> banmenList) {
        int te = -2;
        //
        int teban = this.getBanmen().getTeban();
        float value = (teban == 0) ? Float.MIN_VALUE : Float.MAX_VALUE;
        boolean forceFlagTarget = false;
        for (Map.Entry<Integer, BanmenNext> entry : this.getChild(factory).entrySet()) {
            if (banmenList.contains(entry.getValue())) {
                // 千日手を回避するため、同じ局面にはしない
                continue;
            }
            Float winLossValue = entry.getValue().getEvel();
            if (winLossValue == null) {
                if (te < 0) {
                    te = entry.getKey();
                }
            } else {
                if (!forceFlagTarget) {
                    // forceFlagTargetがfalseの場合
                    // つまり、childのforceFlagが一度もtrueになったことがない場合、
                    forceFlagTarget = entry.getValue().getForceFlag();
                    if (forceFlagTarget) {
                        // これまでに来た値はpostgreの値でないので書き換える
                        te = entry.getKey();
                        continue;
                    }
                    // forceFlagTargetがfalseの場合は続行
                } else if (!entry.getValue().getForceFlag()) {
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
                        te = entry.getKey();
                    }
                } else {
                    if (winLossValue < value) {
                        value = winLossValue;
                        te = entry.getKey();
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
        Collection<BanmenNext> childSet = this.getChild(factory).values();
        //
        // childを取ることで中身が展開されて自身のpndn値が更新されることがある場合対策
        if ((pndn[0].pn == 0) || (pndn[1].pn == 0) || (!expand)) {
            return false; // すでに詰み or 詰まされ状態 or 展開されていなければ次ループへ
        }
        BanmenNext target;
        if (seme == this.getBanmen().getTeban()) {
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
        if (seme == this.getBanmen().getTeban()) {
            updateMyPnDnSeme(seme,target,old);
            updateMyPnDnUke(seme,target,old);       
        } else {
            updateMyPnDnUke(seme,target,old);       
            updateMyPnDnSeme(1- seme,target,old);
        }
        return limit;
    }
    public BanmenNext executePnDnSeme(int seme,Collection<BanmenNext> childSet, HashSet<BanmenNext> route) {
        BanmenNext target = null;
        pndn[seme].pn = Integer.MAX_VALUE;
        pndn[seme].dn = 0;
        for (BanmenNext child : childSet) {
            if (route.contains(child)
                    || (! child.isEnemyOute())
                    || (child.getPnDn(seme).dn == 0)) {
                continue; // ルート重複か、敵に王手が掛かっていないか 不詰め確定なら無視する
            }
            if (child.getPnDn(seme).pn == 0) {
                break; // 一つでも詰み手順があるならその手を指すのでもう探索不要
            }
            PnDn childPnDn = child.getPnDn(seme);
            //
            // 次の階層の枝(target)を更新する
            if (target == null) {
                target = child;
            } else {
                if ((childPnDn.pn < pndn[seme].pn)
                        || ((childPnDn.pn == pndn[seme].pn) 
                                && (childPnDn.dn < pndn[seme].dn))) {
                    // 新しいpnの方が小さいか、pnが同じでdnが少なければ
                    target = child; // 次の階層の枝をpn最小値にする
                }
            }
            pndn[seme].minPn(childPnDn);
            pndn[seme].sumDn(childPnDn);
        }
        return target;
    }
    public BanmenNext executePnDnUke(int seme,Collection<BanmenNext> childSet, HashSet<BanmenNext> route) {
        BanmenNext target = null;
        pndn[seme].pn = 0;
        pndn[seme].dn = Integer.MAX_VALUE;
        for (BanmenNext child : childSet) {
            if (route.contains(child)
                    || (child.getPnDn(seme).pn == 0)) {
                continue; // 重複や詰む手は無視する
            } else if (child.getPnDn(seme).dn == 0) {
                break; // 一つでも不詰み手順があるならその手を指すのでもう探索不要
            }
            PnDn childPnDn = child.getPnDn(seme);
            //
            // 次の階層の枝(target)を更新する
            if (target == null) {
                target = child;
            } else {
                if ((childPnDn.dn <= pndn[seme].dn)
                        || ((childPnDn.dn == pndn[seme].dn) 
                            && (childPnDn.pn < pndn[seme].pn))) {
                    // 新しいdnの方が小さいか、dnが同じでdnが少なければ
                    target = child; // 次の階層の枝をdn最小値にする
                }            
            }
            pndn[seme].sumPn(childPnDn);
            pndn[seme].minDn(childPnDn);
        }
        return target;
    }
    protected void updateMyPnDnSeme(int seme,BanmenNext target,PnDn old) {
        // 更新後の詰み先の情報で更新する
        PnDn targetPnDn = target.getPnDn(seme);
        pndn[seme].minPn(targetPnDn);
        pndn[seme].diffDn(old, targetPnDn);
    }
    protected void updateMyPnDnUke(int seme,BanmenNext target,PnDn old) {
        // 更新後の詰み先の情報で更新する
        PnDn targetPnDn = target.getPnDn(seme);
        pndn[seme].diffPn(old, targetPnDn);
        pndn[seme].minDn(targetPnDn);
    }
    
    private final PnDn[] pndn = {new PnDn(1,1),new PnDn(1,1)};

}
