package morocco.mycom.myrecorder;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import morocco.mycom.myrecorder.utils.CustomRecyclerView;
import morocco.mycom.myrecorder.utils.GetOption;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;


public class FragmentTab1 extends Fragment{

    public ImageLoader imageLoader = ImageLoader.getInstance();

    private MainActivity mActivity;

    private static CustomRecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    TextView tvDate;
    TextView tvCount;
    TextView tvSize;



    public static List<MainActivity.RecordingsDataContainer> tab1Container;
    public static RecyclerView.Adapter<RecordingsAdapter.RecordingViewHolder> tab1Adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmenttab1, container, false);
        mActivity = (MainActivity)getActivity();

        recyclerView = (CustomRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);

        imageLoader.init(GetOption.getConfig(mActivity));
        updateRecycleView();

        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvCount = (TextView) view.findViewById(R.id.tvCount);
        tvSize = (TextView) view.findViewById(R.id.tvSize);

        return view;
    }
    public void updateRecycleView(){
        mActivity = (MainActivity)getActivity();
        if (mActivity == null){
            return;
        }
        if (mActivity.isSearch == true){
            tab1Container  = new ArrayList<MainActivity.RecordingsDataContainer>();
            tab1Container = mActivity.tempContainer;
            tab1Adapter = new RecordingsAdapter(tab1Container);

            recyclerView.setAdapter(tab1Adapter);
        }
        else{
            tab1Container  = new ArrayList<MainActivity.RecordingsDataContainer>();
            tab1Container = mActivity.dataContainer;
            tab1Adapter = new RecordingsAdapter(tab1Container);

            recyclerView.setAdapter(tab1Adapter);
        }
    }
    public Uri getPhotoUri(int contactid) {
        Cursor photoCur = mActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'", null, ContactsContract.Contacts.DISPLAY_NAME+" COLLATE LOCALIZED ASC");
        photoCur.moveToPosition(contactid);
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, photoCur.getLong(photoCur.getColumnIndex(ContactsContract.Contacts._ID)));
        Uri photo = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        return photo;
    }
    public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.RecordingViewHolder> {
        private List<MainActivity.RecordingsDataContainer> mDataset;
        MediaPlayer mp = null;

        public RecordingsAdapter(List<MainActivity.RecordingsDataContainer> contactList) {
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
            final MainActivity.RecordingsDataContainer data = mDataset.get(position);
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

            Log.d("###", data.imagePath.toString());

//            Uri contactphoto = getPhotoUri(contact_id);
//            holder.imgPerson.setImageURI(contactphoto);
//            if (holder.imgPerson.getDrawable() == null) {  // If no image is found.
//                holder.imgPerson.setImageResource(R.drawable.defaultImage);
//            }
//            String path = data.imagePath.getPath();
//            Bitmap bmp = BitmapFactory.decodeFile(path);
//            holder.imgPerson.setImageBitmap(bmp);
            imageLoader.displayImage(data.imagePath.toString(),
                    holder.imgPerson,
                    GetOption.getProfileOption(data.name),
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                            Log.d("###", "ImageLoader started");
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1,
                                                    FailReason arg2) {

                            Log.d("###", "ImageLoader Failed!");
                            Log.d("###", arg2.toString());
                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1,
                                                      Bitmap arg2) {
                            Log.d("###", "ImageLoader Complete!");
                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                            Log.d("###", "ImageLoader Cancelled!");
                        }
                    });
            if (holder.imgPerson.getDrawable() == null){
                Log.d("###", "ImageLoader null!");
            }

            holder.relMain.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mActivity.isLongpress) {
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

                        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager
                                .beginTransaction();
                        fragmentTransaction.setCustomAnimations(
                                R.anim.slide_in, R.anim.back_slide_in,
                                R.anim.slide_out, R.anim.back_slide_out);
                        MediaplayerFragment fragment = new MediaplayerFragment();

                        Bundle bundle = new Bundle();

                        bundle.putBoolean("isSearch", mActivity.isSearch);
                        bundle.putInt("position", position);

                        fragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.mainFrame, fragment,
                                "MediaplayerFragment");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }

                }
            });
            holder.relMain.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    mActivity.isLongpress = true;
                    mActivity.mHeader.setVisibility(View.GONE);
                    mActivity.mToolbar.setVisibility(View.VISIBLE);

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
}
