package samples.speech.cognitiveservices.microsoft.com;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import com.microsoft.cognitiveservices.speech.translation.*;

import java.util.concurrent.Future;

import static android.Manifest.permission.*;


public class MainActivity extends AppCompatActivity {

    private static String SpeechSubscriptionKey = "881e98ee3ca74e72ab85a5add74b5f19";
    private static String SpeechRegion = "eastus";

    private SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int requestCode = 5;
        ActivityCompat.requestPermissions(samples.speech.cognitiveservices.microsoft.com.MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);

        speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
        speechConfig.setSpeechRecognitionLanguage("ru-RU");
        assert (speechConfig != null);

        synthesizer = new SpeechSynthesizer(speechConfig);
        assert (synthesizer != null);

        SpeechTranslationConfig translationConfig = SpeechTranslationConfig.fromSubscription(
                SpeechSubscriptionKey, SpeechRegion);
        translationConfig.setSpeechRecognitionLanguage("ru-RU");
        translationConfig.addTargetLanguage("en");

        TextView txt = (TextView) this.findViewById(R.id.text); // 'hello' is the ID of your text view

        findViewById(R.id.button4).setOnTouchListener(new View.OnTouchListener() {
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

            // SpeechRecognitionResult result;
            //SpeechRecognizer reco;
            TranslationRecognitionResult result;
            TranslationRecognizer recognizer;
            String language;
            String translation;

            private void Start() {
                try {
                    recognizer = new TranslationRecognizer(translationConfig);
                    assert (recognizer != null);

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
                    language = "хуй";
                    translation = "хуй";
                    recognizer.stopContinuousRecognitionAsync();
                    if(result != null)
                    for (Map.Entry<String, String> pair : result.getTranslations().entrySet()) {
                        language = pair.getKey();
                        translation = pair.getValue();
                    }
                    txt.setText(translation);
                } catch (Exception ex) {
                    Log.e("SpeechSDKDemo", "myauuufds unexpected " + ex.getMessage());
                    assert (false);
                }
//                if (result.getReason() == ResultReason.RecognizedSpeech) {
//                    txt.setText(result.getText());
//                } else {
//                    txt.setText("Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString());
//                }
//                reco.close();

            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TextView txt = (TextView) this.findViewById(R.id.text); // 'hello' is the ID of your text view

        try {

            SpeechRecognizer reco = new SpeechRecognizer(speechConfig);
            assert (reco != null);

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert (task != null);

            // Note: this will block the UI thread, so eventually, you want to
            //        register for the event (see full samples)
            SpeechRecognitionResult result = task.get();
            assert (result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                txt.setText(result.getText());
            } else {
                txt.setText("Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString());
            }

            reco.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert (false);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release speech synthesizer and its dependencies
        synthesizer.close();
        speechConfig.close();
    }

    //    @SuppressLint("SetTextI18n")
//    public void onTranslateButtonClicked(View v) throws ExecutionException, InterruptedException {
//        TextView speakText = this.findViewById(R.id.text);
//
//
////        //TranslationRecognizer recognizer = new TranslationRecognizer(translationConfig);
////        TranslationRecognitionResult result = recognizer.recognizeOnceAsync().get();
////        speakText.setText(result.getTranslations().toString());
//    }
//    @SuppressLint("SetTextI18n")
//    public void onTranslateButtonClicked(View v) throws ExecutionException, InterruptedException {
////        TextView debugMassage = this.findViewById(R.id.debugMassage);
////        TextView translationText = this.findViewById(R.id.Texttest);
////        SpeechTranslationConfig translationConfig = SpeechTranslationConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
////        translationConfig.setSpeechRecognitionLanguage("ru-RU");
////        translationConfig.addTargetLanguage("en");
////        TranslationRecognizer recognizer = new TranslationRecognizer(translationConfig);
////        TranslationRecognitionResult result = recognizer.recognizeOnceAsync().get();
////        String language = null;
////        String translation = null;
////        for (Map.Entry<String, String> pair : result.getTranslations().entrySet()) {
////            language = pair.getKey();
////            translation = pair.getValue();
////        }
////        translationText.setText(translation);
////        try {
////            // Note: this will block the UI thread, so eventually, you want to register for the event
////            SpeechSynthesisResult voip = synthesizer.SpeakText(translation);
////            assert (voip != null);
////            if (voip.getReason() == ResultReason.SynthesizingAudioCompleted) {
////                debugMassage.setText("Speech synthesis succeeded.");
////            } else if (voip.getReason() == ResultReason.Canceled) {
////                String cancellationDetails =
////                        SpeechSynthesisCancellationDetails.fromResult(voip).toString();
////                debugMassage.setText("Error synthesizing. Error detail: " +
////                        System.lineSeparator() + cancellationDetails +
////                        System.lineSeparator() + "Did you update the subscription info?");
////            }
////
////            voip.close();
////        } catch (Exception ex) {
////            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
////            assert (false);
////        }
//
//    }
    @SuppressLint("SetTextI18n")
    public void onSpeechButtonClicked(View v) {
        TextView txt = (TextView) this.findViewById(R.id.text); // 'hello' is the ID of your text view

        try {
            SpeechConfig config = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
            config.setSpeechRecognitionLanguage("ru-RU");
            assert (config != null);

            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert (reco != null);

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert (task != null);

            // Note: this will block the UI thread, so eventually, you want to
            //        register for the event (see full samples)
            SpeechRecognitionResult result = task.get();
            assert (result != null);

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                txt.setText(result.getText());
            } else {
                txt.setText("Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString());
            }

            reco.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert (false);
        }
    }


    @SuppressLint("SetTextI18n")
    public void onTalkButtonClicked(View v) {

        TextView outputMessage = this.findViewById(R.id.text3);
        TextView speakText = this.findViewById(R.id.text);

        try {
            // Note: this will block the UI thread, so eventually, you want to register for the event
            SpeechSynthesisResult result = synthesizer.SpeakText(speakText.getText().toString());
            assert (result != null);

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                outputMessage.setText("Speech synthesis succeeded.");
            } else if (result.getReason() == ResultReason.Canceled) {
                String cancellationDetails =
                        SpeechSynthesisCancellationDetails.fromResult(result).toString();
                outputMessage.setText("Error synthesizing. Error detail: " +
                        System.lineSeparator() + cancellationDetails +
                        System.lineSeparator() + "Did you update the subscription info?");
            }

            result.close();
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            assert (false);
        }
    }
}