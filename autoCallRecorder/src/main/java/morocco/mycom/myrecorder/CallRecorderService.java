package morocco.mycom.myrecorder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import morocco.mycom.myrecorder.R;
import morocco.mycom.myrecorder.helper.SecurePreferences;

public class CallRecorderService extends Service {
	public static File amr;
	public static final String ACTION = "com.cn6000.callrec.CALL_RECORD";
	public static final String STATE = "STATE";
	public static final String START = "START";
	public static final String STORAGE = "STORAGE";
	public static final String INCOMING = "INCOMING";
	public static final String OUTGOING = "OUTGOING";
	public static final String BEGIN = "BEGIN";
	public static final String END = "END";

	protected static final String TAG = CallRecorderService.class.getName();
	protected static final boolean DEBUG = false;
	private static final int RECORDING_NOTIFICATION_ID = 1;

	private static final String AMR_DIR = "/AutoCallRecording/";
	private static final String IDLE = "";
	private static final String INCOMING_CALL_SUFFIX = "_inCall";
	private static final String OUTGOING_CALL_SUFFIX = "_outCall";

	private Context cntx;
	private volatile String fileNamePrefix = IDLE;
	private volatile MediaRecorder recorder;
	private volatile PowerManager.WakeLock wakeLock;
	private volatile boolean isMounted = false;
	private volatile boolean isInRecording = false;

	@Override
	public IBinder onBind(Intent i) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.cntx = getApplicationContext();
		this.prepareAmrDir();
		log("service create");
	}

	@Override
	public void onDestroy() {
		log("service destory");
		this.stopRecording();
		this.cntx = null;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null == intent || !ACTION.equals(intent.getAction())) {
			return super.onStartCommand(intent, flags, startId);
		}
		String state = intent.getStringExtra(STATE);
		String phoneNo = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		log("state: " + state + " phoneNo: " + phoneNo);
		if (OUTGOING.equals(state)) {
			fileNamePrefix = "(" + phoneNo + ")" + OUTGOING_CALL_SUFFIX;
		} else if (INCOMING.equals(state)) {
			fileNamePrefix = "(" + phoneNo + ")" + INCOMING_CALL_SUFFIX;
		} else if (BEGIN.equals(state)) {
			if (SecurePreferences.getBooleanPreference(this,
					Constants.PREF_RECORD_CALLS)) {
				log("###: " + state + " phoneNo: " + phoneNo);
				startRecording();
			}
		} else if (END.equals(state)) {
			stopRecording();
		} else if (STORAGE.equals(state)) {
			String mountState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(mountState)) {
				prepareAmrDir();
			} else {
				isMounted = false;
			}
			if (!isInRecording) {
				stopSelf();
			}
		}
		return START_STICKY;
	}

	public Context getContext() {
		return cntx;
	}

	private void stopRecording() {
//		updateNotification(false);
		if (isInRecording) {
			isInRecording = false;
			try {
				recorder.stop();
				recorder.release();
				recorder = null;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			releaseWakeLock();

			if (SecurePreferences.getBooleanPreference(this,
					Constants.PREF_SAVE_RECORDING)) {

				Intent intent = new Intent(CallRecorderService.this,
						AutoRunReceiver.class);
				String ACTION_SHOW_ALERT_DIALOG = "morocco.mycom.myrecorder.SHOW_ALERT_DIALOG";
				intent.setAction(ACTION_SHOW_ALERT_DIALOG);
				intent.putExtra("absolutepath", amr.getAbsolutePath());
				sendBroadcast(intent);
			}

			stopSelf();
			log("call recording stopped");

		}
	}

//	private void updateNotification(Boolean status) {
//		Context context = getApplicationContext();
//
//		String ns = Context.NOTIFICATION_SERVICE;
//		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
//
//		if (status) {
//			int icon = R.drawable.ic_notification_true;
//
//			// Explicit intent to wrap
//			Intent intent = new Intent(context, MainActivity.class);
//
//			// Create pending intent and wrap our intent
//			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//			try {
//				// Perform the operation associated with our pendingIntent
//				pendingIntent.send();
//			} catch (PendingIntent.CanceledException e) {
//				e.printStackTrace();
//			}
//
//			NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
//					.setContentTitle("call recording start")
//					.setContentIntent(pendingIntent)
//					.setSmallIcon(icon);
//			mNotificationManager
//					.notify(RECORDING_NOTIFICATION_ID, notification.build());
//
//
//		} else {
//			int icon = R.drawable.ic_notification_false;
//
//			// Explicit intent to wrap
//			Intent intent = new Intent(context, MainActivity.class);
//
//			// Create pending intent and wrap our intent
//			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//			try {
//				// Perform the operation associated with our pendingIntent
//				pendingIntent.send();
//			} catch (PendingIntent.CanceledException e) {
//				e.printStackTrace();
//			}
//			NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
//					.setContentTitle("call recording start")
//					.setContentIntent(pendingIntent)
//					.setSmallIcon(icon);
//			mNotificationManager
//					.notify(RECORDING_NOTIFICATION_ID, notification.build());
//		}
//	}

	private String getDateTimeString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'_'HHmmss");
		Date now = new Date();
		return sdf.format(now);
	}

	private String getMonthString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Date now = new Date();
		return sdf.format(now);
	}

	private String getDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date now = new Date();
		return sdf.format(now);
	}

	private String getTimeString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		Date now = new Date();
		return sdf.format(now);
	}

	private void startRecording() {
//		if (SecurePreferences.getBooleanPreference(this,
//				Constants.PREF_NOTIFICATION_ENABLE)) {
//			updateNotification(true);
//		} else {
//			updateNotification(false);
//		}
		if (!isMounted) {
			stopRecording();
//			updateNotification(false);
			return;
		}
		try {

			int format = SecurePreferences.getIntegerPreference(this,
					Constants.PREF_AUDIO_FORMAT);

			String exten = format == 1 ? ".3gpp" : format == 2 ? ".mpg"
					: ".amr";
			amr = new File(Constants.StoragePath + getDateTimeString()
					+ "_" + fileNamePrefix + exten);
			log("Prepare recording in " + amr.getAbsolutePath());
			recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
			recorder.setOutputFormat(format);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recorder.setOutputFile(amr.getAbsolutePath());
			recorder.prepare();
			recorder.start();
			isInRecording = true;
			acquireWakeLock();
			log("Recording in " + amr.getAbsolutePath());
		} catch (Exception e) {
			Log.w(TAG, e);
		}
	}

	private void prepareAmrDir() {
		isMounted = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (!isMounted)
			return;
		File amrRoot = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + AMR_DIR);
		if (!amrRoot.isDirectory())
			amrRoot.mkdir();
	}

	private void log(String info) {
		if (DEBUG && isMounted) {
			File log = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ AMR_DIR
					+ "log_"
					+ getMonthString()
					+ ".txt");
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(log,
						true));
				try {
					synchronized (out) {
						out.write(getDateString() + getTimeString());
						out.write(" ");
						out.write(info);
						out.newLine();
					}
				} finally {
					out.close();
				}
			} catch (IOException e) {
				Log.w(TAG, e);
			}
		}
	}

	private void acquireWakeLock() {
		if (wakeLock == null) {
			log("Acquiring wake lock");
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
					.getClass().getCanonicalName());
			wakeLock.acquire();
		}

	}

	private void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
			log("Wake lock released");
		}

	}
}
