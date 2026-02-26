package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.*;

public class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private TextView resultView, percentText;
    private ProgressBar gaugeView;
    private LinearLayout resultCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.input_text);
        resultView = findViewById(R.id.result_view);
        percentText = findViewById(R.id.percent_text);
        gaugeView = findViewById(R.id.gauge_view);
        resultCard = findViewById(R.id.result_card);

        findViewById(R.id.btn_analyze).setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            if (text.length() >= 100) {
                resultCard.setVisibility(View.VISIBLE);
                startScanningAnimation();
                detectAIContent(text);
            }
        });

        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            inputText.setText("");
            resultCard.setVisibility(View.GONE);
            gaugeView.setProgress(0);
        });
    }

    private void startScanningAnimation() {
        ObjectAnimator.ofInt(gaugeView, "progress", 0, 100).setDuration(1500).start();
        percentText.setText("...");
        resultView.setText("SCANNING...");
    }

    private void detectAIContent(String text) {
        OkHttpClient client = new OkHttpClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("text", text);
        RequestBody body = RequestBody.create(new Gson().toJson(map), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://ai-content-detector-ai-gpt.p.rapidapi.com/api/detectText/")
                .post(body)
                .addHeader("x-rapidapi-key", "f85841d994mshf6d5ee39ed6f66ep1542c6jsnda3872138755")
                .addHeader("x-rapidapi-host", "ai-content-detector-ai-gpt.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {}
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    DetectorResponse data = new Gson().fromJson(response.body().string(), DetectorResponse.class);
                    runOnUiThread(() -> {
                        ObjectAnimator.ofInt(gaugeView, "progress", 100, (int)data.fakePercentage)
                                .setDuration(1000).start();
                        percentText.setText((int)data.fakePercentage + "%");
                        resultView.setText(data.fakePercentage > 50 ? "AI Detected" : "Likely Human");
                    });
                }
            }
        });
    }
}