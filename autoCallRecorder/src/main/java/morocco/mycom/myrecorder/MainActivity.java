package morocco.mycom.myrecorder;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import morocco.mycom.myrecorder.helper.SecurePreferences;
import morocco.mycom.myrecorder.utils.CustomTextViewRoboto;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity {

	private static final int RECORDING_NOTIFICATION_ID = 1;
	private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

	MainActivity mActivity;

	private StartAppAd startAppAd = new StartAppAd(this);

	ImageView mImgMenu, mImgSearch, mImgOrderby, mImgPlaystore, mImgBackSearch, mImgClose;
	CustomTextViewRoboto mViewTitle;
	EditText mTxtSearch;

	ImageView mImgBack, mImgDelete, mImgShare, mImgStar, mImgSellectAll, mImgBackMenu;

	RelativeLayout mHeader, mToolbar;



	private LeftNavAdapter navadapter;
	ImageView mImgRecording;
	TextView mTxtRecording;

	ViewPager pager;
	FragmentTab1 tab1;
	FragmentTab2 tab2;
	FragmentTab3 tab3;
	FragmentTab4 tab4;
	MerchantPagerAdapter merchantPagerAdapter;

	public ImageLoader imageLoader = ImageLoader.getInstance();
	public boolean isLongpress = false, isSearch = false, isSelectAll = false;
	boolean isOrderby = false;

	public static List<RecordingsDataContainer> dataContainer;
	public static List<RecordingsDataContainer> tempContainer;
	public static RecyclerView.Adapter<RecordingsAdapter.RecordingViewHolder> adapter;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.recording_list_activity_back);
		mActivity = this;

		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(MainActivity.this,
				Manifest.permission.READ_CONTACTS)
				!= PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					Manifest.permission.READ_CONTACTS)&&ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					Manifest.permission.PROCESS_OUTGOING_CALLS)&&ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					Manifest.permission.RECORD_AUDIO)&&ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

			}  else {
				ActivityCompat.requestPermissions(MainActivity.this,
						new String[]{Manifest.permission.READ_CONTACTS,
								Manifest.permission.PROCESS_OUTGOING_CALLS,
								Manifest.permission.RECORD_AUDIO,
								Manifest.permission.WRITE_EXTERNAL_STORAGE},
						MY_PERMISSIONS_REQUEST_READ_CONTACTS);
			}
		}

		StartAppSDK.init(MainActivity.this, "106508271", "205945600",
				true);

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

		pager = (ViewPager) findViewById(R.id.pager);
		imageLoader.init(GetOption.getConfig(this));

		navadapter = new LeftNavAdapter(this, getResources().getStringArray(
				R.array.arr_left_nav_list));
		final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
		final ListView navList = (ListView) findViewById(R.id.drawer);

		View header = getLayoutInflater().inflate(R.layout.left_nav_header_one,
				null);
		mTxtRecording = (TextView) header.findViewById(R.id.textRecording);
		mImgRecording = (ImageView) header.findViewById(R.id.imageRecording);
		mImgRecording.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickRecordingButton();
			}
		});


		if (SecurePreferences.getBooleanPreference(this,
				Constants.PREF_RECORD_CALLS)) {
			mImgRecording.setImageResource(R.drawable.recording_on);
			updateNotification(true);
		} else {
			mImgRecording.setImageResource(R.drawable.recording_off);
			updateNotification(false);
		}

		navList.addHeaderView(header);
		navList.setAdapter(navadapter);
		navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
									long arg3) {
				drawer.closeDrawers();
				if (pos != 0)
					launchFragment(pos - 1);
				else
					launchFragment(-2);
			}
		});
		initHeaderAndToolbar();
		mImgMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawer.openDrawer(navList);
			}
		});

		AdView adView = (AdView) findViewById(R.id.adView_main);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		new LoadRecordings().execute();
	}

	private void updateNotification(Boolean status) {
		Context context = getApplicationContext();

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		if (status) {
			int icon = R.drawable.ic_notification_true;
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
					.setContentTitle("Enable")
					.setAutoCancel(false)
					.setContentIntent(pendingIntent)
					.setSmallIcon(icon);
			mNotificationManager
					.notify(RECORDING_NOTIFICATION_ID, notification.build());


		} else {
			int icon = R.drawable.ic_notification_false;
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
					.setContentTitle("Disable")
					.setAutoCancel(false)
					.setContentIntent(pendingIntent)
					.setSmallIcon(icon);
			mNotificationManager
					.notify(RECORDING_NOTIFICATION_ID, notification.build());
		}
	}

	public void clickRecordingButton(){
		if (SecurePreferences.getBooleanPreference(this,
				Constants.PREF_RECORD_CALLS)) {
			SecurePreferences.savePreferences(this,
					Constants.PREF_RECORD_CALLS, false);
			mTxtRecording.setText("Disable");
			mImgRecording.setImageResource(R.drawable.recording_off);
			updateNotification(false);

		} else {
			SecurePreferences.savePreferences(this,
					Constants.PREF_RECORD_CALLS, true);
			mTxtRecording.setText("Enable");
			mImgRecording.setImageResource(R.drawable.recording_on);
			updateNotification(true);
		}
	}
	public void initHeaderAndToolbar(){

		mTxtSearch = (EditText) findViewById(R.id.edittextSearch);
		mTxtSearch.addTextChangedListener(new TextWatcher() {

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

		mViewTitle = (CustomTextViewRoboto)findViewById(R.id.view);
		mImgMenu = (ImageView)findViewById(R.id.imgMenu);

		mImgBackSearch = (ImageView)findViewById(R.id.imageBackSearch);
		mImgBackSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickCloseButton();
			}
		});
		mImgClose = (ImageView)findViewById(R.id.imageClose);
		mImgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickCloseButton();
			}
		});

		mImgSearch = (ImageView)findViewById(R.id.imageSearchButton);
		mImgSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickSearchButton();
			}
		});
		mImgOrderby = (ImageView)findViewById(R.id.imgOrder);
		mImgOrderby.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickOrderbyButton();
			}
		});
		mImgPlaystore = (ImageView)findViewById(R.id.imgPlayStore);
		mImgPlaystore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
				}
			}
		});

		mImgBack = (ImageView)findViewById(R.id.imageBackButton);
		mImgBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickBackMenu();
			}
		});
		mImgDelete = (ImageView)findViewById(R.id.imageDeleteButton);
		mImgDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean deleted = false;
				if (!isSearch) {
					for (int i = dataContainer.size() - 1; i >= 0; i--) {
						RecordingsDataContainer container = dataContainer.get(i);
						if (container.isChecked) {
							File amr = new File(Constants.StorageDeletePath + container.audio.getName());
							amr.getParentFile().mkdirs();
							try {
								copy(container.audio, amr);
							} catch (IOException e) {
								e.printStackTrace();
							}
							container.audio.delete();
							deleted = true;
							dataContainer.remove(i);
							adapter.notifyItemRemoved(i);
							adapter.notifyItemRangeChanged(0, dataContainer.size());
							updateFragments();
						}
					}
					if (!deleted) {
						Toast.makeText(MainActivity.this,
								"Make Selection by Tap on Recordings",
								Toast.LENGTH_LONG).show();
					}
				} else {
					for (int i = tempContainer.size() - 1; i >= 0; i--) {
						RecordingsDataContainer container = tempContainer.get(i);
						if (container.isChecked) {
							File amr = new File(Constants.StorageDeletePath + container.audio.getName());
							amr.getParentFile().mkdirs();
							try {
								copy(container.audio, amr);
							} catch (IOException e) {
								e.printStackTrace();
							}
							container.audio.delete();
							deleted = true;
							tempContainer.remove(i);
							adapter.notifyItemRemoved(i);
							adapter.notifyItemRangeChanged(0, dataContainer.size());
							updateFragments();

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
						Toast.makeText(MainActivity.this,
								"Make Selection by Tap on Recordings",
								Toast.LENGTH_LONG).show();
					}
				}

				mHeader.setVisibility(View.VISIBLE);
				mToolbar.setVisibility(View.GONE);
			}
		});
		mImgShare = (ImageView)findViewById(R.id.imageShare);
		mImgShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ArrayList<Uri> imageUris = new ArrayList<Uri>();
				for (int i = dataContainer.size() - 1; i >= 0; i--) {
					RecordingsDataContainer container = dataContainer.get(i);
					if (container.isChecked) {
						Uri path = Uri.fromFile(container.audio);
						imageUris.add(path); // Add your image URIs here
					}
				}
				Intent iEmail = new Intent(Intent.ACTION_SEND_MULTIPLE);
				iEmail.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
				iEmail.setType("*/*");

				for (int i = 0; i < dataContainer.size(); i ++){
					dataContainer.get(i).isChecked = false;
				}
				visibleMainDataContainer();
				updateFragments();
				mHeader.setVisibility(View.VISIBLE);
				mToolbar.setVisibility(View.GONE);

				try {
					startActivity(Intent.createChooser(iEmail,
							"Send file..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(MainActivity.this,
							"There are no email clients installed.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mImgStar = (ImageView)findViewById(R.id.imageStar);
		mImgStar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				imageShare();
				mHeader.setVisibility(View.VISIBLE);
				mToolbar.setVisibility(View.GONE);
			}
		});
		mImgSellectAll = (ImageView)findViewById(R.id.imageSellectAll);
		mImgSellectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				clickSelectAllButton();

			}
		});
		mImgBackMenu = (ImageView)findViewById(R.id.imgBack);
		mImgBackMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickBackMenu();
			}
		});

		mHeader = (RelativeLayout)findViewById(R.id.relHeader);
		mToolbar = (RelativeLayout)findViewById(R.id.relSearch);
		mToolbar.setVisibility(View.GONE);
	}

	public void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public void clickSelectAllButton(){
		if (isSelectAll == false){
			for (int i = 0; i < dataContainer.size(); i ++){
				dataContainer.get(i).isChecked = true;
			}
			visibleMainDataContainer();
			updateFragments();
			isSelectAll = true;
		}else{
			for (int i = 0; i < dataContainer.size(); i ++){
				dataContainer.get(i).isChecked = false;
			}
			visibleMainDataContainer();
			updateFragments();
			mHeader.setVisibility(View.VISIBLE);
			mToolbar.setVisibility(View.GONE);
			isSelectAll = false;
		}
	}
	public void clickBackMenu(){
		for (int i = 0; i < dataContainer.size(); i ++){
			dataContainer.get(i).isChecked = false;
		}
		visibleMainDataContainer();
		updateFragments();
		mHeader.setVisibility(View.VISIBLE);
		mToolbar.setVisibility(View.GONE);
		isSelectAll = false;
	}
	public void clickSearchButton(){
		mImgSearch.setVisibility(View.GONE);
		mViewTitle.setVisibility(View.GONE);
		mImgMenu.setVisibility(View.GONE);

		mImgBackSearch.setVisibility(View.VISIBLE);
		mTxtSearch.setVisibility(View.VISIBLE);
		mImgClose.setVisibility(View.VISIBLE);

		isSearch = true;
	}
	public void clickCloseButton(){
		mImgSearch.setVisibility(View.VISIBLE);
		mViewTitle.setVisibility(View.VISIBLE);
		mImgMenu.setVisibility(View.VISIBLE);

		mImgBackSearch.setVisibility(View.GONE);
		mTxtSearch.setVisibility(View.GONE);
		mTxtSearch.setText("");
		mImgClose.setVisibility(View.GONE);

		isSearch = false;
		visibleMainDataContainer();
		updateFragments();
	}

	public void clickOrderbyButton(){
		if (isOrderby == true){
			mImgOrderby.setImageResource(R.drawable.ic_order_up);
			Collections.sort(dataContainer, new Comparator() {
				public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
					//use instanceof to verify the references are indeed of the type in question
					return ((RecordingsDataContainer) synchronizedListOne).name
							.compareTo(((RecordingsDataContainer) synchronizedListTwo).name);
				}
			});
			visibleMainDataContainer();
			updateFragments();
			isOrderby = false;
		}else{
			mImgOrderby.setImageResource(R.drawable.ic_order_down);
			Collections.sort(dataContainer, new Comparator() {
				public int compare(Object synchronizedListOne, Object synchronizedListTwo) {
					//use instanceof to verify the references are indeed of the type in question
					return ((RecordingsDataContainer) synchronizedListTwo).name
							.compareTo(((RecordingsDataContainer) synchronizedListOne).name);
				}
			});
			visibleMainDataContainer();
			updateFragments();
			isOrderby = true;
		}
	}

	public void updateFragments(){
		tab1.updateRecycleView();
		tab2.updateRecycleView();
		tab3.updateRecycleView();
		tab4.updateRecycleView();
	}
	public void launchFragment(int pos)
	{

		String title = null;
		if (pos == -1)
		{
			title = "Your Match";
			Toast.makeText(getApplicationContext(), title, Toast.LENGTH_LONG).show();
		}
		else if (pos == -2)
		{
			title = "Profile";

			Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
		}
		else if (pos == 0)
		{
			title = "Home";
			Intent intent = new Intent(MainActivity.this, RecycleBinActivity.class);
			startActivity(intent);
			finish();
			Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
		}
		else if (pos == 1)
		{
			title = "Find Match";


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

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					startAppAd.showAd();
					startAppAd.loadAd();
				}
			}, 2000);

			Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
		}
		else if (pos == 2)
		{
			title = "Chat with Nikita";
			final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
			}
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					startAppAd.showAd();
					startAppAd.loadAd();
				}
			}, 2000);

			Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
		}
		else if (pos == 3)
		{
			title = "Video Chat with Nikita";
			RateThisApp.showRateDialog(MainActivity.this);
			Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();

		}
		else if (pos == 4)
		{
			title = "Liked You";
			Intent iEmail = new Intent(Intent.ACTION_SEND);
			iEmail.setType("message/rfc822");
//			iEmail.putExtra(Intent.EXTRA_EMAIL,
//					new String[] { "info@creativeinfoway.com" });
			iEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
			try {
				startActivity(Intent.createChooser(iEmail,
						"Tell a friend"));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(MainActivity.this,
						"There are no email clients installed.",
						Toast.LENGTH_SHORT).show();
			}

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					startAppAd.showAd();
					startAppAd.loadAd();
				}
			}, 2000);

			Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
		}
		else if (pos == 5)
		{
			title = "Favorites";
			Intent iEmail = new Intent(Intent.ACTION_SEND);
			iEmail.setType("message/rfc822");
			iEmail.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "info@creativeinfoway.com" });
			iEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
			try {
				startActivity(Intent.createChooser(iEmail,
						"Help"));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(MainActivity.this,
						"There are no email clients installed.",
						Toast.LENGTH_SHORT).show();
			}
			Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
		}
		else if (pos == 6)
		{
			title = "Visitores";
			Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
		}

	}

	public void initPager(){
		tab1 = new FragmentTab1();
		tab2 = new FragmentTab2();
		tab3 = new FragmentTab3();
		tab4 = new FragmentTab4();

		List<Fragment> lst = new ArrayList<>();
		lst.add(tab1);
		lst.add(tab2);
		lst.add(tab3);
		lst.add(tab4);

		merchantPagerAdapter = new MerchantPagerAdapter(mActivity.getSupportFragmentManager(), lst);
		pager.setAdapter(merchantPagerAdapter);
	}

	public class LoadRecordings extends AsyncTask<Void, Void, Void> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage("Loading Recordings...");
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
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

						String phoneNo = data.substring(data.indexOf("(") + 1,
								data.indexOf(")"));

						if (null == phoneNo) {
							container.name = "";
						} else {

							ContentResolver cr = getContentResolver();
							Uri uri = Uri
									.withAppendedPath(
											ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
											Uri.encode(phoneNo));


							String[] projection = new String[] {
									ContactsContract.PhoneLookup.DISPLAY_NAME,
									ContactsContract.PhoneLookup._ID };
							String contactId = null;
							Cursor cursor = cr.query(uri, projection, null,
									null, null);

							if (cursor != null) {
								while (cursor.moveToNext()) {
									contactId = cursor
											.getString(cursor
													.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
								}
								cursor.close();
							}
							try {
//								container.imagePath = getPhotoUri(Integer.valueOf(contactId));
								container.imagePath = ContentUris
										.withAppendedId(
												ContactsContract.Contacts.CONTENT_URI,
												new Long(contactId));
							} catch (Exception e) {

							}

							uri = Uri.withAppendedPath(
									ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
									Uri.encode(phoneNo));


							Cursor c = cr.query(uri, new String[] {
									ContactsContract.PhoneLookup.DISPLAY_NAME,
									ContactsContract.PhoneLookup.PHOTO_URI }, null, null, null);
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
				adapter = new RecordingsAdapter(dataContainer);
			}
			initPager();
		}
	}
	public Uri getPhotoUri(int contactid) {
		Cursor photoCur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'", null, ContactsContract.Contacts.DISPLAY_NAME+" COLLATE LOCALIZED ASC");
		photoCur.moveToPosition(contactid);
		Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, photoCur.getLong(photoCur.getColumnIndex(ContactsContract.Contacts._ID)));
		Uri photo = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
		return photo;
	}

	public String getDuration(File f) {
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

	public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingViewHolder> {
		private List<RecordingsDataContainer> mDataset;
		MediaPlayer mp = null;

		public RecordingsAdapter(List<RecordingsDataContainer> contactList) {
			mDataset = contactList;
		}

		@Override
		public RecordingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
					holder.imgPerson,
					GetOption.getProfileOption(data.name),
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

			holder.relMain.setOnClickListener(new View.OnClickListener() {

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
			holder.relMain.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					isLongpress = true;
					mHeader.setVisibility(View.GONE);
					mToolbar.setVisibility(View.VISIBLE);

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
//			mHandler.postDelayed(mUpdateTimeTask, 100);
		}

		private Runnable mUpdateTimeTask = new Runnable() {
			public void run() {
				long totalDuration = mp.getDuration();
				long currentDuration = mp.getCurrentPosition();

				int progress = (int) (getProgressPercentage(currentDuration,
						totalDuration));
//				bar.setProgress(progress);
//				mHandler.postDelayed(this, 100);
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

	public void visibleTempDataContainer() {
		adapter = new RecordingsAdapter(tempContainer);
	}
	public void visibleMainDataContainer() {
		adapter = new RecordingsAdapter(dataContainer);
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
				updateFragments();
			}
		} else {
			tempContainer = dataContainer;
			visibleTempDataContainer();
			updateFragments();
		}
	}

	@Override
	public void onBackPressed() {
		if (isLongpress == true){
			clickBackMenu();
			isLongpress = false;
		}else {
			super.onBackPressed();
		}
	}
}
