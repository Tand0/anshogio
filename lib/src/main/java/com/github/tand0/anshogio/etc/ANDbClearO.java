package com.github.tand0.anshogio.etc;

import java.sql.SQLException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** データベースのテーブルをクリアする
 * @author A.N.DB クリア王
 */
public class ANDbClearO extends ANPostgreO  {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANDbClearO.class);
    
    /**
     * コンストラクタ
     * @param setting 設定情報
     */
    public ANDbClearO(JSONObject setting) {
        super(setting);
    }
    /** ログフォルダにあるファイルを読み込んでDBに押し込む */
    @Override
    public void run() {
        logger.debug("run start!");
        try {
            //
            connect();
            dbClear();
            //
        } catch (SQLException e){
            logger.error("e=" + e.getMessage());
            close();
        } finally {
            close();
        }
        logger.debug("run over!");  
    }
}
