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
    private PracticeAdapter mAdapter;
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
//        mAdapter = new PracticeAdapter(getContext(), R.layout.fragment_flashcard_page, mList);
//        mVpFlashCard.setAdapter(mAdapter);
//        mVpFlashCard.addOnPageChangeListener(
//                new ViewPager.OnPageChangeListener() {
//                    @Override
//                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                    }
//
//                    @Override
//                    public void onPageSelected(int position) {
//                        currentPage = position;
//                    }
//
//                    @Override
//                    public void onPageScrollStateChanged(int state) {
//
//                    }
//                }
//        );

        mVpFlashCard.setCurrentItem(currentPage);
        return root;
    }

    public class PracticeAdapter extends PagerAdapter {
        private Context mContext;
        private int mLayoutId;
        private ArrayList<Word> mList;
        private TextView mTvResult;
        private TextToSpeech textToSpeech = null;

        public PracticeAdapter(Context context, int layoutId, ArrayList<Word> list) {
            mContext = context;
            mLayoutId = layoutId;
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View layout = inflater.inflate(mLayoutId, container, false);
            final TextView mTvWord = layout.findViewById(R.id.tv_word);
            TextView mTvMean = layout.findViewById(R.id.tv_meaning);
            mTvResult = layout.findViewById(R.id.tv_listenedWord);
            FloatingActionButton mFabSpeak = layout.findViewById(R.id.fab_flash_speak);
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
            final Word word = mList.get(position);
            mTvWord.setText(word.getName());
            mTvMean.setText(getWordMeaning(word.getContent()));
            FloatingActionButton mFabListen = layout.findViewById(R.id.fab_flash_listen);
            mFabListen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListenButtonHandler(word.getName());
                }
            });
            container.addView(layout);
            TextView mTvDef = layout.findViewById(R.id.tv_goto_definition);
            mTvDef.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickViewDefinition(word);
                }
            });
            return layout;
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

        public void onClickViewDefinition(Word word) {
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

        public void setResultText(String s) {
            mTvResult.setText(s);
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == REQUEST_SPEAK) {
//            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            String word = result.get(0);
//            mAdapter.setResultText(word);
//        }
//    }
}