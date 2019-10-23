package com.example.nagidictionary.ui.favorites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import dbhelpers.DatabaseAccess;
import model.Word;

public class favoriteWord_Fragment extends Fragment {
    // Store instance variables
    private List<Word> favorites = new ArrayList<>();
    private int page;
    private RecyclerView recyclerView;
    private customAdapter.customApdater customApdater;
    private LinearLayoutManager mLayoutManager;
    private String dictionaryCode;
    private DatabaseAccess dbAccess;

    // newInstance constructor for creating fragment with arguments
    public static favoriteWord_Fragment newInstance(int page, String dictionaryCode) {
        favoriteWord_Fragment fragmentFirst = new favoriteWord_Fragment();
        Bundle bundle = new Bundle();
        bundle.putInt("someInt", page);
        bundle.putString("dictionary", dictionaryCode);
        fragmentFirst.setArguments(bundle);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        dictionaryCode = getArguments().getString("dictionary", "anh_viet");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("dictionary", dictionaryCode);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            dictionaryCode = savedInstanceState.getString("text");
            favorites.clear();
            loadFavotieWords();
        }
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_list, container, false);
        dbAccess = DatabaseAccess.getInstance(getContext(), dictionaryCode);
        recyclerView = view.findViewById(R.id.rv_favorite);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        customApdater = new customApdater(favorites);
        recyclerView.setAdapter(customApdater);
        addEvent();
        loadFavotieWords();
        return view;
    }

    private void addEvent() {
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                gotoWordDetail(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                cancelFavoriteOfAWord(position);
                            }
                        })
        );
    }

    public void gotoWordDetail(int position) {
        Intent intent = new Intent(getActivity(), WordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("word", favorites.get(position));
        intent.putExtra("dictionaryCOde",
                MainActivity.DATABASE_EN_VIE);
        intent.putExtra("package", bundle);
        startActivity(intent);
    }

    public void cancelFavoriteOfAWord(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove Favorite Confirm?");
        builder.setMessage("Are you sure to remove this word from Favorite?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dbAccess.cancelLikeAWord(favorites.get(position).getId())) {
                    favorites.remove(position);
                    customApdater.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Removed from favorite",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Something went wrong!",
                            Toast.LENGTH_SHORT).show();
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

    public void loadFavotieWords() {
        List<Word> love = dbAccess.getAllFavoritesWord();
        favorites.addAll(love);
        customApdater.notifyDataSetChanged();
    }
}
