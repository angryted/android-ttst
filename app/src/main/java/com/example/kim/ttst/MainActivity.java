package com.example.kim.ttst;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import android.speech.tts.TextToSpeech;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Locale;
import android.widget.Toast;


public class MainActivity extends Activity {
    TextToSpeech tts;
    EditText inputText;
    EditText answerText;
    Button buttonQ;
    Button buttonA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText=(EditText)findViewById(R.id.editText);
        answerText=(EditText)findViewById(R.id.editTextAnswer);
        buttonQ=(Button)findViewById(R.id.button_Q);
        buttonA=(Button)findViewById(R.id.button_A);


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

                String text = inputText.getText().toString() + answerText.getText().toString();
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
                    String sampleAnswer = "This is the sample answer.";
                    answerText.setText(text + "\n\n" + sampleAnswer);

                    buttonA.setText("Save");
                }
                else {
                    String text = answerText.getText().toString();
                    String sampleAnswer = "This is the sample answer.";
                    answerText.setText(text + "\n\n" + sampleAnswer);

                    saveAnswer();

                    buttonA.setText("Answer");
                }


            }
        });
    }

    public void loadQuestion() {
        File file = new File(getFilesDir(),"file.txt") ;
        FileReader fr = null ;
        BufferedReader bufrd = null;
        String str;

        try {
            // open file.
            fr = new FileReader(file) ;
            bufrd = new BufferedReader(fr);

            // read file.
            while ((str = bufrd.readLine()) != null) {
                String text = inputText.getText().toString();
                inputText.setText(text + str + "\n");
            }

            fr.close() ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        return;
    }

    public void saveAnswer() {
        File file = new File(getFilesDir(),"file.txt") ;
        FileWriter fw = null ;

        String text = answerText.getText().toString();

        try {
            // open file.
            fw = new FileWriter(file) ;

            // write file.
            fw.write(text) ;

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
