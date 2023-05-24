package com.krishnaditya.foodlink;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserReservationAdapter extends RecyclerView.Adapter<UserReservationAdapter.MyViewHolder> {
    private ArrayList<Reservation> reservationList;
    private Context context;
    private String name, restaurant;
    public UserReservationAdapter(Context context, ArrayList<Reservation> reservationList) {
        this.context = context;
        this.reservationList = reservationList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Reservation reservation = reservationList.get(position);

        getUserName(reservation.getUserId(), holder.reservationUser);
        getRestaurantName(reservation.getRestaurantId(), holder.reservationRestaurant);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy");

        try {
            Date date = inputFormat.parse(reservation.getDate());
            String formattedDate = outputFormat.format(date);

            holder.reservationDate.setText(formattedDate);
            holder.reservationTime.setText(reservation.getTime());
            holder.reservationPerson.setText(String.format("%s persons", reservation.getPerson()));
            holder.reservationNotes.setText(reservation.getNotes());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView reservationRestaurant, reservationDate, reservationTime, reservationUser, reservationPerson, reservationNotes;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            reservationRestaurant = itemView.findViewById(R.id.user_reservation_restaurant);
            reservationDate = itemView.findViewById(R.id.user_reservation_date);
            reservationTime = itemView.findViewById(R.id.user_reservation_time);
            reservationUser = itemView.findViewById(R.id.user_reservation);
            reservationPerson = itemView.findViewById(R.id.user_reservation_person);
            reservationNotes = itemView.findViewById(R.id.user_reservation_notes);
        }
    }

    private void getUserName(String userId, TextView reservationUser) {
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    name = snapshot.child("name").getValue(String.class);
                    reservationUser.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to get restaurant name: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getRestaurantName(String restaurantId, TextView reservationRestaurant)  {
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference().child("restaurant").child(restaurantId);
        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    restaurant = snapshot.child("name").getValue(String.class);
                    reservationRestaurant.setText(restaurant);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to get restaurant name: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
