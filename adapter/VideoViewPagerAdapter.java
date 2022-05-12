package com.yeslabapps.fictionfocus.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.yeslabapps.fictionfocus.fragments.FilmSearchFragment;
import com.yeslabapps.fictionfocus.fragments.TagSearchFragment;
import com.yeslabapps.fictionfocus.fragments.VideoSearchFragment;

public class VideoViewPagerAdapter extends FragmentPagerAdapter {

    public VideoViewPagerAdapter(
            @NonNull FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = null;
        if (position == 0)
            fragment = new VideoSearchFragment();






        return fragment;
    }

    @Override
    public int getCount()
    {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        String title = null;
        if (position == 0)
            title = "Movie/Series";






        return title;
    }
}
