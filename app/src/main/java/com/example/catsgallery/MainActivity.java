package com.example.catsgallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<CatImage> catImages;
    private static final String URL = "https://api.imgur.com/3/gallery/search/?q=cats";
    private static final String CLIENT_ID = "1ceddedc03a5d71";

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadRecycleViewData();
    }

    private void loadRecycleViewData() {
        catImages = new ArrayList<>();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando imagens...");
        progressDialog.show();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + CLIENT_ID)
                .url(URL)
                .method("GET", null)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try {

                        JSONObject data = new JSONObject(response.body().string());
                        JSONArray items = data.getJSONArray("data");

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            if (item.getBoolean("is_album")) {
                                catImages.add(new CatImage(item.getString("cover")));
                            } else {
                                catImages.add(new CatImage(item.getString("id")));
                            }
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                render(catImages);
                            }
                        });
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        Log.e("fetchData", e.getMessage());
                    }
                }
            }
        });

    }

    private void render(final List<CatImage> photos) {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new MyAdapter(photos);
        recyclerView.setAdapter(adapter);
    }
}