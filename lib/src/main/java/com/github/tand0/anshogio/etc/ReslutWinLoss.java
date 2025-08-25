package com.github.tand0.anshogio.etc;

/** win と loss の戻り値 */
public class ReslutWinLoss {
    
    /** win 値 */
    public int win;

    /** loss 値 */
    public int loss;
    
    /** 定跡値 0: 定跡でない、1以上: 定跡 */
    public int joseki;
    
    /** コンストラクタ
     * @param win win値
     * @param loss loss値
     * @param joseki  0: 定跡でない、1以上: 定跡
     */
    public ReslutWinLoss(int win, int loss, int joseki) {
        this.win = win;
        this.loss = loss;
        this.joseki = joseki;
    }
}