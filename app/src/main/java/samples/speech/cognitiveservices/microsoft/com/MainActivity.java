package samples.speech.cognitiveservices.microsoft.com;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.microsoft.cognitiveservices.speech.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Future;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.translation.*;


public class MainActivity extends AppCompatActivity {

    String[] langs_codes = { "en-US", "ru-RU", "uk-UA", "fr-FR", "it-IT", "ja-JP", "pl-PL", "de-DE", "ko-KR", "zh-CHS"};
    String[] langs_names = { "English", "Русский", "Український", "Français", "Italiano", "日本語", "Polski", "Deutsch", "한국어", "中文"};

    private static String SpeechSubscriptionKey = "881e98ee3ca74e72ab85a5add74b5f19";
    private static String SpeechRegion = "eastus";

    private static String language1 = "en-US";
    private static String language2 = "ru-RU";

    private SpeechSynthesizer synthesizer;
    private SpeechConfig speechConfig;
    private SpeechTranslationConfig translationConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinnerRight = (Spinner) findViewById(R.id.spinnerRight);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, langs_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRight.setAdapter(adapter);
        spinnerRight.setSelection(1);

        Spinner spinnerLeft = (Spinner) findViewById(R.id.spinnerLeft);
        ArrayAdapter<String> adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, langs_names);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLeft.setAdapter(adapter2);
        spinnerLeft.setSelection(0);

        int requestCode = 5;
        ActivityCompat.requestPermissions(samples.speech.cognitiveservices.microsoft.com.MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);

        translationConfig = SpeechTranslationConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
        translationConfig.setSpeechRecognitionLanguage(language1);
        translationConfig.addTargetLanguage(language2);

        speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
        speechConfig.setSpeechSynthesisLanguage(language2);
        assert (speechConfig != null);

        TextView txt = (TextView) this.findViewById(R.id.text); // 'hello' is the ID of your text view

        findViewById(R.id.hold).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Start();
                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        Close();
                        return true;
                    }

                    default:
                        return false;
                }
            }

            TranslationRecognitionResult result;
            TranslationRecognizer recognizer;
            String language;
            String translation;

            private void Start() {
                try {
                    synthesizer = new SpeechSynthesizer(speechConfig);

                    recognizer = new TranslationRecognizer(translationConfig);

                    Future<TranslationRecognitionResult> task = recognizer.recognizeOnceAsync();
                    assert (task != null);

                    result = task.get();
                    assert (result != null);
                } catch (Exception ex) {
                    Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
                    assert (false);
                }
            }

            private void Close() {
                try {
                    language = "hui";
                    translation = "hui";
                    recognizer.close();
                    if (result != null)
                        for (Map.Entry<String, String> pair : result.getTranslations().entrySet()) {
                            language = pair.getKey();
                            translation = pair.getValue();
                        }
                    txt.setText(translation);

                    // Note: this will block the UI thread, so eventually, you want to register for the event
                    SpeechSynthesisResult voip = synthesizer.SpeakText(translation);
                    assert (voip != null);
                    voip.close();
                } catch (Exception ex) {
                    Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
                    assert (false);
                }
            }
        });
        findViewById(R.id.swap).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Button left = findViewById(R.id.buttonLanguage1);
                Button right = findViewById(R.id.buttonLanguage2);


                String t = left.getText().toString();
                left.setText(right.getText().toString());
                right.setText(t);

                t = language1;
                language1 = language2;
                language2 = t;

                translationConfig = SpeechTranslationConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
                translationConfig.setSpeechRecognitionLanguage(language1);
                translationConfig.addTargetLanguage(language2);

                speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
                speechConfig.setSpeechSynthesisLanguage(language2);
            }
        });
        spinnerLeft.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                int lang_id = Arrays.asList(item).indexOf(item);
                String lang_code = langs_codes[lang_id];

                translationConfig = SpeechTranslationConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
                language1 = lang_code;
                translationConfig.setSpeechRecognitionLanguage(language1);
                translationConfig.addTargetLanguage(language2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerRight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                int lang_id = Arrays.asList(item).indexOf(item);
                String lang_code = langs_codes[lang_id];

                translationConfig = SpeechTranslationConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
                language2 = lang_code;
                translationConfig.setSpeechRecognitionLanguage(language1);
                translationConfig.addTargetLanguage(language2);

                speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
                speechConfig.setSpeechSynthesisLanguage(language2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}