package morocco.mycom.myrecorder;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import morocco.mycom.myrecorder.RecycleBinActivity.RecordingsAdapter.RecordingViewHolder;
import morocco.mycom.myrecorder.helper.SecurePreferences;
import morocco.mycom.myrecorder.utils.CustomRecyclerView;
import morocco.mycom.myrecorder.utils.GetOption;
import com.kskkbys.rate.RateThisApp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordingsActivityAgain extends FragmentActivity implements
		OnClickListener {

//	TextView tvNodata;

	private StartAppAd startAppAd = new StartAppAd(this);

	int textlength = 0;

	private static CustomRecyclerView recyclerView;
	public static Adapter<RecordingViewHolder> adapter;
	LinearLayoutManager layoutManager;

	EditText edtSearch;
	ImageView imgMenu, imgRecording, imgDelete, imgBack, imgSearch, imgClose;
	RelativeLayout relSearch;
	View tempView;
	Handler mHandler = new Handler();

	SeekBar bar;

	boolean isLongpress = false, isSearch = false;

	ImageLoader imageLoader = ImageLoader.getInstance();

	public static List<RecordingsDataContainer> dataContainer;
	public static List<RecordingsDataContainer> tempContainer;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.recording_list_activity);

		StartAppSDK.init(RecordingsActivityAgain.this, "106508271", "205945600",
				true);

		recyclerView = (CustomRecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);

		edtSearch = (EditText) findViewById(R.id.edtSearch);
		relSearch = (RelativeLayout) findViewById(R.id.relSearch);
		imgMenu = (ImageView) findViewById(R.id.imgMenu);
		imgRecording = (ImageView) findViewById(R.id.imgRecording);
		imgDelete = (ImageView) findViewById(R.id.imgDelete);
		imgBack = (ImageView) findViewById(R.id.imgBack);
		imgSearch = (ImageView) findViewById(R.id.imageSearch);
		imgClose = (ImageView) findViewById(R.id.imgClose);
		tempView = (View) findViewById(R.id.tempView);

