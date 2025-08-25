package com.github.tand0.anshogio.etc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.util.BanmenKey;
import com.github.tand0.anshogio.util.BanmenOnly;

/**
 * 定跡ファイルを読み込んでデータベースへ書き込む
 * @author A.N.DB 定跡王
 */
public class ANDbJosekiO extends ANPostgreO {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANDbJosekiO.class);
    
    /**
     * コンストラクタ
     * @param setting 設定ファイル
     */
    public ANDbJosekiO(JSONObject setting) {
        super(setting);
    }
    /** チェックする拡張子 */
    @Override
    public String getExtention() {
        return ".db";
    }

    /** 盤面のhash */
    public HashSet<ResultKey> keySet = new HashSet<>();
    
    /** ログフォルダにあるファイルを読み込んでDBに押し込む */
    @Override
    public void run() {
        logger.debug("run start!");
        try {
            //
            connect();
            init();
            //
            String downloadDir = getSetting().getString("download.book");
            //
            runDbFile(new File(downloadDir));
            logger.debug("size={}", keySet.size());
            //
            for (ResultKey rKey : keySet) {
                addKey(rKey.key,rKey.win,rKey.loss,rKey.joseki,false);
            }
            //
        } catch (SQLException e){
            logger.error("e=" + e.getMessage());
            close();
        } catch (IOException e) {
            logger.error("e=" + e.getMessage());
        } finally {
            close();
        }
        logger.debug("run over!");  
    }
    
    /**
     * ファイルを検索する。フォルダがあったら再帰する
     * @param dir フォルダ
     * @throws IOException 例外
     */
    public void runDbFile(File dir ) throws IOException {
        if (dir.isDirectory()) {
            for (File nextFile : dir.listFiles()) {
                runDbFile(nextFile);
            }
            return; //フォルダチェック後は終了
        }
        String extension = dir.getName();
        extension = extension.substring(extension.lastIndexOf("."));
        if (!extension.equals(getExtention())) {
            return; // 拡張子が違う
        }
        //
        // ファイル１個分の処理
        runDbFileOne(dir);
    }
    /**
     * ファイル一つ分の処理
     * @param file ファイル
     * @throws IOException 例外
     */
    public void runDbFileOne(File file) throws IOException {
        // 1以上で定跡
        int joseki = 1;
        //
        logger.debug("f={}",file.getAbsolutePath());
        try (FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr)) {
            String string;
            BanmenOnly only = new BanmenOnly();
            while ((string = br.readLine()) != null) {
                if (string.trim().equals("")) {
                    continue; // 空文
                } else if (0 <= string.trim().indexOf("#")) {
                    continue; // コメント
                } else if (0 <= string.indexOf("sfen")) {
                    only = BanmenOnly.createSfen(string);
                    BanmenKey key = new BanmenKey(only);
                    int teban = key.getTeban(); // 先手0、後手1
                    // 評価値を見るとき、手を打った後の盤面を見るので、先手番0なら後手1の勝率を上げる。
                    // 例：初期局面は先手0番、７七歩を打った時は後手1番⇒後手1で7七歩の時winを高くする
                    int win = teban * 1000; // 自分の番の処理数をあげる
                    int loss = (1 - teban) * 1000; // 自分の番の処理数をあげる
                    //
                    // 良く指される手として両方ともに1,000づつ入れる
                    // すると評価値は0.0に近づくので結局winが1個とかのマイナーな手を指されがち。
                    //int win = 1000;
                    //int loss = 1000;
                    //
                     // 0以外なら定跡
                    ResultKey rKey = new ResultKey(key.toString(),win,loss,joseki);
                    keySet.add(rKey);
                    joseki++; // 後ろの手の方が優先順位は低い
                } else {
                    // 手が指される
                    int te = only.changeUsiTeToInt(string);
                    only = new BanmenOnly(only,te);
                }
            }
        }
    }   
}
