package com.krishnaditya.foodlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {

    private MaterialButton orderPrice;
    private TextView orderTitle, orderRestaurant, orderRating, orderPriceSummary, orderDeliveryPrice, orderTotalPrice, orderTotal;
    private EditText orderQuantity, orderAddress, orderNotes, orderMenuId;
    private ImageView orderMenuImage, backOrder;
    private double pricePerItem;
    private FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderPrice = findViewById(R.id.order_price);
        orderTitle = findViewById(R.id.order_title);
        orderRestaurant = findViewById(R.id.order_restaurant);
        orderRating = findViewById(R.id.order_rating);
        orderMenuImage = findViewById(R.id.order_menu_image);
        backOrder = findViewById(R.id.back_order);

        orderPriceSummary = findViewById(R.id.order_price_summary);
        orderDeliveryPrice = findViewById(R.id.order_delivery_price);
        orderTotalPrice = findViewById(R.id.order_total_price);

        orderQuantity = findViewById(R.id.order_quantity);
        orderAddress = findViewById(R.id.order_address);
        orderNotes = findViewById(R.id.order_notes);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String restaurant = intent.getStringExtra("restaurant");
        String price = intent.getStringExtra("price");
        String rating = intent.getStringExtra("rating");
        String imageMenuUrl = getIntent().getStringExtra("imageMenuUrl");
        String menuId = intent.getStringExtra("menuId");

        pricePerItem = Double.parseDouble(price);

        orderPrice.setText(String.format("$%s", price));
        orderTitle.setText(title);
        orderRestaurant.setText(restaurant);
        orderRating.setText(String.format("%s Star", rating));
        Glide.with(OrderActivity.this)
                .load(imageMenuUrl)
                .into(orderMenuImage);

        backOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        orderTotal = findViewById(R.id.order_total);
        orderTotal.setVisibility(View.GONE);
        setupQuantityEditText();

        Button submitOrderButton = findViewById(R.id.order_button);
        submitOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = orderQuantity.getText().toString();
                String address = orderAddress.getText().toString();
                String notes = orderNotes.getText().toString();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String total = orderTotal.getText().toString();

                saveOrderDataToFirebase(quantity, address, notes, total, userId, menuId);
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(OrderActivity.this);
        displayUserLocation();

    }

    private void saveOrderDataToFirebase(String quantity, String address, String notes, String total, String userId, String menuId) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("order");
        String orderId = ordersRef.push().getKey();

        if (orderId != null) {
            // Create a new Order object with the provided data
            Order order = new Order(quantity, address, notes, total, userId, menuId);

            // Save the new Order to the database using its ID
            ordersRef.child(orderId).setValue(order)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a success message
                            Toast.makeText(OrderActivity.this, "Order saved to database.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(OrderActivity.this, UserOrderActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Show an error message
                            Toast.makeText(OrderActivity.this, "Failed to save order to database.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void displayUserLocation() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(OrderActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Get the last known location
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(OrderActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Get the latitude and longitude
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();

                                // Use geocoder to get the location name
                                Geocoder geocoder = new Geocoder(OrderActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    if (addresses.size() > 0) {
                                        String address = addresses.get(0).getAddressLine(0);

                                        // Display the location in the text view
                                        orderAddress.setText(address);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(OrderActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(OrderActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Handle the result of location permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, call the function to display user location
                displayUserLocation();
            } else {
                Toast.makeText(OrderActivity.this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupQuantityEditText() {
        orderQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateTotalPrice();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void calculateTotalPrice() {
        String quantityString = orderQuantity.getText().toString();
        if (!quantityString.isEmpty()) {
            double deliveryFee = 5;
            int quantity = Integer.parseInt(quantityString);

            double calculateOrder = quantity * pricePerItem;
            orderPriceSummary.setText(String.format("$%.2f", calculateOrder));

            double calculateTotal = calculateOrder + deliveryFee;
            orderTotalPrice.setText(String.format("$%.2f", calculateTotal));

            orderTotal.setText(Double.toString(calculateTotal));
        }
    }

}