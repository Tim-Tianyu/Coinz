package com.example.tim.coinz;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddFriendDialog extends Dialog {
    ClipboardManager clipboard;
    ClipData clip = ClipData.newPlainText("UID", User.currentUser.getUserId());
    Context context;

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
        // TODO btnSend

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
