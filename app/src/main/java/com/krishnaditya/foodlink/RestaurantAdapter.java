package com.krishnaditya.foodlink;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {
    private ArrayList<Restaurant> restaurantList;
    private Context context;
    public RestaurantAdapter(Context context, ArrayList<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Restaurant restaurant = restaurantList.get(position);

        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantDescription.setText(restaurant.getDescription());
        Glide.with(context).load(restaurant.getImageRestaurant()).into(holder.restaurantImage);

        holder.restaurantReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("restaurant");
                menuRef.orderByChild("name").equalTo(restaurant.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String restaurantId = snapshot.getChildren().iterator().next().getKey();
                            // Pass the menu ID and other data to OrderActivity
                            Intent intent = new Intent(context, ReservationActivity.class);
                            intent.putExtra("restaurantId", restaurantId);
                            intent.putExtra("name", restaurant.getName());
                            intent.putExtra("description", restaurant.getDescription());
                            intent.putExtra("imageRestaurantUrl", restaurant.getImageRestaurant());
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView restaurantImage;
        TextView restaurantName, restaurantDescription;
        MaterialButton restaurantReserve;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantDescription = itemView.findViewById(R.id.restaurant_description);
            restaurantReserve = itemView.findViewById(R.id.restaurant_reserve);
        }
    }
}