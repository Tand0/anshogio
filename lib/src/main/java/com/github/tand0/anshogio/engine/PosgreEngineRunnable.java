package com.github.tand0.anshogio.engine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.etc.ANPostgreO;
import com.github.tand0.anshogio.etc.ReslutWinLoss;
import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenNext;
import com.github.tand0.anshogio.util.ChildTeNext;

/** postgress に直近の手が登録されているか探しに行く */
public class PosgreEngineRunnable extends EngineRunnable {
	/** ログ */
	private static final Logger logger = LoggerFactory.getLogger(PosgreEngineRunnable.class);
	
	/** postgresアクセス用 */
	private final ANPostgreO aNPostgreO;
	
	/** 盤面のリスト(千日手防止用) */
	private final LinkedList<BanmenNext> banmenList;
	
	/** 子供 */
	private final List<ChildTeNext> child;
	
	/** 現在の指し手 */
	private int te = -2;

	/** コンストラクタ
	 * 
	 * @param aNPostgreO postgresアクセス用
	 * @param banmenList 盤面のリスト
	 * @param child 子供
	 */
	public PosgreEngineRunnable(ANPostgreO aNPostgreO, LinkedList<BanmenNext> banmenList, List<ChildTeNext> child) {
		super(PosgreEngineRunnable.class.getSimpleName());
		this.aNPostgreO = aNPostgreO;
		this.banmenList = banmenList;
        //java.util.ConcurrentModificationException 対策で移し替えておく
		this.child = new ArrayList<>(child);
	}
	
	/** 実処理を進める */
	@Override
	public void run() {
		//
		int teban = banmenList.getLast().getMyKey().getTeban();
		float value = (teban == 0) ? Float.MIN_VALUE : Float.MAX_VALUE;
		int joseki = 0;
		//
		// postgres から評価値を取得する
		for (ChildTeNext target : child) {
			if (this.isEnd()) {
				break; // 停止が呼ばれている
			}
			if (! this.aNPostgreO.isAlive()) {
				break; // 切断されている
			}
			if (banmenList.contains(target.getNext())) {
				continue; // 千日手を回避するため、同じ局面にはしない
			}
			if ((target.getNext().getPnDn(0).pn == 0) || (target.getNext().getPnDn(1).pn == 0)) {
				// 詰みエンジンにより詰みが確定している
				continue;
			}
			BanmenKey key = target.getNext().getMyKey();
			ReslutWinLoss winLoss;
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
            //
            // 値を設定する
            target.getNext().setEval(true,winLossValue);
            //
	        if (0 < winLoss.joseki ) { // 定跡なら
                if (joseki == 0) {
                    te = target.getTe();
                    joseki = winLoss.joseki;
                    target.getNext().setEval(true,winLossValue);
                } else {
                    if (winLoss.joseki < joseki) {
                        te = target.getTe();
                        // より先に現れた定跡を選ぶ
                        joseki = winLoss.joseki;
                    } else if (winLoss.joseki == joseki) {
                        if (Math.random() < 0.5) {
                            te = target.getTe();
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
					te = target.getTe();
				}
			} else {
				if (winLossValue < value) {
					value = winLossValue;
					te = target.getTe();
				}
			}
			//
		}
	}

	@Override
	public int getTe() {
		return this.te;
	}
}
