package com.github.tand0.anshogio;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CSAのメイン処理スレッド
 */
public class CSAMainThread implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(CSAMainThread.class);

    private final CSAWorker worker;
    
	private CSAClient client;

    /** セレクタ */
    private Selector selector;
    
    /** 終了フラグ */
    private boolean aliveFlag = true;
    
	/** コンストラクタ
     */
    public CSAMainThread(CSAWorker worker) {
    	logger.debug("start");
    	this.worker = worker;
    }

    /** スレッド実行 */
    public void run() {
    	logger.debug("CSAMainThread.run() start");
        try {
            this.selector = Selector.open();
            this.client = new CSAClient(this.selector, worker);
    		//
    		this.client.run();
            //
            while ((this.selector != null)
            		&& (worker.getStatus() != ANStatus.END)
            		&& aliveFlag) {
                this.runWhile();
            }
            //
            if (this.selector != null) {
            	this.selector.close();
            }
            this.selector = null;
            //
        } catch (IOException| IllegalStateException e) {
            this.selector = null;
            logger.error("constructor error e=" + e.getMessage());
        }
        logger.debug("CSAMainThread.run() end");
    }
    /**
     * runループ１つ分の処理
     */
    public void runWhile() throws IllegalStateException {
    	//logger.trace("ANMainThreadImpl runWhile start");
    	Selector selector = this.selector;
    	CSAClient client = this.client;
    	if ((selector == null) || (! aliveFlag)) {
    		return;
    	}
        java.util.Set<SelectionKey> keys = selector.selectedKeys();
        keys.forEach(key->{
            try {
                if (key.isWritable()) {
                    client.write(key);
                }
                if (key.isReadable()) {
                    client.read(key);
                }
                if (key.isConnectable()) {
                    client.connect(key);
                }
            } catch(CancelledKeyException e) {
                key.cancel();
                close();
            }
        });
        //
        // close()を呼んでnullになっているパターンへの対応
        selector = this.selector;
    	if ((selector == null) || (! aliveFlag)) {
    		return;
    	}
        final long sleep = 1000 * 60 * 60;
        try {
            //logger.debug("sleep start");
            selector.select(sleep);
        } catch (IOException e) {
            /*EMPTY*/
        }
    }
    /** 
     * 社畜から手を指された
     */
	public void sendTe(int te) {
		CSAClient client = this.client;
		if (client != null) {
			client.sendTe(te);
		}
	}
    /** セレクタの取得 */
    public Selector getSelector() {
        return this.selector;
    }

    /** 終了する */
    public void close() {
		this.aliveFlag = false;
    	CSAClient client = this.client;
    	Selector selector = this.selector;
		this.client = null;
		this.selector = null;
		//
    	if (client != null) {
    		client.setClose();
    	}
    	if (selector != null) {
    		try {
    			selector.close();
			} catch (IOException e) {
				// EMPTY
			}
    	}
    }
}
