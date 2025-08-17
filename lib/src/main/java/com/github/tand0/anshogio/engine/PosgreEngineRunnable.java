package com.github.tand0.anshogio.engine;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.etc.ANPostgreO;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNext;

/** postgress に直近の手が登録されているか探しに行く */
public class PosgreEngineRunnable extends EngineRunnable {
	/** ログ */
	private static final Logger logger = LoggerFactory.getLogger(PosgreEngineRunnable.class);
	
	private final ANPostgreO aNPostgreO;
	private final LinkedList<BanmenNext> banmenList;
	private final HashMap<Integer,BanmenNext> child;
	private int te = -2;

	/** コンストラクタ */
	public PosgreEngineRunnable(ANPostgreO aNPostgreO, LinkedList<BanmenNext> banmenList, HashMap<Integer,BanmenNext> child) {
		super(PosgreEngineRunnable.class.getSimpleName());
		this.aNPostgreO = aNPostgreO;
		this.banmenList = banmenList;
        //java.util.ConcurrentModificationException 対策で移し替えておく
		this.child = new HashMap<>(child);
	}
	
	/** 実処理を進める */
	@Override
	public void run() {
		//
		int teban = banmenList.getLast().getBanmen().getTeban();
		float value = (teban == 0) ? Float.MIN_VALUE : Float.MAX_VALUE;
		//
		// postgres から評価値を取得する
		for (Map.Entry<Integer,BanmenNext> target : child.entrySet()) {
			if (this.isEnd()) {
				break; // 停止が呼ばれている
			}
			if (! this.aNPostgreO.isAlive()) {
				break; // 切断されている
			}
			if (banmenList.contains(target.getValue())) {
				continue; // 千日手を回避するため、同じ局面にはしない
			}
			if ((target.getValue().getPnDn(0).pn == 0) || (target.getValue().getPnDn(1).pn == 0)) {
				// 詰みエンジンにより詰みが確定している
				continue;
			}
			BanmenKey key = target.getValue().getMyKey();
			ANPostgreO.ReslutWinLoss winLoss;
			try {
				winLoss = aNPostgreO.readKey(key.toString());
			} catch (SQLException e) {
				logger.error(e.getMessage());
				aNPostgreO.close();
				break;
			}
			if (winLoss == null) {
				continue; // 局面が保存されていない
			}
			//
			float winLossValue;
			int sum = winLoss.win + winLoss.loss;
			if (sum == 0) {
				winLossValue = 0.0f; // 0除算回避
			} else { 
				// 評価値を -1.0～1.0の間で出す
				winLossValue = ((((float)winLoss.win) / sum) - 0.5f) * 2;
			}
			if (teban == 0) { // 先手の場合、一番高手を選ぶ
				if (value < winLossValue) {
					value = winLossValue;
					te = target.getKey();
				}
			} else {
				if (winLossValue < value) {
					value = winLossValue;
					te = target.getKey();
				}
			}
			//
			// 値を設定する
			target.getValue().setEval(true,winLossValue);
			//
		}
	}

	@Override
	public int getTe() {
		return this.te;
	}
}
