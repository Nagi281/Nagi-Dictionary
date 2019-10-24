package com.example.nagidictionary.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.nagidictionary.MainActivity;
import com.example.nagidictionary.R;
import com.google.android.material.tabs.TabLayout;

import model.Word;

public class FavoritesFragment extends Fragment {
    private static Word word;
    private ViewPager mVpWord;
    FavoriteAdapter adapterViewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        mVpWord = root.findViewById(R.id.vp_favorite);
        adapterViewPager = new FavoriteAdapter(getFragmentManager());
        mVpWord.setAdapter(adapterViewPager);
        mVpWord.setCurrentItem(0);
        TabLayout mTabLayout = root.findViewById(R.id.sliding_tabs);
        mTabLayout.setupWithViewPager(mVpWord);
        return root;
    }

    public class FavoriteAdapter extends FragmentStatePagerAdapter {
        private int NUM_ITEMS = 2;
        private String[] titles = {"English - Vietnamese", "Vietnamese - English"};

        public FavoriteAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return favoriteWord_Fragment.newInstance(0, MainActivity.DATABASE_EN_VIE);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return favoriteWord_Fragment.newInstance(1, MainActivity.DATABASE_VIE_EN);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
