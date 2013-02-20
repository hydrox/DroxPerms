package de.hydrox.bukkit.DroxPerms.data;

import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Logs transactions of timed tracks and subgroups
 * @author James
 *
 */
public class TransactionLogger extends Handler {

	BufferedWriter bw;
	public TransactionLogger(File file) throws IOException{
		bw = new BufferedWriter(new FileWriter(file,true));

	}

	@Override
	public void publish(LogRecord record) {
		SimpleDateFormat d = new SimpleDateFormat();
		String rec = d.format(new Date(record.getMillis()));
		rec += " :: [" + record.getLevel().getLocalizedName() + "] ";
		rec += record.getMessage();
		try {
			bw.write(rec + "\n");
			flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		try {
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws SecurityException {
		// TODO Auto-generated method stub
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
