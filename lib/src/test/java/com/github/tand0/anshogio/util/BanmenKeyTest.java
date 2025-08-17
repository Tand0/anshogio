package com.github.tand0.anshogio.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BanmenKeyTest {
	private final static Logger logger = LoggerFactory.getLogger(BanmenKeyTest.class);
	
	public BanmenFactory factory = new BanmenFactory();
	
	@BeforeEach
	void testBefore() {
		// とりあえずクリアしておく
		factory.clearAllHash();
	}
	@AfterEach
	void testAfter() {
		// とりあえずクリアしておく
		factory.clearAllHash();
	}
	
	/** encode test
	 * 
	 */
	@Test
	void pointTest() {
		BanmenOnly only = new BanmenOnly(null,0); // デフォルト設定
		String q = (new BanmenKey(only)).toString(); // 16進出力
		assertEquals(q.length(),64);
		//
		BanmenKey key = new BanmenKey(q); // もじから復元
		assertEquals(q,key.toString()); // 動作確認
		//
	}
	/**
	 * 64bitテスト
	 */
	@Test
	void bitTest() {
		BanmenKey key = new BanmenKey("0000000000000000000000000000000000000000000000000000000000000000");
		Pointer p = new Pointer();
		//
		clearKey(key,p);
		for (int i = 0 ; i < 256;i++) {
			key.setData(p, 1, 1);
		}
		//
		clearKey(key,p);
		for (int i = 0 ; i < 256-3;i+=3) {
			key.setData(p, 3, 0b111);
		}
	}
	/**
	 * 単純にキーから盤面読みだしてあっているかを確認する
	 */
	@Test
	void firstTest() {
		logger.debug("test start!");
		BanmenOnly oldBanmen = new BanmenOnly(null,0);
		BanmenKey key1 = new BanmenKey(oldBanmen);
		BanmenOnly newBanmen = key1.createBanmenOnly();
		BanmenKey key2 = new BanmenKey(newBanmen);
		//
		logger.debug(oldBanmen.toString());
		logger.debug(newBanmen.toString());
		logger.debug(key1.toString());
		logger.debug(key2.toString());
		logger.debug("test old!");
		logger.debug(oldBanmen.toString16());
		logger.debug("test new!");
		logger.debug(newBanmen.toString16());
		//
		assertEquals(oldBanmen, newBanmen);
		assertEquals(key1, key2);
	}
	
	/** set のチェック */
	@Test
	void setDataTest() {
		BanmenOnly oldBanmen = new BanmenOnly(null,0);
		BanmenKey key = new BanmenKey(oldBanmen);
		Pointer p = new Pointer();
		//
		clearKey(key,p);
		assertEquals(
				key.toString(),
				"0000000000000000" +
				"0000000000000000" +
				"0000000000000000" +
				"0000000000000000");
		//
		long data;
		data = 1;
		key.setData(p, 1, data);
		assertEquals(
				key.toString(),
				"8000000000000000" +
				"0000000000000000" +
				"0000000000000000" +
				"0000000000000000");
		//
		data = 0b10;
		key.setData(p, 2, data);
		assertEquals(
				key.toString(),
				"c000000000000000" +
				"0000000000000000" +
				"0000000000000000" +
				"0000000000000000");
		//
		clearKey(key,p);
		data = 0xc000000000000001L;
		key.setData(p, 64, data);
		assertEquals(
				key.toString(),
				"c000000000000001" +
				"0000000000000000" +
				"0000000000000000" +
				"0000000000000000");
		//
		data = 0x1L;
		key.setData(p, 1, data);
		assertEquals(
				key.toString(),
				"c000000000000001" +
				"8000000000000000" +
				"0000000000000000" +
				"0000000000000000");
		//
		data = 0x3L;
		key.setData(p, 2, data);
		assertEquals(
				key.toString(),
				"c000000000000001" +
				"e000000000000000" +
				"0000000000000000" +
				"0000000000000000");
		//
		clearKey(key,p);
		data = 0x4000000000000001L;
		key.setData(p, 63, data);
		assertEquals(
				key.toString(),
				"8000000000000002" +
				"0000000000000000" +
				"0000000000000000" +
				"0000000000000000");
		data = 0x7L;
		key.setData(p, 3, data);
		assertEquals(
				key.toString(),
				"8000000000000003" +
				"c000000000000000" +
				"0000000000000000" +
				"0000000000000000");
		//
		clearKey(key,p);
		data = 0x1L;
		key.setData(p, 63, data);
		assertEquals(
				key.toString(),
				"0000000000000002" +
				"0000000000000000" +
				"0000000000000000" +
				"0000000000000000");
	}

	/** set のチェック */
	@Test
	void getDataTest() {
		BanmenOnly oldBanmen = new BanmenOnly(null,0);
		BanmenKey key = new BanmenKey(oldBanmen);
		Pointer p = new Pointer();
		//
		clearKey(key,p);
		long data;
		long result;
		data = 0;
		result = key.getData(p, 64);
		assertEquals(data,result);
		//
		clearKey(key,p);
		data = 1;
		key.setData(p, 1, data);
		p.clear();
		result = key.getData(p, 1);
		assertEquals(data,result);
		//
		//
		clearKey(key,p);
		data = 0b10;
		key.setData(p, 2, data);
		p.clear();
		result = key.getData(p, 2);
		assertEquals(data,result);
		//
		clearKey(key,p);
		long data1 = 0xc000000000000001L;
		key.setData(p, 64, data1);
		logger.debug("p1:" + key.toString());
		long data2 = 0x1L;
		key.setData(p, 1, data2);
		logger.debug("p2:" + key.toString());
		long data3 = 0x3L;
		key.setData(p, 2, data3);
		logger.debug("p3:" + key.toString());
		p.clear();
		result = key.getData(p, 64);
		assertEquals(data1,result);
		result = key.getData(p, 1);
		assertEquals(data2,result);
		result = key.getData(p, 2);
		assertEquals(data3,result);
		//
		//
		clearKey(key,p);
		key.setData(p, 63, data1);
		data1 = 0x4000000000000001L;
		logger.debug("d1:" + key.toString());
		data2 = 0x7L;
		key.setData(p, 3, data2);
		logger.debug("d2:" + key.toString());
		p.clear();
		result = key.getData(p, 63);
		assertEquals(data1,result);
		result = key.getData(p, 3);
		assertEquals(data2,result);
		//
		
		long sum = 2480048881256L;
		clearKey(key,p);
		key.setData(p, 39+2+2+2+2, sum);
		p.clear();
		result = key.getData(p, 39+2+2+2+2); 
		assertEquals(sum,result);
	}

	/** 初期手のチェック */
	@Test
	void nextKeyTest() {
		BanmenFactory factory = new BanmenFactory();
		BanmenNext next = factory.create(null, null);
		HashMap<Integer,BanmenNext> map = next.getChild(factory);
		assertEquals(map.size(),30);
		int te = BanmenDefine.changeTeStringToInt("+3938GI");
		logger.debug(BanmenDefine.changeTeIntToString(te));
		BanmenNext target = map.get(te);
		if (target == null) {
			fail("target Null");
		}
		assertNotEquals(target.getMyKey(),0);
	}
	
	/** clear key */
	void clearKey(BanmenKey key,Pointer p) {
		key.key[0] = 0L;
		key.key[1] = 0L;
		key.key[2] = 0L;
		key.key[3] = 0L;
		p.clear();
	}

	/** 持ち駒のテスト */
	@Test
	void mochigomaTest() {
		//
		int teban = 0;
		BanmenOnly only = new BanmenOnly(null,0);
		BanmenKey key = new BanmenKey(only);
		mochiGomaNext(key);
		for (int i = 0; i < BanmenDefine.B_MAX; i++) {
			for (int j = 0; j < BanmenDefine.B_MAX; j++) {
				byte koma = (byte)(0xF & only.getKoma(i, j));
				if ((koma == BanmenDefine.pK) || (koma == BanmenDefine.pNull)) {
					continue;
				}
				only.setKoma(BanmenDefine.pNull, i, j);
				only.setTegoma(koma,  teban, only.getTegoma(koma, teban) + 1);
				//
				key = new BanmenKey(only);
				mochiGomaNext(key);
				//
			}
		}
		key = new BanmenKey(only);
		mochiGomaNext(key);
		//
		for (byte koma = BanmenDefine.pp ; koma < BanmenDefine.pR; koma++) {
			int sum = only.getTegoma(koma, teban);
			if (sum <= 0) {
				continue;
			}
			only.setTegoma(koma,    teban, only.getTegoma(koma, teban) - 1);
			only.setTegoma(koma,1 - teban, only.getTegoma(koma, teban) + 1);
			//
			mochiGomaNext(key);
		}
	}
	
	public void mochiGomaNext(BanmenKey key) {
		// 打ったあとにキー値を使って同じか確認する
		String q = key.toString(); // キーを文字にする
		BanmenKey banmenTeKey = new BanmenKey(q); // 文字からキーを作る
		BanmenOnly banmenTeOnly2 = banmenTeKey.createBanmenOnly(); // キーから盤面を作る

		// 同じキーのはずだから、同じキーになるはずだ
		assertEquals(q,banmenTeKey.toString());
		assertEquals(banmenTeOnly2.toString(),banmenTeOnly2.toString());		
	}
}