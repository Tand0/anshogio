package com.github.tand0.anshogio.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.tand0.anshogio.eval.ANModel;
import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNext;
import com.github.tand0.anshogio.util.ChildTeNext;

/** TensorFlowによる評価エンジン */
public class TensorEngineRunnable extends EngineRunnable {

    /** TensorFlowアクセス用 */
	private final ANModel aNModel;
	
	/** 工場 */
	private final BanmenFactory factory;
	
    /** 盤面のリスト */
    private final LinkedList<BanmenNext> banmenList;

	/** コンストラクタ
	 * 
	 * @param aNModel  TensorFlowアクセス用
	 * @param factory  工場
	 * @param banmenList 盤面のリスト
	 */
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
	    List<ChildTeNext> hashMap = new ArrayList<ChildTeNext>(banmenList.getLast().getChild(factory));
        //
        // モデルから評価値を取得する
		for (ChildTeNext teNext : hashMap) {
			if (! this.aNModel.isAlive()) {
				break; // 死んでる
			}
			if (this.isEnd()) {
				break; // 停止が呼ばれている
			}
			if (banmenList.contains(teNext.getNext()) && (teNext.getNext().getMyKey().getTeban() == 0)) {
				continue; // 千日手を回避するため、"先手は"同じ局面にはしない
			}
			if (teNext.getNext().getEvel() != null) {
				continue; // すでに評価されている
			}
			BanmenKey key = teNext.getNext().getMyKey();
			Float result = this.aNModel.getKey(key.toString());
			if (result == null) {
				break;
			}
            //
            // 上限超えていたら補正する
            result = Math.max(-1, Math.min(1, result));
            //
            // 評価値を設定する
			teNext.getNext().setEval(false, result);
			//
		}
	}

	@Override
	public int getTe() {
		return banmenList.getLast().getEvelTe(factory,banmenList);
	}
}
