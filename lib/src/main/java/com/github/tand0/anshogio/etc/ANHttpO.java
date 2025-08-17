package com.github.tand0.anshogio.etc;

import com.github.tand0.anshogio.CSAWorker2;
import com.github.tand0.anshogio.log.StackAppender;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONObject;
import com.github.tand0.anshogio.ANStatus;

public class ANHttpO implements HttpHandler {

    /** 将棋データの保存場所 */
    protected final CSAWorker2 worker;

    /** コンストラクタ */
    public ANHttpO(CSAWorker2 worker) {
        this.worker = worker;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // リクエストボディを取得
            JSONObject request = null;
            try (InputStream is = exchange.getRequestBody()) {
                byte[] b = is.readAllBytes();
                if (b.length != 0) {
                    String b2 = new String(b, StandardCharsets.UTF_8);
                    request = new JSONObject(b2);
                } else {
                    request = new JSONObject();
                    
                }
            } catch (org.json.JSONException e) {
                request = new JSONObject();
                request.append("error",e.getMessage());
            }

            int statusCode = 200;
            String urlString = exchange.getRequestURI().toString();
            JSONObject response = new JSONObject();
            if (urlString.equals("/")) {
                statusCode = 301;
            } else if (urlString.equals("/state")) {
                    doStatus(request, response);
            } else if (urlString.equals("/download")) {
                doDownload(request, response);
            } else if (urlString.equals("/postgres")) {
                doPostgres(request, response);
            } else if (urlString.equals("/connectOne")) {
                doConnect(request, response, true);
            } else if (urlString.equals("/connect")) {
                doConnect(request, response, false);
            } else if (urlString.equals("/stop")) {
                doStop(request, response);
            } else if (urlString.equals("/server")) {
                doServer(request, response);
            } else if (urlString.equals("/log")) {
                doLog(request, response);
            } else if (0 == urlString.indexOf("/flutter")) {
                File file = doFlutter(urlString);
                if (file == null) {
                    // ファイルが存在しない
                    statusCode = 404; //404 Not Found
                } else if (file.getName().equals("ip.json")) {
                    String serverName = this.worker.getSetting().getString("server.name");
                    int serverPort = this.worker.getSetting().getInt("server.port");
                    String serverData = "http://" + serverName + ":" + serverPort;
                    response.put("url", serverData);
                } else {
                    //
                    String mime = getMime(file);
                    // 
                    // レスポンスヘッダを作成
                    long contentLength = file.length();
                    // Content-Length 以外のレスポンスヘッダを設定
                    Headers resHeaders = exchange.getResponseHeaders();
                    resHeaders.set("Content-Type", mime);
                    exchange.sendResponseHeaders(statusCode, contentLength);

                    // レスポンスボディを送信
                    byte[] b = new byte[1024];
                    try (FileInputStream fis = new FileInputStream(file);
                        OutputStream os = exchange.getResponseBody()) {
                        while (true) {
                            int len = fis.read(b);
                            if (len <= 0) {
                                break;
                            }
                            os.write(b, 0, len);
                        }
                    }
                    return;
                }
            } else {
                statusCode = 400;
            }
            // 状態の設定
            response.put("state", this.getStatusInt(this.worker.getStatus()));
            response.put("url", urlString);
            response.put("stateString", this.worker.getStatus().toString());
            //
            // Content-Length 以外のレスポンスヘッダを設定
            Headers resHeaders = exchange.getResponseHeaders();
            // 戻り値はJSONを指定
            resHeaders.set("Content-Type", "application/json");
            // CORSに対応
            resHeaders.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
            resHeaders.add("Access-Control-Allow-Origin","*");
            if (statusCode == 301) {
                resHeaders.add("location","/flutter");
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            // レスポンスヘッダを作成
            byte[] resultByte = response.toString().getBytes();
            long contentLength = resultByte.length;
            exchange.sendResponseHeaders(statusCode, contentLength);

            // レスポンスボディを送信
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(resultByte);
            }
        } finally {
            // 閉じる
            exchange.close();
        }
    }
    protected void doStatus(JSONObject req, JSONObject res) {
        JSONObject result = worker.getDisplayStatus();
        res.put("result", result);
    }
    protected void doPostgres(JSONObject req, JSONObject res) {
        if (this.worker.getProcessFlag()) {
            res.put("result", "Already Go");
            return;
        }
        // ダウンロード開始
        this.worker.doProcessFlag(1);
        //
        res.put("result", "Postgres Start");
    }
    protected void doDownload(JSONObject req, JSONObject res) {
        if (this.worker.getProcessFlag()) {
            res.put("result", "Already Go");
            return;
        }
        // ダウンロード開始
        this.worker.doProcessFlag(0);
        //
        res.put("result", "Download Start");
    }
    protected void doServer(JSONObject req, JSONObject res) {
        if (this.worker.getServerFlag()) {
            res.put("result", "Already Go");
            return;
        }
        // ダウンロード開始
        this.worker.doServerFlag();
        //
        res.put("result", "Server Start");
    }
    protected void doConnect(JSONObject req, JSONObject res, boolean stopFlag) {
        // 接続実施
        this.worker.doConnect(stopFlag);
        // コネクトしてね
        res.put("result", "OK");
    }
    protected void doStop(JSONObject req, JSONObject res) {
        // 接続実施
        this.worker.setStop(true);
        // コネクトしてね
        res.put("result", "OK");
    }
    protected void doLog(JSONObject req, JSONObject res) {
        res.put("log", StackAppender.getResult());
    }
    
    /** ステータスの取得 */
    public int getStatusInt(ANStatus status) {
        int ans = 0;
        switch (status) {
        case START:
            ans = 1;
            break;
        case FIGHT:
            ans = 2;
            break;
        case END:
            ans = 3;
            break;
        case ERROR:
            ans = 4;
            break;
        default:
            ans = 5;
        }
        return ans;
    }
    protected File doFlutter(String fromURL) {
        // 先頭のフォルダ名がいたら取る
        fromURL = fromURL.substring("/flutter".length());
        if (0 == fromURL.indexOf("/")) {
            // 先頭が / で始まったら取る
            fromURL = fromURL.substring("/".length());
        }
        if (0 <= fromURL.indexOf("..")) {
            // 戻るとセキュリティ的にまずそうなので終了
            return null;
        }
        if (fromURL.equals("")) {
            // 空なら index.html を補完する
            fromURL = "index.html";
        }
        String serverDir = this.worker.getSetting().getString("fliutter.dir");
        File serverFolder = new File(serverDir);
        if (! serverFolder.isDirectory()) {
            return null; // 元の親フォルダがフォルダでない
        }
        File targetFile = new File(serverFolder,fromURL);
        if (! targetFile.isFile()) {
            return null; // 元のファイルがファイルでない
        }
        return targetFile;
    }
    protected String getMime(File file) {
        String mime;
        String name = file.getName();
        String ext = name.substring(name.lastIndexOf("."));
        switch (ext) {
        case ".json":
            mime = "application/json";
            break;
        case ".png":
            mime = "image/png";
            break;
        case ".jpg":
        case ".jpeg":
            mime = "image/jpeg";
            break;
        case ".gif":
            mime = "image/gif";
            break;
        case ".htm":
        case ".html":
            mime = "text/html";
            break;
        case ".otf":
            mime = "font/otf";
            break;
        case ".ttf":
            mime = "font/ttf";
            break;
        case ".js":
            mime = "text/javascript";
            break;
        default:
            mime = "text/plain";
            break;
        }
        return mime;
    }
}
