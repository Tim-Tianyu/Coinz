package com.example.tim.coinz;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpDialog extends Dialog {
    private Pattern patternEmail = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
    private Context context;
    private FirebaseAuth mAuth;
    private TextView txtHelper;
    private static final String TAG = "SignUpDialog";

    public SignUpDialog(Context context, FirebaseAuth mAuth) {
        super(context);
        this.context = context;
        this.mAuth = mAuth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_dialog);
        EditText etEmail = findViewById(R.id.sign_up_dialog_et_email);
        EditText etName = findViewById(R.id.sign_up_dialog_et_name);
        EditText etPassword = findViewById(R.id.sign_up_dialog_et_password);
        EditText etConfirmPassword = findViewById(R.id.sign_up_dialog_et_password_re);
        txtHelper = findViewById(R.id.sign_up_dialog_txt_helper);
        Button btnConfirm = findViewById(R.id.sign_up_dialog_btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String name = etName.getText().toString();
                String password = etPassword.getText().toString();
                String confirmedPassword = etConfirmPassword.getText().toString();

                if (! patternEmail.matcher(email).matches()){
                    txtHelper.setText("Invalid email address");
                } else if (name.isEmpty()) {
                    txtHelper.setText("Empty username");
                } else if (password.isEmpty()){
                    txtHelper.setText("Empty password");
                } else if (! confirmedPassword.equals(password)){
                    txtHelper.setText("Confirm password not same as password");
                } else {
                    SignUp(email, password, name);
                }
            }
        });

        Button btnCancel = findViewById(R.id.sign_up_dialog_btn_cancel);
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void createUser(String userId, String name){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> docData = new HashMap<>();
        docData.put("FriendList", Collections.emptyList());
        docData.put("Name", name);
        db.collection("USER").document(userId).set(docData)
                .addOnFailureListener(e -> Log.w(TAG, e));
    }

    private void SignUp(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((MainActivity) context, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("INFO", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(context, "confirmation email send", Toast.LENGTH_SHORT).show();
                        createUser(Objects.requireNonNull(user).getUid(), name);
                        dismiss();
                    } else {
                        // If sign in fails, display a message to the user.
                        Exception ex = task.getException();
                        if (FirebaseNetworkException.class.isInstance(ex)){
                            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
                        } else if (FirebaseAuthWeakPasswordException.class.isInstance(ex)) {
                            txtHelper.setText("Password too weak");
                        } else if (FirebaseAuthUserCollisionException.class.isInstance(ex)) {
                            txtHelper.setText("E-mail address already sign up");
                        } else {
                            Log.w( "INFO", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "failed to sign up", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
