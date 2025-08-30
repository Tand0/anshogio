package com.github.tand0.anshogio.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.github.tand0.anshogio.util.BanmenFactoryMock;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNextMock;


/** PnDnアルゴリズムに対するテスト */
public class PnDnEngineRunnableTest {
    /** コンストラクタ */
    public PnDnEngineRunnableTest() {
    }
    /**
     * 書籍No1のテスト
     */
    @Test
    public void no1Test() {
        int teban = 0; // 先手が攻める0、後手が攻める1
        HashSet<BanmenKey> route = new HashSet<>();
        BanmenFactoryMock factory = new BanmenFactoryMock();
        //
        // 位置をチェックする
        BanmenNextMock next = getBaseText(null, factory, teban);
        //
        // pndnを１回動かす(合法手を展開する)
        next.executePnDn(factory, teban, 0, route);
        //
        // 後手側のPnが0になっている
        assertEquals(next.getPnDn()[1- teban].pn,0);
    }
    /**
     * 書籍No2のテスト
     */
    @Test
    public void no2Test() {
        int teban = 1; // 先手が攻める0、後手が攻める1
        HashSet<BanmenKey> route = new HashSet<>();
        BanmenFactoryMock factory = new BanmenFactoryMock();
        //
        // 位置をチェックする
        BanmenNextMock next = getBaseText(null, factory, teban);
        //
        // pndnを１回動かす(合法手を展開する)
        next.executePnDn(factory, teban, 0, route);
        //
        // Pnが0になっている
        assertEquals(next.getPnDn()[1- teban].pn,0);
    }
    /**
     * 書籍No2-2のテスト
     */
    @Test
    public void no2no2Test() {
        int teban = 0; // 先手が攻める0、後手が攻める1
        LinkedList<BanmenKey> banmenList = new LinkedList<>();
        BanmenFactoryMock factory = new BanmenFactoryMock();
        //
        // 位置をチェックする
        BanmenNextMock next = getBaseText(null, factory, teban);
        banmenList.addLast(next.getMyKey());
        getBaseText(next, factory, 1- teban);
        //
        //
        PnDnEngineRunnable run = new PnDnEngineRunnable(factory,banmenList);
        run.run(); // スレッドを使わず直実行
        //
        // Pnが0になっている
        assertEquals(next.getPnDn()[teban].pn,0);
    }
    /**
     * 書籍No2-3のテスト
     */
    @Test
    public void no2no3Test() {
        for (int teban = 0 ; teban < 2 ; teban++) { // 先手が攻める0、後手が攻める1
            LinkedList<BanmenKey> banmenList = new LinkedList<>();
            BanmenFactoryMock factory = new BanmenFactoryMock();
            //
            // 位置をチェックする
            BanmenNextMock a = getBaseText(null, factory, teban);
            banmenList.add(a.getMyKey());
            BanmenNextMock b = getBaseText(a, factory, 1 - teban);
            @SuppressWarnings("unused")
            BanmenNextMock c = getBaseText(b, factory, teban);
            //
            PnDnEngineRunnable run = new PnDnEngineRunnable(factory,banmenList);
            run.run(); // スレッドを使わず直実行
            //
            // Pnが0になっている
            assertEquals(a.getPnDn()[1 - teban].pn,0);
        }
    }
    /**
     * 書籍No3のテスト
     */
    @Test
    public void no3Test() {
        int teban = 0; // 先手が攻める0、後手が攻める1
        no3to4Test(teban);
    }
    /**
     * 書籍No4のテスト
     */
    @Test
    public void no4Test() {
        int teban = 1; // 先手が攻める0、後手が攻める1
        no3to4Test(teban);
    }
    /**
     * 書籍No3から4のテストの中身
     * @param teban 手番
     */
    public void no3to4Test(int teban) {
        HashSet<BanmenKey> route = new HashSet<>();
        BanmenFactoryMock factory = new BanmenFactoryMock();
        //
        // 位置をチェックする
        BanmenNextMock next = getBaseText(null, factory, teban,true);
        //
        // pndnを１回動かす(合法手を展開する)
        next.executePnDn(factory, teban, 0, route);
        //
        // Pnが0になっている
        assertEquals(next.getPnDn()[1 - teban].pn,0);
    }

    /**
     * 書籍No5以降のテスト
     */
    @Test
    public void no5OverTest() {
        int teban = 0; // 先手が攻める0、後手が攻める1
        LinkedList<BanmenKey> banmenList = new LinkedList<>();
        BanmenFactoryMock factory = new BanmenFactoryMock();
        //
        // 位置をチェックする
        BanmenNextMock a1 = getBaseText(null, factory, teban, false , false);
        banmenList.add(a1.getMyKey());
        //
        BanmenNextMock b1 = getBaseText(a1,factory, 1- teban, false , true);
        BanmenNextMock b2 = getBaseText(a1,factory, 1- teban, false , true);
        @SuppressWarnings("unused")
        BanmenNextMock b3 = getBaseText(a1,factory, 1- teban, false , false);
        //
        BanmenNextMock c1 = getBaseText(b1,factory, 1- teban, false , true);
        @SuppressWarnings("unused")
        BanmenNextMock c2 = getBaseText(b2,factory, 1- teban, false , true);
        BanmenNextMock c3 = getBaseText(b2,factory, 1- teban, true , false);
        //
        @SuppressWarnings("unused")
        BanmenNextMock d1 = getBaseText(c1,factory, 1- teban, false , false);
        @SuppressWarnings("unused")
        BanmenNextMock d2 = getBaseText(c3,factory, 1- teban, false , false);
        @SuppressWarnings("unused")
        BanmenNextMock d3 = getBaseText(c3,factory, 1- teban, false , false);
        //
        PnDnEngineRunnable run = new PnDnEngineRunnable(factory,banmenList);
        run.run(); // スレッドを使わず直実行
        //
        // Pnが0になっている
        assertEquals(a1.getPnDn()[teban].pn,0);
    }
    
    /**
     * mockを取得する
     * @param base 親のMock
     * @param factory 工場
     * @param teban 手番
     * @return 盤面のMock
     */
    public BanmenNextMock getBaseText(BanmenNextMock base, BanmenFactoryMock factory, int teban) {
        return getBaseText(base, factory, teban,false);
    }
    /**
     * mockを取得する
     * @param base 親のMock
     * @param factory 工場
     * @param teban 手番
     * @param isKingWin 入玉詰みならtrue
     * @return 盤面のMock
     */
    public BanmenNextMock getBaseText(BanmenNextMock base, BanmenFactoryMock factory, int teban, boolean isKingWin) {
        return getBaseText(base, factory, teban,false, true);
    }
    
    /**
     * mock用のkey値
     */
    private int number = 0;

    /**
     * mockを取得する
     * @param base 親のMock
     * @param factory 工場
     * @param teban 手番
     * @param isKingWin 入玉詰みならtrue
     * @param enemyOute 相手に王手が掛かっているならtrue
     * @return 盤面のMock
     */
    public BanmenNextMock getBaseText(BanmenNextMock base, BanmenFactoryMock factory, int teban, boolean isKingWin, boolean enemyOute) {
        number++;
        String keyString = String.format(Locale.JAPANESE,"%064x", number);
        BanmenKey key = new BanmenKey(keyString);
        BanmenNextMock next = new BanmenNextMock(key,teban, isKingWin, enemyOute); // キーから復元する
        factory.createMock(next);
        if (base != null) {
            base.addMockChild(next);
        }
        return next;
    }
}
