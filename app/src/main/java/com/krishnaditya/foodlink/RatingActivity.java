package com.krishnaditya.foodlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RatingActivity extends AppCompatActivity {

    private TextView ratingMenuTitle, ratingRestaurant, ratingPrice, ratingQuantity, ratingTotalPrice, ratingUser;
    private ImageView backRating, ratingImageMenu;
    private Button ratingButton;
    private EditText ratingNotes;
    private RatingBar ratingBar;
    final  private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("rating");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ratingMenuTitle = findViewById(R.id.rating_menu_title);
        ratingRestaurant = findViewById(R.id.rating_restaurant);
        ratingPrice = findViewById(R.id.rating_price);
        ratingQuantity = findViewById(R.id.rating_quantity);
        ratingTotalPrice = findViewById(R.id.rating_total_price);
        ratingImageMenu = findViewById(R.id.rating_image_menu);
        ratingUser = findViewById(R.id.rating_user);
        ratingBar = findViewById(R.id.rating_star);
        ratingNotes = findViewById(R.id.rating_notes);
        ratingButton = findViewById(R.id.rating_button);

        Intent intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        String menuId = intent.getStringExtra("menuId");
        String userId = intent.getStringExtra("userId");
        String title = intent.getStringExtra("title");
        String restaurant = intent.getStringExtra("restaurant");
        String quantity = intent.getStringExtra("quantity");
        String total = intent.getStringExtra("total");
        String imageMenuUrl = intent.getStringExtra("imageMenuUrl");

        double price = (Double.parseDouble(total) - 5)/Double.parseDouble(quantity);
        ratingMenuTitle.setText(title);
        ratingRestaurant.setText(restaurant);
        ratingPrice.setText(String.format("Price: $%s", price));
        ratingQuantity.setText(String.format("Amount: %sx", quantity));
        ratingTotalPrice.setText(String.format("Total: $%s", total));
        Glide.with(RatingActivity.this).load(imageMenuUrl).into(ratingImageMenu);

        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        userDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String fullName = user.getName();
                    String[] nameArray = fullName.split(" ");
                    String firstName = nameArray[0];
                    ratingUser.setText(String.format("%s's Rating", firstName));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RatingActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        backRating = findViewById(R.id.back_rating);
        backRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rate = String.valueOf(ratingBar.getRating());
                String notes = ratingNotes.getText().toString();
                Rating rating = new Rating(rate, notes, userId, menuId, orderId);
                String key = databaseReference.push().getKey();
                databaseReference.child(key).setValue(rating);

                Toast.makeText(RatingActivity.this, "Rating added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RatingActivity.this, UserOrderActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}