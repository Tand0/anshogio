package com.github.tand0.anshogio.util;

import static com.github.tand0.anshogio.util.BanmenDefine.changeTeStringToInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 盤面のテスト
 */
public class BanmenOnlyTest {
    /** ログ */
    private final static Logger logger = LoggerFactory.getLogger(BanmenOnlyTest.class);
    
    /** コンストラクタ */
    public BanmenOnlyTest() {
    }
    
    /** 基本盤面の確認
     * ｘ軸が反転している場合は、飛車角が反転するので検知できる
     */
    @Test
    public void baseTest() {
        String sfen = "sfen lnsgkgsnl/1r5b1/ppppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b -";
        BanmenOnly sfenOnly = BanmenOnly.createSfen(sfen); // sfenで初期盤面を再現
        BanmenOnly baseOnly = new BanmenOnly(); // 初期盤面
        assertEquals(sfenOnly,baseOnly); // 初期盤面が同じこと！
    }
    /** 持ちコマのチェック */
    @Test
    public void baseMochiTest() {
        String sfen = "sfen lnsgkgsnl/1r5b1/1pppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL b P 1048";
        BanmenOnly sfenOnly = BanmenOnly.createSfen(sfen);
        assertEquals(sfenOnly.getTeban(),0); // 先手番
        assertEquals(sfenOnly.getTegoma(BanmenDefine.pP, 0), 1); // 先手歩１枚
        //
        sfen = "sfen 1nsgkgsnl/1r5b1/2ppppppp/9/9/9/PPPPPPPPP/1B5R1/LNSGKGSNL w 2PL";
        assertEquals(sfenOnly.getTeban(),0); // 後手番
        sfenOnly = BanmenOnly.createSfen(sfen);
        assertEquals(sfenOnly.getTegoma(BanmenDefine.pP, 0), 2); // 先手歩2枚
        assertEquals(sfenOnly.getTegoma(BanmenDefine.pL, 0), 1); // 桂馬１枚
        //
    }
    
    /**エラーが出たパターン
     */
    @Test
    public void baseMyTest() {
        String sfen = "sfen lr3g1nl/3sg1kb1/p3sp1p1/2p1p1p1p/3S5/PBP2PP1P/1P2P2P1/L+p1RG1SK1/5G1NL b N2Pn 45";
        BanmenOnly sfenOnly = BanmenOnly.createSfen(sfen); // sfenで初期盤面を再現
        BanmenKey key = sfenOnly.createBanmenKey(); // キー値が得られるか？
        logger.debug(key.toString());
        assertEquals(sfenOnly.getTeban(),0); 
        assertEquals(sfenOnly.getTegoma(BanmenDefine.pN, 0), 1);
        assertEquals(sfenOnly.getTegoma(BanmenDefine.pP, 0), 2);
    }
    
    /** 移動のテスト。飛車先の歩を前進させて２枚の歩を取る
      */
    @Test
    public void baseMove() {
        BanmenOnly only = new BanmenOnly();
        int csa = changeTeStringToInt("+2726FU");
        int usi = only.changeUsiTeToInt("2g2f");
        assertEquals(csa,usi);
        only = new BanmenOnly(only,csa);
        //
        csa =  changeTeStringToInt("-4132KI");
        usi = only.changeUsiTeToInt("4a3b"); // 1a 2b 3c 4d 5e 6f 7g 8h 9i
        assertEquals(csa,usi);
        only = new BanmenOnly(only,csa);
        //
        csa =  changeTeStringToInt("+2625FU");
        usi = only.changeUsiTeToInt("2f2e"); // 1a 2b 3c 4d 5e 6f 7g 8h 9i
        assertEquals(csa,usi);
        only = new BanmenOnly(only,csa);
        //
        csa =  changeTeStringToInt("-3242KI");
        usi = only.changeUsiTeToInt("3b4b"); // 1a 2b 3c 4d 5e 6f 7g 8h 9i
        assertEquals(csa,usi);
        only = new BanmenOnly(only,csa);
        //
        csa =  changeTeStringToInt("+2524FU");
        usi = only.changeUsiTeToInt("2e2d"); // 1a 2b 3c 4d 5e 6f 7g 8h 9i
        assertEquals(csa,usi);
        only = new BanmenOnly(only,csa);
        //
        csa =  changeTeStringToInt("-4232KI");
        usi = only.changeUsiTeToInt("4b3b"); // 1a 2b 3c 4d 5e 6f 7g 8h 9i
        assertEquals(csa,usi);
        only = new BanmenOnly(only,csa);
        //
        csa =  changeTeStringToInt("+2423TO");
        usi = only.changeUsiTeToInt("2d2c+"); // 1a 2b 3c 4d 5e 6f 7g 8h 9i
        assertEquals(csa,usi);
        only = new BanmenOnly(only,csa);
        //
        csa =  changeTeStringToInt("-3242KI");
        usi = only.changeUsiTeToInt("3b4b"); // 1a 2b 3c 4d 5e 6f 7g 8h 9i
        assertEquals(csa,usi);
        only = new BanmenOnly(only,csa);
        //
        csa =  changeTeStringToInt("+2333TO");
        usi = only.changeUsiTeToInt("2c3c"); // 1a 2b 3c 4d 5e 6f 7g 8h 9i
        assertEquals(csa,usi);
        only = new BanmenOnly(only,csa);
        //
        // 先手に歩が２枚持ちコマに入っていたらOK
        assertEquals(only.getTegoma(BanmenDefine.pP,0),2);
        //
    }
}
