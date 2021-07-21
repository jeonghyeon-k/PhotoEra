package com.example.photoera;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoutListActivity extends AppCompatActivity implements View.OnClickListener {

    String setitem, UserID;
    ImageButton return_shout;
    Button btn_shouting;
    ListView shout_list;
    ArrayList<ShoutingDTO> s;
    ShoutListAdapter sAdapter;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shout_list);
        btn_shouting = (Button) findViewById(R.id.btn_shouting);
        shout_list = (ListView) findViewById(R.id.shout_list);
        return_shout = (ImageButton) findViewById(R.id.return_shout);
        s = new ArrayList<ShoutingDTO>();

        btn_shouting.setOnClickListener(this);
        return_shout.setOnClickListener(this);


        databaseReference.child("shout").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    ShoutingDTO d = new ShoutingDTO();
                    d.name = datas.child("name").getValue().toString();
                    d.message = datas.child("message").getValue().toString();
                    d.date = datas.child("date").getValue().toString();
                    s.add(d);
                    Log.i("TAG", "생성 객체" + s);

                    sAdapter = new ShoutListAdapter(ShoutListActivity.this, s);
                    shout_list.setAdapter(sAdapter);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });


        auth = FirebaseAuth.getInstance();
        final String email1 = auth.getCurrentUser().getEmail().toString().trim();
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
        Query queries = ref1.child("users").orderByChild("email").equalTo(email1);
        queries.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    UserID = datas.child("name").getValue().toString().trim();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        shout_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        setitem = s.get(position).getName();
                        AlertDialog.Builder dlg = new AlertDialog.Builder(ShoutListActivity.this);
                        dlg.setTitle(setitem + "님과 채팅 하시겠습니까?");
                        dlg.setPositiveButton("네",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ShoutListActivity.this, ChatActivity.class);

                                        intent.putExtra("chatName", setitem);
                                        intent.putExtra("TestID", UserID);
                                        startActivity(intent);
                                    }
                                });
                        dlg.setNegativeButton("아니요", null);
                        dlg.show();

                    }
                }
        );


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), UnityPlayerActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_shouting) {
            startActivity(new Intent(getApplicationContext(), ShoutingActivity.class));
            finish();
        }
        if (v.getId() == R.id.return_shout) {
            Intent intent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
            startActivity(intent);
            finish();

        }
    }

}
