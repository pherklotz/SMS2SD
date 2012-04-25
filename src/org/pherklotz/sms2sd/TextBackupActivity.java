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
import org.pherklotz.sms2sd.writer.SmsWriter;
import org.pherklotz.sms2sd.writer.XmlSmsWriter;

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

/**
 * Starting activity. The layout is very simple. It shows some informations and
 * a button. A click on the button starts the backup process. During the
 * progress a {@link ProgressDialog} is visible.
 * 
 * @author pherklotz
 */
public class TextBackupActivity extends Activity implements Runnable {

	private final static String TAG = TextBackupActivity.class.getName();
	private final static int TOAST_TIME = 2000;
	private ProgressDialog progressDialog;
	private SmsAccessManager smsAccess;
	private static final int HANDLER_WHAT_CLOSE_PROGRESS_DIALOG = 1;
	private static final int HANDLER_WHAT_ERROR_MESSAGE = 2;
	private static final int HANDLER_WHAT_SUCCESSFULL = 3;

	/**
	 * Define the Handler that receives messages from the backup thread.
	 */
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
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		smsAccess = new SmsAccessManager(getContentResolver());
		setContentView(R.layout.main);
		TextView text = (TextView) findViewById(R.id.text);
		String state = Environment.getExternalStorageState();
		// If an SD-Card is mounted.
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			String backupText = getString(R.string.text, this
					.getExternalFilesDir(null).getAbsolutePath());
			text.setText(backupText);
		} else {
			Toast.makeText(this, R.string.error_no_sd_mounted, TOAST_TIME)
					.show();
			finish();
		}
	}

	/**
	 * OnClick-Handler for the Backup-Button. Triggers the backup.
	 * 
	 * @param view
	 *            The clicked component.
	 */
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

	/**
	 * Reads all text messages from the given folder and create for each a
	 * record in the {@link SmsWriter}.
	 * 
	 * @param folder
	 *            The message folder.
	 * @param creator
	 *            the creator
	 * @throws IOException
	 */
	private void backupSms(final SmsFolder folder, final SmsWriter writer)
			throws IOException {
		Cursor c = smsAccess.getFolder(folder);
		if (c != null && c.moveToFirst()) {
			Log.d(TAG, "Starting backup");
			do {
				String address = c.getString(c.getColumnIndex("address"));
				String body = c.getString(c.getColumnIndex("body"));
				String date = c.getString(c.getColumnIndex("date"));
				writer.writeRecord(address, date, folder.name(), body);

			} while (c.moveToNext());
			Log.d(TAG, "Finishing backup");
		}
		c.close();
	}

	@Override
	public void run() {
		// Needed to close the ProgressDialog.
		Looper.prepare();
		try {
			// example file name: sms_backup_2012-05-23_133700.xml
			String dateStr = DateFormat.format("yyyy-MM-dd_hhmmss",
					System.currentTimeMillis()).toString();
			File backupFile = new File(getExternalFilesDir(null), "sms_backup_"
					+ dateStr + ".xml");

			OutputStream out = new FileOutputStream(backupFile);
			SmsWriter backupCreator = new XmlSmsWriter();
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