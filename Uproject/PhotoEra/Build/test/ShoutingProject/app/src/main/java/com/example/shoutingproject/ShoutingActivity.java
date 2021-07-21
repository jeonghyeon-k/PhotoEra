package com.example.;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                        name = datas.child("Name").getValue().toString();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    @Override
    public void onClick(View v) {
            if(v.getId() == R.id.btn_shout) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
                Date now = new Date();
                String Date=formatter.format(now);
                ShoutingDTO shout = new ShoutingDTO(name, shout_text.getText().toString(), Date); //ChatDTO를 이용하여 데이터를 묶는다.
                databaseReference.child("shout").child(uid).setValue(shout);
                shout_text.setText(""); //입력창 초기화
            }

    }
}
