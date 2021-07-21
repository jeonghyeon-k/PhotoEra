package com.example.photoera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FindActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG="FindActivity";

    private EditText userName;
    private EditText userEmail;
    private Button findBtn;
    private Button findBtn2;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        userName = (EditText) findViewById(R.id.userName);
        userEmail = (EditText) findViewById(R.id.userEmail);
        findBtn = (Button) findViewById(R.id.findBtn);
        findBtn2 = (Button) findViewById(R.id.findBtn2);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        findBtn.setOnClickListener(this);
        findBtn2.setOnClickListener(this);
    }

    public void dbQuery(){
        final String name = userName.getText().toString().trim();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users");
        Query queries=ref.orderByChild("name").equalTo(name);
        queries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    String Email=datas.child("email").getValue().toString();
                    Intent intent=new Intent(getApplicationContext(), PopupActivity.class);
                    intent.putExtra("data","회원님의 이메일주소는 "+Email+" 입니다.");
                    startActivityForResult(intent,1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void dbQuery2(){
        final String email = userEmail.getText().toString().trim();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users");
        Query queries=ref.orderByChild("email").equalTo(email);
        queries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    String pass=datas.child("password").getValue().toString();
                    Intent intent=new Intent(getApplicationContext(), PopupActivity.class);
                    intent.putExtra("data","회원님의 비밀번호는 "+pass+" 입니다.");
                    startActivityForResult(intent,1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view==findBtn){
            dbQuery();
        }
        if(view==findBtn2){
            dbQuery2();
        }
    }


    protected void onDestroy() {

        Log.d(TAG, "called onDestroy");

        progressDialog.dismiss();

        super.onDestroy();

    }
}
