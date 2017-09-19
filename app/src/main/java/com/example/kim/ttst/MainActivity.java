package com.example.kim.ttst;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    TextToSpeech tts;
    EditText questionText;
    EditText answerText;
    Button buttonQ;
    Button buttonA;

    Random mRand;
    int randomNumber=0;

    String buffQuestion, buffAnswer;


    String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ttst";
    File fileList[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionText =(EditText)findViewById(R.id.editText);
        answerText=(EditText)findViewById(R.id.editTextAnswer);
        buttonQ=(Button)findViewById(R.id.button_Q);
        buttonA=(Button)findViewById(R.id.button_A);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        // baseDir 생성.
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"ttst");
        if( !file.exists() )  // 원하는 경로에 폴더가 있는지 확인
            file.mkdirs();

        fileList = file.listFiles();
        mRand = new Random();


        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    //tts.setLanguage(Locale.KOREAN);
                    tts.setLanguage(Locale.US);
                }
            }
        });

        buttonQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadQuestion();

                String text = questionText.getText().toString() + answerText.getText().toString();
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

                //http://stackoverflow.com/a/29777304
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(text);
                } else {
                    ttsUnder20(text);
                }


            }
        });

        buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonName = buttonA.getText().toString();
                if(buttonName.compareTo("Answer") == 0) {
                    String text = answerText.getText().toString();
                    answerText.setText(text + "\r\n\r\n" + buffAnswer);

                    buttonA.setText("Save");
                }
                else {
                    saveAnswer();
                }


            }
        });
    }

    public void loadQuestion() {
        randomNumber = mRand.nextInt(fileList.length);
        File file = new File(baseDir,fileList[randomNumber].getName()) ;
        FileReader fr = null ;
        BufferedReader bufrd = null;
        String str;

        try {
            // open file.
            fr = new FileReader(file) ;
            bufrd = new BufferedReader(fr);

            // read file.
            buffQuestion = "";
            buffAnswer = "";
            int bAnswer=0;
            while ((str = bufrd.readLine()) != null) {
                if(str.indexOf("<<ANSWER>>") == 0) {
                    bAnswer = 1;
                    continue;
                }
                if(bAnswer == 0)
                    buffQuestion = buffQuestion + str + "\r\n";
                else
                    buffAnswer = buffAnswer + str + "\r\n";
            }
            questionText.setText(buffQuestion);

            fr.close() ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }

        buttonA.setText("Answer");

        return;
    }

    public void saveAnswer() {
        File file = new File(baseDir,fileList[randomNumber].getName());
        FileWriter fw = null ;

        String qText = questionText.getText().toString();
        String aText = answerText.getText().toString();
        try {
            // open file.
            fw = new FileWriter(file) ;

            // write file.
            fw.write(qText+"\r\n");
            fw.write("<<ANSWER>>\r\n");
            fw.write(aText);
        } catch (Exception e) {
            e.printStackTrace() ;
        }

        // close file.
        if (fw != null) {
            // catch Exception here or throw.
            try {
                fw.close() ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(tts !=null){
            tts.stop();
            tts.shutdown();
        }
    }


    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}
