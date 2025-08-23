package com.github.tand0.anshogio.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** コマの移動のテスト */
public class KomaMoveTest {
    /** コンストラクタ */
    public KomaMoveTest() {
        
    }
    /** 移動先が合法手である手を探す用。
     * サイズが18個あるか確認する
     */
    @Test
	public void moversTest() {
		List<KomaMove> movers = KomaMove.movers;
		
		assertEquals(movers.size(),18);
		
	}
}
