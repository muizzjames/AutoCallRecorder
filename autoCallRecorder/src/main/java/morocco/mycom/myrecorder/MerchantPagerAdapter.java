package morocco.mycom.myrecorder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MerchantPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 4;
    // Tab Titles
    private String tabtitles[] = new String[] { "ALL", "INCOMING","OUTGOING","IMPORTANT"};

    List<Fragment> lst;
    public MerchantPagerAdapter(FragmentManager fm, List<Fragment> lst) {
        super(fm);
        this.lst=lst;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
    @Override
    public Fragment getItem(int position) {
        return lst.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
