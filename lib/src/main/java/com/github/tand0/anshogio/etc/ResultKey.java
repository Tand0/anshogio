package com.github.tand0.anshogio.etc;

/** 結果配置用のクラス */
public class ResultKey {
    /** key値 */
    public final String key;
    /** 勝った数 */
    public final int win;
    /** 負けた数 */
    public final int loss;
    /** 定跡：０なら定跡でない、１以上なら定跡 */
    public final int joseki;
    /**
     * コンストラクタ
     * @param key 局面のkey値
     * @param win この局面で先手が勝った数
     * @param loss この局面で先手が負けた数
     * @param joseki 定跡：０なら定跡でない、１以上なら定跡
     */
    public ResultKey(String key, int win, int loss,int joseki) {
        this.key = key;
        this.win = win;
        this.loss = loss;
        this.joseki = joseki;
    }
}
