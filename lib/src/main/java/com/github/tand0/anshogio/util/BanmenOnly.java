package com.github.tand0.anshogio.util;

import static com.github.tand0.anshogio.util.BanmenDefine.*;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 盤面情報検索
 * <pre>
 * +-------------------------------+-------------------------------+-------------------------------+-------------------------------+
 * +---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+
 * +-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |t| XYZ | ----P---- | --koma9-- | --koma8-- | --koma7-- | --koma6-- | --koma5-- | --koma4-- | --koma3-- | --koma2-- | --koma1-- |0
 * +-+-----+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
 * |sen ouX| ----p---- | --koma9-- | --koma8-- | --koma7-- | --koma6-- | --koma5-- | --koma4-- | --koma3-- | --koma2-- | --koma1-- |1
 * +-------+-----+-----+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
 * |sen ouY| -l- | -L- | --koma9-- | --koma8-- | --koma7-- | --koma6-- | --koma5-- | --koma4-- | --koma3-- | --koma2-- | --koma1-- |2
 * +-------+-----+-----+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
 * |goteouX| -n- | -N- | --koma9-- | --koma8-- | --koma7-- | --koma6-- | --koma5-- | --koma4-- | --koma3-- | --koma2-- | --koma1-- |3
 * +-------+-----+-----+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
 * |goteouY| -s- | -S- | --koma9-- | --koma8-- | --koma7-- | --koma6-- | --koma5-- | --koma4-- | --koma3-- | --koma2-- | --koma1-- |4
 * +-------+-----+-----+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
 * |------ | -g- | -G- | --koma9-- | --koma8-- | --koma7-- | --koma6-- | --koma5-- | --koma4-- | --koma3-- | --koma2-- | --koma1-- |5
 * |------ +-----+-----+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
 * |------ | -b- | -B- | --koma9-- | --koma8-- | --koma7-- | --koma6-- | --koma5-- | --koma4-- | --koma3-- | --koma2-- | --koma1-- |6
 * |------ +-----+-----+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
 * |------ | -r- | -R- | --koma9-- | --koma8-- | --koma7-- | --koma6-- | --koma5-- | --koma4-- | --koma3-- | --koma2-- | --koma1-- |7
 * |------ +-----+-----+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
 * |------ | -k- | -K- | --koma9-- | --koma8-- | --koma7-- | --koma6-- | --koma5-- | --koma4-- | --koma3-- | --koma2-- | --koma1-- |8
 * +------ +-----+-----+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
 *
 * komaX
 * +-+-+-------+
 * |e|n| koma1 |
 * +-+-+-------+
 *
 * te
 * +-------------------------------+-------------------------------+
 * +---------------+---------------+---------------+---------------+
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   | ---koma-- | ---newx-- | ---newy-- | ---oldx-- | ---oldy-- |
 * +---+-----------+-----------+-----------+-----------+-----------+
 * if oldY = 9 then from tegoma.
 *
 *
 * XYZ
 *   +-+-+-+
 *   |X Y Z|
 *   +-----+
 * X= sente ou is tumi
 * Y= gote  ou is tumi
 * Z= ???
 *
 * </pre>
 * @author おれ
 *
 */
public class BanmenOnly {

    /** 盤面情報 */
    private final long[] vanmen = new long[B_MAX];

