package com.github.tand0.anshogio.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 盤面工場のテスト */
class BanmenFactoryTest {
    /** ロガー */
	private final static Logger logger = LoggerFactory.getLogger(BanmenFactoryTest.class);

	/** 盤面工場のコンストラクタ */
	public BanmenFactoryTest() {
	}

	/** 削除がちゃんと動くかテストする */
	@Test
	public void removeTest() {
	    logger.debug("start");
	    // 初期場外
	    BanmenFactory factory = new BanmenFactory();
	    BanmenKey key = (new BanmenOnly()).createBanmenKey();
	    BanmenNext banmenNext = factory.create(key);
        assertEquals(factory.size(),1); // 初期盤面が登録されている
        //
	    int size = banmenNext.getChild().size();
	    assertEquals(size,30);
        assertEquals(factory.size(),1); // この時点では作られない
	    for (BanmenKey child : banmenNext.getChild()) {
	        factory.create(child); // 子供を全部登録
	    }
        assertEquals(factory.size(),31); // この時点で作られる
	    //
	    // 指す手を決定
	    String teString = "+7776FU";
	    int te = BanmenDefine.changeTeStringToInt(teString);
        BanmenKey teKey = banmenNext.getMyKey().createTeToKey(te);
	    factory.decisionTe(banmenNext, teKey);
	    //
        assertEquals(factory.size(),2); // 手は２つになっている
	}
	
}