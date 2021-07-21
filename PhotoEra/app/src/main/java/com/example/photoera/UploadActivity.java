package com.example.photoera;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.alhazmy13.imagefilter.ImageFilter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UploadActivity";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int CROP_FROM_IMAGE = 1;
    private Uri mlmageCaptureUri;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth auth;
    private Button btUpload;
    private ImageButton btnreturn;
    private ImageView ivPreview;
    private Uri absolutePath;
    private ImageView filter1;
    private ImageView filter2;
    private ImageView filter3;
    private ImageView filter4;
    private ImageView filter5;
    private ImageView filter6;
    double longitude;
    double latitude;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        //checkPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UploadActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            } else {
                final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener);
            }
        }

        btUpload = (Button) findViewById(R.id.bt_upload);
        btnreturn = (ImageButton) findViewById(R.id.returnbtn);
        ivPreview = (ImageView) findViewById(R.id.iv_preview);
        filter1 = (ImageView) findViewById(R.id.filter1);
        filter2 = (ImageView) findViewById(R.id.filter2);
        filter3 = (ImageView) findViewById(R.id.filter3);
        filter4 = (ImageView) findViewById(R.id.filter4);
        filter5 = (ImageView) findViewById(R.id.filter5);
        filter6 = (ImageView) findViewById(R.id.filter6);

        //버튼 클릭 이벤트
        btUpload.setOnClickListener(this);
        filter1.setOnClickListener(this);
        filter2.setOnClickListener(this);
        filter3.setOnClickListener(this);
        filter4.setOnClickListener(this);
        filter5.setOnClickListener(this);
        filter6.setOnClickListener(this);

        btnreturn.setOnClickListener(this);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mlmageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mlmageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }


    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PICK_FROM_CAMERA: {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mlmageCaptureUri, "image/*");
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                intent.putExtra("output", mlmageCaptureUri);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }

            case CROP_FROM_IMAGE: {
                if (resultCode != RESULT_OK) {
                    return;
                }
                final Bundle extras = data.getExtras();

                if (extras != null) {

                    Bitmap photo = BitmapFactory.decodeFile(mlmageCaptureUri.getPath());
                    ivPreview.setImageBitmap(photo); //레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                    filter1.setImageBitmap(ImageFilter.applyFilter(photo, ImageFilter.Filter.MOTION_BLUR));
                    filter2.setImageBitmap(ImageFilter.applyFilter(photo, ImageFilter.Filter.HDR));
                    filter3.setImageBitmap(ImageFilter.applyFilter(photo, ImageFilter.Filter.SOFT_GLOW));
                    filter4.setImageBitmap(ImageFilter.applyFilter(photo, ImageFilter.Filter.OLD));
                    filter5.setImageBitmap(ImageFilter.applyFilter(photo, ImageFilter.Filter.LOMO));
                    filter6.setImageBitmap(ImageFilter.applyFilter(photo, ImageFilter.Filter.LIGHT));
                    break;
                }
                File f = new File(mlmageCaptureUri.getPath());
                if (f.exists()) {
                    f.delete();
                }

            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //upload the file
    private void uploadFile() {
        //업로드할 파일이 있으면 수행
        auth = FirebaseAuth.getInstance();
        final String email = auth.getCurrentUser().getEmail().toString().trim();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query queries = ref.child("users").orderByChild("email").equalTo(email);
        queries.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    String Name = datas.child("name").getValue().toString();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_hhmm");
                    Date now = new Date();
                    String Date = formatter.format(now);
                    String picture = "gs://project-2dfe0.appspot.com" + "/upload/" + Name + Date + ".png";
                    Upload upload = new Upload(Name, Date, picture, longitude, latitude);
                    ref.child("upload").push().setValue(upload);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (absolutePath != null) {

            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_hhmm");
            Date now = new Date();
            String filename = formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://project-2dfe0.appspot.com").child("upload/" + filename);
            //올라가거라...
            storageRef.putFile(absolutePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                            databaseReference.child("usertoken").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                        uid = datas.child("userName").getValue().toString();
                                        sendPostToFCM(uid, "누군가가 사진을 올렸습니다 ! 궁금하죠? ");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            startActivity(new Intent(UploadActivity.this, UnityPlayerActivity.class));
                            finish();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }
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

    private Location lastKnownLocation;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LocationManager lm2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lastKnownLocation = location;
            lm2.removeUpdates(mLocationListener);
            longitude = lastKnownLocation.getLongitude();
            latitude = lastKnownLocation.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    public void onClick(View v) {
        if (v == btnreturn) {
            Intent intent = new Intent(getApplicationContext(),
                    UploadActivity.class);
            startActivity(intent);
        }
        if (v == btUpload) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("위치 정보를 받는 중입니다 잠시만 기다려 주세요...");
            progressDialog.show();
            progressDialog.dismiss();
            Context mContext;
            mContext = getApplicationContext();
            Bitmap bitmap = ((BitmapDrawable) ivPreview.getDrawable()).getBitmap();
            absolutePath = getImageUri(mContext, bitmap);
            uploadFile();
        }
        if (v == filter1) {
            Bitmap bitmap = ((BitmapDrawable) filter1.getDrawable()).getBitmap();
            ivPreview.setImageBitmap(bitmap);
        }
        if (v == filter2) {
            Bitmap bitmap = ((BitmapDrawable) filter2.getDrawable()).getBitmap();
            ivPreview.setImageBitmap(bitmap);
        }
        if (v == filter3) {
            Bitmap bitmap = ((BitmapDrawable) filter3.getDrawable()).getBitmap();
            ivPreview.setImageBitmap(bitmap);
        }
        if (v == filter4) {
            Bitmap bitmap = ((BitmapDrawable) filter4.getDrawable()).getBitmap();
            ivPreview.setImageBitmap(bitmap);
        }
        if (v == filter5) {
            Bitmap bitmap = ((BitmapDrawable) filter5.getDrawable()).getBitmap();
            ivPreview.setImageBitmap(bitmap);
        }
        if (v == filter6) {
            Bitmap bitmap = ((BitmapDrawable) filter6.getDrawable()).getBitmap();
            ivPreview.setImageBitmap(bitmap);
        }
    }
}