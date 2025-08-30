package com.github.tand0.anshogio.engine;

import java.util.LinkedList;

import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNext;

/** ベースエンジン */
public abstract class BaseEngineRunnable extends EngineRunnable {

    /** 同一局面のリスト */
    private final LinkedList<BanmenKey> banmenList;

    /** 工場 */
    private final BanmenFactory factory;
    
    /**
     * 工場の取得
     * @return 工場
     */
    protected BanmenFactory getFactory() {
        return this.factory;
    }
    
    /** 最後の盤面の取得
     * @return 最後の盤面
     */
    protected BanmenKey getBaseBanmenKey() {
        return banmenList.getLast();
    }
    /**
     * 盤面のリストの取得
     * @return 盤面のリスト
     */
    protected LinkedList<BanmenKey> getBanmenList() {
        return banmenList;
    }
    
    /** コンストラクタ
     * @param name 名前
     * @param factory 工場
     * @param banmenList 手持ちの盤面の一覧
     */
    public BaseEngineRunnable(String name, BanmenFactory factory, LinkedList<BanmenKey> banmenList) {
        super(name);
        this.factory = factory;
        this.banmenList = banmenList;
    }
    
    @Override
    public int getTe() {
        BanmenNext next = factory.create(getBaseBanmenKey());
        return next.getEvelTe(factory, getBanmenList());
    }
    
}
