package kakaobank.core.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TraceLog {
	public static void SetDebug(String name, String log) {
		//로그저장
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		String strDate = formatter.format(new Date());
		
		String sFilename = "/home/kakaobank_project/" + strDate + ".log"; //로그파일 년월일.log

		try {
			File f = new File(sFilename);
			if (!f.exists()) {
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(sFilename, true);
			BufferedWriter bw = new BufferedWriter(fw);
			String msg = String.valueOf(new Date()) + " [" + name + "] " + log;
			bw.write(msg + "\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
