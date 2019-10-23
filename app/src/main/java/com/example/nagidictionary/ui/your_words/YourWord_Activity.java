package com.example.nagidictionary.ui.your_words;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nagidictionary.R;

import model.Word;

public class YourWord_Activity extends AppCompatActivity {
    private EditText mEdtName;
    private EditText mEdtContent;
    private Button mBtnCancel;
    private Button mBtnConfirm;
    private int state;
    private int wordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_word_activity);
        addControl();
        Intent intent = getIntent();
        state = intent.getExtras().getInt("state");
        if (state == 1) {
            Bundle bundle = intent.getExtras().getBundle("package");
            Word word = (Word) bundle.getSerializable("word");
            wordId = word.getId();
            mEdtName.setText(word.getName());
            mEdtContent.setText(word.getContent());
        }
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    private void addControl() {
        mEdtContent = findViewById(R.id.edt_content);
        mEdtName = findViewById(R.id.edt_name);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mBtnConfirm = findViewById(R.id.btn_confirm);
    }

    public void onBtnCancelClick(View v) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void onBtnConfirmClick(View v) {
        if (mEdtName.getText().toString().equals("") || mEdtContent.getText().toString().equals("")) {
            Toast.makeText(this, "Please input data!", Toast.LENGTH_SHORT).show();
        } else {
            Word newContact = new Word(wordId,
                    mEdtName.getText().toString(),
                    mEdtContent.getText().toString());
            Bundle returnBundle = new Bundle();
            returnBundle.putSerializable("addWord", newContact);
            final Intent returnResult = new Intent();
            returnResult.putExtra("returnPackage", returnBundle);
            setResult(Activity.RESULT_OK, returnResult);
            finish();
        }
    }
}
