package com.jacobstechnologies.wordle;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.jacobstechnologies.wordle.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // add tiles fragment
        TilesFragmentClass f = TilesFragmentClass.TilesFragmentClass(getApplicationContext());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_main_fragment_container, f)
                .commit();

        refresh = findViewById(R.id.button);
        refresh.setOnClickListener(l -> {
            f.reset();
        });
    }
}