//		tvNodata = (TextView) findViewById(R.id.tvNodata);

		AdView adView = (AdView) findViewById(R.id.adView_main);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		startAppAd.loadAd(new AdEventListener() {

			@Override
			public void onReceiveAd(Ad arg0) {
				startAppAd.showAd();

			}

			@Override
			public void onFailedToReceiveAd(Ad arg0) {
				// TODO Auto-generated method stub

			}
		});

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String searchStr = s.toString();
				refineSearch(searchStr);
			}
		});

		edtSearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
					imgSearch.performClick();
					return true;
				}
				return false;
			}
		});

		imageLoader.init(GetOption.getConfig(this));

		if (SecurePreferences.getBooleanPreference(this,
				Constants.PREF_RECORD_CALLS)) {
			imgRecording.setImageResource(R.drawable.recording_on);
		} else {
			imgRecording.setImageResource(R.drawable.recording_off);
		}

		imgRecording.setOnClickListener(this);
		imgMenu.setOnClickListener(this);

		new LoadRecordings().execute();

		imgBack.setOnClickListener(this);
		imgDelete.setOnClickListener(this);
		imgSearch.setOnClickListener(this);
		imgClose.setOnClickListener(this);
	}

	public class LoadRecordings extends AsyncTask<Void, Void, Void> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(RecordingsActivityAgain.this);
			dialog.setMessage("Loading Recordings...");
			dialog.show();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// loadRecordingsFromDir();

			try {

				File dir = new File(Constants.StoragePath);
				dataContainer = new ArrayList<RecordingsDataContainer>();
				if (dir.exists()) {
					String[] dlist = dir.list();
					for (int i = dlist.length - 1; i >= 0; i--) {
						RecordingsDataContainer container = new RecordingsDataContainer();
						String data = dlist[i];
						File f = new File(Constants.StoragePath + data);
						Date lastModDate = new Date(f.lastModified());

						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"MMM dd , HH:mm", Locale.US);
						try {
							container.time = dateFormat.format(lastModDate);
						} catch (Exception e) {

						}
						container.audio = f;
						RecordingsDataContainer.audio1 = f;
						// container.name = getContactName(this,
						// data.substring(data.indexOf("(") + 1,
						// data.indexOf(")")));
						String phoneNo = data.substring(data.indexOf("(") + 1,
								data.indexOf(")"));

						if (null == phoneNo) {
							container.name = "";
						} else {

							ContentResolver cr = getContentResolver();
							Uri uri = Uri
									.withAppendedPath(
											PhoneLookup.CONTENT_FILTER_URI,
											Uri.encode(phoneNo));

							String[] projection = new String[] {
									PhoneLookup.DISPLAY_NAME,
									PhoneLookup._ID };
							String contactId = null;
							Cursor cursor = cr.query(uri, projection, null,
									null, null);

							if (cursor != null) {
								while (cursor.moveToNext()) {
									contactId = cursor
											.getString(cursor
													.getColumnIndexOrThrow(PhoneLookup._ID));
								}
								cursor.close();
							}
							try {
								container.imagePath = ContentUris
										.withAppendedId(
												ContactsContract.Contacts.CONTENT_URI,
												new Long(contactId));
							} catch (Exception e) {

							}

							uri = Uri.withAppendedPath(
									PhoneLookup.CONTENT_FILTER_URI,
									Uri.encode(phoneNo));
							Cursor c = cr.query(uri, new String[] {
									PhoneLookup.DISPLAY_NAME,
									PhoneLookup.PHOTO_URI }, null, null, null);
							if (null == c) {
								container.name = phoneNo;
							}
							try {
								if (c.moveToFirst()) {
									String name = c.getString(0);
									name = name.replaceAll(
											"(\\||\\\\|\\?|\\*|<|:|\"|>)", "");
									container.name = name;
								} else {
									container.name = phoneNo;
								}
							} finally {
								c.close();
							}
						}
						container.duration = getDuration(f);
						if (data.contains("_inCall")) {
							container.isIncomming = true;
						}
						dataContainer.add(container);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (dataContainer.size() > 0) {

//				tvNodata.setVisibility(View.GONE);
//				adapter = new RecordingsAdapter(dataContainer);
//				recyclerView.setAdapter(adapter);
			}
		}

	}

	@Override
	public void onBackPressed() {
		if (isLongpress) {
			imgBack.performClick();
		} else {
			super.onBackPressed();
		}

	}

	private String getDuration(File f) {
		long duration;
		try {
			MediaPlayer mp = new MediaPlayer();
			FileInputStream stream = new FileInputStream(f);
			mp.setDataSource(stream.getFD());
			stream.close();
			mp.prepare();
			duration = mp.getDuration();
			mp.release();
		} catch (Exception e) {
			return "00:00";
		}

		int seconds = (int) ((duration / 1000) % 60);
		int minutes = (int) (((duration - seconds) / 1000) / 60);
		String time = "";
		if (minutes < 10) {
			time = "0" + minutes;
		} else {
			time = "" + minutes;
		}

		if (seconds < 10) {
			time = time + " : 0" + seconds;
		} else {
			time = time + " : " + seconds;
		}
		return time;
	}

	public static class RecordingsDataContainer {
		public String name = "";
		public String time = "";
		public boolean isIncomming = false;
		public boolean isChecked = false;
		public String duration = "";
		public Uri imagePath = null;
		public File audio = null;
		public static File audio1 = null;
	}

	public class RecordingsAdapter extends
			Adapter<RecordingsAdapter.RecordingViewHolder> {
		private List<RecordingsDataContainer> mDataset;
		MediaPlayer mp = null;

		public RecordingsAdapter(List<RecordingsDataContainer> contactList) {
			mDataset = contactList;
		}

		@Override
		public RecordingViewHolder onCreateViewHolder(ViewGroup parent,
				int viewType) {
			View itemView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.row_recordings, parent, false);

			return new RecordingViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(final RecordingViewHolder holder,
				final int position) {
			final RecordingsDataContainer data = mDataset.get(position);
			holder.txtName.setText(data.name);
			// holder.txtTime.setText(data.time);
			holder.txtDuration.setText(data.duration);
			holder.txtTime.setText(data.time);

			if (data.isIncomming) {
				holder.imgCallType.setImageResource(R.drawable.in_call);
			} else {
				holder.imgCallType.setImageResource(R.drawable.out_call);
			}

			if (data.isChecked) {
				holder.imgCheck.setVisibility(View.VISIBLE);
				holder.overlay.setVisibility(View.VISIBLE);
			} else {
				holder.imgCheck.setVisibility(View.GONE);
				holder.overlay.setVisibility(View.GONE);
			}

			if (data.imagePath == null) {
				data.imagePath = Uri.parse("//");
			} else if (data.imagePath.equals("")) {
				data.imagePath = Uri.parse("//");
			}

			imageLoader.displayImage(data.imagePath.toString(),
					holder.imgPerson, GetOption.getProfileOption(data.name),
					new SimpleImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
						}
					});

			holder.relMain.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isLongpress) {
						if (data.isChecked) {
							holder.imgCheck
									.setImageResource(R.drawable.checked_false);
							holder.imgCheck.setVisibility(View.GONE);
							holder.overlay.setVisibility(View.GONE);
							data.isChecked = false;
							notifyItemChanged(position);
						} else {
							holder.imgCheck
									.setImageResource(R.drawable.selected);
							holder.imgCheck.setVisibility(View.VISIBLE);
							holder.overlay.setVisibility(View.VISIBLE);
							data.isChecked = true;
							notifyItemChanged(position);
						}
					} else {

						FragmentManager fragmentManager = getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager
								.beginTransaction();
						fragmentTransaction.setCustomAnimations(
								R.anim.slide_in, R.anim.back_slide_in,
								R.anim.slide_out, R.anim.back_slide_out);
						MediaplayerFragment fragment = new MediaplayerFragment();

						Bundle bundle = new Bundle();

						bundle.putBoolean("isSearch", isSearch);
						bundle.putInt("position", position);

						fragment.setArguments(bundle);
						fragmentTransaction.replace(R.id.mainFrame, fragment,
								"MediaplayerFragment");
						fragmentTransaction.addToBackStack(null);
						fragmentTransaction.commit();

						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								startAppAd.showAd();
								startAppAd.loadAd();
							}
						}, 2000);

					}

				}
			});
			holder.relMain.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					isLongpress = true;
					imgDelete.setVisibility(View.VISIBLE);
					imgBack.setVisibility(View.VISIBLE);
					imgMenu.setVisibility(View.GONE);
					imgRecording.setVisibility(View.GONE);

					if (data.isChecked) {
						holder.imgCheck
								.setImageResource(R.drawable.checked_false);
						holder.imgCheck.setVisibility(View.GONE);
						holder.overlay.setVisibility(View.GONE);
						data.isChecked = false;
						notifyItemChanged(position);
					} else {
						holder.imgCheck.setImageResource(R.drawable.selected);
						holder.imgCheck.setVisibility(View.VISIBLE);
						holder.overlay.setVisibility(View.VISIBLE);
						data.isChecked = true;
						notifyItemChanged(position);
					}
					return true;
				}
			});
		}

		public void updateProgressBar() {
			mHandler.postDelayed(mUpdateTimeTask, 100);
		}

		private Runnable mUpdateTimeTask = new Runnable() {
			public void run() {
				long totalDuration = mp.getDuration();
				long currentDuration = mp.getCurrentPosition();

				int progress = (int) (getProgressPercentage(currentDuration,
						totalDuration));
				bar.setProgress(progress);
				mHandler.postDelayed(this, 100);
			}
		};

		public int progressToTimer(int progress, int totalDuration) {
			int currentDuration = 0;
			totalDuration = (int) (totalDuration / 1000);
			currentDuration = (int) ((((double) progress) / 100) * totalDuration);

			// return current duration in milliseconds
			return currentDuration * 1000;
		}

		@Override
		public int getItemCount() {
			return mDataset.size();
		}

		public int getProgressPercentage(long currentDuration,
				long totalDuration) {
			Double percentage = (double) 0;

			long currentSeconds = (int) (currentDuration / 1000);
			long totalSeconds = (int) (totalDuration / 1000);

			// calculating percentage
			percentage = (((double) currentSeconds) / totalSeconds) * 100;

			// return percentage
			return percentage.intValue();
		}

		public class RecordingViewHolder extends RecyclerView.ViewHolder {
			protected TextView txtName, txtTime, txtDuration;
			protected ImageView imgPerson, imgCallType, imgCheck;
			protected View overlay;
			RelativeLayout relMain;

			public RecordingViewHolder(View v) {
				super(v);
				txtName = (TextView) v.findViewById(R.id.txtName);
				txtTime = (TextView) v.findViewById(R.id.txtTime);
				txtDuration = (TextView) v.findViewById(R.id.txtDuration);
				imgCallType = (ImageView) v.findViewById(R.id.imgCallType);
				relMain = (RelativeLayout) v.findViewById(R.id.relMain);
				imgCheck = (ImageView) v.findViewById(R.id.imgCheck);
				imgPerson = (ImageView) v.findViewById(R.id.imgPerson);
				overlay = (View) v.findViewById(R.id.overlay);

			}
		}

	}

	private String getContactName(Context cntx, String phoneNo) {
		if (null == phoneNo)
			return "";
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNo));
		ContentResolver cr = cntx.getContentResolver();
		Cursor c = cr.query(uri, new String[] { PhoneLookup.DISPLAY_NAME,
				PhoneLookup.PHOTO_URI }, null, null, null);
		if (null == c) {
			return phoneNo;
		}
		try {
			if (c.moveToFirst()) {
				String name = c.getString(0);
				name = name.replaceAll("(\\||\\\\|\\?|\\*|<|:|\"|>)", "");
				System.out.println(c.getString(1));
				return name;
			} else {
				return phoneNo;
			}
		} finally {
			c.close();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == imgMenu) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
			PopupMenu popupMenu = new PopupMenu(this, tempView);

			try {
				Field[] fields = popupMenu.getClass().getDeclaredFields();
				for (Field field : fields) {
					if ("mPopup".equals(field.getName())) {
						field.setAccessible(true);
						Object menuPopupHelper = field.get(popupMenu);
						Class<?> classPopupHelper = Class
								.forName(menuPopupHelper.getClass().getName());
						Method setForceIcons = classPopupHelper.getMethod(
								"setForceShowIcon", boolean.class);
						setForceIcons.invoke(menuPopupHelper, true);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// prepareMenu(popupMenu.getMenu());
			popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {

				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
					case R.id.item_Search:
						isSearch = true;
						edtSearch.setText("");
						relSearch.setVisibility(View.VISIBLE);
						tempContainer = dataContainer;
						visibleTempDataContainer();
						return true;
					case R.id.item_Settings:

						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								startAppAd.showAd();
								startAppAd.loadAd();
							}
						}, 2000);

						FragmentManager fragmentManager = getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager
								.beginTransaction();
						fragmentTransaction.setCustomAnimations(
								R.anim.slide_in, R.anim.back_slide_in,
								R.anim.slide_out, R.anim.back_slide_out);
						SettingsFragment fragment = new SettingsFragment();

						fragmentTransaction.replace(R.id.mainFrame, fragment,
								"SettingsFragment");
						fragmentTransaction.addToBackStack(null);
						fragmentTransaction.commit();
						return true;
					case R.id.item_help:

						Intent iEmail = new Intent(Intent.ACTION_SEND);
						iEmail.setType("message/rfc822");
						iEmail.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "info@creativeinfoway.com" });
						iEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
						try {
							startActivity(Intent.createChooser(iEmail,
									"Send mail..."));
						} catch (android.content.ActivityNotFoundException ex) {
							Toast.makeText(RecordingsActivityAgain.this,
									"There are no email clients installed.",
									Toast.LENGTH_SHORT).show();
						}
						return true;
					case R.id.item_rate:
						RateThisApp.showRateDialog(RecordingsActivityAgain.this);
						return true;
					default:
						return false;
					}
				}
			});

			popupMenu.getMenuInflater().inflate(R.menu.popup_menu,
					popupMenu.getMenu());

			popupMenu.show();
		} else if (v == imgRecording) {
			if (SecurePreferences.getBooleanPreference(this,
					Constants.PREF_RECORD_CALLS)) {
				SecurePreferences.savePreferences(this,
						Constants.PREF_RECORD_CALLS, false);
				imgRecording.setImageResource(R.drawable.recording_off);
			} else {
				SecurePreferences.savePreferences(this,
						Constants.PREF_RECORD_CALLS, true);
				imgRecording.setImageResource(R.drawable.recording_on);
			}
		} else if (v == imgBack) {
			isLongpress = false;
			imgBack.setVisibility(View.GONE);
			imgDelete.setVisibility(View.GONE);
			imgMenu.setVisibility(View.VISIBLE);
			imgRecording.setVisibility(View.VISIBLE);
			for (int i = 0; i < dataContainer.size(); i++) {
				RecordingsDataContainer container = dataContainer.get(i);
				container.isChecked = false;
			}
			adapter.notifyDataSetChanged();

		} else if (v == imgDelete) {

			boolean deleted = false;
			if (!isSearch) {
				for (int i = dataContainer.size() - 1; i >= 0; i--) {
					RecordingsDataContainer container = dataContainer.get(i);
					if (container.isChecked) {
						container.audio.delete();
						deleted = true;
						dataContainer.remove(i);
						adapter.notifyItemRemoved(i);
						adapter.notifyItemRangeChanged(0, dataContainer.size());
					}
				}
				if (!deleted) {
					Toast.makeText(RecordingsActivityAgain.this,
							"Make Selection by Tap on Recordings",
							Toast.LENGTH_LONG).show();
				}
			} else {
				for (int i = tempContainer.size() - 1; i >= 0; i--) {
					RecordingsDataContainer container = tempContainer.get(i);
					if (container.isChecked) {
						container.audio.delete();
						deleted = true;
						tempContainer.remove(i);
						adapter.notifyItemRemoved(i);
						adapter.notifyItemRangeChanged(0, dataContainer.size());
						for (int j = dataContainer.size() - 1; j >= 0; j--) {
							RecordingsDataContainer data = dataContainer.get(j);
							if (container.audio.getAbsolutePath().equals(
									data.audio.getAbsolutePath())) {
								dataContainer.remove(j);
							}
						}
					}
				}
				if (!deleted) {
					Toast.makeText(RecordingsActivityAgain.this,
							"Make Selection by Tap on Recordings",
							Toast.LENGTH_LONG).show();
				}

			}

		} else if (v == imgSearch) {
			tempContainer = new ArrayList<RecordingsDataContainer>();
			String searchStr = edtSearch.getText().toString().trim();
			for (int i = 0; i < dataContainer.size(); i++) {
				RecordingsDataContainer container = dataContainer.get(i);
				container.isChecked = false;
			}
			if (!searchStr.equals("")) {
				for (int i = 0; i < dataContainer.size(); i++) {
					RecordingsDataContainer data = dataContainer.get(i);
					if (data.name.toLowerCase(Locale.getDefault()).contains(
							searchStr.toLowerCase())) {
						tempContainer.add(data);
					}
				}
				if (tempContainer.size() > 0) {
					visibleTempDataContainer();
				} else {
					recyclerView.setVisibility(View.GONE);
				}
			} else {
				visibleMainDataContainer();
			}
		} else if (v == imgClose) {
			relSearch.setVisibility(View.GONE);
			isSearch = false;
			visibleMainDataContainer();
		}
	}

	public void visibleMainDataContainer() {
//		adapter = new RecordingsAdapter(dataContainer);
		recyclerView.setAdapter(adapter);
		recyclerView.setVisibility(View.VISIBLE);
	}

	public void visibleTempDataContainer() {
//		adapter = new RecordingsAdapter(tempContainer);
		recyclerView.setAdapter(adapter);
		recyclerView.setVisibility(View.VISIBLE);
	}

	public void refineSearch(String searchStr) {
		for (int i = 0; i < dataContainer.size(); i++) {
			RecordingsDataContainer container = dataContainer.get(i);
			container.isChecked = false;
		}
		tempContainer = new ArrayList<RecordingsDataContainer>();
		if (!searchStr.equals("")) {
			for (int i = 0; i < dataContainer.size(); i++) {
				RecordingsDataContainer data = dataContainer.get(i);
				if (data.name.toLowerCase().contains(searchStr.toLowerCase())) {
					tempContainer.add(data);
				}
			}
			if (tempContainer.size() > 0) {
				visibleTempDataContainer();
			} else {
				recyclerView.setVisibility(View.GONE);
			}
		} else {
			tempContainer = dataContainer;
			visibleTempDataContainer();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startAppAd.onResume();
	}

}
