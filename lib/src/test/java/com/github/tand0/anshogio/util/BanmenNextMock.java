package com.github.tand0.anshogio.util;

import java.util.HashMap;

/** BanmenNextのMockオブジェクト */
public class BanmenNextMock extends BanmenNextEval {

    /** MOck用のオブジェクト */
    private HashMap<Integer,BanmenNext> mockChild = new HashMap<>();
    
    /** MOKC用の入玉勝ちチェック (trueなら入玉勝ち) */
    private boolean kingWin;
    
    /** 敵を王手しているかチェック (trueなら王手 */
    private final boolean enemyOute;

    /** コンストラクタ */
    public BanmenNextMock(BanmenKey key,int teban, boolean kingWin,boolean enemyOute) {
        super(key, new BanmenOnly(null,0));
        this.getBanmen().setTeban(teban); // ダミーを作る
        this.kingWin = kingWin;
        this.enemyOute = enemyOute;
    }
    /** 子づくりする(Mockなのでfactoryは使わない。盤面から合法手を作らない) */
    public synchronized HashMap<Integer, BanmenNext> getChildGetGouhou(BanmenFactory factory) {
        if (this.childFileld == null) {
            this.childFileld = this.mockChild;
        }
        return this.mockChild;
    }
    /** mock用に合法手を追加する */
    public void addMockChild(BanmenNext next) {
        int key = next.getMyKey().hashCode();
        mockChild.put(key, next);
    }

    /**
     * 入玉勝ちチェック
     * @return 入玉勝ちの場合 true
     */
    @Override
    public boolean isKingWin() {
        return kingWin;
    }
    /** 敵に王手をしているならtrue
     */
    @Override
    public boolean isEnemyOute() {
        return this.enemyOute;
    }
}
