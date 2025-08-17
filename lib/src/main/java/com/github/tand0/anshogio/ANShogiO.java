package com.github.tand0.anshogio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.engine.EngineRunnable;
import com.github.tand0.anshogio.engine.PnDnEngineRunnable;
import com.github.tand0.anshogio.engine.PosgreEngineRunnable;
import com.github.tand0.anshogio.engine.TensorEngineRunnable;
import com.github.tand0.anshogio.etc.ANDownloadO;
import com.github.tand0.anshogio.etc.ANHttpO;
import com.github.tand0.anshogio.etc.ANPostgreO;
import com.github.tand0.anshogio.etc.ANShogiServerO;
import com.github.tand0.anshogio.eval.ANModel;
import com.github.tand0.anshogio.util.BanmenDefine;
import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenNext;
import com.github.tand0.anshogio.util.BanmenOnly;
import com.sun.net.httpserver.HttpServer;



/** 将棋用メイン処理 */
public class ANShogiO {
    /** ログ */
    private static final Logger logger = LoggerFactory.getLogger(ANShogiO.class);

    /** メイン処理 */
    public static final void main(String[] argc) throws IOException {
        logger.debug("start debug!");
        ANShogiO aNShogiO = new ANShogiO();
        if (0 < argc.length) aNShogiO.setSettingFileName(argc[0]);
        logger.debug("settingFileName={}",aNShogiO.getSettingFileName());
        aNShogiO.run();
    }

    /** ステータス情報 */
    protected ANStatus status = ANStatus.START;
    
    /** 設定ファイル名のデフォルト値 */
    protected final String SETTING_FILE = "setting.json";

    /** 設定ファイル名 */
    private String settingFile = null;
    
    /** 設定情報 */
    private JSONObject setting = null;
    
    /** ブロッキングキュー */
    private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(10);

    /** 連戦フラグ */
    private boolean stopFlag;

    private CSAMainThread casMain = null;
    
    /** DBへの接続用 */
    private ANPostgreO aNPostgreO = null;
    
    /** tensorへの接続用 */
    private ANModel aNModel = null;
    
    /** 現在の盤面 */
    private final LinkedList<BanmenNext> banmenList = new LinkedList<>();

    /** 現在の盤面 */
    private final BanmenFactory factory = new BanmenFactory();
    
    /** ログバッファー */
    private StringBuffer logBuff = new StringBuffer();
    
    /** 設定ファイルの取得 */
    public void setSettingFileName(String settingFile) {
        this.settingFile = settingFile;
    }

    /** 設定ファイルの取得 */
    public String getSettingFileName() {
        return (settingFile == null)? SETTING_FILE : settingFile;
    }

