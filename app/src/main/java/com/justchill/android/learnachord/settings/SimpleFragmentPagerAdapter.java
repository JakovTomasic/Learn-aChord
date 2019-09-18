package com.justchill.android.learnachord.settings;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.justchill.android.learnachord.R;

// Fragment pager adapter for options' (SettingsActivity's) ViewPager
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter{

    private Context mContext;

    SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // Returns item (fragment) at given position
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new ChordsFragment();
            case 2:
                return new TonesFragment();
            case 3:
                return new PreferencesFragment();
            default:
                return new IntervalsFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    // Setting page titles
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        super.getPageTitle(position);
        switch (position)
        {
            case 1:
                return mContext.getString(R.string.settings_1_title);
            case 2:
                return mContext.getString(R.string.settings_2_title);
            case 3:
                return mContext.getString(R.string.settings_3_title);
            default:
                return mContext.getString(R.string.settings_0_title);
        }
    }
}
