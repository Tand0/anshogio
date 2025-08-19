package com.github.tand0.anshogio.util;

import static com.github.tand0.anshogio.util.BanmenDefine.BEAT;
import static com.github.tand0.anshogio.util.BanmenDefine.B_MAX;
import static com.github.tand0.anshogio.util.BanmenDefine.ENEMY;
import static com.github.tand0.anshogio.util.BanmenDefine.NARI;
import static com.github.tand0.anshogio.util.BanmenDefine.changeTeToInt;
import static com.github.tand0.anshogio.util.BanmenDefine.checkLow;
import static com.github.tand0.anshogio.util.BanmenDefine.pNull;
import static com.github.tand0.anshogio.util.BanmenDefine.pP;
import static com.github.tand0.anshogio.util.BanmenDefine.pR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

import com.github.tand0.anshogio.util.BanmenDefine.CheckLow;

public abstract class BanmenNextBase implements BanmenNext {

	/** 自分のキー */
	private final BanmenKey key;

	
	/** 合法手のハッシュ */
	protected HashMap<Integer,BanmenNext> childFileld = null;
	
	/** コンストラクタ
	 * @param key キー
	 */
	protected BanmenNextBase(BanmenKey key) {
		this.key = key;
	}

	/** 自分のキーを渡す */
	@Override
	public BanmenKey getMyKey() {
		return this.key;
	}

	/**
	 * テーブルから削除する
	 * @param owner 消そうとしている親
	 */
	@Override
	public void createDown(BanmenFactory factory) {
		//
		// ここから削除処理
		//
		
		//
		// ファクトリーから先に削除する
		factory.remove(getMyKey());
		//
		HashMap<Integer,BanmenNext> childNow = this.childFileld;
		if (childNow != null) {
	        // 子供を念入りに殺しに行く
	        for (BanmenNext backup : childNow.values()) {
	            backup.createDown(factory); // 子孫も消す(オーナーは自分になる)
	        }
	        //
		}
		clearAllHash();
	}
	@Override
	public void clearAllHash() {
	    HashMap<Integer,BanmenNext> childNow = this.childFileld;
        if (childNow != null) {
            childNow.clear(); // テーブルを消す(ガベージコレクト対策)
        }
        this.childFileld = null; // テーブルの土台も消す(ループ対策で先に消す)
	}
	
