package com.krishnaditya.foodlink;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RestaurantMenuFragment extends Fragment {

    private ImageView profileRestaurant, searchRestaurant;
    private RecyclerView restaurantRecyclerView;
    private RestaurantAdapter restaurantAdapter;
    private ArrayList<Restaurant> restaurantList;
    private LinearLayout restaurantReservationList;
    private TextView textRestaurant;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("restaurant");
    public RestaurantMenuFragment() {
        // Required empty public constructor
    }

    public static RestaurantMenuFragment newInstance(String param1, String param2) {
        RestaurantMenuFragment fragment = new RestaurantMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant_menu, container, false);

        profileRestaurant = view.findViewById(R.id.profileRestaurant);
        profileRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        textRestaurant = view.findViewById(R.id.text_restaurant);
        textRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRestaurantActivity.class);
                startActivity(intent);
            }
        });

        searchRestaurant = view.findViewById(R.id.search_restaurant);
        searchRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchRestaurantActivity.class);
                startActivity(intent);
            }
        });

//        restaurantList = new ArrayList<>();
//        restaurantRecyclerView = view.findViewById(R.id.restaurant_recycler_view);
//        restaurantRecyclerView.setHasFixedSize(true);
//        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        restaurantAdapter = new RestaurantAdapter(getContext(), restaurantList);
//        restaurantRecyclerView.setAdapter(restaurantAdapter);
        restaurantRecyclerView = view.findViewById(R.id.restaurant_recycler_view);
        restaurantRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        restaurantRecyclerView.setLayoutManager(layoutManager);

        restaurantList = new ArrayList<>();
        restaurantAdapter = new RestaurantAdapter(getContext(), restaurantList);
        restaurantRecyclerView.setAdapter(restaurantAdapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                    restaurantList.add(restaurant);
                }
                restaurantAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        restaurantReservationList = view.findViewById(R.id.restaurant_reservation_list);
        restaurantReservationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserReservationActivity.class);
                startActivity(intent);
            }
        });

        // Call function to display user profile picture
        displayProfilePicture(view);

        // Inflate the layout for this fragment
        return view;
    }

    private void displayProfilePicture(View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        mDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String imageUrl = user.getImageUrl();

                    Glide.with(RestaurantMenuFragment.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.profile_image)
                            .transform(new CircleCrop())
                            .into(profileRestaurant);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}