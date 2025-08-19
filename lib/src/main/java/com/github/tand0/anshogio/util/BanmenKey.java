package com.github.tand0.anshogio.util;


/** 盤面をハフマンで圧縮したキーを作る */
public class BanmenKey implements Comparable<BanmenKey> {
    /** キー値 */
    protected long[] key = new long[4];

    @Override
    public String toString() {
        if (key == null) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        //
        for (long x : key) {
            b.append(String.format("%016x", x));
        }
        return  b.toString();
    }
    
    public BanmenKey(String moji) {
        if (moji.length() != 64) {
            throw new java.lang.UnsupportedOperationException();
        }
        // indexごとに分割してループ
        for (int i = 0 ; i < 4 ; i++) {
            // 負の数bitで変な動作になるのを恐れて２つに分けました
            String cut1 = moji.substring(i*16, i*16+8);
            long cut116 = Long.parseLong(cut1,16);
            String cut2 = moji.substring(i*16+8, i*16+16);
            long cut216 = Long.parseLong(cut2,16);
            key[i] =(cut116 << 32) | cut216;
        }
    }
    /**盤面上の処理
     * 駒  数 Hafuman   持成 必要
     * 先後 1 ------ -   - -  1 teban(0=先手,1=後手)
     * 王   2 ------ -  - - 13 pK
     * 空  41 0xxxxx 1  - - 41 pNull
     * 歩  18 10xxxx 2  1 1 72 pP
     * 香   4 1100xx 3  1 1 24 pL
     * 桂   4 1101xx 3  1 1 24 pN
     * 銀 　 4 1110xx 4  1 1 24 pS
     * 金 　 4 11110x 5  1 - 20 pG
     * 飛   2 111110 6  1 1 16 pR
     * 角 　 2 111111 6  1 1 16 pB
     * 合計 251
     * 
     * 持ち駒の処理
     * 歩  18 10xxxx 2  1 1 72 pP
     * 香   4 1100xx 4  1 1 24 pL
     * 桂   4 1101xx 4  1 1 24 pN
     * 銀   4 1110xx 4  1 1 24 pS
     * 金   4 11110x 5  1 - 20 pG
     * 飛   2 111111 6  1 1 16 pR
     * 角   2 111110 6  1 1 16 pB
     * 
     * @param only
     */
    public BanmenKey(BanmenOnly only) {
        if (only == null) {
            only = new BanmenOnly(null,0);
        }
        final Pointer p = new Pointer();
        //
        // 王角飛のいちを決める
        int ou0 = 0;
        int ou1 = 0;
        //
        // 王飛車角の位置特定
        for (int x = 0 ;  x < BanmenDefine.B_MAX ; x++) {
            for (int y = 0 ;  y < BanmenDefine.B_MAX ; y++) {
                byte koma = only.getKoma(x, y);
                if (koma == BanmenDefine.pK) { // 先手玉
                    ou0 = (x * 9) + y;
                } else if (koma == BanmenDefine.pk) { // 後手玉
                    ou1 = (x * 9) + y;
                }
            }
        }
        //
        setData(p, 1,only.getTeban()); // 手番  
        setData(p, 13,(ou0 *9*9) + ou1); // 王の位置
        //
        // 盤面上の処理をする
        // 持ち駒の処理
        for (int y = 0 ;  y < BanmenDefine.B_MAX ; y++) {
            if (p.okSum()) {
                break; // すべての駒が揃ったらループ不要
            }
            for (int x = 0 ;  x < BanmenDefine.B_MAX ; x++) {
                if (p.okSum()) {
                    break; // すべての駒が揃ったらループ不要
                }
                // 一つでも超過があったらそれはおかしいので終了
                p.checkSum();
                //
                int koma = (int)only.getKoma(x, y);
                int komaOnly = 0xF & koma;
                int komaEnemy = (koma & BanmenDefine.ENEMY) == 0 ? 0 : 1;
                int komaNari  = (koma & BanmenDefine.NARI) == 0 ? 0 : 1;
                if (komaOnly == BanmenDefine.pK) {
                    p.pKSum++;
                    p.checkSum();
                    continue;
                }
                //
                if (komaOnly == BanmenDefine.pNull) {
                    setData(p,1,0);
                    continue;
                }
                setData(p,1,1);
                switch (komaOnly) {
                case BanmenDefine.pP:
                    p.pPSum++;
                    setData(p,1,0b0);
                    setData(p,1,komaEnemy);
                    setData(p,1,komaNari);
                    p.checkSum();
                    break;
                case BanmenDefine.pL:
                    p.pLSum++;
                    setData(p,3,0b100);
                    setData(p,1,komaEnemy);
                    setData(p,1,komaNari);
                    p.checkSum();
                    break;
                case BanmenDefine.pN:
                    p.pNSum++;
                    setData(p,3,0b101);
                    setData(p,1,komaEnemy);
                    setData(p,1,komaNari);
                    p.checkSum();
                    break;
                case BanmenDefine.pS:
                    p.pSSum++;
                    setData(p,3,0b110);
                    setData(p,1,komaEnemy);
                    setData(p,1,komaNari);
                    p.checkSum();
                    break;
                case BanmenDefine.pG:
                    p.pGSum++;
                    setData(p,4,0b1110);
                    setData(p,1,komaEnemy);
                    //金は成れません
                    p.checkSum();
                    break;
                case BanmenDefine.pR:
                    p.pRSum++;
                    setData(p,5,0b11110);
                    setData(p,1,komaEnemy);
                    setData(p,1,komaNari);
                    p.checkSum();
                    break;
                default:
                case BanmenDefine.pB:
                    p.pBSum++;
                    setData(p,5,0b11111);
                    setData(p,1,komaEnemy);
                    setData(p,1,komaNari);
                    p.checkSum();
                    break;
                }
            }
        }
        for (int teban = 0 ; teban < 2 ; teban++) { // 手番処理
            if (p.okSum()) {
                break; // すべての駒が揃ったらループ不要
            }
            long sum = only.getTegoma(BanmenDefine.pP, teban);
            p.pPSum+= sum;
            for (int i = 0; i < sum ; i++) {
                setData(p,1,0b0);
                setData(p,1,teban);
            }
            sum = only.getTegoma(BanmenDefine.pL, teban);
            p.pLSum+= sum;
            for (int i = 0; i < sum ; i++) {
                setData(p,3,0b100);
                setData(p,1,teban);
            }
            sum = only.getTegoma(BanmenDefine.pN, teban);
            p.pNSum+= sum;
            for (int i = 0; i < sum ; i++) {
                setData(p,3,0b101);
                setData(p,1,teban);
            }
            sum = only.getTegoma(BanmenDefine.pS, teban);
            p.pNSum+= sum;
            for (int i = 0; i < sum ; i++) {
                setData(p,3,0b110);
                setData(p,1,teban);
            }
            sum = only.getTegoma(BanmenDefine.pG, teban);
            p.pGSum+= sum;
            for (int i = 0; i < sum ; i++) {
                setData(p,4,0b1110);
                setData(p,1,teban);
            }
            sum = only.getTegoma(BanmenDefine.pR, teban);
            p.pRSum+= sum;
            for (int i = 0; i < sum ; i++) {
                setData(p,5,0b11110);
                setData(p,1,teban);
            }
            sum = only.getTegoma(BanmenDefine.pB, teban);
            p.pBSum+= sum;
            for (int i = 0; i < sum ; i++) {
                setData(p,5,0b11111);
                setData(p,1,teban);
            }
        }
    }
    
