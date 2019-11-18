package com.example.nagidictionary.ui.flashcard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.nagidictionary.MainActivity;
import com.example.nagidictionary.R;
import com.example.nagidictionary.WordActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dbhelpers.DatabaseAccess;
import model.Word;

import static android.app.Activity.RESULT_OK;

public class FlashCardFragment extends Fragment {
    public final static int REQUEST_SPEAK = 456;
    private ViewPager mVpFlashCard;
    private FlashCardFragmentAdapter adapter;
    private ArrayList<Word> mList;
    private int currentPage = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_flashcard, container, false);
        mVpFlashCard = root.findViewById(R.id.vp_flash);
        DatabaseAccess dbAccess = DatabaseAccess.getInstance(getContext(), MainActivity.DATABASE_EN_VIE);
        mList = dbAccess.getListRandomForFlashCard(20);
        adapter = new FlashCardFragmentAdapter(getFragmentManager(), mList);
        mVpFlashCard.setAdapter(adapter);
        mVpFlashCard.setCurrentItem(currentPage);
        return root;
    }
}