	@Override
	public int compareTo(BanmenNext o) {
		if (o == null) return -1;
		return getMyKey().compareTo(o.getMyKey());
	}
	@Override
	public long hashCodeLong() {
		return getMyKey().hashCodeLong();
	}
	@Override
	public int hashCodeInt() {
		return getMyKey().hashCodeInt();
	}
	@Override
	public short hashCodeShort() {
		return getMyKey().hashCodeShort();
	}
	@Override
	public byte hashCodeByte() {
		return getMyKey().hashCodeByte();
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof BanmenNextEval) {
			BanmenNextEval target = (BanmenNextEval)o;
			return getMyKey().equals(target.getMyKey());
		}
		return false;
	}

	/** childが展開されていたらtrue */
	public boolean isExpandChild() {
		return this.childFileld != null;
	}
	
	/** 子づくりする */
	public synchronized HashMap<Integer, BanmenNext> getChildGetGouhou(BanmenFactory factory,BanmenOnly banmenOnly) {
		if (this.childFileld != null) {
			return this.childFileld;
		}
		//
		final HashMap<Integer,BanmenNext> child = new HashMap<Integer,BanmenNext>();
		//
		// 自分の王の位置を到底する
		int myOuX;
		int myOuY;
		int enemyOuX;
		int enemyOuY;
		if (banmenOnly.getTeban()==0) { // 先手
			myOuX = banmenOnly.getSenteOuX();
			myOuY = banmenOnly.getSenteOuY();
			enemyOuX = banmenOnly.getGoteOuX();
			enemyOuY = banmenOnly.getGoteOuY();
		} else {
			myOuX = banmenOnly.getGoteOuX();
			myOuY = banmenOnly.getGoteOuY();
			enemyOuX = banmenOnly.getSenteOuX();
			enemyOuY = banmenOnly.getSenteOuY();
		}
		//
        // 合法手の生成
		IntStream.range(0, B_MAX).forEach(x ->IntStream.range(0, B_MAX).forEach(
				y->getChildXY(factory,child, x,y,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly)));
		//
		// 値の設定
		this.childFileld = child;
		//
		return child;
	}
	/** 子づくりする（盤面上の移動元、および、王の位置は分かっている場合） */
	private void getChildXY(
			BanmenFactory factory,
			HashMap<Integer,BanmenNext> child,
			int x, int y,
			int myOuX,int myOuY,
			int enemyOuX,int enemyOuY,
			BanmenOnly banmenOnly) {
		final int teban = banmenOnly.getTeban();
        final byte koma = banmenOnly.getKoma(x, y);
		//
		if (koma == pNull) {
			// 打つ
		    getChildXYUchi(factory, child, x, y, myOuX, myOuY, enemyOuX, enemyOuY, teban, banmenOnly);
		} else if ((teban != 0)  != ((koma & ENEMY) != 0)) {
			return;  // 自分の駒でなければ終了
		} else {
		    // 移動する
		    getChildXYMove(factory, child, x, y, myOuX, myOuY, enemyOuX, enemyOuY, koma, teban, banmenOnly);;
		}
	}
	/** 打ち込み処理 */
	private void getChildXYUchi(
            BanmenFactory factory,
            HashMap<Integer,BanmenNext> child,
            int x, int y,
            int myOuX,int myOuY,
            int enemyOuX,int enemyOuY,
            int teban,
            BanmenOnly banmenOnly) {
        IntStream.rangeClosed(pP, pR)
        .filter(tegomaKey -> 0 < banmenOnly.getTegoma((byte)tegomaKey, teban))//手ゴマあり
        .filter(tegomaKey -> ! checkLow[0xF & tegomaKey].getUchiKinshi(teban, y)) // 置いてはいけないところに置いていないかチェック
        .forEach(tegomaKey -> {
            byte tegomaHave = (byte) (tegomaKey | (ENEMY * teban));
            if (tegomaKey == pP) {
                // ２歩チェック
                if (IntStream.range(0, B_MAX).anyMatch(yy-> tegomaHave == banmenOnly.getKoma(x,yy) )) {
                    return;
                }
            }
            setNext(factory, child, tegomaHave, BEAT,BEAT,x,y,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
        });
	}
	/** 移動処理 */
    private void getChildXYMove(
            BanmenFactory factory,
            HashMap<Integer,BanmenNext> child,
            int x, int y,
            int myOuX,int myOuY,
            int enemyOuX,int enemyOuY,
            byte koma,
            int teban,
            BanmenOnly banmenOnly) {
        //
        // チェック用クラス
        CheckLow low = checkLow[0xF & koma];
        //
        // 成りごまの値を用意しておく
        final byte naryKey = (byte) (koma | NARI);

        MoveKoma moverKoma = MoveKoma.moverKomaMap.get(koma & 0x1F);
        moverKoma.getXYFlag().stream().forEach(xYFlag -> {
            final int dx = xYFlag.getX();
            final int dy = xYFlag.getY(teban);
            int xx = x + dx;
            int yy = y + dy;
            for (; (0 <= xx) && (0 <= yy) && (xx < B_MAX) && (yy < B_MAX); xx += dx, yy += dy) {
                //
                if (((koma & NARI)!=0) || low.isNarenai()) {
                    // 既に成っている or 成れません
                    setNext(factory, child, koma, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                } else {
                    // 成ることのできる駒でまだ成っていない、または、成れない
                    //
                    if (low.getKyouseiNari(teban, y) || low.getKyouseiNari(teban, yy)) {
                        //NG 成ることが強制される位置
                        setNext(factory, child, naryKey, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                    } else if (low.isTekijin(teban,y) || low.isTekijin(teban, yy)) {
                        // 敵陣に入った or 敵陣から出た
                        // 成る
                        setNext(factory, child, naryKey, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                        // 成らずもできる
                        if (low.isNarazuOK()) {
                            setNext(factory, child, koma, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                        }
                    } else { //
                        // 成れません
                        setNext(factory, child, koma, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                    }
                }
                if ((!xYFlag.getFlag()) || (pNull != banmenOnly.getKoma(xx, yy))) {
                    break;//8方向チェックか、移動先に駒がいたらそこまで
                }
            }
        });
    }
	
	/** 移動する（移動元と移動先が決まっているパターン) */
	private void setNext(
	        BanmenFactory factory,
            HashMap<Integer,BanmenNext> child,
            byte koma,
			int oldX, int oldY,
			int newX, int newY,
			int myOuX,int myOuY,
			int enemyOuX,int enemyOuY,
			BanmenOnly banmenOnly) {
		if ((newX < 0) || (B_MAX <= newX) || (newY < 0) || (B_MAX <= newY)) {
			return; // はみ出る
		}
		//
		boolean tebanNow = banmenOnly.getTeban()!=0; //手番が反対になっている
		byte nextKey = banmenOnly.getKoma(newX,newY);
		boolean tebanKoma = (nextKey & ENEMY) == 0;
		if ((nextKey != pNull) && ((tebanKoma && !tebanNow) || (!tebanKoma && tebanNow))) {
			return; // 移動先が空でなくて、かつ、自身のコマなら移動できない
		}
		//
		int te = changeTeToInt(koma,oldX,oldY,newX, newY);
		//
		// 新しい盤面を作る
		BanmenOnly nextBanmen = new BanmenOnly(banmenOnly, te);
		//
		if ((koma & 0b1111) == BanmenDefine.pK) {
			// 移動する駒が王なら移動先が王の位置だ
			myOuX = newX;
			myOuY = newY;
		}
		//
		if (nextBanmen.checkSelfMate(banmenOnly.getTeban(),myOuX,myOuY)) {
			return;	// 空き王手ならばNGなので、Factoryに入れずに終了
		}
		//
		// 盤面を工場から取り出して使用フラグを１あげる
		BanmenNext result = factory.create(this, nextBanmen);
		//
		// 手を追加する
		child.put(te, result);
		//
		// 相手に王手をかけているかチェック
		if (nextBanmen.checkSelfMate(1- banmenOnly.getTeban(),enemyOuX,enemyOuY)) {
			// 相手に王手をかけているかチェック
			result.setEnemyOute();
		}
		// 打ち歩詰めチェック
		if ((oldX == BEAT) && ((koma & 0xF) == pP) && result.isEnemyOute()) {
			// もしも、打ちっていて、それが歩で、王手チェックが入っている場合
			//
			if (result.getChild(factory).size() == 0) {
				// 次の手がない場合
				//
				child.remove(te); // 手を削除する（すでに子供として登録しているので必要）
			    this.createDown(factory); // 検索テーブルから取り除く
				return; // 打ち歩詰めチェック
			}
		}
	}
    /** 王手なら true */
    private boolean oute = false;

    /** 王手ならばtrue
     */
    @Override
    public void setEnemyOute() {
        this.oute = true;
    }

    /** 敵に王手をしているならtrue
     */
    @Override
    public boolean isEnemyOute() {
        return this.oute;
    }

    /** 子供に手を追加する */
	@Override
	public BanmenNext decisionTe(BanmenFactory factory, int te) {
		return decisionTe(factory,te,true);
	}
	
    /** 手を決定する(ほかの手は消す) */
	@Override
	public BanmenNext addTe(BanmenFactory factory, int te) {
		return decisionTe(factory,te,false);
	}

	/**
	 * ツリーの手を追加または決定する
	 * @param te
	 * @param deleteOtherChild 追加時に兄弟がいたら全消し
	 */
	private BanmenNext decisionTe(BanmenFactory factory, int te,boolean deleteOtherChild) {
	    HashMap<Integer,BanmenNext> child = this.childFileld;
		if (child == null) {
		    child = new HashMap<Integer,BanmenNext>();
			this.childFileld = child;
		}
		// テーブルから消しながら取得
		BanmenNext next = child.remove(te);
		if (deleteOtherChild) {
			// 一子相伝ゆえにほかの子は……全消しします
            // 一回退避しないと ConcurrentModificationException が出る
		    ArrayList<BanmenNext> backupList = new ArrayList<>(child.values());
		    for (BanmenNext childChild : backupList) {
		        childChild.createDown(factory); // ownerは自分です
		    }
			child.clear();
		}
	    if (next == null) {
	        // 存在しない場合は作ります
	        BanmenOnly nextBanmen = new BanmenOnly(this.getMyKey().createBanmenOnly(), te);
	        next = factory.create(this, nextBanmen);
	    }
		// ほかの子がいなくなったところで、自身を追加
		child.put(te, next);

		return next;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (key != null) {
		    b.append("key:");
			b.append(key.toString());
		    b.append("\n");
	        b.append("banmen:\n");
	        b.append(key.createBanmenOnly().toString());
		}
		//
		HashMap<Integer,BanmenNext> child = this.childFileld;
		if (child != null) {
			b.append("te: ");
			for (Integer data : child.keySet()) {
				String teString = BanmenDefine.changeTeIntToString(data);
				b.append(teString);
				b.append(" ");
			}
			b.append("\n");
		}
		//
		return  b.toString();
	}

}
