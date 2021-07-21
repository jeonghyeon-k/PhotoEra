package com.example.photoera;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShoutingActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText shout_text;
    private Button btn_shout;
    private String uid;
    private String name;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_shouting);

        uid = intent.getStringExtra("TestID");
        shout_text = (EditText) findViewById(R.id.shout_text);
        btn_shout = (Button) findViewById(R.id.btn_shout);

        btn_shout.setOnClickListener(this);

        auth=FirebaseAuth.getInstance();
        final String email=auth.getCurrentUser().getEmail().toString().trim();
        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
        Query queries=ref.child("users").orderByChild("email").equalTo(email);
        queries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    name = datas.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAA6o8IPrE:APA91bF4SN6rgBDVaBY6NGUProF7oXGEALolIJ8hOfBIBHtFa11SYL5KmXVLWJVPnAuVXhKKNEPpSrE9Z7gVYPtld4IPFthwyqeeGLRRqd8KRex0eeUx2kOoe64rx0TDkbJEnujfl1b0";
    private void sendPostToFCM(final String uid, final String message) {
        firebaseDatabase.getReference("usertoken")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final UserToken userToken = dataSnapshot.getValue(UserToken.class);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // FMC 메시지 생성 start
                                    JSONObject root = new JSONObject();
                                    JSONObject notification = new JSONObject();
                                    notification.put("body", message);
                                    notification.put("title", getString(R.string.app_name));
                                    root.put("notification", notification);
                                    root.put("to", userToken.fcmToken);
                                    // FMC 메시지 생성 end

                                    URL Url = new URL(FCM_MESSAGE_URL);
                                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                    conn.setRequestMethod("POST");
                                    conn.setDoOutput(true);
                                    conn.setDoInput(true);
                                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setRequestProperty("Content-type", "application/json");
                                    OutputStream os = conn.getOutputStream();
                                    os.write(root.toString().getBytes("utf-8"));
                                    os.flush();
                                    conn.getResponseCode();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),ShoutListActivity.class));
        finish();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_shout) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date now = new Date();
            String Date=formatter.format(now);
            ShoutingDTO shout = new ShoutingDTO(name, shout_text.getText().toString(), Date); //ChatDTO를 이용하여 데이터를 묶는다.
            databaseReference.child("shout").push().setValue(shout);
            databaseReference.child("usertoken").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot datas : dataSnapshot.getChildren()) {
                        uid = datas.child("userName").getValue().toString();
                        sendPostToFCM(uid, "주변의 누군가가 외쳤습니다 ! 궁금하죠? ");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            startActivity(new Intent(getApplicationContext(),ShoutListActivity.class));
            finish();
        }

    }
}