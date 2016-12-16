package morocco.mycom.myrecorder;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import morocco.mycom.myrecorder.utils.CustomRecyclerView;
import morocco.mycom.myrecorder.utils.GetOption;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;


public class FragmentTab3 extends Fragment{

    private MainActivity mActivity;

    private static CustomRecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    TextView tvDate;
    TextView tvCount;
    TextView tvSize;



    public static List<MainActivity.RecordingsDataContainer> tab2Container;
    public static RecyclerView.Adapter<RecordingsAdapter.RecordingViewHolder> tab2Adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmenttab1, container, false);
        mActivity = (MainActivity)getActivity();

        recyclerView = (CustomRecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);

        updateRecycleView();

        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvCount = (TextView) view.findViewById(R.id.tvCount);
        tvSize = (TextView) view.findViewById(R.id.tvSize);

        return view;
    }
    public void updateRecycleView(){
        if (mActivity == null){
            return;
        }
        if (mActivity.isSearch == true){
            tab2Container  = new ArrayList<MainActivity.RecordingsDataContainer>();
            for (int i = 0; i < mActivity.tempContainer.size(); i ++){
                if (mActivity.tempContainer.get(i).isIncomming == true)
                    tab2Container.add(mActivity.tempContainer.get(i));
            }
            tab2Adapter = new RecordingsAdapter(tab2Container);

            recyclerView.setAdapter(tab2Adapter);
        }
        else{
            tab2Container  = new ArrayList<MainActivity.RecordingsDataContainer>();
            for (int i = 0; i < mActivity.dataContainer.size(); i ++){
                if (mActivity.dataContainer.get(i).isIncomming == true)
                    tab2Container.add(mActivity.dataContainer.get(i));
            }

            tab2Adapter = new RecordingsAdapter(tab2Container);

            recyclerView.setAdapter(tab2Adapter);
        }
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

            mActivity.imageLoader.displayImage(data.imagePath.toString(),
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
