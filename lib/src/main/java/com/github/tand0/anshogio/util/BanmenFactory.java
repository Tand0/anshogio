package com.github.tand0.anshogio.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
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
	
	/** コンストラクタ */
	public BanmenFactory() {
	}

	/** 
	 * 盤面がfactoryに登録してあるか確認する
	 * @param next 検索対象の盤面情報
	 * @return 工場に登録さてている盤面情報
	 */
	public BanmenNext get(BanmenNext next) {
		return hashSet.get(next.getMyKey());
	}
	/** 
	 * 盤面を取得する
	 * @param key key値
	 * @return 工場に登録さてている盤面情報
	 */
	public BanmenNext get(BanmenKey key) {
		return hashSet.get(key);
	}
	
	/**
	 * 工場から盤面を削除する
	 * @param key key値
	 * @return 工場に登録されていた盤面情報
	 */
	public BanmenNext remove(BanmenKey key) {
		return hashSet.remove(key);
	}
		
	/** 盤面を生成する
	 * 
	 * @param owner 親の盤面
	 * @param key 登録する盤面
	 * @return 盤面
	 */
	public synchronized BanmenNext create(BanmenNext owner,@NonNull BanmenKey key) {
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
	
	/** メモリのチェック
	 * 
	 * @return メモリがやばいときに true
	 */
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
