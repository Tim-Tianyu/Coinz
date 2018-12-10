package com.example.tim.coinz;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddFriendDialog extends Dialog {
    // dialog to show when user want to add new friend
    private ClipboardManager clipboard;
    private ClipData clip = ClipData.newPlainText("UID", User.currentUser.getUserId());
    Context context;
    private static final String TAG = "AddFriendDialog";

    AddFriendDialog(Context context) {
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
        btnSend.setOnClickListener(v -> {
            // try to send request to friend
            String userId = etUid.getText().toString();
            // below are list of invalid situation
            if (userId.equals("")){
                Toast.makeText(context, "Empty UID", Toast.LENGTH_SHORT).show();
                return;
            } else if (userId.equals(User.currentUser.getUserId())){
                Toast.makeText(context, "Can't send to yourself", Toast.LENGTH_SHORT).show();
                return;
            } else if (Request.findSentRequestByReceiverId(userId) != null) {
                Toast.makeText(context, "Request already sent", Toast.LENGTH_SHORT).show();
                return;
            } else if (User.findFriendById(userId) != null){
                Toast.makeText(context, "Already a friend of yours", Toast.LENGTH_SHORT).show();
                return;
            } else if (Request.findReceivedRequestBySenderId( userId) != null) {
                Toast.makeText(context, "Already received a request from this user", Toast.LENGTH_SHORT).show();
                return;
            }
            // add friend request to firestore
            Request.sendNewRequest(context, userId);
        });

        Button btnCopy = findViewById(R.id.add_friend_dialog_btn_copy);
        btnCopy.setOnClickListener(v -> {
            // copy user's own Uid to clipboard
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copy to clipboard", Toast.LENGTH_SHORT).show();
        });

        Button btnClose = findViewById(R.id.add_friend_dialog_btn_close);
        btnClose.setOnClickListener(v -> dismiss());
    }
}