    public int getOuData() {
        long data = (key[0] >>> (64-13)) % (81*81);
        return (int)data;
    }

    @Override
    public int compareTo(BanmenKey o) {
        if (key[0] != o.key[0]) {
            return (int) (key[0] - o.key[0]);
        } else if (key[1] != o.key[1]) {
            return (int) (key[1] - o.key[1]);
        } else if (key[2] != o.key[2]) {
            return (int) (key[2] - o.key[2]);
        }
        return (int) (key[3] - o.key[3]);
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof BanmenKey) {
            BanmenKey target = (BanmenKey)o;
            return (key[0] == target.key[0]) &&
                    (key[1] == target.key[1]) &&
                    (key[2] == target.key[2]) &&
                    (key[3] == target.key[3]);
        }
        return false;
    }
    public long[] hashCodeLongArray() {
        return key;
    }
    public long hashCodeLong() {
        return key[0] ^ key[1] ^ key[2] ^ key[3];
    }
    @Override
    public int hashCode() {
        long x = hashCodeLong();
        return (int)((0xFFFFFFFFL & x) ^ (x >>> 32));
    }
    public int hashCodeInt() {
        return hashCode();
    }
    public short hashCodeShort() {
        int x = hashCode();
        return (short) ((0xFFFF & x) ^ (x >>> 16));
    }
    public byte hashCodeByte() {
        short x = hashCodeShort();
        return (byte) ((0xFF & x) ^ (x >>> 8));
    }

    /**
     * 盤面しかないときにキー値から作る
     * @return 盤面情報
     */
    public BanmenOnly createBanmenOnly() {
        BanmenOnly banmen = new BanmenOnly(null,0);
        // 盤面をクリアする
        for (int i = 0 ; i < BanmenDefine.B_MAX; i++) {
            for (int j = 0 ; j < BanmenDefine.B_MAX; j++) {
                banmen.setKoma(BanmenDefine.pNull, i, j);           
            }
        }
        // 持ちゴマをクリアする
        for (int teban = 0 ; teban < 2 ; teban++) {
            for (byte koma = BanmenDefine.pB ; koma < BanmenDefine.pK ; koma++) {
                banmen.setTegoma(koma, teban, 0);
            }
        }
        //
        Pointer p = new Pointer();
        //
        // 手番を設定する
        banmen.setTeban((int)getData(p,1));
        //
        // 王角飛車の設定の取得
        long sum;
        //
        // 値を投入する
        sum = getData(p, 13);
        long ou1 = sum % (9*9);
        long ou0 = sum / (9*9);
        banmen.setKoma(BanmenDefine.pK, (int)ou0/9, (int)ou0%9);
        banmen.setKoma(BanmenDefine.pk, (int)ou1/9, (int)ou1%9);
        //
        // ハフマン処理ここから
        for (int y = 0 ;  y < BanmenDefine.B_MAX ; y++) {
            if (p.okSum()) {
                break; // すべての駒が揃ったらループ不要
            }
            for (int x = 0 ;  x < BanmenDefine.B_MAX ; x++) {
                if (p.okSum()) {
                    break; // すべての駒が揃ったらループ不要
                }
                //
                // 超過チェック
                p.checkSum();
                //
                if ( ((x == ou0/9) && (y == ou0%9)) ||  ((x == ou1/9) && (y == ou1%9)) ) {
                    p.pKSum++;
                    continue; // すでに駒が置かれている
                }
                //
                // 空かどうかチェックする
                long check = getData(p,1);
                if (check == 0) {
                    continue; // 空だ
                }
                //
                // 中身のデータ
                check = getData(p,1);
                byte koma;
                long teban;
                long nari = 0;
                if (check == 0) { // 00xxx
                    // 歩である
                    p.pPSum++;
                    koma = BanmenDefine.pP;
                    teban = (getData(p, 1) != 0) ? BanmenDefine.ENEMY : 0;
                    nari = (getData(p, 1) != 0) ? BanmenDefine.NARI : 0;
                } else { // 1xxxx
                    check = getData(p,2);
                    if (check == 0b0) {
                        p.pLSum++;
                        koma = BanmenDefine.pL;
                        teban = (getData(p, 1) != 0) ? BanmenDefine.ENEMY : 0;
                        nari = (getData(p, 1) != 0) ? BanmenDefine.NARI : 0;
                    } else if (check == 0b1) {
                        p.pNSum++;
                        koma = BanmenDefine.pN;
                        teban = (getData(p, 1) != 0) ? BanmenDefine.ENEMY : 0;
                        nari = (getData(p, 1) != 0) ? BanmenDefine.NARI : 0;
                    } else if (check == 0b10) {
                        p.pSSum++;
                        koma = BanmenDefine.pS;
                        teban = (getData(p, 1) != 0) ? BanmenDefine.ENEMY : 0;
                        nari = (getData(p, 1) != 0) ? BanmenDefine.NARI : 0;                            
                    } else { // 0b11
                        check = getData(p,1);
                        if (check == 0) {
                                p.pGSum++;
                                koma = BanmenDefine.pG;
                                teban = (getData(p, 1) != 0) ? BanmenDefine.ENEMY : 0;
                                // 金に成りはない
                        } else {
                            check = getData(p,1);
                            if (check == 0) {
                                p.pRSum++;
                                koma = BanmenDefine.pR;
                                teban = (getData(p, 1) != 0) ? BanmenDefine.ENEMY : 0;
                                nari = (getData(p, 1) != 0) ? BanmenDefine.NARI : 0;                            
                            } else {
                                p.pBSum++;
                                koma = BanmenDefine.pB;
                                teban = (getData(p, 1) != 0) ? BanmenDefine.ENEMY : 0;
                                nari = (getData(p, 1) != 0) ? BanmenDefine.NARI : 0;                                                                
                            }
                        }
                    }
                }
                koma = (byte) (teban | nari | koma);
                banmen.setKoma(koma, x, y);
            }
        }
        //
        // 持ち駒チェック
        while (! p.okSum()) {
            //
            // 超過チェック
            p.checkSum();
            //
            byte koma;
            long check = peakData(p,1);
            if (check == 0b0) { //0b0
                getData(p,1);
                p.pPSum++;// 歩である
                koma = BanmenDefine.pP;
                p.checkSum();
            } else { // 0b1
                check = peakData(p,3);
                if (check == 0b100) {
                    getData(p,3);
                    p.pLSum++;
                    koma = BanmenDefine.pL;
                    p.checkSum();
                } else if (check == 0b101) {
                    getData(p,3);
                    p.pNSum++;
                    koma = BanmenDefine.pN;                     
                    p.checkSum();
                } else if (check == 0b110) {
                    getData(p,3);
                    p.pSSum++;
                    koma = BanmenDefine.pS;                     
                    p.checkSum();
                } else { //0b111
                    check = peakData(p,4);
                    if (check == 0b1110) {
                        getData(p,4);
                        p.pGSum++;
                        koma = BanmenDefine.pG;
                        p.checkSum();
                    } else { // 0b1111
                        check = peakData(p,5);
                        getData(p,5);
                        if (check == 0b11110) {
                            p.pRSum++;
                            koma = BanmenDefine.pR;
                            p.checkSum();
                        } else { // 0b11111
                            p.pBSum++;
                            koma = BanmenDefine.pB;
                            p.checkSum();
                        }
                    }
                }
            }
            int teban = (int)getData(p,1);
            banmen.setTegoma(koma, teban,banmen.getTegoma(koma, teban) + 1);
        }
        //
        return banmen;
    }

    
    /** データを一つ書き込む */
    protected void setData(Pointer p,int len, long data) {
        int max = p.pos + len;
        if (max < 64) {
            long newData = data << (64 - max);
            if (4 <= p.index) {
                throw new java.lang.UnsupportedOperationException("setData Exception(1)! index=" + p.index + " pos=" + p.pos);
            }
            key[p.index] = key[p.index] | newData;
            p.pos = max;
            return;
        }
        long newData = data >>> (max - 64);
        key[p.index] = key[p.index] | newData;
        p.index++;
        p.pos = 0;
        len = max - 64;
        if (len != 0) {
            if (4 <= p.index) {
                throw new java.lang.UnsupportedOperationException("setData Exception(2)! index=" + p.index + " pos=" + p.pos);
            }
            newData = data << (64 - max);
            key[p.index] = key[p.index] | newData;          
        }
        p.pos = len;
    }
    
    /** データを一つ取得する */
    protected long getData(Pointer p,int len) {
        long result = 0;
        if (4 <= p.index) {
            throw new java.lang.UnsupportedOperationException("getData Exception(1)! index=" + p.index + " pos=" + p.pos);
        }
        int max = p.pos + len;
        if (max < 64) {
            result = key[p.index];
            result = result << p.pos;
            result = result >>> (64 - len);
            //
            p.pos = max;
            return result;
        }
        int nextLen = max - 64;
        result = key[p.index] << p.pos; //pos分だけ左シフトして
        result = result >>> p.pos; //pos分だけ右シフトすればposより前が0になる
        result = result << nextLen; // 次の長さ分だけ左シフトして次のindex分のデータが詰めるようにする
        int tIndex = p.index + 1;
        if ((4 <= tIndex) && (nextLen != 0)) {
            throw new java.lang.UnsupportedOperationException("getData Exception(2)! index" + tIndex + " pos=" + nextLen);
        }
        if (nextLen != 0) {
            result = result | (key[tIndex] >>> (64 - nextLen));
        }
        p.index = tIndex;
        p.pos = nextLen;
        return result;
    }
    
    /** データを一つ取得する */
    protected long peakData(Pointer p,int len) {
        long result = 0;
        if (4 <= p.index) {
            return 0;
        }
        int max = p.pos + len;
        if (max < 64) {
            result = key[p.index];
            result = result << p.pos;
            result = result >>> (64 - len);
            //
            //p.pos = max; // peekなので処理しない
            return result;
        }
        int nextLen = max - 64;
        result = key[p.index] << p.pos; //pos分だけ左シフトして
        result = result >>> p.pos; //pos分だけ右シフトすればposより前が0になる
        result = result << nextLen; // 次の長さ分だけ左シフトして次のindex分のデータが詰めるようにする
        int tIndex = p.index + 1;
        if ((4 <= tIndex) && (nextLen != 0)) {
            return 0;
        }
        if (nextLen != 0) {
            result = result | (key[tIndex] >>> (64 - nextLen));
        }
        // p.index = tIndex; // peekなので処理しない
        // p.pos = nextLen; // peekなので処理しない
        return result;
    }
    
    /** 手番を取得する */
    public int getTeban() {
        return (int) (key[0] >>> 63);
    }
    /** 手番を設定する */
    protected void setTeban(int teban) {
        key[0] = (key[0] & ((~ 0L) >>> 1)) | ((teban & 1L) << 63);
    }
}
