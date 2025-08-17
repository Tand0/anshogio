package com.github.tand0.anshogio.etc;

import com.github.tand0.anshogio.CSAWorker2;

/** ロードする */
public class ANTensorO extends ANShogiServerO {
	
    /** 親データ */
    public ANTensorO(CSAWorker2 parent) {
    	super(parent);
    }
    public String getDir() {
    	return "tensor.dir";
    }
    public String getName() {
    	return "tensor.name";
    }
}
