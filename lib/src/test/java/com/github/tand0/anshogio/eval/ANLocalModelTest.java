package com.github.tand0.anshogio.eval;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.tensorflow.TensorFlow;

/**
 * tensorflow のテスト
 */
public class ANLocalModelTest {
    /**
     * Tensorflow の評価用Mock
     */
    public class ANLocalModelMock extends ANLocalModel {
        /**
         * コンストラクタ
         * @param setting 設定ファイル
         */
        public ANLocalModelMock(JSONObject setting) {
            super(setting);
        }
        @Override
        public String getTarget() {
            return "./ANShogiPy/next_model_binal";
        }
    }
    
    /**
     * tensorflow のテスト本体(エラーにならなければOK)
     */
    @Test
    public void mainTest() {
        ANLocalModelMock mock = new ANLocalModelMock(null);
        //
        System.out.println("Hello TensorFlow " + TensorFlow.version());
        //
        for (int i = 0 ; i < 16 ; i++) {
            String key = String.format("%064x", i);
            Float result = mock.getKey(key);
            assertNotNull(result);
            System.out.println("key=" + key);
            System.out.println("  i=" + i + " result=" + result);
        }
    }
}
