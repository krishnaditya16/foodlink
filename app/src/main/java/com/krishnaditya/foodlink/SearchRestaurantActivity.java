package com.krishnaditya.foodlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchRestaurantActivity extends AppCompatActivity {
    private TextView noResultsText;
    private EditText searchText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private ArrayList<Restaurant> restaurantList;
    private ImageView backSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_restaurant);

        searchText = findViewById(R.id.search_restaurant_text);
        searchButton = findViewById(R.id.search_restaurant_button);
        noResultsText = findViewById(R.id.no_results_restaurant_text);

        recyclerView = findViewById(R.id.search_restaurant_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantList = new ArrayList<>();
        restaurantAdapter = new RestaurantAdapter(this, restaurantList);
        recyclerView.setAdapter(restaurantAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchText.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(SearchRestaurantActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                } else {
                    searchRestaurant(query);
                }
            }
        });

        backSearch = findViewById(R.id.back_search_restaurant);
        backSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void searchRestaurant(String searchText) {
        DatabaseReference restaurantDatabaseRef = FirebaseDatabase.getInstance().getReference("restaurant");
        restaurantDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                restaurantList.clear();
                for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        String title = restaurant.getName().toLowerCase();
                        String query = searchText.toLowerCase();
                        if (title.contains(query)) {
                            restaurantList.add(restaurant);
                        }
                    }
                }
                restaurantAdapter.notifyDataSetChanged();
                if (restaurantList.isEmpty()) {
                    noResultsText.setVisibility(View.VISIBLE);
                } else {
                    noResultsText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

