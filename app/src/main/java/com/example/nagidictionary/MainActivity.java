package com.example.nagidictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.nagidictionary.ui.your_words.YourWord_Activity;
import com.example.nagidictionary.ui.your_words.YourWordsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import dbhelpers.DatabaseAccess;
import model.Word;

public class MainActivity extends AppCompatActivity {
    public static final String DATABASE_EN_VIE = "anh_viet";
    public static final String DATABASE_VIE_EN = "viet_anh";
    public static final String FAVORITE = "favorite";
    public static final String YOUR_WORD = "your_word";
    public static final int RESULT_CODE_ADD = 113;
    public static final int RESULT_CODE_EDIT = 114;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, YourWord_Activity.class);
                intent.putExtra("state", 0);
                startActivityForResult(intent, MainActivity.RESULT_CODE_ADD);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_en_en, R.id.nav_vn_en, R.id.nav_favorites, R.id.nav_your_words,
                R.id.nav_flashcard, R.id.nav_speaking)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_CODE_ADD) {
                Bundle returnBundle = data.getExtras().getBundle("returnPackage");
                Word word = (Word) returnBundle.getSerializable("addWord");
                DatabaseAccess dbAccess = DatabaseAccess.getInstance(this, DATABASE_EN_VIE);
                if (dbAccess.addNewWordIntoYours(word)) {
                    YourWordsFragment.favorites.add(word);
                    YourWordsFragment.customApdater.notifyDataSetChanged();
                    Toast.makeText(this, "Insert completed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
