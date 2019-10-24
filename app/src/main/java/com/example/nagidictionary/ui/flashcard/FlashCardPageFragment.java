package com.example.nagidictionary.ui.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.nagidictionary.MainActivity;
import com.example.nagidictionary.R;
import com.example.nagidictionary.WordActivity;
import com.example.nagidictionary.WordDetailsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Word;

import static android.app.Activity.RESULT_OK;

public class FlashCardPageFragment extends Fragment {
    public final static int REQUEST_SPEAK = 456;
    private Word word;
    private int page;
    private TextView mTvWord;
    private TextView mTvMeaning;
    private TextView mTvListened;
    private TextView mTvScoreLabel;
    private TextView mTvScore;
    private TextView mTvGoDef;
    private FloatingActionButton mFabListen;
    private FloatingActionButton mFabSpeak;
    private static TextToSpeech textToSpeech = null;

    public static FlashCardPageFragment newInstance(int page, Word word) {
        FlashCardPageFragment fragmentFirst = new FlashCardPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        bundle.putSerializable("word", word);
        fragmentFirst.setArguments(bundle);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page", 0);
        word = (Word) getArguments().getSerializable("word");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcard_page, container, false);
        mTvWord = view.findViewById(R.id.tv_word);
        mTvMeaning = view.findViewById(R.id.tv_meaning);
        mTvScoreLabel = view.findViewById(R.id.tv_ScoreLabel);
        mTvScore = view.findViewById(R.id.tv_ScoreResult);
        mTvListened = view.findViewById(R.id.tv_listenedWord);
        mTvGoDef = view.findViewById(R.id.tv_goto_definition);
        mFabListen = view.findViewById(R.id.fab_flash_listen);
        mFabSpeak = view.findViewById(R.id.fab_flash_speak);

        mTvWord.setText(word.getName());
        mTvMeaning.setText(getWordMeaning(word.getContent()));
        addEvent();
        return view;
    }

    private void addEvent() {
        mFabSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSpeakButtonHandler();
            }
        });
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
            });
        }
        mFabListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListenButtonHandler(word.getName());
            }
        });
        mTvGoDef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickViewDefinition();
            }
        });
    }

    public void onClickListenButtonHandler(String wordToSpeak) {
        textToSpeech.speak(wordToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onClickSpeakButtonHandler() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak it loud!");
        try {
            startActivityForResult(intent, REQUEST_SPEAK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onClickViewDefinition() {
        Intent intent = new Intent(getActivity(), WordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("word", word);
        intent.putExtra("dictionaryCOde",
                MainActivity.DATABASE_EN_VIE);
        intent.putExtra("package", bundle);
        startActivity(intent);
    }

    public String getWordMeaning(String content) {
        Pattern pattern = Pattern.compile("<ul><li>(.*?)<");
        Matcher matcher = pattern.matcher(content);
        String intro = "";
        if (matcher.find()) {
            intro = matcher.group(1);
        } else {
            intro = content;
        }
        return intro;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_SPEAK) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mTvScore.setText(getScore(result.get(0), word.getName()));
            mTvListened.setText(result.get(0));
        }
    }

    private int getScore(String listened, String name) {
        int score = 0;
        if (listened.equals(name)) {
            score = 100;
        }
        return score;
    }
}
