package morocco.mycom.myrecorder;

import android.os.Environment;

public class Constants {
	public static String prefRecording = "Recording";
	public static String prefInitial = "Initial";

	static public String PREF_RECORD_CALLS = "PREF_RECORD_CALLS";
	static public String PREF_AUDIO_SOURCE = "PREF_AUDIO_SOURCE";
	static public String PREF_AUDIO_FORMAT = "PREF_AUDIO_FORMAT";

	static public String PREF_NOTIFICATION_ENABLE = "PREF_NOTIFICATION_ENABLE";
	static public String PREF_SAVE_RECORDING = "PREF_SAVE_RECORDING";
	static public String PREF_ENABLE_PASSCODE = "PREF_ENABLE_PASSCODE";

	static public String PREF_REAL_PASSCODE = "PREF_REAL_PASSCODE";

	static public String StoragePath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/AutoCallRecording/";
	static public String StorageDeletePath = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/AutoCallRecordingDelete/";


}
