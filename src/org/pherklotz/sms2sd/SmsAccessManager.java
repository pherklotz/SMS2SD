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
 * Helper class to access text messages with a {@link ContentResolver}.
 * 
 * @author pherklotz
 */
public class SmsAccessManager {

	/**
	 * Columns.
	 */
	public final String[] PROJECTION = new String[] { "address", "body", "date" };

	/**
	 * {@link ContentResolver}.
	 */
	private ContentResolver contentResolver;

	/**
	 * Enum to specify message folders.
	 * 
	 * @author pherklotz
	 */
	public enum SmsFolder {

		/** Outgoing messages. */
		OUTBOX("sent"),
		/** Incoming messages. */
		INBOX("inbox");

		/**
		 * Android folder path.
		 */
		private String folder;

		/**
		 * @param folder
		 *            Suffix of the folder path in Android.
		 */
		SmsFolder(final String folder) {
			this.folder = folder;
		}

		/**
		 * @return Suffix of the folder path in Android.
		 */
		public String getFolder() {
			return folder;
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param contentResolver
	 *            used to query the messages.
	 */
	public SmsAccessManager(final ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	/**
	 * Access an {@link SmsFolder} and return the db cursor. Accessible are all
	 * columns in {@link SmsAccessManager#PROJECTION}.
	 * 
	 * @param folder
	 *            the folder.
	 * @return a db cursor.
	 */
	public Cursor getFolder(final SmsFolder folder) {
		Uri queryUri = Uri.parse("content://sms/" + folder.getFolder());
		Cursor c = contentResolver.query(queryUri, new String[] { "address",
				"body", "date" }, "", null, "date DESC");

		return c;
	}

}
