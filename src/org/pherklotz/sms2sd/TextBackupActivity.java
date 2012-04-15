/*
 * (c) by Peter Herklotz 2012
 * Email: dev@cloudeta.com 
 * Lizenz: GNU GPLv3
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.pherklotz.sms2sd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.pherklotz.sms2sd.SmsAccessManager.SmsFolder;
import org.pherklotz.sms2sd.backup.SmsBackupCreator;
import org.pherklotz.sms2sd.backup.XmlBackupCreator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TextBackupActivity extends Activity implements Runnable {

	private final static String TAG = TextBackupActivity.class.getName();
	private final static int TOAST_TIME = 1000;
	private ProgressDialog progressDialog;
	private SmsAccessManager smsAccess;
	private static final int HANDLER_WHAT_CLOSE_PROGRESS_DIALOG = 1;
	private static final int HANDLER_WHAT_ERROR_MESSAGE = 2;
	private static final int HANDLER_WHAT_SUCCESSFULL = 3;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		smsAccess = new SmsAccessManager(getContentResolver());
		setContentView(R.layout.main);
		TextView text = (TextView) findViewById(R.id.text);
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			String backupText = getString(R.string.text, this
					.getExternalFilesDir(null).getAbsolutePath());
			text.setText(backupText);
		} else {
			Toast.makeText(this, R.string.error_cant_write_file, TOAST_TIME)
					.show();
		}
	}

	public void onEvtBackup(final View view) {
		switch (view.getId()) {
		case R.id.backupBtn:
			progressDialog = ProgressDialog.show(TextBackupActivity.this, "",
					getString(R.string.progress_dialog_create_backup));
			new Thread(this).start();

			break;
		default:
			break;
		}
	}

	// Define the Handler that receives messages from the thread and update the
	// progress
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case HANDLER_WHAT_CLOSE_PROGRESS_DIALOG:
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				break;
			case HANDLER_WHAT_SUCCESSFULL:
				Toast.makeText(TextBackupActivity.this,
						R.string.info_backup_successful, TOAST_TIME).show();
				break;
			case HANDLER_WHAT_ERROR_MESSAGE:
				Toast.makeText(TextBackupActivity.this,
						R.string.error_cant_write_file, TOAST_TIME).show();
				break;
			}

		}
	};

	/**
	 * @throws IOException
	 * 
	 */
	private void backupSms(final SmsFolder folder,
			final SmsBackupCreator creator) throws IOException {
		Cursor c = smsAccess.getFolder(folder);
		if (c != null && c.moveToFirst()) {
			Log.d(TAG, "Starting backup");
			do {
				String address = c.getString(c.getColumnIndex("address"));
				String body = c.getString(c.getColumnIndex("body"));
				String date = c.getString(c.getColumnIndex("date"));
				creator.writeRecord(address, date, folder.name(), body);

			} while (c.moveToNext());
			Log.d(TAG, "Finishing backup");
		}
		c.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Looper.prepare();
		try {
			String dateStr = DateFormat.format("yyyy-MM-dd_hhmmss",
					System.currentTimeMillis()).toString();

			File backupFile = new File(getExternalFilesDir(null), "sms_backup_"
					+ dateStr + ".xml");

			OutputStream out = new FileOutputStream(backupFile);
			SmsBackupCreator backupCreator = new XmlBackupCreator();
			backupCreator.setOutputstream(out);
			backupCreator.startBackup();
			backupSms(SmsFolder.INBOX, backupCreator);
			backupSms(SmsFolder.OUTBOX, backupCreator);
			backupCreator.finishBackup();
			out.close();
			handler.sendEmptyMessage(HANDLER_WHAT_SUCCESSFULL);
		} catch (IOException e) {
			handler.sendEmptyMessage(HANDLER_WHAT_ERROR_MESSAGE);
			Log.e(TAG, getString(R.string.error_cant_write_file), e);
		} finally {
			handler.sendEmptyMessage(HANDLER_WHAT_CLOSE_PROGRESS_DIALOG);
			Looper.myLooper().quit();
		}
	}
}