    /** 次の手番
     */
    public BanmenOnly() {
        this(null,0);
    }
    /** 次の手番
     * @param old 一手前の盤面
     * @param te 指す手
     */
    public BanmenOnly(BanmenOnly old, int te) {
        if (old == null) {
            this.setTeban(0);// 次が先手なので後手にしておく
            //
            // 後手
            setKoma(pl, 0, 0);
            setKoma(pn,2 - 1, 0);
            setKoma(ps,3 - 1, 0);
            setKoma(pg,4 - 1, 0);
            setKoma(pk,5 - 1, 0);
            setKoma(pg,6 - 1, 0);
            setKoma(ps,7 - 1, 0);
            setKoma(pn,8 - 1, 0);
            setKoma(pl,9 - 1, 0);
            //
            setKoma(pb,2 - 1, 2 - 1);
            setKoma(pr,8 - 1, 2 - 1);
            //;
            setKoma(pp, 0, 3 - 1);
            setKoma(pp,2 - 1, 3 - 1);
            setKoma(pp,3 - 1, 3 - 1);
            setKoma(pp,4 - 1, 3 - 1);
            setKoma(pp,5 - 1, 3 - 1);
            setKoma(pp,6 - 1, 3 - 1);
            setKoma(pp,7 - 1, 3 - 1);
            setKoma(pp,8 - 1, 3 - 1);
            setKoma(pp,9 - 1, 3 - 1);
            //
            //
            // 先手
            setKoma(pL, 0, 9 - 1);
            setKoma(pN,2 - 1, 9 - 1);
            setKoma(pS,3 - 1, 9 - 1);
            setKoma(pG,4 - 1, 9 - 1);
            setKoma(pK,5 - 1, 9 - 1);
            setKoma(pG,6 - 1, 9 - 1);
            setKoma(pS,7 - 1, 9 - 1);
            setKoma(pN,8 - 1, 9 - 1);
            setKoma(pL,9 - 1, 9 - 1);
            //
            setKoma(pB,8 - 1, 8 - 1);
            setKoma(pR,2 - 1, 8 - 1);
            //
            setKoma(pP, 0, 7 - 1);
            setKoma(pP,2 - 1, 7 - 1);
            setKoma(pP,3 - 1, 7 - 1);
            setKoma(pP,4 - 1, 7 - 1);
            setKoma(pP,5 - 1, 7 - 1);
            setKoma(pP,6 - 1, 7 - 1);
            setKoma(pP,7 - 1, 7 - 1);
            setKoma(pP,8 - 1, 7 - 1);
            setKoma(pP,9 - 1, 7 - 1);
        } else {
            System.arraycopy(old.vanmen, 0, vanmen, 0, vanmen.length);
            int teban = old.getTeban();
            setTeban((teban == 0) ? 1 : 0);
            int oldY = (int) (te & 0x1FL);
            int oldX = (int) ((te >> 6) & 0x1FL);
            int newY = (int) ((te >> (6 * 2)) & 0x1FL);
            int newX = (int) ((te >> (6 * 3)) & 0x1FL);
            byte koma = (byte) ((te >> (6 * 4)) & 0x3FL);
            moveKoma(koma,oldX,oldY,newX,newY,teban);
        }
    }
    
    /** 同じ盤面か？(評価値と差し手は無視する
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject instanceof BanmenOnly) {
            BanmenOnly banmen = (BanmenOnly) anObject;
            boolean ans;
            ans =        (this.vanmen[0] == banmen.vanmen[0]);
            ans = ans && (this.vanmen[1] == banmen.vanmen[1]);
            ans = ans && (this.vanmen[2] == banmen.vanmen[2]);
            ans = ans && (this.vanmen[3] == banmen.vanmen[3]);
            ans = ans && (this.vanmen[4] == banmen.vanmen[4]);
            ans = ans && (this.vanmen[5] == banmen.vanmen[5]);
            ans = ans && (this.vanmen[6] == banmen.vanmen[6]);
            ans = ans && (this.vanmen[7] == banmen.vanmen[7]);
            ans = ans && (this.vanmen[8] == banmen.vanmen[8]);
            return ans;
        }
        return false;
    }
    /** クローンを取得する */
    @Override
    public BanmenOnly clone() {
        BanmenOnly banmenOnly = new BanmenOnly();
        System.arraycopy(this.vanmen, 0, banmenOnly.vanmen, 0, banmenOnly.vanmen.length);
        return banmenOnly;
    }
    /**
     * 手番を設定する
     *
     * @param teban 先手=0, 後手=1
     */
    public void setTeban(int teban) {
        vanmen[0] = vanmen[0] & (~0x8000000000000000L);
        vanmen[0] = vanmen[0] | ( 0x8000000000000000L * teban);
    }

    /**
     * 手番を取得する
     *
     * @return 先手=0, 後手=1
     */
    public int getTeban() {
        return (vanmen[0] & 0x8000000000000000L) == 0 ? 0 : 1;
    }

