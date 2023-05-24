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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {
    private ArrayList<Menu> menuList;
    private Context context;
    public MenuAdapter(Context context, ArrayList<Menu> menuList) {
        this.context = context;
        this.menuList = menuList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Menu menu = menuList.get(position);

        holder.menuTitle.setText(menu.getTitle());
        holder.menuType.setText(menu.getType());
        holder.menuRating.setText(String.format("%s Star", menu.getRating()));
        holder.menuPrice.setText(String.format("$%s", menu.getPrice()));
        Glide.with(context).load(menu.getImageMenuUrl()).into(holder.menuImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("menu");
                menuRef.orderByChild("title").equalTo(menu.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String menuId = snapshot.getChildren().iterator().next().getKey();

                            DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference().child("restaurant").child(menu.getRestaurantId());
                            restaurantRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String restaurantName = snapshot.child("name").getValue(String.class);
                                        Intent intent = new Intent(context, OrderActivity.class);
                                        intent.putExtra("menuId", menuId);
                                        intent.putExtra("title", menu.getTitle());
                                        intent.putExtra("type", menu.getType());
                                        intent.putExtra("rating", menu.getRating());
                                        intent.putExtra("price", menu.getPrice());
                                        intent.putExtra("imageMenuUrl", menu.getImageMenuUrl());
                                        intent.putExtra("restaurant", restaurantName);
                                        context.startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, "Failed to get restaurant name: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
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
        return menuList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView menuImage;
        TextView menuTitle, menuType, menuPrice, menuRating, menuRestaurant;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            menuImage = itemView.findViewById(R.id.menu_image_view);
            menuTitle = itemView.findViewById(R.id.menu_title_text_view);
            menuType = itemView.findViewById(R.id.menu_type_text_view);
            menuPrice = itemView.findViewById(R.id.menu_price_text_view);
            menuRating = itemView.findViewById(R.id.menu_rating_text_view);

            menuRestaurant = itemView.findViewById(R.id.menu_restaurant_text_view);
            menuRestaurant.setVisibility(View.GONE);
        }
    }
}



