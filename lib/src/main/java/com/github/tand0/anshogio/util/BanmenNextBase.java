package com.github.tand0.anshogio.util;

import static com.github.tand0.anshogio.util.BanmenDefine.BEAT;
import static com.github.tand0.anshogio.util.BanmenDefine.B_MAX;
import static com.github.tand0.anshogio.util.BanmenDefine.ENEMY;
import static com.github.tand0.anshogio.util.BanmenDefine.NARI;
import static com.github.tand0.anshogio.util.BanmenDefine.changeTeToInt;
import static com.github.tand0.anshogio.util.BanmenDefine.checkLow;
import static com.github.tand0.anshogio.util.BanmenDefine.pB;
import static com.github.tand0.anshogio.util.BanmenDefine.pNull;
import static com.github.tand0.anshogio.util.BanmenDefine.pP;
import static com.github.tand0.anshogio.util.BanmenDefine.pR;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import com.github.tand0.anshogio.util.BanmenDefine.CheckLow;

/**
 * 盤面情報の実態
 */
public abstract class BanmenNextBase implements BanmenNext {

	/** 自分のキー */
	private final BanmenKey myKey;

	
	/** 合法手のハッシュ */
	protected List<BanmenKey> childFileld = null;
	
	/** コンストラクタ
	 * @param myKey キー
	 */
	protected BanmenNextBase(BanmenKey myKey) {
		this.myKey = myKey;
	}

