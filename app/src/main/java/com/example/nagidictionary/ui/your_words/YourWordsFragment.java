package com.example.nagidictionary.ui.your_words;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nagidictionary.MainActivity;
import com.example.nagidictionary.R;

import java.util.ArrayList;
import java.util.List;

import customAdapter.RecyclerItemClickListener;
import customAdapter.customApdater;
import dbhelpers.DatabaseAccess;
import model.Word;

public class YourWordsFragment extends Fragment {
    public static List<Word> favorites = new ArrayList<>();
    private RecyclerView recyclerView;
    public static customApdater customApdater;
    private LinearLayoutManager mLayoutManager;
    private DatabaseAccess dbAccess;
    private int index;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_your_words, container, false);
        dbAccess = DatabaseAccess.getInstance(getContext(), MainActivity.DATABASE_EN_VIE);
        recyclerView = root.findViewById(R.id.rv_your_word);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        customApdater = new customApdater(favorites);
        recyclerView.setAdapter(customApdater);
        addEvent();
        new WordLoaderTask().execute();
        return root;
    }

    private void addEvent() {
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getActivity(), YourWord_Activity.class);
                                Bundle bundle = new Bundle();
                                intent.putExtra("state", 1);
                                bundle.putSerializable("word", favorites.get(position));
                                intent.putExtra("package", bundle);
                                index = position;
                                startActivityForResult(intent, MainActivity.RESULT_CODE_EDIT);
                            }

                            @Override
                            public void onLongItemClick(View view, final int position) {
                                deteleAWord(position);
                            }
                        })
        );
    }

    public void deteleAWord(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Confirm!");
        builder.setMessage("Are you sure to delete this word?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dbAccess.deleteWord(favorites.get(position).getId())) {
                    favorites.remove(position);
                    customApdater.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MainActivity.RESULT_CODE_EDIT) {
                editComplete(data);
            }
        }
    }

    public void editComplete(Intent data) {
        Bundle returnBundle = data.getExtras().getBundle("returnPackage");
        Word word = (Word) returnBundle.getSerializable("addWord");
        DatabaseAccess dbAccess = DatabaseAccess.getInstance(getContext(), MainActivity.DATABASE_EN_VIE);
        if (dbAccess.editAWord(word)) {
            favorites.remove(index);
            favorites.add(index, word);
            customApdater.notifyDataSetChanged();
            Toast.makeText(getContext(), "Update completed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Something wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadYourWords() {
        List<Word> love = dbAccess.getAllOfYourWords();
        favorites.clear();
        favorites.addAll(love);
    }

    class WordLoaderTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            loadYourWords();
            return null;
        }

        protected void onPostExecute(Void param) {
            customApdater.notifyDataSetChanged();
        }
    }
}
