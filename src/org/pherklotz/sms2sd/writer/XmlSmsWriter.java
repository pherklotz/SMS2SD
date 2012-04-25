/*
 * (c) by Peter Herklotz 2012
 * Email: dev@cloudeta.com 
 * Lizenz: GNU GPLv3
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.pherklotz.sms2sd.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Writes text messages in an XML file. Structure:</br/>
 * 
 * <pre>
 * 	&lt;messages&gt;
 * 		&lt;msg address='0123456789' date='2374682347' type='INBOX'&gt;
 * 			This is the message body.
 * 		&lt;/msg&gt;
 * 		...
 * 	&lt;/messages&gt;
 * </pre>
 * 
 * @author pherklotz
 */
public class XmlSmsWriter implements SmsWriter {

	private BufferedWriter out;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOutputstream(final OutputStream out) {
		this.out = new BufferedWriter(new OutputStreamWriter(out));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startBackup() {
		try {
			out.append("<messages>\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
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
