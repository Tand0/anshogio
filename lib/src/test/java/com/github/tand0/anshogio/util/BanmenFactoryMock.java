package com.github.tand0.anshogio.util;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * 工場のmock
 */
public class BanmenFactoryMock extends BanmenFactory {
    /** 盤面用ハッシュ */
    private final Map<BanmenKey,BanmenNext> mockMap = new HashMap<>();
    
    /** 
     * 盤面を取得する
     * @param key key値
     * @return 工場に登録さてている盤面情報。mock側でnullを返すならバグです
     */
    @Override
    protected BanmenNext get(@NonNull BanmenKey key) {
        BanmenNext next = mockMap.get(key);
        if (next == null) {
            // 登録されていないモノには例外を出す
            throw new java.lang.UnsupportedOperationException();
        }
        return next;
    }
    
    /**
     * mockの登録
     * @param mock mock
     */
    public void createMock(BanmenNextMock mock) {
        mockMap.put(mock.getMyKey(), mock);
    }
    
}