	/** 自分のキーを渡す */
	@Override
	public BanmenKey getMyKey() {
		return this.myKey;
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
	

	@Override
	public boolean isExpandChild() {
		return this.childFileld != null;
	}
	
    @Override
	public List<BanmenKey> getChild() {
        BanmenOnly banmenOnly = createBanmenOnly();
		if (this.childFileld != null) {
			return this.childFileld;
		}
		//
		final List<BanmenKey> child = new LinkedList<>();
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
				y->getChildXY(child, x,y,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly)));
		//
		// 値の設定
		this.childFileld = child;
		//
		return child;
	}

    
	/**
	 * 子づくりする（盤面上の移動元、および、王の位置は分かっている場合）
	 * @param child 子供
	 * @param x x位置
	 * @param y y位置
	 * @param myOuX 自分の王の位置x
	 * @param myOuY 自分の王の位置y
	 * @param enemyOuX 相手の王の位置x
	 * @param enemyOuY 相手の王の位置y
	 * @param banmenOnly 盤面
	 */
	private void getChildXY(
			List<BanmenKey> child,
			int x, int y,
			int myOuX,int myOuY,
			int enemyOuX,int enemyOuY,
			BanmenOnly banmenOnly) {
		final int teban = banmenOnly.getTeban();
        final byte koma = banmenOnly.getKoma(x, y);
		//
		if (koma == pNull) {
			// 打つ
		    getChildXYUchi(child, x, y, myOuX, myOuY, enemyOuX, enemyOuY, teban, banmenOnly);
		} else if ((teban != 0)  != ((koma & ENEMY) != 0)) {
			return;  // 自分のコマでなければ終了
		} else {
		    // 移動する
		    getChildXYMove(child, x, y, myOuX, myOuY, enemyOuX, enemyOuY, koma, teban, banmenOnly);;
		}
	}
	/** 打ち込み処理
     * @param child 子供
     * @param x x位置
     * @param y y位置
     * @param myOuX 自分の王の位置x
     * @param myOuY 自分の王の位置y
     * @param enemyOuX 相手の王の位置x
     * @param enemyOuY 相手の王の位置y
     * @param teban 手番
     * @param banmenOnly 盤面
	 */
	private void getChildXYUchi(
            List<BanmenKey> child,
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
            setNext(child, tegomaHave, BEAT,BEAT,x,y,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
        });
	}
	/** 移動処理
     * @param child 子供
     * @param x x位置
     * @param y y位置
     * @param myOuX 自分の王の位置x
     * @param myOuY 自分の王の位置y
     * @param enemyOuX 相手の王の位置x
     * @param enemyOuY 相手の王の位置y
     * @param koma コマ
     * @param teban 手番
     * @param banmenOnly 盤面
	 */
    private void getChildXYMove(
            List<BanmenKey> child,
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
                    setNext(child, koma, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                } else {
                    // 成ることのできるコマでまだ成っていない、または、成れない
                    //
                    if (low.getKyouseiNari(teban, y) || low.getKyouseiNari(teban, yy)) {
                        //NG 成ることが強制される位置
                        setNext(child, naryKey, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                    } else if (low.isTekijin(teban,y) || low.isTekijin(teban, yy)) {
                        // 敵陣に入った or 敵陣から出た
                        // 成る
                        setNext( child, naryKey, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                        // 成らずもできる
                        if (low.isNarazuOK()) {
                            setNext(child, koma, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                        }
                    } else { //
                        // 成れません
                        setNext( child, koma, x, y, xx, yy,myOuX,myOuY,enemyOuX,enemyOuY,banmenOnly);
                    }
                }
                if ((!xYFlag.getFlag())
                        || (pNull != banmenOnly.getKoma(xx, yy))) {
                    break;//8方向チェックか、移動先にコマがいたらそこまで
                }
            }
        });
    }
	
	/**
	 * 移動する（移動元と移動先が決まっているパターン)
     * @param child 子供
     * @param oldX 移動元のx位置
     * @param oldY 移動元のy位置
     * @param newX 移動先のx位置
     * @param newY 移動先のy位置
     * @param myOuX 自分の王の位置x
     * @param myOuY 自分の王の位置y
     * @param enemyOuX 相手の王の位置x
     * @param enemyOuY 相手の王の位置y
     * @param koma コマ
     * @param banmenOnly 盤面
	 */
	private void setNext(
	        List<BanmenKey> child,
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
		boolean tebanNow = banmenOnly.getTeban() != 0; //手番が反対になっている
		byte targetKoma = banmenOnly.getKoma(newX,newY);
		boolean targetTeban = (targetKoma & ENEMY) == 0;
		if ((targetKoma != pNull) &&
		        ((targetTeban != tebanNow))) {
			return; // 移動先が空でなくて、かつ、自身のコマならば移動できない
		}
		if ((targetKoma & 0b1111) == BanmenDefine.pK) {
		    return; // 王は取ってはいけない
		}
		//
		int te = changeTeToInt(koma,oldX,oldY,newX, newY);
		//
		// 新しい盤面を作る
		BanmenOnly newOnly = new BanmenOnly(banmenOnly, te);
		//
		if ((koma & 0b1111) == BanmenDefine.pK) {
			// 移動するコマが王なら移動先が王の位置だ
			myOuX = newX;
			myOuY = newY;
		}
		//
		if (newOnly.checkSelfMate(banmenOnly.getTeban(),myOuX,myOuY)) {
			return;	// 空き王手ならばNGなので、Factoryに入れずに終了
		}
		//
		// 盤面を工場から取り出して使用フラグを１あげる
		BanmenKey key = newOnly.createBanmenKey();
		//
		// factoryには入れずresultを作る
        BanmenNext result = new BanmenNextEval(key);

		// 打ち歩詰めチェック
		if ((oldX == BEAT) && ((koma & 0xF) == pP) && key.isMyOute()) {
			// もしも、打ちっていて、それが歩で、王手チェックが入っている場合
			//
			if (result.getChild().size() == 0) {
				// 次の手がない場合
				return; // 打ち歩詰めチェック
			}
		}
        //
        // 手を追加する
        child.addLast(key);
        //
	}
    
    /** 入玉勝ちならtrue, 入玉勝ちでなければfalse, 未チェックならnu;; */
    public Boolean kingWin = null;
    
    /**
     * 入玉勝ちチェック
     * @return 入玉勝ちの場合 true
     */
    @Override
    public boolean isKingWin() {
        if (this.kingWin != null) {
            return this.kingWin;
        }
        return isKingWin(this.createBanmenOnly());
    }
    
    /**
     * 入玉勝ちチェック
     * @param banmen 盤面
     * @return 入玉勝ちの場合 true
     */
    public boolean isKingWin(BanmenOnly banmen) {
        if (kingWin == null) {
            // kingWin チェックをしていないのでこれを実行する
            int myOuX;
            int myOuY;
            if (this.getMyKey().getTeban()==0) { // 先手
                myOuX = banmen.getSenteOuX();
                myOuY = banmen.getSenteOuY();
            } else {
                myOuX = banmen.getGoteOuX();
                myOuY = banmen.getGoteOuY();
            }
            kingWin = isKingWin(myOuX,myOuY, banmen);
        }
        return kingWin;
    }

    /**
     * 入玉勝ちチェック
     * @param myOuX 自分の王x
     * @param myOuY 自分の王y
     * @param banmenOnly 盤面
     * @return 入玉勝ちならtrue
     */
    protected boolean isKingWin(int myOuX,int myOuY,BanmenOnly banmenOnly) {
        int teban = this.getMyKey().getTeban(); // 先手-1,後手1に変換
        int range0;
        if ((teban == 0) && (myOuY <= 2)) {
            range0 = 0;
        } else if ((teban != 0) && (6 <= myOuY)) {
            range0 = 6;
        } else {
            return false; // 入玉していない
        }
        if (banmenOnly.checkSelfMate(teban,myOuX,myOuY)) {
            return false;// 大手を掛けられていない
        }
        
        //
        // 盤面上の敵陣のコマを数える
        int sum = 0;
        int value = 0;
        for (int x = 0 ; x < BanmenDefine.B_MAX ; x++) {
            for (int y = range0 ; y <= (range0 +2) ; y++) {
                byte koma = banmenOnly.getKoma(x, y);
                if (koma == BanmenDefine.pNull) {
                    continue;
                }
                if (((koma & BanmenDefine.ENEMY)/BanmenDefine.ENEMY) == teban) {
                    if ((koma & 0b111) != BanmenDefine.pK) {
                        sum++;
                        if (((koma & 0b111) == BanmenDefine.pB)
                                && ((koma & 0b111) == BanmenDefine.pR)){
                            value = value + 5; // 大コマは５点
                        } else {
                            value = value + 1; // 小コマは１点
                        }
                    }
                }
            }
        }
        if (sum < 10) {
            return false; // 王を除く自コマが10枚より少ない
        }
        
        // 手持ちの小コマの数を数える
        for (byte i = 0 ; i < BanmenDefine.pB ; i++) {
            value = value + banmenOnly.getTegoma(i, teban);
        }
        // 手持ちの大コマの数を数える
        for (byte i = pB ; i <= BanmenDefine.pR ; i++) {
            value = value + banmenOnly.getTegoma(i, teban) * 5;
        }
        // 先手は28枚、後手は27枚以上ある
        kingWin = (27 + (1-teban)) <= value;
        return kingWin;
    }
    

    /**
     * keyから盤面を作る
     * @return 盤面
     */
    public BanmenOnly createBanmenOnly() {
        return this.getMyKey().createBanmenOnly();
    }

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (myKey != null) {
		    b.append("key:");
			b.append(myKey.toString());
		    b.append("\n");
	        b.append("banmen:\n");
	        b.append(myKey.createBanmenOnly().toString());
		}
		//
		List<BanmenKey> child = this.childFileld;
		if (child != null) {
            b.append("te=");
			for (BanmenKey data : child) {
			    int te = this.getMyKey().createKeyToTe(data);
			    String teString = BanmenDefine.changeTeIntToString(te);
				b.append(teString);
                b.append(" ");
			}
			b.append("\n");
		}
		//
		return  b.toString();
	}

}
