package com.example.photoera;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MypageActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    GoogleMap gMap;
    SupportMapFragment mapFragment;
    Button BtnChat, Btnuser, Btnlogout, Btnback;

    String TestID; //아이디

    String getlat, getlng, getname, getkey;

    private FirebaseAuth firebaseAuth;


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        firebaseAuth = FirebaseAuth.getInstance();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        BtnChat = (Button) findViewById(R.id.Chat);
        Btnuser = (Button) findViewById(R.id.User);
        Btnlogout = (Button) findViewById(R.id.Logout);

        Btnback = (Button) findViewById(R.id.back);

        BtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        StartActivity.class);
                intent.putExtra("TestID", TestID);
                startActivity(intent);
            }
        });

        Btnuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        UserEditActivity.class);
                intent.putExtra("TestID", TestID);
                startActivity(intent);
            }
        });

        Btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MypageActivity.this);
                dlg.setTitle("정말 로그아웃 하시겠습니까?");
                dlg.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "로그아웃 완료 !!", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

        Btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        UnityPlayerActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onMapReady(GoogleMap map) {

        gMap = map;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.568256, 126.897240), 10));
        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.setOnMarkerClickListener(this);

        auth = FirebaseAuth.getInstance();
        final String email = auth.getCurrentUser().getEmail().toString().trim();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query queries = ref.child("users").orderByChild("email").equalTo(email);
        queries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    TestID = datas.child("name").getValue().toString();
                }
                databaseReference.child("upload").orderByChild("Name").equalTo(TestID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                getlat = snapshot.child("latitude").getValue().toString();
                                getlng = snapshot.child("longitude").getValue().toString();
                                getkey = snapshot.getKey();
                                getname = snapshot.child("Name").getValue().toString();

                                double lat = Double.valueOf(getlat);
                                double lon = Double.valueOf(getlng);

                                MarkerOptions marker = new MarkerOptions();

                                marker.position(new LatLng(lat, lon))
                                        .title(getkey);

                                gMap.addMarker(marker).showInfoWindow();
                            }
                        } else {
                            System.out.println("No data");
                        }

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), UnityPlayerActivity.class));
        finish();
    }

    public boolean onMarkerClick(Marker mark) {

        Intent intent = new Intent(MypageActivity.this,
                MyPhotoActivity.class);
        intent.putExtra("Getkey", mark.getTitle());
        startActivity(intent);
        return true;
    }


}