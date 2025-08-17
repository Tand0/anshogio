package com.github.tand0.anshogio.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 盤面工場のテスト */
class BanmenFactoryTest {
    /** ロガー */
	private final static Logger logger = LoggerFactory.getLogger(BanmenFactoryTest.class);

	/** 削除がちゃんと動くかテストする */
	@Test
	public void removeTest() {
	    logger.debug("start");
	    // 初期場外
	    BanmenFactory factory = new BanmenFactory();
	    BanmenNext banmenNext = factory.create(null, null);
        assertEquals(factory.size(),1); // 初期盤面が登録されている
        //
	    int size = banmenNext.getChild(factory).size();
	    assertEquals(size,30);
	    assertEquals(factory.size(),31); // 合法手が足されている
	    //
	    // 指す手を決定
	    String teString = "+7776FU";
	    int te = BanmenDefine.changeTeStringToInt(teString);
	    banmenNext.decisionTe(factory, te);
	    //
        assertEquals(factory.size(),2); // 手は２つになっている
	}
	
}