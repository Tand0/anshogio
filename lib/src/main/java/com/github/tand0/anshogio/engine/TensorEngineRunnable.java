package com.github.tand0.anshogio.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.github.tand0.anshogio.eval.ANModel;
import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNext;

/** postgress に直近の手が登録されているか探しに行く */
public class TensorEngineRunnable extends EngineRunnable {

	private final ANModel aNModel;
	private final LinkedList<BanmenNext> banmenList;
	private final BanmenFactory factory;
	/** コンストラクタ */
	public TensorEngineRunnable(ANModel aNModel, BanmenFactory factory, LinkedList<BanmenNext> banmenList) {
		super(TensorEngineRunnable.class.getSimpleName());
		this.aNModel = aNModel;
		this.banmenList = banmenList;
		this.factory = factory;
	}
	
	/** 実処理を進める */
	@Override
	public void run() {
	    //java.util.ConcurrentModificationException 対策で移し替えておく
	    HashMap<Integer, BanmenNext> hashMap = new HashMap<>(banmenList.getLast().getChild(factory));
        //
        // モデルから評価値を取得する
		for (Map.Entry<Integer, BanmenNext> set : hashMap.entrySet()) {
			if (! this.aNModel.isAlive()) {
				break; // 死んでる
			}
			if (this.isEnd()) {
				break; // 停止が呼ばれている
			}
			if (banmenList.contains(set.getValue())) {
				continue; // 千日手を回避するため、同じ局面にはしない
			}
			if (set.getValue().getEvel() != null) {
				continue; // すでに評価されている
			}
			BanmenKey key = set.getValue().getMyKey();
			Float result = this.aNModel.getKey(key.toString());
			if (result == null) {
				break;
			}
			set.getValue().setEval(false, result);
			//
			// 上限超えていたら補正する
			result = Math.max(-1, Math.min(1, result));
			//
		}
	}

	@Override
	public int getTe() {
		return banmenList.getLast().getEvelTe(factory,banmenList);
	}
}
