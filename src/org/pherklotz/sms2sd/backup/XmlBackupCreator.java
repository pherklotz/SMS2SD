/*
 * (c) by Peter Herklotz 2012
 * Email: dev@cloudeta.com 
 * Lizenz: GNU GPLv3
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.pherklotz.sms2sd.backup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author Peter
 * 
 */
public class XmlBackupCreator implements SmsBackupCreator {

	private BufferedWriter out;

	@Override
	public void setOutputstream(final OutputStream out) {
		this.out = new BufferedWriter(new OutputStreamWriter(out));
	}

	@Override
	public void startBackup() {
		try {
			out.append("<messages>\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeRecord(final String address, final String date,
			final String type, final String text) {

		try {
			out.write("\t<msg address='");
			out.write(address);
			out.write("' date='");
			out.write(date);
			out.write("' type='");
			out.write(type);
			out.write("'>\n");
			out.write("\t\t");
			out.write(text);
			out.write("\n");
			out.write("\t</msg>\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void finishBackup() {
		try {
			out.append("</messages>\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
