package com.github.tand0.anshogio.eval;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenOnly;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.github.tand0.anshogio.ANShogiO;

/** ロードする */
public class ANServerModelTest {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANServerModelTest.class);
    

    /** コンストラクタ */
    public ANServerModelTest() {
    }
    
    /** データが取れることを確認する 例外が出なければＯＫ 
     * @throws IOException 例外が発生した。テストとしてはダメです
     * @throws InterruptedException 割り込みが入った。テストとしてはダメです
     * @throws URISyntaxException URLの変換失敗。テストとしてはダメです。
     */
    @Test
    public void mainTest() throws IOException, InterruptedException, URISyntaxException {
        ANShogiO mock = new ANShogiO();
        mock.createSetting();
        //
        // 観賞用のサーバを起動する
        String uriString = mock.getSetting().getString("tensor.url");
        URI uri = new URI(uriString);
        logger.debug("server port={}",uri.getPort());
        HttpServer server;
        server = HttpServer.create(new InetSocketAddress(uri.getPort()), 0);
        server.createContext("/", new Working());
        server.start();
        //
        Thread.sleep(5 *1000); // wait to invoked HttpServer.
        //
        //
        // 初期情報を作成する
        BanmenKey key = new BanmenKey(new BanmenOnly());
        //
        // モデルはちゃんと動きますか？
        ANModel aNModel = new ANServerModel(mock.getSetting());
        Float result = aNModel.getKey(key.toString());
        //
        assertNotNull(result);
        logger.debug("reslut=" + result);
        //
        server.stop(0);
        //
        Thread.sleep(3 *1000); // wait for HttpServer
    }
    
    /**
     * サーバ側の模擬
     */
    class Working implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logger.debug("handle");
            // レスポンスヘッダを作成
            JSONObject response = new JSONObject();
            response.put("result", -0.2012939602136612f);
            byte[] resultByte = response.toString().getBytes();
            long contentLength = resultByte.length;
            exchange.sendResponseHeaders(200, contentLength);

            // レスポンスボディを送信
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resultByte);
            }
            logger.debug("handle end");
        }
    }
}
