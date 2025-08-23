package com.github.tand0.anshogio.etc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tand0.anshogio.CSAWorker;


/** ダウンロードする
 * @author A.N.ダウンロード王
 */
public class ANDownloadO implements Runnable {

	/** ダウンロードフォルダ */
	private final static String DOWNLLOAD_DIR = "download.dir";
	
	/** ターゲットとする年 */
	protected final static String DOWNLLOAD_YEAR_START = "target.year";
	
    /** ロガーの位置 */
    private final static Logger logger = LoggerFactory.getLogger(ANDownloadO.class);

    /** 社畜 */
    private final CSAWorker parent;
    
    /** コンストラクタ
     * @param parent 社畜やデータです
     */
    public ANDownloadO(CSAWorker parent) {
    	this.parent = parent;
    }
    
    @Override
    public void run() {
        logger.trace("run start");
        final String downloadDir;
        if (this.parent.getSetting().has(DOWNLLOAD_DIR)) {
        	downloadDir = this.parent.getSetting().getString(DOWNLLOAD_DIR);
        } else {
        	downloadDir = "./log";
        }
        File saveFile = new File(downloadDir);
        if (!saveFile.exists()) {
            logger.error("save dir not found s={}",downloadDir);
            return;
        }
        // 現在の日を取得
        Calendar nowCalendar = Calendar.getInstance();
        int targetYearEnd = nowCalendar.get(Calendar.YEAR);
        int targetMonthEnd = nowCalendar.get(Calendar.MONTH) + 1;
        int targetDateEnd = nowCalendar.get(Calendar.DATE);
        //
        final int targetYearStart;
        if (this.parent.getSetting().has(DOWNLLOAD_YEAR_START)) {
        	targetYearStart = this.parent.getSetting().getInt(DOWNLLOAD_YEAR_START);
        } else {
        	targetYearStart = targetYearEnd;
        }
        // スタートを設定
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,targetYearStart);
        cal.set(Calendar.MONTH,0);
        cal.set(Calendar.DATE,1);
                
        while (true) {
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int date = cal.get(Calendar.DATE);
            String stringYear = String.format(Locale.JAPANESE,"%04d", year);
            File saveYear = new File(downloadDir,stringYear);
            if (targetYearEnd < year) {
                break;
            } else if (targetYearEnd == year) {
            	if (targetMonthEnd < month) {
            		break;
            	} else if (targetMonthEnd == month) {
            		if (targetDateEnd <= date) {
            			break;
            		}
            	}
            }
            try {
                if (!saveYear.exists()) {
                    boolean isDirectoryCreated = saveYear.mkdir();
                    logger.debug("save year create flag={}",isDirectoryCreated);
                }
                String stringMonth = String.format(Locale.JAPANESE,"%02d", month);
                File saveMonth = new File(saveYear,stringMonth);
                if (!saveMonth.exists()) {
                    boolean isDirectoryCreated = saveMonth.mkdir();
                    logger.debug("save month create flag={}",isDirectoryCreated);
                }
                String stringDate = String.format(Locale.JAPANESE,"%02d", date);
                File saveDate = new File(saveMonth,stringDate);
                if (saveDate.exists()) {
                    logger.debug(String.format(Locale.JAPANESE,"already create(%04d/%02d/%02d)", year,month,date));
                    continue;
                }
                boolean isDirectoryCreated = saveDate.mkdir();
                logger.debug("save date create flag={}",isDirectoryCreated);
                File index = new File(saveDate,"index.html");
                URL url = URI.create("http://wdoor.c.u-tokyo.ac.jp/shogi/LATEST/"
                        + stringYear + "/" + stringMonth + "/" + stringDate + "/").toURL();
                createIndex(url,index);
                //
                if (! index.exists()) {
                    logger.error("index not found index={}",index);
                    return;
                }
                try (FileInputStream fis = new FileInputStream(index);
                     InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                     BufferedReader reader = new BufferedReader(isr)) {
                    String string;
                    List<String> lines = new ArrayList<>();
                    while ((string = reader.readLine()) != null) {
                        lines.add(string);
                    }
                    lines.forEach(str->{
                        final Pattern p = Pattern.compile("href=\"([^\"]+\\.csa)\""); //�d�b�ԍ�
                        final Matcher m = p.matcher(str);
                        if (!m.find()) {
                            return;
                        }
                        String target = m.group(1);
                        if (target == null) {
                            return;
                        }
                        File saveTarget = new File(saveDate,target);
                        try {
                            URL loadURL = URI.create(url + target).toURL();
                            createIndex(loadURL,saveTarget);
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                logger.error(e.getMessage());
                return;
            } catch (IOException e) {
                logger.error(e.getMessage());
            } finally {
                cal.add(Calendar.DATE,1);
                //
            }
        }
        //
        logger.trace("download end");
    }
    /**
     * インデックスを生成する
     * @param url URL
     * @param file ファイル
     * @throws IOException 例外
     */
    private void createIndex(URL url,File file) throws IOException {
        if (file.exists()) {
            logger.debug("skip={}", file);
            return;
        }
        logger.debug(url.toString());
        // logger.debug("file={}", file);
        //
        try (DataInputStream in = new DataInputStream(url.openStream());
             FileOutputStream fos = new FileOutputStream(file);
             DataOutputStream out = new DataOutputStream(fos)) {

            final byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
        }
    }
}
