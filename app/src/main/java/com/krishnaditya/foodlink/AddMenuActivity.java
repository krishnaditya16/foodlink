package com.krishnaditya.foodlink;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddMenuActivity extends AppCompatActivity {

    private ImageView imageMenu;
    private Button addMenu;
    EditText titleMenu, priceMenu, typeMenu, ratingMenu, restaurantMenu;
    private Uri imageUri;
    private ProgressBar progressBar;
    private Spinner spinnerRestaurant;
    private ArrayList<String> restaurantNames = new ArrayList<>();
    private ArrayList<String> restaurantIds = new ArrayList<>();
    final  private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("menu");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        titleMenu = findViewById(R.id.titleMenu);
        priceMenu = findViewById(R.id.priceMenu);
        typeMenu = findViewById(R.id.typeMenu);
        ratingMenu = findViewById(R.id.ratingMenu);
        imageMenu = findViewById(R.id.imageMenu);
        addMenu = findViewById(R.id.addMenu);
        progressBar = findViewById(R.id.progressBar);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            imageMenu.setImageURI(imageUri);
                        } else {
                            Toast.makeText(AddMenuActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        addMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null){
                    uploadToFirebase(imageUri);
                } else  {
                    Toast.makeText(AddMenuActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spinnerRestaurant = findViewById(R.id.spinner_restaurant);
        DatabaseReference restaurantsRef = FirebaseDatabase.getInstance().getReference().child("restaurant");
        ValueEventListener restaurantListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing lists
                restaurantNames.clear();
                restaurantIds.clear();

                // Iterate through the snapshot and add each restaurant to the lists
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String restaurantName = snapshot.child("name").getValue(String.class);
                    String restaurantId = snapshot.getKey();
                    restaurantNames.add(restaurantName);
                    restaurantIds.add(restaurantId);
                }

                // Create an ArrayAdapter to display the restaurant names in the Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddMenuActivity.this, android.R.layout.simple_spinner_item, restaurantNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRestaurant.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        };

        restaurantsRef.addValueEventListener(restaurantListener);
        spinnerRestaurant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRestaurantId = restaurantIds.get(position);
                // Save the selected restaurant ID to the database
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });

    }

    private void uploadToFirebase(Uri uri){
        String title_menu = titleMenu.getText().toString();
        String type_menu = typeMenu.getText().toString();
        String price_menu = priceMenu.getText().toString();
        String rating_menu = ratingMenu.getText().toString();
        // String restaurant_menu = ratingMenu.getText().toString();
        String restaurant_id = restaurantIds.get(spinnerRestaurant.getSelectedItemPosition());
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Menu menu = new Menu(title_menu, type_menu, rating_menu, price_menu, uri.toString(), restaurant_id);
                        String key = databaseReference.push().getKey();
                        databaseReference.child(key).setValue(menu);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(AddMenuActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddMenuActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(AddMenuActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}