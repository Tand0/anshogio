package com.github.tand0.anshogio.engine;


/** エンジン用スレッド */
public abstract class EngineRunnable implements Runnable {
	private final Thread thread;
	private boolean stopFlag;

	/** コンストラクタ */
	public EngineRunnable(String engineName) {
        this.thread = new Thread(this);
        this.thread.setName(engineName);
		this.stopFlag = false;
	}
	/** 開始 */
	public EngineRunnable start() {
		this.thread.start();
		return this;
	}

	/** 終わっているならTrue */
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
	
	/** 想定する手 */
	public abstract int getTe();
}
