package com.krishnaditya.foodlink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerName, registerEmail, registerMobile, registerPassword, registerConPassword;
    private Button registerButton;
    private ProgressBar progressBar;
    private ImageView passwordIconRegister, conPasswordIconRegister;
    TextView loginRedirectText;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerName = findViewById(R.id.registerName);
        registerEmail = findViewById(R.id.registerEmail);
        registerMobile = findViewById(R.id.registerMobile);
        registerPassword = findViewById(R.id.registerPassword);
        registerConPassword = findViewById(R.id.registerConPassword);
        registerButton = findViewById(R.id.registerButton);

        progressBar = findViewById(R.id.progressBarRegister);
        progressBar.setVisibility(View.GONE);

        loginRedirectText = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.registerButton:
                        registerUser();
                        break;
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        View.OnClickListener passwordToggleClickListener = new View.OnClickListener() {
            boolean passwordVisible = false;
            @Override
            public void onClick(View v) {
                if (passwordVisible) {
                    registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    registerConPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIconRegister.setImageResource(R.drawable.baseline_remove_red_eye_24);
                    conPasswordIconRegister.setImageResource(R.drawable.baseline_remove_red_eye_24);
                } else {
                    registerPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    registerConPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    passwordIconRegister.setImageResource(R.drawable.baseline_visibility_off_24);
                    conPasswordIconRegister.setImageResource(R.drawable.baseline_visibility_off_24);
                }
                passwordVisible = !passwordVisible;
            }
        };

        passwordIconRegister = findViewById(R.id.passwordIconRegister);
        conPasswordIconRegister = findViewById(R.id.conPasswordIconRegister);
        passwordIconRegister.setOnClickListener(passwordToggleClickListener);
        conPasswordIconRegister.setOnClickListener(passwordToggleClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }

    private void registerUser() {
        final String name = registerName.getText().toString().trim();
        final String email = registerEmail.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();
        String confirm_password = registerConPassword.getText().toString().trim();
        final String phone = registerMobile.getText().toString().trim();

        if (name.isEmpty()) {
            registerName.setError(getString(R.string.input_error_name));
            registerName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            registerEmail.setError(getString(R.string.input_error_email));
            registerEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registerEmail.setError(getString(R.string.input_error_email_invalid));
            registerEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            registerPassword.setError(getString(R.string.input_error_password));
            registerPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            registerPassword.setError(getString(R.string.input_error_password_length));
            registerPassword.requestFocus();
            return;
        }

        if (confirm_password.isEmpty()) {
            registerConPassword.setError(getString(R.string.input_error_password));
            registerConPassword.requestFocus();
            return;
        }

        if (confirm_password.length() < 6) {
            registerConPassword.setError(getString(R.string.input_error_password_length));
            registerConPassword.requestFocus();
            return;
        }

        if (!password.equals(confirm_password)) {
            registerConPassword.setError(getString(R.string.input_error_password_not_match));
            registerConPassword.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            registerMobile.setError(getString(R.string.input_error_phone));
            registerMobile.requestFocus();
            return;
        }

//        if (phone.length() != 10) {
//            registerMobile.setError(getString(R.string.input_error_phone_invalid));
//            registerMobile.requestFocus();
//            return;
//        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User registration successful
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            User user = new User(name, email, phone);
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // User object saved successfully
                                        Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Failed to save user object
                                        Toast.makeText(getApplicationContext(), "Failed to save user object", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            // User registration failed
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}