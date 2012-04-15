/*
 * (c) by Peter Herklotz 2012
 * Email: dev@cloudeta.com 
 * Lizenz: GNU GPLv3
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.pherklotz.sms2sd.backup;

import java.io.OutputStream;

/**
 * @author Peter
 * 
 */
public interface SmsBackupCreator {

	void setOutputstream(OutputStream out);

	void startBackup();

	void writeRecord(String address, String date, String type, String text);

	void finishBackup();

}
