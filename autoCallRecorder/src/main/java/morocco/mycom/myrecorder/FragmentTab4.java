package morocco.mycom.myrecorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import morocco.mycom.myrecorder.utils.CustomRecyclerView;

import java.util.ArrayList;



public class FragmentTab4 extends Fragment{

    private MainActivity mActivity;

    private static CustomRecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    TextView tvDate;
    TextView tvCount;
    TextView tvSize;


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
        recyclerView.setAdapter(mActivity.adapter);

    }

}
