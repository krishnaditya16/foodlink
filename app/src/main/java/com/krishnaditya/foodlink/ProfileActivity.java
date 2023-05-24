package com.krishnaditya.foodlink;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.Instant;

public class ProfileActivity extends AppCompatActivity {
    private ImageView backButton, logoutButton, photoProfile;
    private EditText phoneProfile, passwordProfile, conPasswordProfile;
    private TextView userProfile, userEmailProfile;
    private Button updateProfileBtn;
    private static final int PICK_IMAGE = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseStorage mStorage;
    private String userId;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        mStorage = FirebaseStorage.getInstance();

        userId = mAuth.getCurrentUser().getUid();

//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Set up the user's profile information
        phoneProfile = findViewById(R.id.phoneProfile);
        passwordProfile = findViewById(R.id.passwordProfile);
        conPasswordProfile = findViewById(R.id.conPasswordProfile);

        userProfile = findViewById(R.id.userProfile);
        userEmailProfile = findViewById(R.id.userEmailProfile);

        mDatabaseRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    String name = user.getName();
                    String email = user.getEmail();
                    String phone = user.getPhone();
                    String imageUrl = user.getImageUrl();

                    phoneProfile.setText(phone);

                    userProfile.setText(name);
                    userEmailProfile.setText(email);

                    Glide.with(ProfileActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.profile_image)
                            .transform(new CircleCrop())
                            .into(photoProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Set up the image view for the user's profile picture
        photoProfile = findViewById(R.id.photoProfile);
        photoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        // Set up the back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set up the logout button
        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        updateProfileBtn = findViewById(R.id.updateProfile);
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            // Upload the image to Firebase Storage and update the user's profile image URL in the database
            final StorageReference fileReference = mStorage.getReference("profile_images").child(userId + ".jpg");

            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            mDatabaseRef.child(userId).child("imageUrl").setValue(imageUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Failed to upload image", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateProfile() {
        final String newPhone = phoneProfile.getText().toString().trim();
        String newPassword = passwordProfile.getText().toString().trim();
        String newConPassword = conPasswordProfile.getText().toString().trim();

        if (newPhone.isEmpty()) {
            phoneProfile.setError("Please enter your phone number");
            phoneProfile.requestFocus();
            return;
        }

//        if (newPhone.length() != 10) {
//            phoneProfile.setError(getString(R.string.input_error_phone_invalid));
//            phoneProfile.requestFocus();
//            return;
//        }

        if (newPassword.isEmpty()) {
            passwordProfile.setError("Please enter your password");
            passwordProfile.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            passwordProfile.setError(getString(R.string.input_error_password_length));
            passwordProfile.requestFocus();
            return;
        }

        if (newConPassword.isEmpty()) {
            conPasswordProfile.setError("Please enter the confirm password field");
            conPasswordProfile.requestFocus();
            return;
        }

        if (newConPassword.length() < 6) {
            conPasswordProfile.setError(getString(R.string.input_error_password_length));
            conPasswordProfile.requestFocus();
            return;
        }

        if (!newPassword.equals(newConPassword)) {
            conPasswordProfile.setError(getString(R.string.input_error_password_not_match));
            conPasswordProfile.requestFocus();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            // Update phone number in realtime database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            databaseReference.child("phone").setValue(newPhone);
            phoneProfile.setText(newPhone);

            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("ProfileActivity", "User password updated.");
                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_LONG).show();
    }

}