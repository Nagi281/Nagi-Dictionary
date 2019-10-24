package com.example.nagidictionary.ui.flashcard;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.nagidictionary.WordDetailsFragment;

import java.util.ArrayList;

import model.Word;

public class FlashCardFragmentAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Word> mList;

    public FlashCardFragmentAdapter(FragmentManager fm, ArrayList<Word> list) {
        super(fm);
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }


    @Override
    public Fragment getItem(int position) {
        return FlashCardPageFragment.newInstance(position, mList.get(position));
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mList.get(position).getName();
    }
}
