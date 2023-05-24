package com.krishnaditya.foodlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ReservationActivity extends AppCompatActivity {

    private TextView reserveRestaurantName, reserveRestaurantDescription;
    private ImageView reserveBack, reserveRestaurantImage;
    private Button reserveDate, reserveTime, reserveButton;
    private EditText reservePerson, reserveNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        reserveRestaurantName = findViewById(R.id.reserve_restaurant_name);
        reserveRestaurantDescription = findViewById(R.id.reserve_restaurant_description);
        reserveRestaurantImage = findViewById(R.id.reserve_restaurant_image);
        reserveBack = findViewById(R.id.reserve_back);
        reserveDate = findViewById(R.id.reserve_date);
        reserveTime = findViewById(R.id.reserve_time);
        reservePerson = findViewById(R.id.reserve_person);
        reserveNotes = findViewById(R.id.reserve_notes);
        reserveButton = findViewById(R.id.reserve_button);

        Intent intent = getIntent();
        String restaurantId = intent.getStringExtra("restaurantId");
        String restaurantName = intent.getStringExtra("name");
        String restaurantDescription = intent.getStringExtra("description");
        String imageRestaurantUrl = getIntent().getStringExtra("imageRestaurantUrl");

        reserveRestaurantName.setText(restaurantName);
        reserveRestaurantDescription.setText(restaurantDescription);
        Glide.with(ReservationActivity.this)
                .load(imageRestaurantUrl)
                .into(reserveRestaurantImage);

        reserveBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reserveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Select a date");
                MaterialDatePicker datePicker = builder.build();
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        // Convert selected date to string
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        calendar.setTimeInMillis(selection);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String selectedDate = sdf.format(calendar.getTime());
                        reserveDate.setText(selectedDate);
                    }
                });
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });

        reserveTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
                builder.setTitleText("Select a time");
                MaterialTimePicker timePicker = builder.build();

                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                        String time = timeFormat.format(calendar.getTime());
                        reserveTime.setText(time);
                    }
                });

                timePicker.show(getSupportFragmentManager(), "timePicker");
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String date = reserveDate.getText().toString();
                String time = reserveTime.getText().toString();
                String person = reservePerson.getText().toString();
                String notes = reserveNotes.getText().toString();

                saveReservationDataToFirebase(restaurantId, userId, date, time, person, notes);
            }
        });
    }

    private void saveReservationDataToFirebase(String restaurantId, String userId, String date, String time, String person, String notes) {
        DatabaseReference reservationRef = FirebaseDatabase.getInstance().getReference().child("reservation");
        String reservationId = reservationRef.push().getKey();

        if (reservationId != null) {
            // Create a new Order object with the provided data
            Reservation reservation = new Reservation(restaurantId, userId, date, time, person, notes);

            // Save the new Order to the database using its ID
            reservationRef.child(reservationId).setValue(reservation)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Show a success message
                            Toast.makeText(ReservationActivity.this, "Reservation saved to database.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ReservationActivity.this, UserReservationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Show an error message
                            Toast.makeText(ReservationActivity.this, "Failed to save order to database.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}