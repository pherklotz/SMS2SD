/*
 * (c) by Peter Herklotz 2012
 * Email: dev@cloudeta.com 
 * Lizenz: GNU GPLv3
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.pherklotz.sms2sd;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author Peter
 * 
 */
public class SmsAccessManager {

	public final String[] PROJECTION = new String[] { "address", "body", "date" };

	private ContentResolver contentResolver;

	public enum SmsFolder {

		OUTBOX("sent"), INBOX("inbox");

		private String folder;

		SmsFolder(final String folder) {
			this.folder = folder;
		}

		/**
		 * @return the folder
		 */
		public String getFolder() {
			return folder;
		}
	}

	/**
	 * 
	 */
	public SmsAccessManager(final ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	public Cursor getFolder(final SmsFolder folder) {
		Uri queryUri = Uri.parse("content://sms/" + folder.getFolder());
		Cursor c = contentResolver.query(queryUri, new String[] { "address",
				"body", "date" }, "", null, "date DESC");

		return c;
	}

}
