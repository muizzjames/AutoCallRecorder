package morocco.mycom.myrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import morocco.mycom.myrecorder.helper.SecurePreferences;

public class PasscodeActivity extends Activity implements OnClickListener {

	TextView lblPasscode;

	Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0;
	ImageView btnDelete;

	String strPasscode = "";
	String strRealpasscode = "";
	CheckBox checkOne, checkTwo, checkThree, checkFour;
	LinearLayout llPassCode;

	Animation animation;

	boolean fromChangeintent = false, fromAgain = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_activity);

		strRealpasscode = SecurePreferences.getStringPreference(this,
				Constants.PREF_REAL_PASSCODE);

		AdView adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		System.out.println("real passcode"
				+ SecurePreferences.getStringPreference(this,
						Constants.PREF_REAL_PASSCODE));

		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();

		if (bundle != null) {
			fromChangeintent = bundle.getBoolean("fromChangeIntent");
			fromAgain = bundle.getBoolean("fromAgain");
		}
		if (fromAgain) {
			lblPasscode = (TextView) findViewById(R.id.lblPasscode);
			lblPasscode.setText("Enter New Passcode");
		}

		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn4 = (Button) findViewById(R.id.btn4);
		btn5 = (Button) findViewById(R.id.btn5);
		btn6 = (Button) findViewById(R.id.btn6);
		btn7 = (Button) findViewById(R.id.btn7);
		btn8 = (Button) findViewById(R.id.btn8);
		btn9 = (Button) findViewById(R.id.btn9);
		btn0 = (Button) findViewById(R.id.btn0);
		llPassCode = (LinearLayout) findViewById(R.id.llPassCode);

		checkOne = (CheckBox) findViewById(R.id.checkOne);
		checkTwo = (CheckBox) findViewById(R.id.checkTwo);
		checkThree = (CheckBox) findViewById(R.id.checkThree);
		checkFour = (CheckBox) findViewById(R.id.checkFour);

		btnDelete = (ImageView) findViewById(R.id.btndelete);
		btnDelete.setOnClickListener(this);

		animation = AnimationUtils.loadAnimation(this, R.anim.shake);

		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn9.setOnClickListener(this);
		btn0.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == btn1) {
			checkAndAdd("1");
		} else if (v == btn2) {
			checkAndAdd("2");
		} else if (v == btn3) {
			checkAndAdd("3");
		} else if (v == btn4) {
			checkAndAdd("4");
		} else if (v == btn5) {
			checkAndAdd("5");
		} else if (v == btn6) {
			checkAndAdd("6");
		} else if (v == btn7) {
			checkAndAdd("7");
		} else if (v == btn8) {
			checkAndAdd("8");
		} else if (v == btn9) {
			checkAndAdd("9");
		} else if (v == btn0) {
			checkAndAdd("0");
		} else if (v == btnDelete) {
			strPasscode = "";
			checkOne.setChecked(false);
			checkTwo.setChecked(false);
			checkThree.setChecked(false);
			checkFour.setChecked(false);
		}
	}

	private void checkAndAdd(String string) {
		strPasscode += string;

		if (strPasscode.length() == 1) {
			checkOne.setChecked(true);
		} else if (strPasscode.length() == 2) {
			checkOne.setChecked(true);
			checkTwo.setChecked(true);
		} else if (strPasscode.length() == 3) {
			checkOne.setChecked(true);
			checkTwo.setChecked(true);
			checkThree.setChecked(true);
		} else if (strPasscode.length() == 4) {
			checkOne.setChecked(true);
			checkTwo.setChecked(true);
			checkThree.setChecked(true);
			checkFour.setChecked(true);

			if (fromAgain) {

				buttonsEnable(false);
				SecurePreferences.savePreferences(this,
						Constants.PREF_REAL_PASSCODE, strPasscode);

				System.out.println("real passcode"
						+ SecurePreferences.getStringPreference(this,
								Constants.PREF_REAL_PASSCODE));
				finish();
			}

			else if (strPasscode.equals(strRealpasscode)) {

				buttonsEnable(false);
				if (fromChangeintent) {

					Intent intent = new Intent(PasscodeActivity.this,
							PasscodeActivity.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean("fromAgain", true);
					intent.putExtras(bundle);
					startActivity(intent);
					overridePendingTransition(0, 0);
					finish();

				}

				else {

					startActivity(new Intent(this, MainActivity.class));
					overridePendingTransition(0, 0);
					finish();
				}
			} else {
				llPassCode.startAnimation(animation);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						buttonsEnable(false);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						buttonsEnable(true);
						strPasscode = "";
						checkOne.setChecked(false);
						checkTwo.setChecked(false);
						checkThree.setChecked(false);
						checkFour.setChecked(false);

					}
				});

			}
		}
	}

	public void buttonsEnable(boolean setEnable) {
		btn1.setEnabled(setEnable);
		btn2.setEnabled(setEnable);
		btn3.setEnabled(setEnable);
		btn4.setEnabled(setEnable);
		btn5.setEnabled(setEnable);
		btn6.setEnabled(setEnable);
		btn7.setEnabled(setEnable);
		btn8.setEnabled(setEnable);
		btn9.setEnabled(setEnable);
		btn0.setEnabled(setEnable);
		btnDelete.setEnabled(setEnable);
	}
}