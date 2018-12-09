package com.example.tim.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    // launcher activity, used for log in

    private FirebaseAuth mAuth;
    private EditText editPassword, editEmail;
    static FeedReaderDbHelper mDbHelper;

    // this two are used for testing
    // disable auto log in if set to true
    private final Boolean disableAutoLogIn = true;

    // unverified account can also log in if set to true
    private final Boolean disableEmailVerify = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDbHelper = new FeedReaderDbHelper(MainActivity.this);
        Button btnLogIn = findViewById(R.id.btnLogIn);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogIn.setOnClickListener(v -> onClickLogIn());
        btnSignUp.setOnClickListener(v -> {
            // user want create new account
            SignUpDialog signUpDialog = new SignUpDialog(MainActivity.this, mAuth);
            signUpDialog.show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // auto log in
        if (!disableAutoLogIn && currentUser != null && (currentUser.isEmailVerified() || disableEmailVerify )) {
            Intent intent = new Intent(MainActivity.this, LoadActivity.class);
            intent.putExtra("userId", currentUser.getUid());
            startActivity(intent);
        }
    }

    private void onClickLogIn() {
        String email = String.valueOf(editEmail.getText());
        String password = String.valueOf(editPassword.getText());
        if (email.isEmpty()) {
            Toast.makeText(MainActivity.this, "Empty e-mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Empty password", Toast.LENGTH_SHORT).show();
            return;
        }

        // log in code copied form firestore
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("INFO", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        // can be replace by
                        if ((user.isEmailVerified() || disableEmailVerify)) {
                            Intent intent = new Intent(MainActivity.this, LoadActivity.class);
                            intent.putExtra("userId", user.getUid());
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "email not verified", Toast.LENGTH_LONG);
                        }
                    } else {
                        if (FirebaseNetworkException.class.isInstance(task.getException())){
                            Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w( "INFO", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
