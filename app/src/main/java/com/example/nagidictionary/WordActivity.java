package com.example.nagidictionary;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import model.Word;

public class WordActivity extends AppCompatActivity {
    private static Word word;
    private ViewPager mVpWord;
    FragmentPagerAdapter adapterViewPager;
    private static String dictionaryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_activity);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras().getBundle("package");
        dictionaryCode = intent.getExtras().getString("dictionaryCOde");
        word = (Word) bundle.getSerializable("word");
        try {
            getSupportActionBar().setTitle(word.getName() + "");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
        }

        mVpWord = findViewById(R.id.vp_Word);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        mVpWord.setAdapter(adapterViewPager);
        mVpWord.setCurrentItem(1);
        mVpWord.setOffscreenPageLimit(3);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
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
                    return WordDetailsFragment.newInstance(0, dictionaryCode, word.getId() - 1);
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return WordDetailsFragment.newInstance(1, dictionaryCode, word.getId());
                case 2: // Fragment # 1 - This will show SecondFragment
                    return WordDetailsFragment.newInstance(2, dictionaryCode, word.getId() + 1);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
}
