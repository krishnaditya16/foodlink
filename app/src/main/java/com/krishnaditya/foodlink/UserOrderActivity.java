package com.krishnaditya.foodlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import java.util.List;

public class UserOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserOrderAdapter adapter;
    private ArrayList<Order> orderList;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ImageView backUserOrder;
    private TextView orderUser, noOrderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);

        backUserOrder = findViewById(R.id.back_user_order);
        backUserOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("order");

        orderList = new ArrayList<>();
        recyclerView = findViewById(R.id.user_order_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserOrderAdapter(this, orderList);
        recyclerView.setAdapter(adapter);

        noOrderText = findViewById(R.id.no_order_text);

        String userId = firebaseAuth.getCurrentUser().getUid();
        Query query = databaseReference.orderByChild("userId").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    orderList.add(order);
                }
                adapter.notifyDataSetChanged();
                if (orderList.isEmpty()) {
                    noOrderText.setVisibility(View.VISIBLE);
                } else {
                    noOrderText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserOrderActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        orderUser = findViewById(R.id.order_user);
        DatabaseReference userDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        userDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String fullName = user.getName();
                    String[] nameArray = fullName.split(" ");
                    String firstName = nameArray[0];
                    orderUser.setText(String.format("%s's Order", firstName));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserOrderActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
