package com.example.photoera;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.List;

public class UnityPlayerActivity extends Activity
{
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    List<Photo> photoList = new ArrayList<Photo>();

    DatabaseReference ref= FirebaseDatabase.getInstance().getReference();

    // Setup activity layout
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);




        ref.child("upload").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String pKey = snapshot.getKey();
                    String pDate = snapshot.child("Date").getValue().toString();
                    String uName = snapshot.child("Name").getValue().toString();
                    double pLat = Double.parseDouble(snapshot.child("latitude").getValue().toString());
                    double pLon = Double.parseDouble(snapshot.child("longitude").getValue().toString());
                    String pPath = snapshot.child("picture").getValue().toString();
                    Photo aPhoto = new Photo(pKey,pDate,uName,pLat,pLon,pPath);
                    photoList.add(aPhoto);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();
    }

    public int getSize () {
        return photoList.size();
    }
    public String getPID(int index) {
        return photoList.get(index).pid;
    }
    public float getLat(int index) { return (float)photoList.get(index).lat; }
    public float getLon(int index) {
        return (float)photoList.get(index).lon;
    }

//    public void SetSysNumber(int sysNumber) { sys = sysNumber; }

    public void SeePhoto(String pid) {
        Intent i = new Intent(getApplicationContext(), PhotoActivity.class);
        i.putExtra("pid", pid);
        startActivity(i);
        finish();
    }

    public int GoMypage() {
        startActivity(new Intent(getApplicationContext(), MypageActivity.class));
        return 0;
    }

    public int GoUpload() {
        startActivity(new Intent(getApplicationContext(), UploadActivity.class));
        return 0;
    }

    public int GoShouting() {
        startActivity(new Intent(getApplicationContext(), ShoutListActivity.class));
        return 0;
    }

    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        mUnityPlayer.destroy();
        super.onDestroy();

    }

    // Pause Unity
    @Override protected void onPause()
    {
//        if(sys == 0) { // Mypage
//            Intent i = new Intent(getApplicationContext(), MypageActivity.class);
//            startActivity(i);
//        } else if (sys == 1) { // Upload
//            Intent i = new Intent(getApplicationContext(), UploadActivity.class);
//            startActivity(i);
//        } else if (sys == 2) {
//            Intent i = new Intent(getApplicationContext(), MypageActivity.class);
//            startActivity(i);
//        }
        super.onPause();
        mUnityPlayer.pause();


    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override protected void onStart()
    {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override protected void onStop()
    {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}
