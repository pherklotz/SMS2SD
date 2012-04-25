/*
 * (c) by Peter Herklotz 2012
 * Email: dev@cloudeta.com 
 * Lizenz: GNU GPLv3
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.pherklotz.sms2sd.writer;

import java.io.OutputStream;

/**
 * Implement that interface to write messages in an special output format.
 * 
 * @author pherklotz
 */
public interface SmsWriter {

	/**
	 * Set the {@link OutputStream}. In this stream the writer should write the
	 * messages.
	 * 
	 * @param out
	 *            OutputStream.
	 */
	void setOutputstream(OutputStream out);

	/**
	 * Start the backup with a call of this method. The implementation can write
	 * some header information.
	 */
	void startBackup();

	/**
	 * Writes an text message.
	 * 
	 * @param address
	 *            phone number of the receiver.
	 * @param date
	 *            delivery date.
	 * @param type
	 *            type of the message folder ("INBOX" or "OUTBOX").
	 * @param text
	 *            content of the message.
	 */
	void writeRecord(String address, String date, String type, String text);

	/**
	 * Finish the backup with a call of this method. The implementation can
	 * write some footer information.
	 */
	void finishBackup();

}
