package com.github.tand0.anshogio.engine;


/** エンジン用スレッド */
public abstract class EngineRunnable implements Runnable {
    /** エンジン起動用のスレッド */
	private final Thread thread;
	/** 停止フラグ。 trueなら停止 */
	private boolean stopFlag;

	/** コンストラクタ
	 * 
	 * @param engineName エンジン名。デバッガやログに名称として付く
	 */
	public EngineRunnable(String engineName) {
        this.thread = new Thread(this);
        this.thread.setName(engineName);
		this.stopFlag = false;
	}
	/** エンジンの開始
	 * 
	 * @return 自分自身
	 */
	public EngineRunnable start() {
		this.thread.start();
		return this;
	}

	/** 終わっているならTrue
	 * 
	 * @return 終わっているならTrue
	 */
	public boolean isEnd() {
	    if (stopFlag) {
	        return true;
	    }
		return ! thread.isAlive();
	}

	/** 処理を停止する */
	public void stop() {
	    stopFlag = true;
	}

	/** 終了するまで待ち合わせる */
	public void join() {
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			stop();
		}
	}
	
	/** 想定する手
	 * 
	 * @return 次の手
	 */
	public abstract int getTe();
}
