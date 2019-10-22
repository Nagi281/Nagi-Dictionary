package com.example.nagidictionary;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

import dbhelpers.DatabaseAccess;
import model.Word;

public class WordDetailsFragment extends Fragment {
    // Store instance variables
    private int wordId;
    private int page;
    private String dictionaryCode;
    private DatabaseAccess dbAccess;
    private WebView webView;
    private FloatingActionButton mFabRead;
    private FloatingActionButton mFabLike;
    private TextToSpeech textToSpeech = null;
    private Word word;
    private static final String STYLE ="<style>\n" +
            "            .title {\n" +
            "                color: green;\n" +
            "                font-size: 20px;\n" +
            "            }\n" +
            "            .type {\n" +
            "                color: red;\n" +
            "                font-size: 20px;\n" +
            "            }\n" +
            "            span {\n" +
            "                font-size: 18px;\n" +
            "            }\n" +
            "            li {\n" +
            "                font-size: 18px;\n" +
            "            }\n" +
            "     </style>";

    // newInstance constructor for creating fragment with arguments
    public static WordDetailsFragment newInstance(int page,String dictionaryCode, int wordId) {
        WordDetailsFragment fragmentFirst = new WordDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("someInt", page);
        bundle.putInt("someWordId", wordId);
        bundle.putString("dictionary",dictionaryCode);
        fragmentFirst.setArguments(bundle);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        wordId = getArguments().getInt("someWordId",260);
        dictionaryCode =getArguments().getString("dictionary","anh_viet");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);
        webView = view.findViewById(R.id.wv_web);
        dbAccess = DatabaseAccess.getInstance(getActivity(),dictionaryCode);
        word = dbAccess.getWordsById(wordId);
        showStaticContent(word.getName(),word.getContent());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    String[] words = url.split("//");
                    changeToClickedLink(words[1].toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        mFabRead = view.findViewById(R.id.fab_speak);
        getTextToSpeakInstance();
        mFabRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = word.getName();
                textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        mFabLike = view.findViewById(R.id.fab_like);
        mFabLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbAccess.isLiked(wordId)){
                    Toast.makeText(getContext(),"This word is already in Favorites",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if(dbAccess.addIntoFavorite(wordId)) {
                        Toast.makeText(getContext(),"Added to Favorites",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),"Something Wrong. Try again!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }
    public void getTextToSpeakInstance() {
        if(textToSpeech == null) {
            textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                }
            });
        }
    }
    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();

    }
    private void changeToClickedLink(String word){
        Word newWord = dbAccess.getWordByName(word);
        if(newWord!=null) {
            Intent intent = new Intent(getActivity(), WordActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("word",newWord);
            intent.putExtra("dictionaryCOde",dictionaryCode);
            intent.putExtra("package",bundle);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(),"No word in database", Toast.LENGTH_SHORT).show();
        }

    }
    private void showStaticContent(String name,String content)  {
        String staticContent= "<h1>"+name+"</h1>\n" +content + STYLE;
        webView.loadData(staticContent, "text/html", "UTF-8");
    }
}

