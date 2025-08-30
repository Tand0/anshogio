package com.github.tand0.anshogio.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.tand0.anshogio.eval.ANModel;
import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNext;
/** TensorFlowによる評価エンジン */
public class TensorEngineRunnable extends BaseEngineRunnable {

    /** TensorFlowアクセス用 */
	private final ANModel aNModel;

	/** コンストラクタ
	 * 
	 * @param aNModel  TensorFlowアクセス用
	 * @param factory  工場
	 * @param banmenList 盤面のリスト
	 */
	public TensorEngineRunnable(ANModel aNModel, BanmenFactory factory, LinkedList<BanmenKey> banmenList) {
        super(TensorEngineRunnable.class.getSimpleName(),factory,banmenList);
		this.aNModel = aNModel;
	}
	
	/** 実処理を進める */
	@Override
	public void run() {
	    //java.util.ConcurrentModificationException 対策で移し替えておく
	    BanmenNext next = this.getFactory().create(this.getBaseBanmenKey());
	    List<BanmenKey> teKeyList = new ArrayList<>(next.getChild());
        //
        // モデルから評価値を取得する
		for (BanmenKey teKey : teKeyList) {
			if (! this.aNModel.isAlive()) {
				break; // 死んでる
			}
			if (this.isEnd()) {
				break; // 停止が呼ばれている
			}
			if (this.getBanmenList().contains(teKey) && (teKey.getTeban() == 0)) {
				continue; // 千日手を回避するため、"先手は"同じ局面にはしない
			}
			BanmenNext childNext = this.getFactory().create(teKey);
			if (childNext.getEvel() != null) {
				continue; // すでに評価されている
			}
			BanmenKey key = childNext.getMyKey();
			Float result = this.aNModel.getKey(key.toString());
			if (result == null) {
				break;
			}
            //
            // 上限超えていたら補正する
            result = Math.max(-1, Math.min(1, result));
            //
            // 評価値を設定する
            childNext.setEval(false, result);
			//
		}
	}
}
