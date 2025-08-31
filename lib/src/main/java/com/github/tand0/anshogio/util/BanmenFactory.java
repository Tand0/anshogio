package com.github.tand0.anshogio.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
	 * 盤面を取得する
	 * @param key key値
	 * @return 工場に登録さてている盤面情報。Nullが返ることがある
	 */
	protected BanmenNext get(@NonNull BanmenKey key) {
		return hashSet.get(key);
	}
	
	/**
	 * 工場から盤面を削除する
	 * @param key key値
	 * @return 工場に登録されていた盤面情報
	 */
	private BanmenNext remove(@NonNull BanmenKey key) {
	    BanmenNext next = hashSet.remove(key);
	    if (next == null) {
	        return null;
	    }
        if (next.isExpandChild()) {
            for (BanmenKey teKey : next.getChild()) {
                remove(teKey);
            }
        }
		return next;
	}
    
    /**
     * 子供に手を追加する
     * @param base 指す前の盤面
     * @param key 指した手
     * @return 結果
     */
    public BanmenNext decisionTe(BanmenNext base, BanmenKey key) {
        return async(base,key,true);
    }
    
    /** 盤面を生成する
     * 
     * @param key 登録する盤面
     * @return 盤面
     */
    public BanmenNext create(BanmenKey key) {
        return async(null,key,false);
    }
	
	/** 手の決定ならtrue, 子供の生成ならfalse
     * 
     * @param base 指す前の盤面
     * @param key 登録する盤面
     * @param flag 手の決定ならtrue, 子供の生成ならfalse
     * @return 盤面
     */
    private @NonNull synchronized BanmenNext async(BanmenNext base, BanmenKey key, boolean flag) {
        if (flag) {
            return decisionTeAsync(base,key);
        }
        return createAsync(key);
        
    }
    /**
     * 子供に手を追加する
     * @param base 指す前の盤面
     * @param key 指した手
     * @return 結果
     */
    private BanmenNext decisionTeAsync(BanmenNext base, BanmenKey key) {
        if ((base == null) || (key == null)) { // 全消し
            hashSet.clear();
            return null;
        }
        List<BanmenKey> child =  base.getChild();
        if (child == null) {
            child = new LinkedList<>();
            child.add(key);
            return createAsync(key);
        }
        // 一子相伝ゆえにほかの子は……全消しします
        for (BanmenKey teKey : child) {
            if (teKey.equals(key)) {
                continue; // 自分は除く
            }
            remove(teKey);
        }
        child.clear(); // 全消し
        child.add(key); // ほかの子がいなくなったところで、自身を追加
        return createAsync(key);
    }
    
	/** 盤面を生成する
	 * 
	 * @param key 登録する盤面
	 * @return 盤面
	 */
    private @NonNull BanmenNext createAsync(@NonNull BanmenKey key) {
		BanmenNext next = this.get(key);
		if (next != null) {
			return next;
		}
		next = new BanmenNextEval(key);
		hashSet.put(key, next);
		return next;
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
