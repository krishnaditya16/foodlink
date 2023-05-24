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

public class SearchMenuActivity extends AppCompatActivity {
    private TextView noResultsText;
    private EditText searchText;
    private Button searchButton;
    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;
    private ArrayList<Menu> menuList;
    private ImageView backSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

        searchText = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.search_button);
        noResultsText = findViewById(R.id.no_results_text);

        recyclerView = findViewById(R.id.search_menu_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuList = new ArrayList<>();
        menuAdapter = new MenuAdapter(this, menuList);
        recyclerView.setAdapter(menuAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchText.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(SearchMenuActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                } else {
                    searchMenu(query);
                }
            }
        });

        backSearch = findViewById(R.id.back_search_menu);
        backSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void searchMenu(String searchText) {
        DatabaseReference menuDatabaseRef = FirebaseDatabase.getInstance().getReference("menu");
        menuDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuList.clear();
                for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                    Menu menu = menuSnapshot.getValue(Menu.class);
                    if (menu != null) {
                        String title = menu.getTitle().toLowerCase();
                        String query = searchText.toLowerCase();
                        if (title.contains(query)) {
                            menuList.add(menu);
                        }
                    }
                }
                menuAdapter.notifyDataSetChanged();
                if (menuList.isEmpty()) {
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

