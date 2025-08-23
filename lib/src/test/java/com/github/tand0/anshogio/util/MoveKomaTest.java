package com.github.tand0.anshogio.util;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** コマ移動のテスト */
public class MoveKomaTest {
    /** ログ */
	private final static Logger logger = LoggerFactory.getLogger(MoveKomaTest.class);
	
    /** コンストラクタ */
    public MoveKomaTest() {
        
    }
	/** コマ移動のテスト */
	@Test
	public void moversTest() {
		Map<Integer,MoveKoma> moveKomaMap = MoveKoma.moverKomaMap;
		//
		assertEquals(moveKomaMap.size(),14);
		//
		MoveKoma moveKoma = moveKomaMap.get((int)BanmenDefine.pK); // 王
		//
		ArrayList<XYFlag> xYFlagList = moveKoma.getXYFlag();
		assertEquals(xYFlagList.size(),8);
		//
		for (XYFlag xYFlag : xYFlagList) {
			logger.debug("ou0 x=" + xYFlag.getX() + " y=" + xYFlag.getY(0) + " f=" + xYFlag.getFlag());
			logger.debug("ou1 x=" + xYFlag.getX() + " y=" + xYFlag.getY(1) + " f=" + xYFlag.getFlag());
		}
	}
}
