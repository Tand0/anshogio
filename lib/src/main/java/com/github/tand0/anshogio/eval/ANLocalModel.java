package com.github.tand0.anshogio.eval;


import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Result;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;


/** tensor のサーバにアクセスしてキー値から値を得る */
public class ANLocalModel extends ANModel {
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANLocalModel.class);

    /** モデル情報 */
    private final SavedModelBundle model;
    
    /** セッション情報 */
    private final Session session;
    
    /**
     * コンストラクタ
     * @param setting 設定
     */
	public ANLocalModel(JSONObject setting) {
	    super(setting);
	    this.model = SavedModelBundle.load(getTarget());
	    this.session = this.model.session();
	}
	/**
	 * ターゲットとなるファイル名
	 * @return ファイル名
	 */
	public String getTarget() {
	    return getSetting().getString("tensor.model");
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
        try (Tensor input = makeInputTensor(key)) {
            Session.Runner runner = this.session.runner();
            //
            // 名前は以下のコマンドで確認する
            // saved_model_cli show --dir next_model --all
            runner = runner.feed("serve_input_model:0", input);
            runner = runner.fetch("StatefulPartitionedCall:0");
            //
            Result result = runner.run();
            try (Tensor output = result.get(0)) {
                if (output instanceof TFloat32) {
                    float answer = ((TFloat32)output).getFloat(0,0);
                    answer = Math.min(1.0f, Math.max(-1.0f, answer));
                    return answer;
                }
            }
        }
        return null;
    }
	
    
    /** インプットを作る
     * 
     * @param key key値
     * @return インプット情報
     */
    private Tensor makeInputTensor(String key) {
        float[] target = new float[256];
        int pos = 0;
        for (int i = 0 ; i < key.length(); i++) {
            int charKey;
            if (('0' <= key.charAt(i)) && (key.charAt(i) <= '9')) {
                charKey = key.charAt(i) - '0';
            } else if (('a' <= key.charAt(i)) && (key.charAt(i) <= 'z')) {
                charKey = key.charAt(i) - 'a' + 10;
            } else {
                throw new java.lang.UnsupportedOperationException();
            }
            target[pos + 0] = (int) ((charKey/8)%2) == 0 ? 0.0f : 1.0f;
            target[pos + 1] = (int) ((charKey/4)%2) == 0 ? 0.0f : 1.0f;
            target[pos + 2] = (int) ((charKey/2)%2) == 0 ? 0.0f : 1.0f;
            target[pos + 3] = (int) ((charKey  )%2) == 0 ? 0.0f : 1.0f;
            pos += 4;
        }
        /*
        for (int i = 0 ; i < target.length ; i++) {
            System.out.print(((int)target[i]) + ", ");
            if ((i % 4) == 3) {
                System.out.println(" // key=" + key.charAt(i/4));
            }
        }
        */
        TFloat32 x = TFloat32.tensorOf(Shape.of(1,256));
        for (int i = 0 ; i < 256 ; i++) {
            x.setFloat(target[i],  0, i);
        }
        return x;
    }
}
