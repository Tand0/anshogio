package com.github.tand0.anshogio.etc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.engine.EngineRunnable;
import com.github.tand0.anshogio.engine.PnDnEngineRunnable;
import com.github.tand0.anshogio.util.BanmenFactory;
import com.github.tand0.anshogio.util.BanmenNext;

/**
 * postgresへのアクセス用のテスト
 */
public class ANPostgreOTest {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANPostgreOTest.class);
	
    /** コンストラクタ */
    public ANPostgreOTest() {
    }
    /**
     * まずは、普通に postgres が呼び出せるかの確認を行う
     */
	@Test
	public void mainTest() {
		JSONObject obj = new JSONObject();
		obj.put("postgres.url", "jdbc:postgresql://127.0.0.1:5432/anshogio");
		obj.put("postgres.user", "postgres");
		obj.put("postgres.passwd", "postgres");
		ANPostgreO data = new ANDbUpgradeO(obj);
		try {
			data.connect(); // 接続
			data.init(); // 初期化
			//
			String key1 = "key1";
			data.addKey(key1, 1, 2, 0);
			data.addKey(key1, 1, 2, 0);
			//
			ReslutWinLoss winLoss = data.readKey(key1);
			logger.debug("key1=" + key1 + ",win=" + winLoss.win + " loss=" + winLoss.loss);
			//
			int len = data.getKeyLen();
			logger.debug("len=" + len);
			//
			int index = len -1;
			ResultKey resultKey = data.readKey(index);
			logger.debug("key=" + resultKey.key + ",win=" + resultKey.win + " loss=" + resultKey.loss);
			//
			data.close();
			//
		} catch (SQLException e){
			logger.debug(e.getMessage());
			data.close();
			return;
		}
	}

	/**
	 * 実践データを使って詰みを検出できるかの評価 
	 * @throws IOException 例外
	 * @throws SQLException 例外
	 */
	@Test
	public void tumeshogiTest() throws IOException, SQLException {
		//
		tumeEngine("test_01_tume.txt", true, 0);
		tumeEngine("test_02_tume.txt", true, 0);
		tumeEngine("test_03_tume.txt", true, 1);
		tumeEngine("test_04_tume.txt", true , 0);
		tumeEngine("test_05_tume.txt", true , 0);
		tumeEngine("test_06_tume.txt", true , 1);
		tumeEngine("test_07_tume.txt", true , 1);
		tumeEngine("test_08_tume.txt", true , 0);
		//
	}
	/**
	 * 詰めろエンジンが正しく動いているか確認する
	 * @param fileNameString 詰めろファイル
	 * @param tume 詰め将棋なら true
	 * @param sente どちらが勝つか？ 0 先手, 1 後手
	 * @return ANPostgreO の Mock用ファイル
	 * @throws IOException 例外パターン
	 * @throws SQLException 例外パターン
	 */
    protected ANPostgreOMock tumeEngine(String fileNameString, boolean tume,int sente) throws IOException, SQLException {
        BanmenFactory factory = new BanmenFactory();
        //
        // クラスローダを使ってクラスパスからの相対パスでファイルを指定
        String fileName =
                ANPostgreOTest.class.getClassLoader().getResource(fileNameString).getPath();
        File file = new File(fileName);
        ANPostgreOMock mock = new ANPostgreOMock(null);
        BanmenNext next = mock.runFileOne(factory, file);
        assertNotNull(next);
        LinkedList<BanmenNext> banmenList = new LinkedList<>();
        banmenList.addLast(next);
        //
        if (tume) {
            EngineRunnable engine = new PnDnEngineRunnable(factory,banmenList).start();
            //
            // 終わるまで待ち合わせる
            engine.join();
            //
            assertNotNull(next);
            // 先手か後手かのどちらかが詰んでいる
            assertTrue((next.getPnDn(0).pn == 0) || (next.getPnDn(1).pn == 0));
        }
        return mock;
    }
}
