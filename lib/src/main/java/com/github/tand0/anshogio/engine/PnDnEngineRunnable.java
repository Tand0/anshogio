package com.github.tand0.anshogio.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenNext;
import com.github.tand0.anshogio.util.ChildTeNext;
import com.github.tand0.anshogio.util.PnDn;

/** 詰めろエンジンを詰む */
public class PnDnEngineRunnable extends EngineRunnable {

    /** 最大でチェックする階層深度 */
    private static final int MAX_CHECK = 12;
    
    /** 最大でチェックする必至の手順 */
    private static final int MAX_TUMERO = 5;
    
    /** 工場 */
    private final BanmenFactory factory;
    
    /** 同一局面のリスト */
    private final LinkedList<BanmenNext> banmenList;

    /** コンストラクタ
     * 
     * @param factory 工場
     * @param banmenList 手持ちの盤面の一覧
     */
    public PnDnEngineRunnable(BanmenFactory factory,LinkedList<BanmenNext> banmenList) {
        super(PnDnEngineRunnable.class.getSimpleName());
        this.banmenList = banmenList;
        this.factory = factory;
    }

    /** MAX_TUMEROで示す階層までPnDnを探索する */
    @Override
    public void run() {
        BanmenNext next = banmenList.getLast();
        int teban = banmenList.getLast().getMyKey().getTeban();
        HashSet<BanmenNext> route = new HashSet<>(banmenList);
        for (int level = 0 ; level < MAX_TUMERO ; level++) {
            if (isEnd() || factory.checkMemory()) {
                return;
            }
            // １手毎に攻め手は先手盤になったり後手番になったりする。0は先手、1は後手
            int seme = (teban + level) % 2;
            // PnDn を開始する盤面を探す
            boolean limitFlag = findBanmenNext(level,seme,next,route);
            if ((next.getPnDn(0).pn == 0)
                    || (next.getPnDn(1).pn == 0)
                    || limitFlag) {
                break; // 詰んだ or 詰まされた or 制限が来た場合は以降不要
            }
        }
    }
    /** PnDn を開始する盤面を探す
     * 
     * @param level 探索深さ
     * @param seme 0なら先手が攻めか、1なら先手が受けか。
     * @param next 次の盤面
     * @param route 盤面のリスト(千日手回避用)
     * @return 終了させる必要がある場合true
     */
    public boolean findBanmenNext(int level, int seme, BanmenNext next,HashSet<BanmenNext> route) {
        if (isEnd()) {
            return true;
        }
        boolean limitFlag = false;
        if (level == 0) { // 0階層なら探索
            limitFlag = findLevel0(level, seme, next, route);
        } else { // 1階層以上なら再帰処理
            limitFlag = findLevelOther(level - 1, seme, next, route);            
        }
        return limitFlag;
    }
    /** 0階層なら探索を行う
     * 
     * @param level 探索深さ
     * @param seme 0なら先手が攻めか、1なら先手が受けか。
     * @param next 次の盤面
     * @param route 盤面のリスト(千日手回避用)
     * @return 終了させる必要がある場合true
     */
    private boolean findLevel0(int level, int seme, BanmenNext next,HashSet<BanmenNext> route) {
        boolean limitFlag = false;
        while ((! isEnd()) && (! factory.checkMemory()) && (! limitFlag)) {
            limitFlag = next.executePnDn(factory, seme, MAX_CHECK, route);
            if ((next.getPnDn(seme).pn == 0) // 詰み確定
                    || (next.getPnDn(seme).dn == 0) // 不詰み確定
                    || (next.getPnDn(1-seme).pn == 0) // 詰まされ確定
                    || (next.getPnDn(seme).dn == 0)) { // 不詰まされ確定
                break;
            }
        }
        return limitFlag;
    }
    /** 1階層以上なるなら1階層下の合法手を探索する
     * 
     * @param level 探索深さ
     * @param seme 0なら先手が攻めか、1なら先手が受けか。
     * @param next 次の盤面
     * @param route 盤面のリスト(千日手回避用)
     * @return 終了させる必要がある場合true
     */
    private boolean findLevelOther(int level, int seme, BanmenNext next,HashSet<BanmenNext> route) {
        //java.util.ConcurrentModificationExceptionが出たので一度退避
        List<ChildTeNext> childHashSet = new ArrayList<>(next.getChild(factory));
        for (ChildTeNext teNext : childHashSet) {
            if (isEnd() || factory.checkMemory()) {
                return true;
            }
            if (route.contains(teNext.getNext())) {
                continue; // 重複している
            }
            //
            route.add(teNext.getNext());
            findBanmenNext(level, seme, teNext.getNext(),route); //再帰処理
            route.remove(teNext.getNext());
        }
        // 戻り値を受けて値を更新する
        boolean flag = true;
        PnDn basePnDn = next.getPnDn(seme);
        for (ChildTeNext teNext : next.getChild(factory)) {
            PnDn childPndn = teNext.getNext().getPnDn(seme);
            if (flag) {
                flag = false; // 初期はこうなる。初期フラグを落とす
                basePnDn.pn = childPndn.pn;
                basePnDn.dn = childPndn.dn;
            } else if (seme == teNext.getNext().getMyKey().getTeban()) {
                basePnDn.sumPn(childPndn);
                basePnDn.minDn(childPndn);
            } else {
                basePnDn.minPn(childPndn);
                basePnDn.sumDn(childPndn);
            }
        }
        if (basePnDn.pn == 0) {
            next.getPnDn(1- seme).dn = 0; //詰みのとき、受け側は不詰み確定
        }
        return false;
    }
    
    @Override
    public int getTe() {
        return banmenList.getLast().getEvelTe(factory,banmenList);
    }
    
}
