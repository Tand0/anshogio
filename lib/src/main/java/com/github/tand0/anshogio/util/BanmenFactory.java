package com.github.tand0.anshogio.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 盤面用ハッシュテーブルの管理を行う用クラス
 *
 * @author おれ
 *
 */
public class BanmenFactory {
    /** ログ */
    private static final Logger logger = LoggerFactory.getLogger(BanmenFactory.class);
    
	/** 盤面用ハッシュ */
	private final Map<BanmenKey,BanmenNext> hashSet = Collections.synchronizedMap(new HashMap<>());
	
	public BanmenFactory() {
	}

	/** 盤面を取得する
	 */
	public BanmenNext get(BanmenNext next) {
		return hashSet.get(next.getMyKey());
	}
	/** 盤面を取得する
	 */
	public BanmenNext get(BanmenKey key) {
		return hashSet.get(key);
	}
	
	/** 盤面を取得する
	 */
	public BanmenNext remove(BanmenKey key) {
		return hashSet.remove(key);
	}
		
	/** 盤面を生成する
	 * 
	 * @param owner 親の盤面
	 * @param bannmen 登録する盤面
	 * @return
	 */
	public synchronized BanmenNext create(BanmenNext owner,BanmenOnly bannmen) {
		bannmen = (bannmen != null) ? bannmen : new BanmenOnly(null,0);
		BanmenKey key = new BanmenKey(bannmen);
		BanmenNext next = hashSet.get(key);
		if (next != null) {
			return next;
		}
		next = new BanmenNextEval(key);
		hashSet.put(key, next);
		return next;
	}

	/** 全てのハッシュをクリアして初期状態に戻す
	 */
	public void clearAllHash() {
	    hashSet.values().stream().forEach(next -> next.clearAllHash());
		hashSet.clear();
	}

	/** サイズを返す
	 *
	 * @return ハッシュの大きさ
	 */
	public int size() {
		return hashSet.size();
	}
	
	/** メモリのチェック */
    public boolean checkMemory() {
        long total = Runtime.getRuntime().totalMemory();
        long max = Runtime.getRuntime().maxMemory();
        if ((max /10 *9) < total) {
            logger.error("Out of memory check! max=" + max + " total=" + total + " size=" + this.size());
            return true;
        }
        return false;
    }
}
