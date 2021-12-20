package samples.speech.cognitiveservices.microsoft.com;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.microsoft.cognitiveservices.speech.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.translation.*;


public class MainActivity extends AppCompatActivity {

    private static final String SpeechSubscriptionKey = "881e98ee3ca74e72ab85a5add74b5f19";
    private static final String SpeechRegion = "eastus";

    private SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;
    TranslationRecognitionResult result;

    TranslationRecognizer recognizer;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView debugMassage = this.findViewById(R.id.debugMassage);
        TextView translationText = this.findViewById(R.id.Texttest);
        // Note: we need to request the permissions
        int requestCode = 5; // unique code for the permission request
        ActivityCompat.requestPermissions(samples.speech.cognitiveservices.microsoft.com.MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);

        // Initialize speech synthesizer and its dependencies
        speechConfig = SpeechConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
        speechConfig.setSpeechSynthesisLanguage("en-EU");
        assert (speechConfig != null);
        synthesizer = new SpeechSynthesizer(speechConfig);
        Button b = findViewById(R.id.button3);
        SpeechTranslationConfig translationConfig = SpeechTranslationConfig.fromSubscription(SpeechSubscriptionKey, SpeechRegion);
        translationConfig.setSpeechRecognitionLanguage("ru-RU");
        translationConfig.addTargetLanguage("en");
        b.setOnTouchListener((v, event) -> {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    try {
                        recognizer = new TranslationRecognizer(translationConfig);
                        Future<TranslationRecognitionResult> task = recognizer.recognizeOnceAsync();
                        assert (task != null);
                        result = task.get();
                        assert (result != null);
                    } catch (Exception ex) {
                        Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
                        assert (false);
                    }

                    return true;
                }

                case MotionEvent.ACTION_UP: {
                    recognizer.close();
                    try {
                        String language = "RU";
                        String translation = "хуй";
                        if (result != null)
                            for (Map.Entry<String, String> pair : result.getTranslations().entrySet()) {
                                language = pair.getKey();
                                translation = pair.getValue();
                            }


                        translationText.setText(translation);

                        // Note: this will block the UI thread, so eventually, you want to register for the event
                        SpeechSynthesisResult voip = synthesizer.SpeakText(translation);
                        assert (voip != null);
                        if (voip.getReason() == ResultReason.SynthesizingAudioCompleted) {
                            debugMassage.setText("Speech synthesis succeeded.");
                        } else if (voip.getReason() == ResultReason.Canceled) {
                            String cancellationDetails =
                                    SpeechSynthesisCancellationDetails.fromResult(voip).toString();
                            debugMassage.setText("Error synthesizing. Error detail: " +
                                    System.lineSeparator() + cancellationDetails +
                                    System.lineSeparator() + "Did you update the subscription info?");
                        }

                        voip.close();
                    } catch (Exception ex) {
                        Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
                        assert (false);
                    }


                    return true;
                }

                default:
                    return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release speech synthesizer and its dependencies
        synthesizer.close();
        speechConfig.close();
    }


    @SuppressLint("SetTextI18n")
    public void onTranslateButtonClicked(View v) throws ExecutionException, InterruptedException {


    }


}