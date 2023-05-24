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

import java.util.ArrayList;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.MyViewHolder> {
    private ArrayList<Order> orderList;
    private Context context;
    private String title, restaurant, imageMenuUrl;
    public UserOrderAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Order order = orderList.get(position);

        getMenuTitle(order.getMenuId(), holder.orderTitle);
        getRestaurantTitle(order.getMenuId(), holder.orderRestaurant);
        getImageMenu(order.getMenuId(), holder.orderImage);

        holder.orderQuantity.setText(String.format("%s x", order.getQuantity()));
        holder.orderTotalPrice.setText(String.format("$%s", order.getTotal()));
        holder.orderAddress.setText(order.getAddress());

        holder.orderRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("order");
                String orderId = orderRef.push().getKey();
                orderRef.orderByChild("menuId").equalTo(order.getMenuId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String menuId = snapshot.getChildren().iterator().next().getKey();
                            Intent intent = new Intent(context, RatingActivity.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("menuId", order.getMenuId());
                            intent.putExtra("userId", order.getUserId());
                            intent.putExtra("title", getTitle());
                            intent.putExtra("restaurant", getRestaurant());
                            intent.putExtra("quantity", order.getQuantity());
                            intent.putExtra("total", order.getTotal());
                            intent.putExtra("imageMenuUrl", getImageMenu());
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
        return orderList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderTitle, orderQuantity, orderTotalPrice, orderAddress, orderRestaurant, orderImage;
        MaterialButton orderRateButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            orderTitle = itemView.findViewById(R.id.user_order_menu);
            orderQuantity = itemView.findViewById(R.id.user_order_quantity);
            orderTotalPrice = itemView.findViewById(R.id.user_order_price);
            orderAddress = itemView.findViewById(R.id.user_order_address);

            orderRestaurant = itemView.findViewById(R.id.user_order_restaurant);
            orderRestaurant.setVisibility(View.GONE);

            orderImage = itemView.findViewById(R.id.user_order_image);
            orderImage.setVisibility(View.GONE);

            orderRateButton = itemView.findViewById(R.id.user_order_rate_button);
        }
    }

    private void getMenuTitle(String menuId, TextView orderTitle) {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("menu").child(menuId);
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    title = snapshot.child("title").getValue(String.class);
                    orderTitle.setText(title);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to get menu title: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getTitle() {
        return title;
    }

//    private void getRestaurantTitle(String menuId, TextView orderRestaurant) {
//        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("menu").child(menuId);
//        menuRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    restaurant = snapshot.child("restaurant").getValue(String.class);
//                    orderRestaurant.setText(restaurant);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(context, "Failed to get menu title: " + error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void getRestaurantTitle(String menuId, TextView orderRestaurant) {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("menu").child(menuId);
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String restaurantId = snapshot.child("restaurantId").getValue(String.class);
                    DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference().child("restaurant").child(restaurantId);
                    restaurantRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                restaurant = snapshot.child("name").getValue(String.class);
                                orderRestaurant.setText(restaurant);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Failed to get restaurant data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to get menu title: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getRestaurant() {
        return restaurant;
    }

    private void getImageMenu(String menuId, TextView orderImage) {
        DatabaseReference menuRef = FirebaseDatabase.getInstance().getReference().child("menu").child(menuId);
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    imageMenuUrl = snapshot.child("imageMenuUrl").getValue(String.class);
                    orderImage.setText(imageMenuUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to get menu image: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getImageMenu() {
        return imageMenuUrl;
    }

}




