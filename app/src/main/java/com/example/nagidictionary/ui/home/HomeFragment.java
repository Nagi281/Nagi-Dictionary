package com.example.nagidictionary.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nagidictionary.MainActivity;
import com.example.nagidictionary.R;
import com.example.nagidictionary.WordActivity;

import java.util.ArrayList;
import java.util.List;

import customAdapter.RecyclerItemClickListener;
import customAdapter.customApdater;
import customAdapter.searchAdapter;
import dbhelpers.DatabaseAccess;
import model.Word;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
    private static final int REQUEST_CODE = 1234;
    private RecyclerView recyclerView;
    private List<Word> wordList = new ArrayList<>();
    private customApdater customApdater;
    private ProgressBar progressBar;
    private LinearLayoutManager mLayoutManager;

    private AutoCompleteTextView mAcTvSearch;
    private ArrayList<String> searchList = new ArrayList<>();
    private searchAdapter searchAdapter;

    private ImageButton mBtnSpeak;

    private int currentIndex, totalItems, scrollOutItems, offset;
    private boolean isScrolling;
    private View root;
    private DatabaseAccess dbAccess;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        currentIndex = 120;
        offset = 10;
        dbAccess = DatabaseAccess.getInstance(getActivity(), MainActivity.DATABASE_EN_VIE);
        addControl();
        addEvent();
        fetchMoreData();
        return root;
    }

    private void addControl() {
        recyclerView = root.findViewById(R.id.rv_words);
        progressBar = root.findViewById(R.id.pb_load);
        wordList = new ArrayList<>();
        customApdater = new customApdater(wordList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(customApdater);

        mAcTvSearch = root.findViewById(R.id.acTv_search);
        searchAdapter = new searchAdapter(getContext(),
                R.layout.search_item, searchList);
        mAcTvSearch.setAdapter(searchAdapter);
        mBtnSpeak = root.findViewById(R.id.btn_speak);
    }

    private void addEvent() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentIndex = mLayoutManager.getChildCount();
                totalItems = mLayoutManager.getItemCount();
                scrollOutItems = mLayoutManager.findFirstVisibleItemPosition();
                if (isScrolling && (currentIndex + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    progressBar.setVisibility(View.VISIBLE);
                    new WordLoaderTask().execute();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getActivity(), WordActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("word", wordList.get(position));
                                intent.putExtra("dictionaryCOde",
                                        MainActivity.DATABASE_EN_VIE);
                                intent.putExtra("package", bundle);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                // do whatever
                            }
                        })
        );
        mAcTvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new WordSearcher().execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mAcTvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word word = dbAccess.getWordByName(searchList.get(position));
                if (word != null) {
                    Intent intent = new Intent(getActivity(), WordActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("word", word);
                    intent.putExtra("package", bundle);
                    intent.putExtra("dictionaryCOde", MainActivity.DATABASE_EN_VIE);
                    searchList.clear();
                    searchAdapter.notifyDataSetChanged();
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),
                            "Action to quick,Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBtnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mAcTvSearch.setText(RecognizerIntent.EXTRA_RESULTS.toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fetchMoreData() {
        ArrayList<Word> temp = dbAccess.getWordsOffset(totalItems + 120, offset);
        for (int i = 0; i < temp.size(); i++) {
            wordList.add(Word.Copy(temp.get(i)));
        }
    }

    private void searchString() {
        String str = mAcTvSearch.getText().toString();
        ArrayList<String> temp = dbAccess.getWordsStartWith(str);
        for (int i = 0; i < temp.size(); i++) {
            searchList.add(temp.get(i));
        }
    }

    @SuppressLint("StaticFieldLeak")
    class WordLoaderTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            fetchMoreData();
            return null;
        }

        protected void onPostExecute(Void param) {
            customApdater.notifyDataSetChanged();
        }
    }

    class WordSearcher extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            searchString();
            return null;
        }

        protected void onPostExecute(Void param) {
            searchAdapter.notifyDataSetChanged();
        }
    }
}