    /**
     * 設定ファイルを読み込む
     * @throws IOException 読み込み失敗
     */
    public void createSetting() throws IOException {
        if (setting != null) {
            return;
        }
        try (FileReader fr = new FileReader(getSettingFileName());
                BufferedReader br = new BufferedReader(fr)){
            String strLine;
            StringBuilder sbSentence = new StringBuilder();
            while ((strLine = br.readLine()) != null) {
                sbSentence.append(strLine);
            }
            setting = new JSONObject(sbSentence.toString());
        }
    }
    
    
    /** メイン処理その２
     * @throws IOException 読み込み失敗
     */
    protected void run() throws IOException {
        this.createSetting(); // 設定ファイルを読み込む
        
        // 観賞用のサーバを起動する
        int port = setting.getInt("server.port");
        logger.debug("server port={}",port);
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new ANHttpO(this.workerImpl));
            server.start();
        } catch (IOException e) {
            setStatus(ANStatus.ERROR);
        }
        
        // DBへ接続する
        this.aNPostgreO = new ANPostgreO(setting);
        try {
            this.aNPostgreO.connect();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        
        // tensorへ接続する
        this.aNModel = new ANModel(setting);
        
        
        // メインのループを呼ぶ
        loop();
    }
    
    /** メインループ */
    protected void loop() {
        while (true) {
            Runnable runnable = null;
            try {
                runnable = queue.poll(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                runnable = null;
            }
            if (runnable != null) {
                runnable.run();
            } else {
                // NONE
            }
        }
    }
    /** 接続する */
    public void doConnect() {
        if (this.casMain != null) {
            if (this.status == ANStatus.END) {
                restart(false);
                return; // restart側で実行する
            } else {
                return; // 処理中なので無視する
            }
        }
        //
        // ２回目呼ばれたときのために前の局面をすべて消す
        banmenList.clear();
        //
        // 初期値を入れる
        banmenList.addLast(factory.create(null, null));
        //
        // 初期値を変更する
        setStatus(ANStatus.START);
        //
        // CSAプロトコルで接続する
        casMain = new CSAMainThread(workerImpl);
        Thread t = new Thread(casMain);
        t.setName("CSAMainThread");
        t.start();
    }
    /** 連戦しないようにする */
    public void setStop(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }
    /** 連戦フラグを取得する */
    public boolean getStopFlag() {
        return this.stopFlag;
    }

    /** */
    public void addQueue(Runnable x) {
        this.queue.add(x);
    }

    /** ステータスの取得 */
    public ANStatus getStatus() {
        return this.status;
    }
    /** ステータスの設定 */
    public void setStatus(ANStatus status) {
        this.status = status;
        if (this.status == ANStatus.END) {
            restart(stopFlag);
        }
    }
    /** 再開する */
    public void restart(boolean flag) {
        logger.debug("restart");
        CSAMainThread casMain = this.casMain;
        this.casMain = null;
        if (casMain != null) {
            casMain.close();
        }
        //
        createLog();
        //
        if (! flag) {
            logger.debug("non stop! wait!");
            //
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.debug(e.getMessage());
            }
            // 停止でなければ繰り返す
            setStatus(ANStatus.START);
            doConnect();
        }
    }
    protected void createLog() {
        String logDir = this.getSetting().getString("downloadDir");
        if (logDir == null) {
            return;
        }
        File file = new File(logDir);
        if (! file.isDirectory()) {
            return;
        }
        LocalDateTime nowDate = LocalDateTime.now();
        System.out.println(nowDate); //2020-12-20T13:32:48.293
        // 表示形式を指定
        DateTimeFormatter dtf1 =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss");
        String formatNowDate = dtf1.format(nowDate);
        file = new File(file, "log-" + myTurn + "-" + formatNowDate + ".csa");        
        //
        try (FileWriter fw = new FileWriter(file);
                PrintWriter writer = new PrintWriter(fw); ) {
            logger.debug("log write start");
            writer.println(logBuff.toString());
            logger.debug("log write end");
        } catch (IOException e) {
            logger.error("e=" + e.getMessage());
        }
    }
    
    /** 設定情報の取得 */
    public JSONObject getSetting() {
        return this.setting;
    }
    
    /** 手番が先手か後手か
     * 0 ならば 先手
     * 1 ならば 後手
     */
    private int myTurn;
    
    /** CSAプロトコルで得られた時間(トータルタイム) */
    private Integer totalTime;
    public int getTotalTime() {
        return (totalTime == null)? 0 : totalTime;
    }
    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }
    /** CSAプロトコルで得られた時間(秒読みタイム) */
    private Integer byoyomiTime;
    public int getByoyomiTime() {
        return (byoyomiTime == null)? 0 : byoyomiTime;
    }
    public void setByoyomiTime(int byoyomiTime) {
        this.byoyomiTime = byoyomiTime;
    }
    /** CSAプロトコルで得られた時間(遅延時間) */
    private Integer delayTime;
    public int getDelayTime() {
        return  (delayTime == null)? 0 : delayTime;
    }
    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    /** CSAプロトコルで得られた時間(1手毎の加算時間) */
    private Integer incrementTime;
    public int getIncrementTime() {
        return  (incrementTime == null)? 0 : incrementTime;
    }
    public void setIncrementTime(Integer incrementTime) {
        this.incrementTime = incrementTime;
    }
    
    /** 処理の開始 */
    public void start(
            String senteName,
            String goteName,
            int myTurn,
            Integer totalTime,
            Integer byoyomiTime,
            Integer delayTime,
            Integer incrementTime) {
        //
        // 開始時間
        final long startBanmenTime = System.currentTimeMillis();
        //
        // ログを設定する
        logBuff.delete(0, logBuff.length());
        logBuff.append("V2.2\n");
        logBuff.append("N+" + senteName + "\n");
        logBuff.append("N-" + goteName + "\n");
        logBuff.append("PI\n");
        logBuff.append("+\n");
        //
        // 設定の投入
        this.myTurn = myTurn;
        this.totalTime = totalTime;
        this.byoyomiTime = byoyomiTime;
        this.delayTime = delayTime;
        this.incrementTime = incrementTime;
        //
        // 対戦の開始
        fight(startBanmenTime);
    }
    /** 相手から呼ばれる次の一手 */
    public void setNextMove(String teString) {
        // 開始時刻をチェックする
        final long startBanmenTime = System.currentTimeMillis();
        //
        logBuff.append(teString);
        logBuff.append("\n");
        int te = BanmenDefine.changeTeStringToInt(teString);
        //
        // 過去の手としてエンジン用のスレッドが動いてたら止める
        if (engineWaitThread != null) {
            engineWaitThread.interrupt();
            engineWaitThread = null;
        }
        // 過去に動いていたエンジンを止める
        if (posgreEngineRunnable != null) posgreEngineRunnable.stop();
        if (tensorEngineRunnable != null) tensorEngineRunnable.stop();
        if (pnDnEngineRunnable   != null) pnDnEngineRunnable.stop();
        if (posgreEngineRunnable != null) posgreEngineRunnable.join();
        if (tensorEngineRunnable != null) tensorEngineRunnable.join();
        if (pnDnEngineRunnable   != null) pnDnEngineRunnable.join();
        if (posgreEngineRunnable != null) posgreEngineRunnable = null;
        if (tensorEngineRunnable != null) tensorEngineRunnable = null;
        if (pnDnEngineRunnable   != null) pnDnEngineRunnable = null;
        //
        // 本来なら過去情報の検索情報を残すために消したくないが、
        // OutOfMemoryに苦しんでいるので、工場の盤面を全部削除する
        factory.clearAllHash();
        logger.debug("totalMemory1 = {}", Runtime.getRuntime().totalMemory());
        System.gc();
        logger.debug("totalMemory2 = {}", Runtime.getRuntime().totalMemory());
        
        //
        // 打った手を決定
        BanmenNext newBannmen = banmenList.getLast().decisionTe(factory, te);
        banmenList.addLast(newBannmen);
        logger.debug(newBannmen.toString());
        //
        // 対戦の開始
        fight(startBanmenTime);
    }
    
    private Thread engineWaitThread = null;
    private PosgreEngineRunnable posgreEngineRunnable = null;
    private TensorEngineRunnable tensorEngineRunnable = null;
    private PnDnEngineRunnable pnDnEngineRunnable = null;
    
    public void fight(long startBanmenTime) {
        
        // 目標時刻を設定する( 秒読み時間 + 加算時間 )
        long addTime = this.getByoyomiTime() + this.getIncrementTime();
        if (addTime <= 0) {
            addTime = 10; // 10sec
        }
        // 処理に100msec掛かるとして計算する
        long targetTime = startBanmenTime + addTime*1000- 100;
        
        if (casMain == null) {
            return; // CASプロトコルが終わっていれば終了する
        }
        if (banmenList.isEmpty()) {
            // 盤面情報がない
            logger.error("presentBannmen == null");
            return;
        }
        //
        // もしも自身が先手で手番が先手でないか、自身が後手で手番が後手でないなら、自分でないので終了
        if ((banmenList.getLast().getBanmen().getTeban() == 0) != (myTurn == 0)) {
            return;
        }
        // もしも自身が先手で手番が先手か、自身が後手で手番が後手なら手を指す
        //
        // 合法手を探す
        HashMap<Integer,BanmenNext> child = banmenList.getLast().getChild(factory);
        //
        // エンジンを手ごとにインスタンス化する
        posgreEngineRunnable = new PosgreEngineRunnable(aNPostgreO, banmenList, child);
        tensorEngineRunnable = new TensorEngineRunnable(aNModel, factory, banmenList);
        pnDnEngineRunnable = new PnDnEngineRunnable(factory, banmenList);
        //
        // 別スレッドで null にされるのを回避するため変換
        final EngineRunnable posgreEngineLc = this.posgreEngineRunnable;
        final EngineRunnable tensorEngineLc = this.tensorEngineRunnable;
        final EngineRunnable pnDnEngineLc = this.pnDnEngineRunnable;
        //
        // エンジンをまとめて起動する
        posgreEngineLc.start();
        tensorEngineLc.start();
        pnDnEngineLc.start();
        //
        if (banmenList.getLast().isKingWin()) {
            casMain.sendTe(-1); // 入玉勝ち
            return;
        }
        //
        // 負けたときは終了する
        if (child.size() == 0) {
            casMain.sendTe(0);// 指す手がない
            return;
        }
        //
        // 少なくともposgreの読み込みが終わるまで待ち合わせる
        posgreEngineLc.join();
        int te = posgreEngineLc.getTe();
        if ((te != -2) && (0 <= te)) {
            sendTe(te); // posgreから得た値は最優先で採用
            return;
        }
        //
        // 現在の tensorEngineRunnable はMin/Max法を使わず、
        // 1手読みなのですぐ終わるはず。終わるまで待ち合わせる。
        // ※将来的には、engineWaitThreadの中で制御すべき
        tensorEngineLc.join();
        //
        // postgresで手が得られなかった場合、
        // mainスレッドでsleepできないので、
        // スレッドを立てて処理する
        this.engineWaitThread = new Thread(() -> {
            //
            try {
                long nowTime = startBanmenTime;
                while (0 < targetTime - nowTime) {
                    //
                    if (tensorEngineLc.isEnd()
                            && pnDnEngineLc.isEnd()) {
                        break; // 双方とも終了なら、手が確定しているので終了する
                    }
                    // 100msec(1秒単位)にチェックする
                    long waitTime = Math.min(1000, targetTime - nowTime);
                    if (waitTime <= 0) {
                        break; // 時間切れの発生
                    }
                    //
                    // InterruptedExceptionはここから発生
                    logger.debug("sleep time={}", ((targetTime - nowTime) /1000) );
                    Thread.sleep(waitTime);
                    //
                    // 新たな時間で処理を行う
                    nowTime = System.currentTimeMillis();
                }
                //
                // 制限時間内の理想の手を入手
                int tesuji = tensorEngineLc.getTe();
                // 手をサーバへ送信する
                ANShogiO.this.sendTe(tesuji);
                //
            } catch (InterruptedException e) {
                // 割り込みが入った＝停止が掛かったので指してを打つと反則になる
                // そのため、即終了にする
                return;
            } finally {
                // 時間切れのときは動いているので念押しで停止しておく
                tensorEngineLc.stop();
                pnDnEngineLc.stop();
            }
        });
        this.engineWaitThread.setName("sleep thread");
        this.engineWaitThread.start();

    }

    /** 手を送信する */
    private void sendTe(int te) {
        // メインスレッドで指す手を実施する
        ANShogiO.this.addQueue(()->{
            if (te == -2) {
                if (casMain != null) {
                    casMain.sendTe(0); // 指す手がないので投了する
                }
            } else {
                if (casMain != null) {
                    casMain.sendTe(te);
                }
            }
        });
    }
    
    /** 社畜の取得 */
    public CSAWorker2 getCSAWorker2() {
        return workerImpl;
    }
    
    /** 社畜 */
    private CSAWorker2 workerImpl = new CSAWorker2() {

        @Override
        public JSONObject getSetting() {
            return ANShogiO.this.getSetting();
        }
        
        @Override
        public void setStatus(ANStatus status) {
            ANShogiO.this.setStatus(status);
        }

        @Override
        public ANStatus getStatus() {
            return ANShogiO.this.getStatus();
        }

        @Override
        public void start(String senteName, String goteName, int myTurn, Integer totalTime, Integer byoyomiTime, Integer delayTime,
                Integer incrementTime) {
            ANShogiO.this.setStatus(ANStatus.FIGHT);
            ANShogiO.this.addQueue(()->{
                ANShogiO.this.start(
                        senteName,
                        goteName,
                    myTurn,
                    totalTime,
                    byoyomiTime,
                    delayTime,
                    incrementTime);
            });
        }

        @Override
        public void setNextMove(String nextMove) {
            ANShogiO.this.setStatus(ANStatus.FIGHT);
            ANShogiO.this.addQueue(()->{
                ANShogiO.this.setNextMove(nextMove);
            });
        }
        
        /** 接続する */
        @Override
        public void doConnect(boolean stopFlag) {
            //
            ANShogiO.this.addQueue(()->{
                ANShogiO.this.setStop(stopFlag);
                ANShogiO.this.doConnect(); // 接続する
            });
        }
        /** 連戦しないようにする */
        @Override
        public void setStop(boolean stopFlag) {
            ANShogiO.this.stopFlag = stopFlag;
        }
        
        /** ステータスを渡す */
        @Override
        public JSONObject getDisplayStatus() {
            JSONObject obj = new JSONObject();
            obj.put("totalTime", getTotalTime());
            obj.put("byoyomiTime", getByoyomiTime());
            obj.put("delayTime", getDelayTime());
            obj.put("incrementTime", getIncrementTime());
            obj.put("myTurn", myTurn);
            //
            if (! banmenList.isEmpty()) { // 最終盤面がある
                BanmenOnly banmen = banmenList.getLast().getBanmen();
                if (banmen != null) {
                    obj.put("banmen", banmen.getDisplayStatus());
                }
            }
            return obj;
        }

        /** ダウンロードを起動したかフラグ */
        public Thread processFlag = null;
        
        /** ダウンロードフラグの設定 */
        @Override
        public void doProcessFlag(int processNum) {         
            if (getProcessFlag()) {
                // もし生きているなら
                return; // 終了
            }
            //
            // スレッド起動
            // 将棋指しに影響ししないのでキューを通さずに起動する
            if (processNum == 0) {
                processFlag = new Thread(new ANDownloadO(this));
                processFlag.setName("ANDownloadO");
            } else {
                processFlag = new Thread(aNPostgreO);
                processFlag.setName("ANPostgreO");
            }
            processFlag.start();
        }
        /** ダウンロードフラグの取得 */
        @Override
        public boolean getProcessFlag() {
            return (processFlag != null) && processFlag.isAlive();
        }
        
        /** 起動したかフラグ */
        public Thread serverFlag = null;
        @Override
        public void doServerFlag() {
            if (getServerFlag()) {
                // もし生きているなら
                return; // 終了
            }
            // スレッド起動
            // 将棋指しに影響ししないのでキューを通さずに起動する
            //
            // これ動かすと、停止したときのポート上がったままになるので消しました。
            //serverFlag = new Thread(new ANTensorO(this));
            //serverFlag.setName(ANTensorO.class.getSimpleName());
            //serverFlag.start();
            //
            // スレッド起動
            // 将棋指しに影響ししないのでキューを通さずに起動する
            serverFlag = new Thread(new ANShogiServerO(this));
            serverFlag.setName(ANShogiServerO.class.getSimpleName());
            serverFlag.start();
        }

        /** サーバフラグ */
        @Override
        public boolean getServerFlag() {
            return (serverFlag != null) && serverFlag.isAlive();
        }
    };
}
