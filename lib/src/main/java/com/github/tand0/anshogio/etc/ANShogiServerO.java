package com.github.tand0.anshogio.etc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.CSAWorker2;

/** ロードする */
public class ANShogiServerO implements Runnable {
	
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANShogiServerO.class);

    private final CSAWorker2 parent;
    
    public String getDir() {
    	return "AriShogiServer.dir";
    }
    public String getName() {
    	return "AriShogiServer.name";
    }
    
    /** 親データ */
    public ANShogiServerO(CSAWorker2 parent) {
    	this.parent = parent;
    }

	@Override
	public void run() {
		logger.debug("server start!");
		JSONObject obj = parent.getSetting();
	    final String dirString;
	    final String command;
		try {
			dirString = obj.getString(getDir());
		    command = obj.getString(getName());
		} catch(JSONException e) {
			// キーがなかったら終了
			logger.error(e.getMessage());
			return;
		}
	    File dir = new File(dirString);
		//
		String[] commands = command.split(" ");
		logger.debug("docommand c=" + command);
		ProcessBuilder p = new ProcessBuilder(commands)
				.directory(dir)
				.redirectErrorStream(true);
		try {
			Process process = p.start();
			Charset df = Charset.defaultCharset();
	        try (   InputStream is = process.getInputStream();
	        		InputStreamReader isr = new InputStreamReader(is, df);
	        		BufferedReader r = new BufferedReader(isr)) {
	            String line;
	            while ((line = r.readLine()) != null) {
	            	logger.debug(line);
	            }
	        }
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		logger.debug("server end!");
	}
}