    /** x,yの位置にコマを移動する
     *
     * @param koma コマ
     * @param oldX 打つ前の位置、手ゴマから出す場合は BEAT
     * @param oldY 打つ前の位置、手ゴマから出す場合は BEAT
     * @param newX 打つ先の位置
     * @param newY 打つ先の位置
     * @param teban 手番
     */
    protected void moveKoma(byte koma, int oldX, int oldY, int newX, int newY,int teban) {
        if (oldX == BEAT) {
            // 打った
            int now = getTegoma(koma, teban) ;
            setTegoma(koma, teban, Math.max(0, now - 1));
            koma = (byte) (koma | (ENEMY * teban));
        } else {
            // 単なる移動だ
            setKoma(pNull,oldX, oldY);
            byte newKoma = (byte) (getKoma(newX, newY) & 0xF); // 成っていたらいたら戻す
            if (newKoma != pNull) {
                int now = getTegoma(newKoma, teban);
                setTegoma(newKoma, teban, now + 1);
            }
        }
        setKoma(koma,newX, newY);
    }

    /** x,yの位置にコマを配置する
     *
     * @param koma コマ
     * @param x 配置する先
     * @param y 配置する先
     */
    protected void setKoma(byte koma, int x, int y) {
        if (((koma & 0x30 ) != 0) && ((koma & 0xF)==0)) {
            return; // error!
        }
        int pos = 6 * x;
        vanmen[y] = (vanmen[y] & (~(0x3FL << pos))) | (((long) koma) << pos);
        //
        // 移動元が王ならば王のフラグを更新する
        if (koma == BanmenDefine.pK) { // 先手王
            vanmen[1] = 0x0FFFFFFFFFFFFFFFL & vanmen[1];
            vanmen[1] = (((long)x) << 60) | vanmen[1];
            vanmen[2] = 0x0FFFFFFFFFFFFFFFL & vanmen[2];
            vanmen[2] = (((long)y) << 60) | vanmen[2];
        } else if (koma == BanmenDefine.pk) { // 後手王
            vanmen[3] = 0x0FFFFFFFFFFFFFFFL & vanmen[3];
            vanmen[3] = (((long)x) << 60) | vanmen[3];
            vanmen[4] = 0x0FFFFFFFFFFFFFFFL & vanmen[4];
            vanmen[4] = (((long)y) << 60) | vanmen[4];
        }
    }
    /**
     *  先手王x の取得
     * @return 先手王x 
     */
    public int getSenteOuX() {
        return (int)(vanmen[1] >>> 60);
    }
    /**
     * 先手王y の取得
     * @return 先手王y
     */
    public int getSenteOuY() {
        return (int)(vanmen[2] >>> 60);
    }
    /**
     * 後手王xの取得
     * @return 後手王x
     */
    public int getGoteOuX() {
        return (int)(vanmen[3] >>> 60);
    }
    /**
     * 後手王yの取得
     * @return 後手王y
     */
    public int getGoteOuY() {
        return (int)(vanmen[4] >>> 60);
    }
    
    
    /** x,yの位置にあるコマを確認する
     *
     * @param x 配置元の位置
     * @param y 配置元の位置
     * @return コマ
     */
    public byte getKoma(int x, int y) {
        return (byte) ((vanmen[y] >> (6 * x)) & 0x3FL);
    }

    /** 手ゴマの数を取得する
     *
     * @param koma コマ
     * @param teban 手番
     * @return 手ゴマの数
     */
    public int getTegoma(byte koma, int teban) {
        koma = (byte) (koma & 0xF);
        long now = (koma == pP) ? ((vanmen[teban] >>> 54) & 0x3FL) : ((vanmen[koma] >>> (54 + (teban * 3))) & 0b111L);
        return (int)now;
    }

    /** 手ゴマの合計を取得する
     *
     * @param teban 手番
     * @return 手番の手ゴマの合計
     */
    protected int sumTegoma(final int teban) {
        return IntStream.rangeClosed(pP, pK).map(koma->getTegoma((byte)koma,teban)).sum();
    }

    /** 手ゴマをセットする
     *
     * @param koma コマ
     * @param teban 手番
     * @param now 手ゴマの数
     */
    protected void setTegoma(byte koma, int teban, int now) {
        koma = (byte) (koma & 0xF);
        if (koma == pP) {
            vanmen[teban] = (vanmen[teban] & (~(0x3FL << 54L))) | ((now&0x3FL) << 54L);
        } else {
            vanmen[koma  ] = (vanmen[koma  ] & (~(0x7L  << (54 + (teban * 3))))) | ((now&0x7L) << (54 + (teban * 3)));
        }
    }

