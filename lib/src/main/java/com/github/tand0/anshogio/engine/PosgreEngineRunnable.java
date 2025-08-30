package com.github.tand0.anshogio.engine;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.etc.ANPostgreO;
import com.github.tand0.anshogio.etc.ReslutWinLoss;
import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNext;
import com.github.tand0.anshogio.util.PnDn;

/** postgress に直近の手が登録されているか探しに行く */
public class PosgreEngineRunnable extends BaseEngineRunnable {
	/** ログ */
	private static final Logger logger = LoggerFactory.getLogger(PosgreEngineRunnable.class);
	
	/** postgresアクセス用 */
	private final ANPostgreO aNPostgreO;
	
	/** 現在の指し手 */
	private BanmenKey te = null;

	/** コンストラクタ
	 * 
	 * @param aNPostgreO postgresアクセス用
	 * @param factory 工場
	 * @param banmenList 盤面のリスト
	 */
	public PosgreEngineRunnable(ANPostgreO aNPostgreO, BanmenFactory factory, LinkedList<BanmenKey> banmenList) {
		super(PosgreEngineRunnable.class.getSimpleName(),factory,banmenList);
		this.aNPostgreO = aNPostgreO;
	}
	
	/** 実処理を進める */
	@Override
	public void run() {
		//
		int teban = this.getBaseBanmenKey().getTeban();
		float value = (teban == 0) ? Float.MIN_VALUE : Float.MAX_VALUE;
		int joseki = 0;
		//
		// postgres から評価値を取得する
		BanmenNext next = this.getFactory().create(this.getBaseBanmenKey());
		List<BanmenKey> childList = next.getChild();
		for (BanmenKey child : childList) {
			if (this.isEnd()) {
				break; // 停止が呼ばれている
			}
			if (! this.aNPostgreO.isAlive()) {
				break; // 切断されている
			}
			if (this.getBanmenList().contains(child)) {
				continue; // 千日手を回避するため、同じ局面にはしない
			}
			BanmenNext childNext = this.getFactory().create(child);
			PnDn[] pndn = childNext.getPnDn();
			if ((pndn != null) && (pndn[0] != null) && (pndn[1] != null)) {
	            if ((childNext.getPnDn()[0].pn == 0) || (childNext.getPnDn()[1].pn == 0)) {
	                // 詰みエンジンにより詰みが確定している
	                continue;
	            }
			}
			ReslutWinLoss winLoss;
			try {
				winLoss = aNPostgreO.readKey(child.toString());
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
            //
            // 値を設定する
			childNext.setEval(true,winLossValue);
            //
	        if (0 < winLoss.joseki ) { // 定跡なら
                if (joseki == 0) {
                    te = child;
                    joseki = winLoss.joseki;
                    childNext.setEval(true,winLossValue);
                } else {
                    if (winLoss.joseki < joseki) {
                        te = child;
                        // より先に現れた定跡を選ぶ
                        joseki = winLoss.joseki;
                    } else if (winLoss.joseki == joseki) {
                        if (Math.random() < 0.5) {
                            te = child;
                            // 同じ値ならランダムで選ぶ
                            joseki = winLoss.joseki;
                        }
                    }
                }
                continue;
            }
            if (0 < joseki ) {
                continue; // 一度定跡の手が現れたらこれ以上は評価しない
            }
			if (teban == 0) { // 先手の場合、一番高手を選ぶ
				if (value < winLossValue) {
					value = winLossValue;
					te = child;
				}
			} else {
				if (winLossValue < value) {
					value = winLossValue;
					te = child;
				}
			}
			//
		}
	}

	@Override
	public int getTe() {
	    if (this.te == null) {
	        return -2;
	    }
        return this.getBaseBanmenKey().createKeyToTe(te);
	}
}
