package com.example.freakyai;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> result;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    //private final int REQ_CODE_SPEECH_INPUT = 100;
    TextToSpeech tts;
    SpeechRecognizer sr;
    private int wallpaper_flag=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String recordAudioPermission = Manifest.permission.RECORD_AUDIO;
            String phonePermission = Manifest.permission.CALL_PHONE;
            String contactsPermission = Manifest.permission.READ_CONTACTS;
            String callLogPermission = Manifest.permission.READ_CALL_LOG;

            if (checkCallingOrSelfPermission(recordAudioPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{recordAudioPermission}, 101);
            }

            if (checkCallingOrSelfPermission(phonePermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{phonePermission}, 101);
            }

            if (checkCallingOrSelfPermission(contactsPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{contactsPermission}, 101);
            }

            if (checkCallingOrSelfPermission(callLogPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{callLogPermission}, 101);
            }
        }


        //BottomSheetDialog bsd = new BottomSheetDialog(MainActivity.this,R.style.BottomSheetTheme);
        //View bottomView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main, (LinearLayout) findViewById(R.id.BottomContainer),false);
        //bsd.setCanceledOnTouchOutside(false);

        onReceive(getApplicationContext(), getIntent());

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //txtSpeechInput.setText("Say Something..");
                speakOut("Say Something..");
                getSpeechInput();
            }
        });

        /*bottomView.findViewById(R.id.btnSpeak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //txtSpeechInput.setText("Say Something..");
                speakOut("Say Something..");
                getSpeechInput();
            }
        });

        bsd.setContentView(bottomView);
        bsd.show();
        */
    }

    public void onReceive(Context context, Intent intent) {
        //---get the SMS message passed in---
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Log.e("TTS", "Working");
                    speakOut("Hey!");
                }
            }
        });
    }


    private void getSpeechInput() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        //i.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);

        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new RecognitionListener() {
            private static final String TAG = "RecognitionListener";

            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.d(TAG, "onRmsChanged");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.d(TAG, "onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d(TAG, "onEndofSpeech");
            }

            @Override
            public void onError(int error) {
                Log.e(TAG, "error " + error);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResults(Bundle data) {
                if (null != data) {
                    result = data.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    String output = result.get(0).toString();
                    getOutput(output);
                    //txtSpeechInput.setText(output);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.d(TAG, "onPartialResults");
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.d(TAG, "onEvent " + eventType);
            }
        });
        sr.startListening(i);
    }

    public void speakOut(String output) {
        tts.speak(output, TextToSpeech.QUEUE_ADD, null);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getOutput(String output) {
        Log.e("Output", output);
        //txtSpeechInput.setText(output);


        //Make a phone Call
        if(output.contains("call home") || output.contains("Call mummy")){
            speakOut("Calling home..");
            startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", "9422469345", null)));
        }

        //Invoke Music Player Activity
        if(output.contains("play music") || output.contains("music player") || output.contains("open music player")){
            speakOut("Opening music player");
            startActivity(new Intent(MainActivity.this, MusicPlayer.class));
        }

        //Create a note
        if(output.contains("create note") || output.contains("create a note") || output.contains("make a note")){
            speakOut("What would you like to add into note");
            //getSpeechInput();

        }

        //Set Wallpaper
        if(output.contains("change") || output.contains("wallpaper")){
            if(output.contains("lockscreen") || output.contains("lock screen")){
                wallpaper_flag = 0;
            }
            else{
                wallpaper_flag = 1;
            }
            speakOut("Changing wallpaper");
            setWallpaper(wallpaper_flag);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setWallpaper(int wallpaper_flag) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.lighthouse);
        WallpaperManager wp = WallpaperManager.getInstance(getApplicationContext());
        try{
            if(wallpaper_flag == 0) {
                wp.setBitmap(bm,null,true, WallpaperManager.FLAG_LOCK);
            }
            else if(wallpaper_flag == 1){
                wp.setBitmap(bm,null,true, WallpaperManager.FLAG_SYSTEM);
            }

            Toast.makeText(this,"Wallpaper Set!",Toast.LENGTH_SHORT).show();
        }
        catch (IOException e){
            Toast.makeText(this,"Couldn't set wallpaper!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
};
