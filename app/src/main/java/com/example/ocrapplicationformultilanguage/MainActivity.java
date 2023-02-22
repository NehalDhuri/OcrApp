package com.example.ocrapplicationformultilanguage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

//    ActivityMainBinding binding;

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    NotesFragment notesFragment = new NotesFragment();
    TranslationFragment translationFragment = new TranslationFragment();

    final int home = R.id.home;
    final int notes = R.id.notes;
    final int translator = R.id.translator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case home:
                    replaceFragment(homeFragment);
                    return true;
                case notes:
                    replaceFragment(notesFragment);
                    return true;
                case translator:
                    replaceFragment(translationFragment);
                    return true;
            }
            return false;
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}