package com.example.photoera;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class MyPhotoActivity extends Activity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth auth;

    ImageView photo, Profile;
    ImageButton Btncomment, Btnlike, btnReturn, btnRemove;
    TextView Title, LikeCount, date;


    String Photo_title, TestID, getkey, getdate, getname, setCount;
    int getCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myphoto);
        Title = (TextView) findViewById(R.id.Title);
        LikeCount = (TextView) findViewById(R.id.likecount);
        date = (TextView) findViewById(R.id.date);
        btnReturn = (ImageButton) findViewById(R.id.return_photo);
        btnRemove = (ImageButton) findViewById(R.id.remove_photo);
        Btncomment = (ImageButton) findViewById(R.id.btncomment);
        Btnlike = (ImageButton) findViewById(R.id.btnlike);
        photo = (ImageView) findViewById(R.id.photo);
        Profile = (ImageView) findViewById(R.id.profile);

        Intent intent = getIntent();

        Btnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStarClicked2();
            }
        });



       auth = FirebaseAuth.getInstance();
        final String email = auth.getCurrentUser().getEmail().toString().trim();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query queries = ref.child("users").orderByChild("email").equalTo(email);
        queries.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    TestID = datas.child("name").getValue().toString().trim();
                    Title.setText(TestID);


                    StorageReference profileRef = mStorageRef.child("profiles/" + TestID + ".png");
                    try {
                        // Storage 에서 다운받아 저장시킬 임시파일
                        final File profileFile = File.createTempFile("upload", "png");
                        profileRef.getFile(profileFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Success Case
                                Bitmap bitmapprofileImage = BitmapFactory.decodeFile(profileFile.getPath());
                                Profile.setImageBitmap(bitmapprofileImage);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Fail Case
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "프로필 불러오기 실패 !!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //데이터 받기
        getkey = intent.getStringExtra("Getkey");
        databaseReference.child("upload").orderByKey().equalTo(getkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        getname = snapshot.child("Name").getValue().toString();
                        getdate = snapshot.child("Date").getValue().toString();
                        Photo_title = snapshot.getKey();
                        date.setText(getdate.substring(0,4) + "년 " + getdate.substring(4,6) + "월 " + getdate.substring(6,8) + "일");


                    }
                } else {
                    System.out.println("No data");
                }

                StorageReference imageRef = mStorageRef.child("upload/" + getdate + ".png");
                try {
                    // Storage 에서 다운받아 저장시킬 임시파일
                    final File imageFile = File.createTempFile("upload", "png");
                    imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Success Case
                            Bitmap bitmapImage = BitmapFactory.decodeFile(imageFile.getPath());
                            photo.setImageBitmap(bitmapImage);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Fail Case
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "사진 불러오기 실패 !!", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        databaseReference.child("Like").orderByKey().equalTo(getkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("countusers").exists()) {
                        getCount = (int) snapshot.child("countusers").getChildrenCount();
                        setCount = Integer.toString(getCount);
                        LikeCount.setText(setCount);
                    } else {
                        getCount = 0;
                        LikeCount.setText("0");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MyPhotoActivity.this);
                dlg.setTitle("정말 삭제 하시겠습니까?");
                dlg.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                StorageReference desertRef = mStorageRef.child("upload/" + getdate + ".png");

                                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });

                                databaseReference.child("upload").child(getkey).removeValue();
                                Toast.makeText(getApplicationContext(), "삭제 완료 !!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),
                                        MypageActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

        Btncomment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        CommentActivity.class);
                intent.putExtra("TestID", TestID);
                intent.putExtra("phototitle", Photo_title);
                startActivity(intent);
            }
        });
    }

    private void onStarClicked2() {

        if (getCount == 0) {
            databaseReference.child("Like").child(getkey).child("countusers").child(TestID).setValue("Like");
            getCount += 1;
            LikeCount.setText("1");
        } else {
            databaseReference.child("Like").orderByKey().equalTo(getkey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("countusers").child(TestID).exists()) {
                            Toast.makeText(getApplicationContext(), "좋아요 취소 !!", Toast.LENGTH_LONG).show();
                            databaseReference.child("Like").child(getkey).child("countusers").child(TestID).removeValue();
                            getCount -= 1;
                            setCount = Integer.toString(getCount);
                            LikeCount.setText(setCount);

                        } else {
                            Toast.makeText(getApplicationContext(), "좋아요 !!", Toast.LENGTH_LONG).show();
                            databaseReference.child("Like").child(getkey).child("countusers").child(TestID).setValue("Like");
                            getCount += 1;
                            setCount = Integer.toString(getCount);
                            LikeCount.setText(setCount);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}