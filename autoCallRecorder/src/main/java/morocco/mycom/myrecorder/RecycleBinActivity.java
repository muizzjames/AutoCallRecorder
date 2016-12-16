package morocco.mycom.myrecorder;

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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import morocco.mycom.myrecorder.RecycleBinActivity.RecordingsAdapter.RecordingViewHolder;
import morocco.mycom.myrecorder.utils.CustomRecyclerView;
import morocco.mycom.myrecorder.utils.CustomTextViewRoboto;
import morocco.mycom.myrecorder.utils.GetOption;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

public class RecycleBinActivity extends FragmentActivity  {

	private static CustomRecyclerView recyclerView;
	public static Adapter<RecordingViewHolder> adapter;
	LinearLayoutManager layoutManager;

	ImageView mImgCancel, mImgSearch, mImgOrderby,  mImgBackSearch;
	CustomTextViewRoboto mViewTitle;
	EditText mTxtSearch;

	ImageView mImgBack, mImgBackup, mImgDelete, mImgSellectAll;

	RelativeLayout mHeader, mToolbar;

	boolean isLongpress = false, isSearch = false, isOrderby = false,isSelectAll = false;

	ImageLoader imageLoader = ImageLoader.getInstance();

	public static List<RecordingsDataContainer> dataContainer;
	public static List<RecordingsDataContainer> tempContainer;

	private StartAppAd startAppAd = new StartAppAd(this);

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.recycle_bin);

		StartAppSDK.init(RecycleBinActivity.this, "106508271", "205945600",
				true);

		recyclerView = (CustomRecyclerView) findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);

		imageLoader.init(GetOption.getConfig(this));




		new LoadDeletedRecordings().execute();

		initHeaderAndToolbar();

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

	}


	public void clickSearchButton(){
		mImgSearch.setVisibility(View.GONE);
		mViewTitle.setVisibility(View.GONE);
		mImgCancel.setVisibility(View.GONE);

		mImgBackSearch.setVisibility(View.VISIBLE);
		mTxtSearch.setVisibility(View.VISIBLE);

		isSearch = true;
	}
	public void clickSearchBackButton(){
		mImgSearch.setVisibility(View.VISIBLE);
		mViewTitle.setVisibility(View.VISIBLE);
		mImgCancel.setVisibility(View.VISIBLE);

		mImgBackSearch.setVisibility(View.GONE);
		mTxtSearch.setVisibility(View.GONE);

		isSearch = false;
	}
	public void clickDeleteButton(){
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
				Toast.makeText(RecycleBinActivity.this,
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
				Toast.makeText(RecycleBinActivity.this,
						"Make Selection by Tap on Recordings",
						Toast.LENGTH_LONG).show();
			}
		}
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
			isOrderby = true;
		}
	}

	public void clickBackupButton(){
		boolean deleted = false;
		if (!isSearch) {
			for (int i = dataContainer.size() - 1; i >= 0; i--) {
				RecordingsDataContainer container = dataContainer.get(i);
				if (container.isChecked) {

					File amr = new File(Constants.StoragePath + container.audio.getName());
					amr.getParentFile().mkdirs();

					Log.d("22222", container.audio.getName());

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

				}
			}
			if (!deleted) {
				Toast.makeText(RecycleBinActivity.this,
						"Make Selection by Tap on Recordings",
						Toast.LENGTH_LONG).show();
			}
		} else {
			for (int i = tempContainer.size() - 1; i >= 0; i--) {
				RecordingsDataContainer container = tempContainer.get(i);
				if (container.isChecked) {

					File amr = new File(Constants.StoragePath + container.audio.getName());
					amr.getParentFile().mkdirs();

					Log.d("22222", container.audio.getName());

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
				Toast.makeText(RecycleBinActivity.this,
						"Make Selection by Tap on Recordings",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	public void clickSelectAllButton(){
		if (isSelectAll == false){
			for (int i = 0; i < dataContainer.size(); i ++){
				dataContainer.get(i).isChecked = true;
			}
			visibleMainDataContainer();
			isSelectAll = true;
		}else{
			for (int i = 0; i < dataContainer.size(); i ++){
				dataContainer.get(i).isChecked = false;
			}
			visibleMainDataContainer();
			mHeader.setVisibility(View.VISIBLE);
			mToolbar.setVisibility(View.GONE);
			isSelectAll = false;
		}


	}
	public void initHeaderAndToolbar(){

		mHeader = (RelativeLayout)findViewById(R.id.relHeader);

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
		mImgCancel = (ImageView)findViewById(R.id.imageCancelButton);
		mImgCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RecycleBinActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		mImgBackSearch = (ImageView)findViewById(R.id.imageBackSearch);
		mImgBackSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickSearchBackButton();
			}
		});
		mImgOrderby = (ImageView)findViewById(R.id.imgOrder);
		mImgOrderby.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickOrderbyButton();
			}
		});
		mImgSearch = (ImageView)findViewById(R.id.imageSearchButton);
		mImgSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickSearchButton();
			}
		});


		mToolbar = (RelativeLayout)findViewById(R.id.relSearch);

		mImgBack = (ImageView)findViewById(R.id.imageBackButton);
		mImgBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mToolbar.setVisibility(View.GONE);
				mHeader.setVisibility(View.VISIBLE);
			}
		});
		mImgBackup = (ImageView)findViewById(R.id.imageBackupButton);
		mImgBackup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickBackupButton();
				mHeader.setVisibility(View.VISIBLE);
				mToolbar.setVisibility(View.GONE);
			}
		});
		mImgDelete = (ImageView)findViewById(R.id.imageDeleteButton);
		mImgDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickDeleteButton();
				mHeader.setVisibility(View.VISIBLE);
				mToolbar.setVisibility(View.GONE);
			}
		});
		mImgSellectAll = (ImageView)findViewById(R.id.imageSellectAll);
		mImgSellectAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickSelectAllButton();
			}
		});

		mToolbar.setVisibility(View.GONE);
	}
	public class LoadDeletedRecordings extends AsyncTask<Void, Void, Void> {

		ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(RecycleBinActivity.this);
			dialog.setMessage("Loading Deleted Recordings...");
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {

				File dir = new File(Constants.StorageDeletePath);
				dataContainer = new ArrayList<RecordingsDataContainer>();
				if (dir.exists()) {
					String[] dlist = dir.list();
					for (int i = dlist.length - 1; i >= 0; i--) {
						RecordingsDataContainer container = new RecordingsDataContainer();
						String data = dlist[i];
						File f = new File(Constants.StorageDeletePath + data);
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

				adapter = new RecordingsAdapter(dataContainer);
				recyclerView.setAdapter(adapter);
			}
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
			RecyclerView.Adapter<RecordingsAdapter.RecordingViewHolder> {
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

				}
			});
			holder.relMain.setOnLongClickListener(new OnLongClickListener() {

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

	public void visibleMainDataContainer() {
		adapter = new RecordingsAdapter(dataContainer);
		recyclerView.setAdapter(adapter);
		recyclerView.setVisibility(View.VISIBLE);
	}

	public void visibleTempDataContainer() {
		adapter = new RecordingsAdapter(tempContainer);
		recyclerView.setAdapter(adapter);
		recyclerView.setVisibility(View.VISIBLE);
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
	}

}
