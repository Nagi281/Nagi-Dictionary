package com.example.nagidictionary.ui.speaking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nagidictionary.R;

public class SpeakingCoachFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_speaking, container, false);
        final TextView textView = root.findViewById(R.id.text_speaking);
        return root;
    }
}