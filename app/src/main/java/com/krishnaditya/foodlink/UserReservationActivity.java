package com.krishnaditya.foodlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserReservationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserReservationAdapter adapter;
    private ArrayList<Reservation> reservationList;
    private TextView reservationUser, noReservationText;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ImageView backUserReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservation);

        reservationList = new ArrayList<>();
        recyclerView = findViewById(R.id.user_reservation_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserReservationAdapter(this, reservationList);
        recyclerView.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("reservation");

        noReservationText = findViewById(R.id.no_reservation_text);

        String userId = firebaseAuth.getCurrentUser().getUid();
        Query query = databaseReference.orderByChild("userId").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reservationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reservation reservation = snapshot.getValue(Reservation.class);
                    reservationList.add(reservation);
                }
                adapter.notifyDataSetChanged();
                if (reservationList.isEmpty()) {
                    noReservationText.setVisibility(View.VISIBLE);
                } else {
                    noReservationText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserReservationActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        reservationUser = findViewById(R.id.reservation_user);
        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        userDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String fullName = user.getName();
                    String[] nameArray = fullName.split(" ");
                    String firstName = nameArray[0];
                    reservationUser.setText(String.format("%s's Reservation", firstName));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserReservationActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        backUserReservation = findViewById(R.id.back_user_reservation);
        backUserReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}