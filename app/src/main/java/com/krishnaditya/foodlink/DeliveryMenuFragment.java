package com.krishnaditya.foodlink;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeliveryMenuFragment extends Fragment {

    private ImageView profileDelivery, userOrderDelivery, searchDelivery;
    private TextView locationDelivery;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RecyclerView menuRecyclerView;
    private ArrayList<Menu> menuList;
    private DeliveryMenuAdapter menuAdapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("menu");
    public DeliveryMenuFragment() {
        // Required empty public constructor
    }


    public static DeliveryMenuFragment newInstance(String param1, String param2) {
        DeliveryMenuFragment fragment = new DeliveryMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delivery_menu, container, false);

        profileDelivery = view.findViewById(R.id.profileDelivery);
        profileDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Call function to display user profile picture
        displayProfilePicture(view);

        // Call the function to display user location
        locationDelivery = view.findViewById(R.id.locationDelivery);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        displayUserLocation();

        userOrderDelivery = view.findViewById(R.id.userOrderDelivery);
        userOrderDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserOrderActivity.class);
                startActivity(intent);
            }
        });

        menuRecyclerView = view.findViewById(R.id.delivery_menu_recycler_view);
        menuRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

        menuRecyclerView.setLayoutManager(layoutManager);

        menuList = new ArrayList<>();
        menuAdapter = new DeliveryMenuAdapter(getContext(), menuList);
        menuRecyclerView.setAdapter(menuAdapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Menu menu = dataSnapshot.getValue(Menu.class);
                    menuList.add(menu);
                }
                menuAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        searchDelivery = view.findViewById(R.id.search_delivery);
        searchDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchMenuActivity.class);
                startActivity(intent);
            }
        });

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

                    Glide.with(DeliveryMenuFragment.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.profile_image)
                            .transform(new CircleCrop())
                            .into(profileDelivery);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayUserLocation() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Get the last known location
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Get the latitude and longitude
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Use geocoder to get the location name
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    if (addresses.size() > 0) {
                                        String address = addresses.get(0).getAddressLine(0);
                                        String city = addresses.get(0).getLocality();
                                        String state = addresses.get(0).getAdminArea();
                                        String knownName = addresses.get(0).getFeatureName();

                                        // Display the location in the text view
                                        locationDelivery.setText(knownName + ", " + city + ", " + state);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Handle the result of location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, call the function to display user location
                displayUserLocation();
            } else {
                Toast.makeText(getActivity(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}