    /** デバッグ用 */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        int teban = this.getTeban();
        buffer.append("teban=").append(teban).append(" oute=").append(this.isMyOute()).append("\n");
        for (int y = 0; y < vanmen.length; y++) {
            buffer.append('P');
            buffer.append((y+1));
            for (int x = vanmen.length - 1; 0 <= x; x--) {
                int koma = getKoma(x,y);
                if (koma == pNull) {
                    buffer.append(' ');
                } else if ((koma & ENEMY) == 0) {
                    buffer.append('+');
                } else {
                    buffer.append('-');
                }
                buffer.append(getKomaToString(koma));
            }
            //buffer.append(String.format("  0x%016x", vanmen[y]));
            buffer.append("\n");
        }
        for (int i = 0; i < 2; i++) {
            buffer.append((i == 0) ? "P+" : "P-");
            for (byte koma = pP; koma <= pK; koma++) {
                int num = getTegoma(koma, i);
                for (int j = 0 ; j < num ; j++) {
                    buffer.append("00");
                    buffer.append(getKomaToString(koma));
                }
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /**
     * key値を16進の文字列に変換する
     * @return 16進の文字列
     */
    public String toString16() {
        StringBuffer buffer = new StringBuffer();
        for (int y = 0 ; y < B_MAX ; y++) {
            buffer.append(String.format("%016x", vanmen[y]));
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /** 王手のチェック
     * @param teban 手番 先手なら0、後手なら1
     * @param x 自分の王の位置X
     * @param y 自分の王の位置Y
     * @return 空き王手ならtrue
     */
    public boolean checkSelfMate(int teban, int x, int y) {
        // ８方向チェック
        // 飛角香の利き対応
        return KomaMove.movers
                .stream()
                .anyMatch(mover->{
                    final int dx = mover.xYFlag.getX();
                    final int dy = mover.xYFlag.getY(teban);
                    int xx = x + dx;
                    int yy = y + dy;
                    for (; (0 <= xx) && (0 <= yy) && (xx < B_MAX) && (yy < B_MAX); xx += dx , yy += dy) {
                        //
                        byte targetKoma = this.getKoma(xx, yy); // 移動先のコマを取得
                        int nariKoma = 0x1F & targetKoma; // 成り情報付きのコマ
                        //
                        if ((targetKoma != pNull)
                                && ((teban == 0) != ((targetKoma & ENEMY) == 0))) {
                            //ターゲットのマスが空でなく、かつ、敵のコマなら本気だす
                            //
                            //
                            if (Arrays.stream(mover.koma).anyMatch(sKoma -> sKoma == nariKoma)) {
                                return true;
                            }

                        }                       
                        if ((!mover.xYFlag.getFlag()) || (pNull != targetKoma)) {
                            // 継続フラグが付いていないか、コマの位置が空なら終了
                            break;
                        }
                    }
                    return false;
                });
    }

    
    /** REST表示用データの取得
     * @return REST表示用データ
     */
    public JSONObject getDisplayStatus() {
        JSONObject d = new JSONObject();
        JSONArray arrayX = new JSONArray();
        for (int y = 0 ; y < BanmenDefine.B_MAX ; y++) {
            JSONArray arrayY = new JSONArray();
            for (int x= 0 ; x < BanmenDefine.B_MAX ; x++) {
                arrayY.put(this.getKoma(x, y));
            }
            arrayX.put(arrayY);
        }
        d.put("banmen", arrayX);
        JSONArray mochi;
        mochi = new JSONArray();
        for (byte i = pP ; i <= pK ; i++) {
            mochi.put(this.getTegoma(i, 0));
        }
        d.put("sente", mochi);
        //
        mochi = new JSONArray();
        for (byte i = pP ; i <= pK ; i++) {
            mochi.put(this.getTegoma(i, 1));
        }
        d.put("gote", mochi);
        //
        d.put("teban", this.getTeban());
        return d;
    }
    
    /** 盤面を初期化する
     * 初期盤面を盤面上のデータをクリアして先手番なら後手の持ちコマ、
     * 後手番なら先手の持ちコマにする
     */
    protected void clearForCSAProtocol() {
        for (int y = 0 ; y < BanmenDefine.B_MAX ; y++) {
            for (int x = 0 ; x < BanmenDefine.B_MAX ; x++) {
                // 盤面から消す
                this.setKoma(BanmenDefine.pNull, x, y);
            }
        }
        // 後手にコマを寄せる
        this.setTegoma(BanmenDefine.pP, 0, 0);
        this.setTegoma(BanmenDefine.pL, 0, 0);
        this.setTegoma(BanmenDefine.pN, 0, 0);
        this.setTegoma(BanmenDefine.pS, 0, 0);
        this.setTegoma(BanmenDefine.pG, 0, 0);
        this.setTegoma(BanmenDefine.pB, 0, 0);
        this.setTegoma(BanmenDefine.pR, 0, 0);
        //
        this.setTegoma(BanmenDefine.pP, 1, 18);
        this.setTegoma(BanmenDefine.pL, 1, 4);
        this.setTegoma(BanmenDefine.pN, 1, 4);
        this.setTegoma(BanmenDefine.pS, 1, 4);
        this.setTegoma(BanmenDefine.pG, 1, 4);
        this.setTegoma(BanmenDefine.pB, 1, 2);
        this.setTegoma(BanmenDefine.pR, 1, 2);
    }

    /**
     * CSA 状態にする。
     * @param message CSAプロトコルからのメッセージ
     * @return trueなら初期化終わり
     */
    public boolean setForCSAProtocol(String message) {
        if (message.equals("+")) {
            this.setTeban(0);//先手
            endForCSAProtocol();
            return true;
        } else if (message.equals("-")) {
            this.setTeban(1);//先手
            endForCSAProtocol();
            return true;
        } else if (0 == message.indexOf("P+")) {
            String[] splits = message.substring(2).split("00");
            for (String split : splits) {
                if (split.length() <= 0) {
                    continue;
                }
                byte koma = BanmenDefine.getStringToKoma(split);
                // 先手のコマを増やす
                this.setTegoma(koma, 0, this.getTegoma(koma, 0) + 1);
                // 後手のコマを減らす
                this.setTegoma(koma, 1, Math.max(0, this.getTegoma(koma, 1) - 1));
            }
            return true;
        } else if (0 == message.indexOf("P-")) {
            // EMPTY(後手詰めは考えない)
            return true;
        } else if (0 == message.indexOf("P")) {
            if (0 <= message.indexOf("P1")) {
                clearForCSAProtocol(); // 盤面上のものを消す
            }
            int y = Math.max(0,Math.min(8,(int)(message.charAt(1) - '1')));
            for (int x = 0 ; ((x*3 + 2) < message.length()) && (x < BanmenDefine.B_MAX) ; x++) {
                //System.out.println(koma + " x=" + x + " x=" + (x*3+2));
                int teban = message.charAt(x*3 + 2) == '+' ? 0 : 1;
                String komaString = message.substring(x*3 + 3,x*3 + 5);
                byte koma = BanmenDefine.getStringToKoma(komaString);
                if (koma != BanmenDefine.pNull) {
                    koma = (teban != 0) ? (byte)(BanmenDefine.ENEMY | koma) : koma;
                    this.setKoma(koma, 8 - x, y);
                    this.setTegoma(koma, 1, Math.max(0, this.getTegoma(koma, 1) - 1));
                }
            }
            return true;
        }
        return false;
    }
    /** CSA 状態にする（最後に先手後手が盤面に乗っていなかったら適当に乗せる） */
    protected void endForCSAProtocol() {
        //
        // 先手後手の持ち物にあったらまずいので消す
        this.setTegoma(BanmenDefine.pK, 0, 0);
        this.setTegoma(BanmenDefine.pK, 1, 0);
        //
        // 先手後手の王がいるかどうか探す
        boolean senteOu = false;
        boolean goteOu = false;
        for (int y = 0; y < BanmenDefine.B_MAX; y++) {
            for (int x = 0; x < BanmenDefine.B_MAX; x++) {
                byte ou = this.getKoma(x, y);
                if (ou == BanmenDefine.pK) {
                    senteOu = true;
                } else if (ou == BanmenDefine.pk) {
                    goteOu = true;
                }
            }
        }
        
        // 盤面上に空があったらそこに置いておく
        for (int y = 0; y < BanmenDefine.B_MAX; y++) {
            if (senteOu) {
                break;
            }
            for (int x = 0; 0 <= BanmenDefine.B_MAX; x++) {
                if (senteOu) {
                    break;
                }
                byte koma = this.getKoma(x, y);
                if (koma == BanmenDefine.pNull) {
                    senteOu = true;
                    this.setKoma(BanmenDefine.pK, x, y);
                }
            }
        }
        
        // 盤面上に空があったらそこに置いておく
        for (int y = BanmenDefine.B_MAX - 1; 0 <= y; y--) {
            if (goteOu) {
                break;
            }
            for (int x = BanmenDefine.B_MAX - 1; 0 <= x; x--) {
                if (goteOu) {
                    break;
                }
                byte koma = this.getKoma(x, y);
                if (koma == BanmenDefine.pNull) {
                    goteOu = true;
                    this.setKoma(BanmenDefine.pk, x, y);
                }
            }
        }
    }
    
    /**
     * sfen形式の文字列を BanmenOnly に変える
     * sfen lnsgk1snl/1r4gb1/p1ppppppp/9/1p5P1/2P6/PP1PPPP1P/1B5R1/LNSGKGSNL b - 7
     * @param sfn  sfen形式の文字列
     * @return 盤面情報
     */
    public static BanmenOnly createSfen(String sfn) {
        BanmenOnly only = new BanmenOnly();
        String[] split = sfn.split(" ");
        if ((split.length < 4) || (!split[0].equals("sfen"))) {
            return only; // 不明なので初期盤面を入れる
        }
        // 開始：全てのコマを後手番の持ちコマに含める
        only.clearForCSAProtocol();
        //
        if (split[2].toLowerCase().equals("b")) {
            only.setTeban(0); // 先手
        } else {
            only.setTeban(1); // 後手
        }
        String target = split[1]; // 盤面
        int pos = 0;
        for (int y = 0 ; y < BanmenDefine.B_MAX ; y++) {
            byte nari = 0;
            for (int x = BanmenDefine.B_MAX - 1 ; 0 <= x ; x--) {
                if (target.length() <= pos) {
                    break;
                }
                char at = target.charAt(pos);
                pos++;
                if (('0' <= at) && (at <= '9')) {
                    int z = (at - '0');
                    x = x +1 - z; // for文補正
                    continue;
                } else if ('/' == at) {
                    x = x +1; // for文補正
                    continue;
                } else if ('+' == at) {
                    nari = BanmenDefine.NARI;
                    x = x +1; // for補正
                    continue;
                }
                byte koma = (byte) (BanmenDefine.getUsiKomaToKoma(at) | nari);
                nari = 0;
                // コマを配置する
                only.setKoma(koma, x, y);
                //  後手の持ちコマを１つ減らす
                only.setTegoma(koma, 1, Math.max(0, only.getTegoma(koma, 1) - 1));
            }
        }
        target = split[3]; // 持ちコマ
        if (!target.equals("-")) {
            int sum = 1;
            for (pos = 0 ; pos < target.length(); pos++) {
                char at = target.charAt(pos);
                byte koma = BanmenDefine.getUsiKomaToKoma(at);
                if (('0' <= at) && (at <= '9')) {
                    sum = at - '0';//枚数指定
                    continue;
                }
                if ((koma == BanmenDefine.pNull) || (at & BanmenDefine.ENEMY) != 0) {
                    // 不明 or 後手のコマは配置済
                    sum = 1; // 枚数指定をクリア
                    continue;
                }
                // 先手の持ちコマを増やす
                only.setTegoma(koma, 0, Math.min(18, only.getTegoma(koma, 0) + sum));
                // 後手の持ちコマを１つ減らす
                only.setTegoma(koma, 1, Math.max(0, only.getTegoma(koma, 1) - sum));
                sum = 1; // 枚数指定をクリア
            }
        }
        // 王がいない場合は生やす
        only.endForCSAProtocol();
        return only;
    }
    /**
     * USI形式の手をintの手に変換する。パターンとしては以下がある
     * <ul>
     *   <li>4e5c  : 手の移動</li>
     *   <li>4e5c+ : 手の移動と成り</li>
     *   <li>P*5d : コマを打つ</li>
     * </ul>
     * @param sfn  sfen形式の文字列
     * @return 盤面情報
     */
    public int changeUsiTeToInt(String sfn) {
        BanmenOnly only = this.clone();
        if (sfn.length() < 4) {
            return -2; // わからん
        }
        // 手番を反転する
        int teban = only.getTeban();
        only.setTeban(1 - teban);
        //
        int newX = Math.max(0, Math.min(BanmenDefine.B_MAX - 1, ((byte)sfn.charAt(2)) - '1'));
        int newY = Math.max(0, Math.min(BanmenDefine.B_MAX - 1, ((byte)sfn.charAt(3)) - 'a'));
        int oldX;
        int oldY;
        byte koma = BanmenDefine.getUsiKomaToKoma(sfn.charAt(0));
        if (koma == BanmenDefine.pNull) {
            oldX = Math.max(0, Math.min(BanmenDefine.B_MAX - 1, ((byte)sfn.charAt(0)) - '1'));
            oldY = Math.max(0, Math.min(BanmenDefine.B_MAX - 1, ((byte)sfn.charAt(1)) - 'a'));            
            //
            koma = only.getKoma(oldX, oldY);
            if ((5 <= sfn.length()) && (sfn.charAt(4) == '+')) {
                // 成りがある
                koma = (byte) (BanmenDefine.NARI | koma);
            }
       } else {
            // 盤面に打つ
            oldX = BEAT;
            oldY = BEAT;
        }
        return changeTeToInt(koma, oldX, oldY, newX, newY);
    }
    
    /**
     * 手を取得する
     * @param newOnly 次の指し手の盤面
     * @return 指して
     */
    public int createTe(BanmenOnly newOnly) {
        int teban = newOnly.getTeban();
        int oldX = BEAT;
        int oldY = BEAT;
        byte oldKoma = BanmenDefine.pNull;
        int newX = BEAT;
        int newY = BEAT;
        for (int x = 0 ; (x < BanmenDefine.B_MAX) && ((oldX == BEAT) || (newX == BEAT)) ; x++) {
            for (int y = 0 ; (y < BanmenDefine.B_MAX) && ((oldX == BEAT) || (newX == BEAT)) ; y++) {
                byte oldBanmen = this.getKoma(x, y);
                byte newBanmen = newOnly.getKoma(x, y);
                if (oldBanmen != newBanmen) {
                    if (newBanmen != BanmenDefine.pNull) {
                        oldKoma = newBanmen; // 移動先のコマ
                        newX = x;
                        newY = y;
                    } else { // コマが空白に変わった＝移動元
                        oldX = x;
                        oldY = y;
                    }
                }
            }
        }
        // 持ちコマの処理（空白に変わったマスがないのであれば持ちコマから出している）
        if (oldX == BEAT) {
            for (byte koma = BanmenDefine.pP ; koma < BanmenDefine.pR ; koma++) {
                if (this.getTegoma(koma,teban) != this.getTegoma(koma,teban)) {
                    oldKoma = koma;
                    break;
                }
            }
        }
        if (newX == BEAT) {
            // 移動先が見つからない
            throw new java.lang.UnsupportedOperationException();
        }
        return changeTeToInt(oldKoma, oldX, oldY, newX, newY);
    }
    
    /** key値の取得
     * 
     * @return key値
     */
    public BanmenKey createBanmenKey() {
        return new BanmenKey(this);
    }
    
    /** 自分が王手か？
     * @return 自分が王手ならtrue
     */
    public boolean isMyOute() {
        // 相手に王手をかけているかチェック
        int teban = this.getTeban();
        int enemyOuX;
        int enemyOuY;
        if (teban == 0) { // 先手
            enemyOuX = this.getSenteOuX();
            enemyOuY = this.getSenteOuY();
        } else {
            enemyOuX = this.getGoteOuX();
            enemyOuY = this.getGoteOuY();
        }
        //
        // 相手に王手をかけているかチェック
        boolean oute;
        if (this.checkSelfMate(teban, enemyOuX, enemyOuY)) {
            oute = true;
        } else {
            oute = false;
        }
        return oute;
    }
}
