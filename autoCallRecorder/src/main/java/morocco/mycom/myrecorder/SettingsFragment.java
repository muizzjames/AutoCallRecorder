package morocco.mycom.myrecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import morocco.mycom.myrecorder.R;
import morocco.mycom.myrecorder.helper.SecurePreferences;
import morocco.mycom.myrecorder.utils.CustomTextViewOpenSans;

public class SettingsFragment extends Fragment implements OnClickListener {

	AlertDialog alertDialog;

	CheckBox checkEnablePasscode, checkSaveConfirmation,
			checkEnableNotification;

	RelativeLayout relNotification, relSaveThisCall, relFormat, relPasscode,
			relChangePasscode;
	CustomTextViewOpenSans txtFormat;
	ImageView ivBack;

	public SettingsFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.settings,
				container, false);
		
		AdView adView = (AdView) v.findViewById(R.id.adView_settingscreen);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		ivBack = (ImageView) v.findViewById(R.id.ivBack);

		txtFormat = (CustomTextViewOpenSans) v.findViewById(R.id.txtFormat);
		checkEnablePasscode = (CheckBox) v
				.findViewById(R.id.checkEnablePasscode);
		checkEnableNotification = (CheckBox) v
				.findViewById(R.id.checkEnableNotification);
		checkSaveConfirmation = (CheckBox) v
				.findViewById(R.id.checkSaveConfirmation);

		relNotification = (RelativeLayout) v.findViewById(R.id.relNotification);
		relSaveThisCall = (RelativeLayout) v.findViewById(R.id.relSaveThisCall);
		relFormat = (RelativeLayout) v.findViewById(R.id.relFormat);
		relPasscode = (RelativeLayout) v.findViewById(R.id.relPasscode);
		relChangePasscode = (RelativeLayout) v
				.findViewById(R.id.relChangePasscode);

		setUI();

		relNotification.setOnClickListener(this);
		relSaveThisCall.setOnClickListener(this);
		relFormat.setOnClickListener(this);
		relPasscode.setOnClickListener(this);
		relChangePasscode.setOnClickListener(this);
		ivBack.setOnClickListener(this);

		return v;
	}

	private void setUI() {

		if (SecurePreferences.getBooleanPreference(getActivity(),
				Constants.PREF_ENABLE_PASSCODE)) {
			checkEnablePasscode.setChecked(true);
		} else {
			checkEnablePasscode.setChecked(false);
		}

		if (SecurePreferences.getBooleanPreference(getActivity(),
				Constants.PREF_NOTIFICATION_ENABLE)) {
			checkEnableNotification.setChecked(true);
		} else {
			checkEnableNotification.setChecked(false);
		}

		int format = SecurePreferences.getIntegerPreference(getActivity(),
				Constants.PREF_AUDIO_FORMAT);
		String settext = format == 3 ? "amr" : format == 2 ? "mp4"
				: ".3gp";
		txtFormat.setText(settext);

		if (SecurePreferences.getBooleanPreference(getActivity(),
				Constants.PREF_SAVE_RECORDING)) {
			checkSaveConfirmation.setChecked(true);
		} else {
			checkSaveConfirmation.setChecked(false);
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		if (v == relNotification) {
			if (SecurePreferences.getBooleanPreference(getActivity(),
					Constants.PREF_NOTIFICATION_ENABLE)) {
				SecurePreferences.savePreferences(getActivity(),
						Constants.PREF_NOTIFICATION_ENABLE, false);
				checkEnableNotification.setChecked(false);
			} else {
				SecurePreferences.savePreferences(getActivity(),
						Constants.PREF_NOTIFICATION_ENABLE, true);
				checkEnableNotification.setChecked(true);
			}
		} else if (v == relFormat) {

			chooseAudioForam();

		} else if (v == relChangePasscode) {

			Intent intent = new Intent(getActivity(), PasscodeActivity.class);

			Bundle bundle = new Bundle();
			bundle.putBoolean("fromChangeIntent", true);
			intent.putExtras(bundle);
			startActivity(intent);

		} else if (v == relPasscode) {
			if (SecurePreferences.getBooleanPreference(getActivity(),
					Constants.PREF_ENABLE_PASSCODE)) {
				SecurePreferences.savePreferences(getActivity(),
						Constants.PREF_ENABLE_PASSCODE, false);
				checkEnablePasscode.setChecked(false);
			} else {
				SecurePreferences.savePreferences(getActivity(),
						Constants.PREF_ENABLE_PASSCODE, true);
				checkEnablePasscode.setChecked(true);
			}
		} else if (v == relSaveThisCall) {
			if (SecurePreferences.getBooleanPreference(getActivity(),
					Constants.PREF_SAVE_RECORDING)) {
				SecurePreferences.savePreferences(getActivity(),
						Constants.PREF_SAVE_RECORDING, false);
				checkSaveConfirmation.setChecked(false);
			} else {
				SecurePreferences.savePreferences(getActivity(),
						Constants.PREF_SAVE_RECORDING, true);
				checkSaveConfirmation.setChecked(true);
			}
		} else if (v == ivBack) {
			getActivity().onBackPressed();
		}
	}

	private void chooseAudioForam() {

		final CharSequence[] items = { "3gp", "mp4", "amr" };
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle("Choose Format type");
		builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0:
							SecurePreferences.savePreferences(getActivity(),
									Constants.PREF_AUDIO_FORMAT, MediaRecorder.OutputFormat.THREE_GPP);
							txtFormat.setText("3gp");
							break;
						case 1:
							SecurePreferences.savePreferences(getActivity(),
									Constants.PREF_AUDIO_FORMAT, MediaRecorder.OutputFormat.MPEG_4);
							txtFormat.setText("mp4");
							break;

						case 2:
							SecurePreferences.savePreferences(getActivity(),
									Constants.PREF_AUDIO_FORMAT, MediaRecorder.OutputFormat.RAW_AMR);
							txtFormat.setText("amr");
							break;
						}
						alertDialog.dismiss();
					}

				});
		alertDialog = builder.create();
		alertDialog.show();

	}

}
