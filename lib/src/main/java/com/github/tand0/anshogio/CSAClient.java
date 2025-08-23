package com.github.tand0.anshogio;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.util.BanmenDefine;


/** CASプロトコル用クライアント */
public class CSAClient implements Runnable {
	
	/** ログ用 */
	private final static Logger logger = LoggerFactory.getLogger(CSAClient.class);
    
	/** host name */
	private final String hostname;
	
	/** layer4 port */
	private final int port;
	
	/** 名前 */
	private final String name;

	/** コミュニティ名 */
	private final String password;
	
	/** ソケットチャネル */
	private SocketChannel socketChannel;
	
	/** RFC854 */
	public static final byte KEY_LF  = 10;

	/** RFC854 */
	public static final byte KEY_CR  = 13;

	/** セレクター */
	private final Selector selector;
	
    /** 書き込みデータのリスト */
    private final java.util.LinkedList<String> writeDataList = new java.util.LinkedList<>();
    
    /** １行分のメッセージバッファ(読み込み方向) */
    private StringBuffer messageBuffer = new StringBuffer();

    /** 社畜 */
    private final CSAWorker worker;
	
    /** ホスト名のdict名 */
	private static final String HOSTNAME = "csa.ostname";

    /** CSAポートのdict名 */
	private static final String PORT = "csa.port";

    /** CSA名のdict名 */
	private static final String NAME = "csa.name";

    /** CSAパスワードのdict名 */
	private static final String PASSEORD = "csa.password";
	
    /**
     * コンストラクタ 
     * @param selector セレクター
     * @param worker ワーカー
     */
	public CSAClient(Selector selector, CSAWorker worker) {
		logger.debug("Client constructor start");
		this.selector = selector;
		this.worker = worker;
		hostname = worker.getSetting().getString(HOSTNAME);
        port = worker.getSetting().getInt(PORT);
        name = worker.getSetting().getString(NAME);
        password = worker.getSetting().getString(PASSEORD);
	}

	/** 実働部 */
	@Override
	public void run() {
		if (worker.getStatus() == ANStatus.START) {
    		logger.debug("Client run connect start {},{}",hostname,port);
			worker.setStatus(ANStatus.BEGIN_CONNECT);
    		//
            try {
            	// ソケットを開く
                socketChannel = SocketChannel.open();
                // ノンロッキングモード
                socketChannel.configureBlocking(false);
                //
                // 接続先を指定
                InetSocketAddress address = new InetSocketAddress(hostname, port);
                socketChannel.connect(address);
                //
                // チャネルに登録
                socketChannel.register(selector, SelectionKey.OP_CONNECT,this);
                //
                //
            } catch (IOException e) {
                logger.error("Cas io error! e={}",e.getMessage());
                //
                setClose();
                //
            }
        } else {
    		logger.debug("Client now"); 
        }
        //logger.trace("Client run end");
	}
	
    /** コネクト処理。finishConnect を呼ぶ処理
     * 
     * @param key セレクションキー
     */
    public void connect(SelectionKey key) {
        if (worker.getStatus() != ANStatus.BEGIN_CONNECT) {
            return;
        }
        logger.trace("Client connect start");
        worker.setStatus(ANStatus.BEGIN_LOGIN);
        //
        try {
            socketChannel.finishConnect();
        } catch (IOException e) {
            logger.error("connect failed e={}",e.getMessage());
            key.cancel();
            setClose();
            return;
        }
        //
        writeDataList.addLast("LOGIN " + name + " " + password + "\r\n");
        nextStage();
        //
        logger.trace("Client connect end");
    }
	
	/** 次のターン
	 */
	public void nextStage() {
		//logger.trace("nextStage start");
		if (socketChannel == null) {
			logger.warn("nextStage channel is null");
			return;
		}
        try {
			if (!writeDataList.isEmpty()) {
                //logger.trace("change OP_WRITE");
                socketChannel.register(selector, SelectionKey.OP_WRITE,this);
                //
			} else {
                //logger.trace("change OP_READ");
                socketChannel.register(selector, SelectionKey.OP_READ,this);
                //
			}
		} catch (ClosedChannelException|CancelledKeyException e) {
			logger.warn("nextStage failed e={}",e.getMessage());
			this.setClose();
		}
	}

