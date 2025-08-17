package com.github.tand0.anshogio.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class KomaMoveTest {
	@Test
	public void moversTest() {
		List<KomaMove> movers = KomaMove.movers;
		
		assertEquals(movers.size(),18);
		
	}
}
