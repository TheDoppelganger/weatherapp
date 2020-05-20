package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText editSearch;
    ImageButton btnSearch;
    TextView desc,temp,city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editSearch = findViewById(R.id.editSearch);
        btnSearch  = findViewById(R.id.btnSearch);
        desc = findViewById(R.id.desc);
        temp = findViewById(R.id.temp);
        city = findViewById(R.id.city);

    btnSearch.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String searchLocation = editSearch.getText().toString();
            if(searchLocation.isEmpty()){
                Toast.makeText(MainActivity.this, "Please Enter a Location", Toast.LENGTH_SHORT).show();
            }else{
                InputMethodManager imm=(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getRootView().getWindowToken(),0);
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://api.openweathermap.org/data/2.5/weather?q=" + searchLocation +"&units=metric&appid=f2a06734054504717fe37ed18078b9bc")
                        .get()
                        .build();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                StrictMode.setThreadPolicy(policy);
                try {
                    Response response = client.newCall(request).execute();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String responseData = response.body().string();
                            try {
                                JSONObject json=new JSONObject(responseData);
                                JSONArray array=json.getJSONArray("weather");
                                JSONObject object=array.getJSONObject(0);

                                final String description=object.getString("description");


                                JSONObject temp1= json.getJSONObject("main");
                                final Double Temperature=temp1.getDouble("temp");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        city.setText(searchLocation);
                                        String temps=Math.round(Temperature)+" °C";
                                        temp.setText("Temperature : " +temps);
                                        desc.setText("Description : " +description);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}
    });

    }
}
