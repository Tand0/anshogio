package com.github.tand0.anshogio.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNextMock;
import com.github.tand0.anshogio.util.BanmenNext;


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
        HashSet<BanmenNext> route = new HashSet<>();
        BanmenFactory factory = new BanmenFactory();
        //
        // 位置をチェックする
        BanmenNextMock next = getBaseText(teban,1);
        //
        // pndnを１回動かす(合法手を展開する)
        next.executePnDn(factory, teban, 100, route);
        //
        // 後手側のPnが0になっている
        assertEquals(next.getPnDn(1- teban).pn,0);
    }
    /**
     * 書籍No2のテスト
     */
    @Test
    public void no2Test() {
        int teban = 1; // 先手が攻める0、後手が攻める1
        HashSet<BanmenNext> route = new HashSet<>();
        BanmenFactory factory = new BanmenFactory();
        //
        // 位置をチェックする
        BanmenNextMock next = getBaseText(teban,1);
        //
        // pndnを１回動かす(合法手を展開する)
        next.executePnDn(factory, teban, 100, route);
        //
        // Pnが0になっている
        assertEquals(next.getPnDn(1- teban).pn,0);
    }
    /**
     * 書籍No2-2のテスト
     */
    @Test
    public void no2no2Test() {
        int teban = 0; // 先手が攻める0、後手が攻める1
        LinkedList<BanmenNext> banmenList = new LinkedList<>();
        BanmenFactory factory = new BanmenFactory();
        //
        // 位置をチェックする
        BanmenNextMock next = getBaseText(teban,1);
        banmenList.add(next);
        BanmenNextMock child = getBaseText(1- teban,2);
        next.addMockChild(child);
        //
        PnDnEngineRunnable run = new PnDnEngineRunnable(factory,banmenList);
        run.start(); // 実行
        run.join(); // 終わるまで待つ
        //
        // Pnが0になっている
        assertEquals(next.getPnDn(teban).pn,0);
    }
    /**
     * 書籍No2-3のテスト
     */
    @Test
    public void no2no3Test() {
        int teban = 1; // 先手が攻める0、後手が攻める1
        LinkedList<BanmenNext> banmenList = new LinkedList<>();
        BanmenFactory factory = new BanmenFactory();
        //
        // 位置をチェックする
        BanmenNextMock a = getBaseText(1 - teban,1);
        banmenList.add(a);
        BanmenNextMock b = getBaseText(teban,2);
        a.addMockChild(b);
        BanmenNextMock c = getBaseText(1- teban,3);
        b.addMockChild(c);
        //
        PnDnEngineRunnable run = new PnDnEngineRunnable(factory,banmenList);
        run.start(); // 実行
        run.join(); // 終わるまで待つ
        //
        // Pnが0になっている
        assertEquals(a.getPnDn(teban).pn,0);
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
        HashSet<BanmenNext> route = new HashSet<>();
        BanmenFactory factory = new BanmenFactory();
        //
        // 位置をチェックする
        BanmenNextMock next = getBaseText(teban,1,true);
        //
        // pndnを１回動かす(合法手を展開する)
        next.executePnDn(factory, teban, 100, route);
        //
        // Pnが0になっている
        assertEquals(next.getPnDn(1 - teban).pn,0);
    }

    /**
     * 書籍No5以降のテスト
     */
    @Test
    public void no5OverTest() {
        int teban = 0; // 先手が攻める0、後手が攻める1
        LinkedList<BanmenNext> banmenList = new LinkedList<>();
        BanmenFactory factory = new BanmenFactory();
        //
        // 位置をチェックする
        BanmenNextMock a1 = getBaseText(teban,1, false , false);
        banmenList.add(a1);
        //
        BanmenNextMock b1 = getBaseText(1- teban,2, false , true);
        BanmenNextMock b2 = getBaseText(1- teban,3, false , true);
        BanmenNextMock b3 = getBaseText(1- teban,4, false , false);
        a1.addMockChild(b1);
        a1.addMockChild(b2);
        a1.addMockChild(b3);
        //
        BanmenNextMock c1 = getBaseText(1- teban,5, false , true);
        BanmenNextMock c2 = getBaseText(1- teban,6, false , true);
        BanmenNextMock c3 = getBaseText(1- teban,7, true , false);
        b1.addMockChild(c1);
        b2.addMockChild(c2);
        b2.addMockChild(c3);
        //
        BanmenNextMock d1 = getBaseText(1- teban,8, false , false);
        BanmenNextMock d2 = getBaseText(1- teban,9, false , false);
        BanmenNextMock d3 = getBaseText(1- teban,10, false , false);
        c1.addMockChild(d1);
        c3.addMockChild(d2);
        c3.addMockChild(d3);
        //
        PnDnEngineRunnable run = new PnDnEngineRunnable(factory,banmenList);
        run.start(); // 実行
        run.join(); // 終わるまで待つ
        //
        // Pnが0になっている
        assertEquals(a1.getPnDn(teban).pn,0);
    }
    
    /**
     * mockを取得する
     * @param teban 手番
     * @param number 評価用の番号
     * @return 盤面のMock
     */
    public BanmenNextMock getBaseText(int teban, int number) {
        return getBaseText(teban,number,false);
    }
    /**
     * mockを取得する
     * @param teban 手番
     * @param number 評価用の番号
     * @param isKingWin 入玉詰みならtrue
     * @return 盤面のMock
     */
    public BanmenNextMock getBaseText(int teban, int number, boolean isKingWin) {
        return getBaseText(teban,number,false, true);
    }
        
    /**
     * mockを取得する
     * @param teban 手番
     * @param number 評価用の番号
     * @param isKingWin 入玉詰みならtrue
     * @param enemyOute 相手に王手が掛かっているならtrue
     * @return 盤面のMock
     */
    public BanmenNextMock getBaseText(int teban, int number, boolean isKingWin, boolean enemyOute) {
        String keyString = String.format(Locale.JAPANESE,"%064x", number);
        BanmenKey key = new BanmenKey(keyString);
        BanmenNextMock next = new BanmenNextMock(key,teban, isKingWin, enemyOute); // キーから復元する
        return next;
    }
}
