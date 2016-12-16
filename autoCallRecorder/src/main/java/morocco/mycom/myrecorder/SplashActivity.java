package morocco.mycom.myrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import morocco.mycom.myrecorder.helper.SecurePreferences;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		if (!SecurePreferences
				.getBooleanPreference(this, Constants.prefInitial)) {

			SecurePreferences
					.savePreferences(this, Constants.prefInitial, true);
			SecurePreferences.savePreferences(this,
					Constants.PREF_RECORD_CALLS, true);
			SecurePreferences.savePreferences(this,
					Constants.PREF_AUDIO_SOURCE, "4");
			SecurePreferences.savePreferences(this,
					Constants.PREF_AUDIO_FORMAT, 1);
			SecurePreferences.savePreferences(this,
					Constants.PREF_REAL_PASSCODE, "1234");
			SecurePreferences.savePreferences(this,
					Constants.PREF_NOTIFICATION_ENABLE, true);
			SecurePreferences.savePreferences(this,
					Constants.PREF_ENABLE_PASSCODE, false);
		}

		if (SecurePreferences.getBooleanPreference(this,
				Constants.PREF_ENABLE_PASSCODE)) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					startActivity(new Intent(SplashActivity.this,
							PasscodeActivity.class));
					finish();
				}
			}, 1000);
		}

		else {

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					startActivity(new Intent(SplashActivity.this,
							MainActivity.class));
					finish();
				}
			}, 1000);
		}
	}

	@Override
	public void onBackPressed() {

	}
}