	/** 書き込みできるようになったら呼ばれる
	 * 
	 * @param key キー
	 */
	public void write(SelectionKey key) {
		//logger.debug("Client write start");
		try {
			if (!writeDataList.isEmpty()) {
				String writeText = this.writeDataList.removeFirst();
		    	logger.debug("<{}",writeText.trim());
				byte[] bytes = writeText.getBytes(StandardCharsets.US_ASCII);
			    ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
			    byteBuffer.put(bytes);
			    byteBuffer.flip();
			    socketChannel.write(byteBuffer);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("write UnsupportedEncodingException e={}",e.getMessage());
		} catch (IOException e) {
			logger.error("write io exception e={}",e.getMessage());
		}
		nextStage();
		//logger.trace("Client write end");
	}

	/** 読み込みできるようになったら呼ばれる
	 * 
	 * @param key キー
	 */
	public void read(SelectionKey key) {

        final ByteBuffer buffer = ByteBuffer.allocate(2048);
        try {
            int len = socketChannel.read(buffer);
            if (len == 0) {
                // socket で自身以外のreadが動いて、
                // 自身にデータが無い場合ここに来る
                // logger.warn("len == 0");                
            } else if (len < 0) {
                throw new IOException("connection failed! (len<0)");
            } else {
                //複数行で処理した方が早いが面倒なのでこれでやっています
                IntStream
                    .range(0,len)
                    .forEachOrdered(i->this.readOneByte(buffer.get(i)));
            }
            //
        } catch (IOException e) {
            logger.error("Error:" + e.getClass().getName() + ":" + e.getMessage());
            key.cancel();
            setClose();
        } finally {
    		nextStage();
        }
		//logger.trace("Client read end");
	}

    /** 1byte受信 
     * 
     * @param aaa 1byte分の情報
     */
    protected void readOneByte(byte aaa) {
    	//logger.trace("ch={} message={}", (char)aaa,messageBuffer.toString());
    	if (aaa == KEY_LF) {
    		readString(this.messageBuffer.toString());
    		// clear
    		this.messageBuffer = new StringBuffer();
    	} else if (aaa != KEY_CR) {
    		this.messageBuffer.append((char) aaa);
    	}
    }
    
    /** 先手名称 */
    private String senteName = "";
    /** 後手名称 */
    private String goteName = "";
    /** 自分のターン、先手0, 後手1 */
    private int myTurn = 0;
    /** トータルタイム */
    private Integer totalTime = null;
    /** 秒読み時間 */
    private Integer byoyomiTime = null;
    /** 遅延時間 */
    private Integer delayTime = null;
    /** 1手の加算時間 */
    private Integer incrementTime = null;
    /**
     * 一行読みます
     * @param message 送られてきたメッセージ
     */
    protected void readString(String message) {
    	logger.debug(">{}->{}",this.worker.getStatus(),message.trim());
    	switch(this.worker.getStatus()) {
    	case BEGIN_LOGIN:
    		String target = "LOGIN:" + this.name + " OK";
    		if (!message.equals(target)) {
    			logger.error("CAS Login failed! message=\"" + message + "\"");
    			logger.error("CAS Login failed! target =\"" + target + "\"");
    			// 終了する
                setClose();
    		} else {
    		    this.worker.setStatus(ANStatus.BIGIN_GAME_SUMMARY);
    		}
    		break;
    	case BIGIN_GAME_SUMMARY:
    		if (0 == message.indexOf("BEGIN Game_Summary")) {
    			logger.trace("begin summary");
    	        //
        		this.worker.setStatus(ANStatus.BIGIN_GAME_NOW);
    		}
    		break;
    	case BIGIN_GAME_NOW:
    		if (   (0 == message.indexOf("Protocol_Version:"))
                || (0 == message.indexOf("Protocol_Mode:"))
                || (0 == message.indexOf("Format:"))
                || (0 == message.indexOf("Declaration:"))
                || (0 == message.indexOf("Name"))
                || (0 == message.indexOf("#")) ){
				logger.debug("skip message={}",message);
            } else if (0 <= message.indexOf("Name+:")) {
                senteName = message.substring("Name+:".length());
            } else if (0 <= message.indexOf("Name-:")) {
                goteName = message.substring("Name-:".length());
            } else if (message.equals("Your_Turn:+")) {
    			myTurn = 0; // 先手
    		} else if (message.equals("Your_Turn:-")) {
    			myTurn = 1; // 後手
    		} else if (message.equals("To_Move:+")) {
    			//EMPTY
    		} else if (message.equals("To_Move:-")) {
    			//EMPTY
    		} else if (0 == message.indexOf("BEGIN Time")) {
    			this.worker.setStatus(ANStatus.BEGIN_TIME);
    		} else if (0 == message.indexOf("BEGIN Position")) {
    			this.worker.setStatus(ANStatus.BEGIN_POSITION);
    		} else if (0 == message.indexOf("END Game_Summary")) {
    			logger.trace("END Game_Summary");
        		writeDataList.addLast("AGREE\r\n");
    			this.worker.setStatus(ANStatus.BEIGN_START);
    		} else {
    			logger.error("unkown message={}",message);
    		}
    		break;
    	case BEGIN_TIME:
    		final String TIME_UNIT = "Time_Unit:";//1sec
    		final String TOTAL_TIME = "Total_Time:";//300
    		final String BYOYOMI = "Byoyomi:";//10
    		final String DELAY = "Delay:";//3
    		final String INCREMENT = "Increment:";//5
    		if (0 == message.indexOf(TIME_UNIT)) {
    			logger.error("unittime={}",message.substring(TIME_UNIT.length()));
    		} else if (0 == message.indexOf(TOTAL_TIME)) {
                totalTime = Integer.parseInt(message.substring(TOTAL_TIME.length()));
    		} else if (0 == message.indexOf(BYOYOMI)) {
                byoyomiTime = Integer.parseInt(message.substring(BYOYOMI.length()));
    		} else if (0 == message.indexOf(DELAY)) {
                delayTime = Integer.parseInt(message.substring(DELAY.length()));
    		} else if (0 == message.indexOf(INCREMENT)) {
                incrementTime = Integer.parseInt(message.substring(INCREMENT.length()));
    		} else if (0 == message.indexOf("END Time")) {
    			//とりあえず、いまは時間は無視する
    			this.worker.setStatus(ANStatus.BIGIN_GAME_NOW);
    		}
    		break;
    	case BEGIN_POSITION:
    		//
    		
    		//
    		if (0 == message.indexOf("END Position")) {
    			//とりあえず配牌も無視する
    			this.worker.setStatus(ANStatus.BIGIN_GAME_NOW);
    		}
    		break;
    	case BEIGN_START:
    		if (0 == message.indexOf("START")) {
    			this.worker.setStatus(ANStatus.FIGHT);
    		}
    		this.worker.start(senteName, goteName, myTurn,  totalTime,  byoyomiTime,  delayTime, incrementTime);
            break;
    	case FIGHT:
    		if (   (0 == message.indexOf("+")
                || (0 == message.indexOf("-")))) {
                this.worker.setNextMove(message);
    		} else if ((0 == message.indexOf("%CHUDAN"))
    				|| (0 == message.indexOf("%TIME_UP"))
    				|| (0 == message.indexOf("#WIN"))
    				|| (0 == message.indexOf("#LOSE")) 
    				|| (0 == message.indexOf("#DRAW")) ) {
    			// 終わった
                this.writeDataList.addLast("LOGOUT\r\n");
                //
                // 終了する
                setClose();
                //
    			// 書き込みを促進する
                this.selector.wakeup();
    		} else if (0 == message.indexOf("LOGOUT:completed")) {
    			logger.trace("Game Over");
                try {
					socketChannel.close();
				} catch (IOException e) {
					//出るのは当然なので無視する
				}
                //
                // 終了する
                setClose();
                //
            }
    		break;
    	case END:
        	logger.debug("end state");
            break;
    	case ERROR:
        	logger.error("error state");
            break;    		
    	default:
        	logger.error("readString unkown state state={}",this.worker.getStatus().toString());
            break;
    	}
    }

    /** 社畜から手を指された
     * 
     * @param te 指し手
     */
	public void sendTe(int te) {
		if (this.worker.getStatus() == ANStatus.FIGHT) {
			if (te == 0) {
				// 指す手がない(負けた)
                this.writeDataList.addLast("%TORYO\r\n");
                this.writeDataList.addLast("LOGOUT\r\n");
			} else if (te == -1) { // 入玉勝ち
                this.writeDataList.addLast("%KACHI\r\n");
                this.writeDataList.addLast("LOGOUT\r\n");
			} else {
                String moveString = BanmenDefine.changeTeIntToString(te);
                this.writeDataList.addLast(moveString + "\r\n");
			}
			// 書き込みを促進する
			this.selector.wakeup();
		}
	}

	/**
	 * 停止する
	 */
	public void setClose() {
		logger.debug("setClose");
		//
		SocketChannel socketChannel = this.socketChannel;
	    this.socketChannel = null;
		if (socketChannel != null) {
		    //
		    worker.setStatus(ANStatus.END);
		    try {
				socketChannel.close();
			} catch (IOException e) {
				//EMOPTY
			}
		}
	}
}
