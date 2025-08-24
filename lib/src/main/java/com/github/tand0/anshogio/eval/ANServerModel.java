package com.github.tand0.anshogio.eval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** tensor のサーバにアクセスしてキー値から値を得る */
public class ANServerModel extends ANModel {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANServerModel.class);

    /**
     * コンストラクタ
     * @param setting 設定
     */
	public ANServerModel(JSONObject setting) {
	    super(setting);
	}

	/**
	 * key値から評価値を取得
	 * @param key key値
	 * @return 評価値
	 */
	@Override
	public Float getKey(String key) {
		if (!isAlive()) {
			logger.error("disconnect!");
			return null;
		}
		Float result;
		//
		HttpURLConnection uc = null;
		try {
            //
            String targetUrl = this.getSetting().getString("tensor.url") + "/" + key;
            URL url = URI.create(targetUrl).toURL();
            uc = (HttpURLConnection) url.openConnection();
            // POST可能にする
            uc.setRequestMethod("GET");
            uc.setDoOutput(true);//送信許可
            uc.setDoInput(true); //受信許可
            //
            // 接続
            uc.connect();
            //
            int responseCode = uc.getResponseCode();
            if (responseCode != 200) {
                this.setDead();
                throw new IOException("IOException status=" + responseCode);
            }
            // 結果の読み込み
            try (InputStream is = uc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr)) {
                String strLine;
                StringBuilder sbSentence = new StringBuilder();
                while ((strLine = br.readLine()) != null) {
                    sbSentence.append(strLine);
                }
                JSONObject json = new JSONObject(sbSentence.toString());
                result = json.getFloat("result");
                //
                // 上限値設定
                result = Math.min(1.0f, Math.max(-1.0f, result));
            }
		} catch (IOException|JSONException e) {
			logger.error("e=" + e.getMessage());
			this.setDead(); // なんかエラーが出た
        	return null;
		} finally {
			if (uc != null) {
				uc.disconnect();
			}
		}
		return result;
	}
}
