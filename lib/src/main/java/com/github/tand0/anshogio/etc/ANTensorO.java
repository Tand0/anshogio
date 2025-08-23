package com.github.tand0.anshogio.etc;

import com.github.tand0.anshogio.CSAWorker;

/** TensorFlowサーバとアクセスする
 * @author A.N. TensorFlow王
 */
public class ANTensorO extends ANShogiServerO {
	
    /** コンストラクタ
     * 
     * @param parent 社畜
     */
    public ANTensorO(CSAWorker parent) {
    	super(parent);
    }
    
    @Override
    public String getDir() {
    	return "tensor.dir";
    }

    @Override
    public String getName() {
    	return "tensor.name";
    }
}
