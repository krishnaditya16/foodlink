package com.krishnaditya.foodlink;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeMenuFragment()).commit();
                    return true;
                case R.id.navigation_restaurant:
                    // Load RestaurantMenuFragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RestaurantMenuFragment()).commit();
                    break;
                case R.id.navigation_delivery:
                    // Load DeliveryMenuFragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DeliveryMenuFragment()).commit();
                    break;
                case R.id.navigation_promo:
                    // Load PromoMenuFragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PromoMenuFragment()).commit();
                    break;
            }
            return true;
        });

        // Set HomeMenuFragment as the default fragment to load
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeMenuFragment()).commit();
    }
}