package com.example.tim.coinz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button btnLogIn,btnSignUp;
    private EditText editPassword, editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        btnLogIn = (Button) findViewById(R.id.btnLogIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogIn(v);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp(v);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // TODO currentUser.isEmailVerified()
        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, LoadActivity.class);
            intent.putExtra("userId", currentUser.getUid());
            startActivity(intent);
            finish();
        }
    }

    private void onClickLogIn(View v) {
        String email = String.valueOf(editEmail.getText());
        String password = String.valueOf(editPassword.getText());
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("INFO", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            // TODO currentUser.isEmailVerified()
                            if (true) {
                                Intent intent = new Intent(MainActivity.this, LoadActivity.class);
                                intent.putExtra("userId", user.getUid());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "email not verified", Toast.LENGTH_LONG);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w( "INFO", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onClickSignUp(View v) {
        String email = String.valueOf(editEmail.getText());
        String password = String.valueOf(editPassword.getText());
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("INFO", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "E-mail have been sent",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("INFO", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
