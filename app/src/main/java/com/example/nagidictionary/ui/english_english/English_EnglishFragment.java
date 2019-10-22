package com.example.nagidictionary.ui.english_english;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nagidictionary.R;

public class English_EnglishFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_en_en, container, false);
        final TextView textView = root.findViewById(R.id.text_en_vn);
        textView.setText("This feature is on development");
        return root;
    }
}