package com.example.tim.coinz;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddFriendDialog extends Dialog {
    ClipboardManager clipboard;
    ClipData clip = ClipData.newPlainText("UID", User.currentUser.getUserId());
    Context context;
    private static final String TAG = "AddFriendDialog";

    public AddFriendDialog(Context context) {
        super(context);
        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_dialog);

        EditText etUid = findViewById(R.id.add_friend_dialog_et_uid);
        Button btnSend = findViewById(R.id.add_friend_dialog_btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = etUid.getText().toString();
                if (userId.equals("")){
                    Toast.makeText(context, "Empty UID", Toast.LENGTH_SHORT).show();
                    return;
                } else if (userId.equals(User.currentUser.getUserId())){
                    Toast.makeText(context, "Can't send to yourself", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Request.findSentRequestByReceiverId(userId) != null) {
                    Toast.makeText(context, "Request already sent", Toast.LENGTH_SHORT).show();
                    return;
                } else if (User.findUserById(User.friends, userId) != null){
                    Toast.makeText(context, "Already a friend of yours", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Request.findReceivedRequestBySenderId( userId) != null) {
                    Toast.makeText(context, "Already received a request from this user", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> data = new HashMap<>();
                CollectionReference collectionReference = db.collection("USER");
                DocumentReference documentReference = collectionReference.document(User.currentUser.getUserId());
                documentReference.getPath();

                Timestamp timestamp = new Timestamp(Calendar.getInstance().getTime());
                data.put("Status", Request.StatusToDouble(Request.Status.PENDING));
                data.put("Time", timestamp);
                data.put("Sender", collectionReference.document(User.currentUser.getUserId()));
                data.put("Receiver", collectionReference.document(userId));
                db.collection("FRIEND_REQUEST").add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Request.sentRequests.add(new Request(documentReference.getId(), User.currentUser.getUserId(), userId, Request.Status.PENDING, timestamp));
                                Toast.makeText(context, "Request successfully sent", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, e);
                                Toast.makeText(context, "Request fail to sent", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        Button btnCopy = findViewById(R.id.add_friend_dialog_btn_copy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copy to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnClose = findViewById(R.id.add_friend_dialog_btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
