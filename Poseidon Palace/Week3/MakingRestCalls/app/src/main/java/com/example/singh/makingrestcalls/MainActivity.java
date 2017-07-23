package com.example.singh.makingrestcalls;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.singh.makingrestcalls.model.googleplaces.ClosePlacesPojo;
import com.example.singh.makingrestcalls.model.weather.Weatherdata;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://api.github.com/users/manroopAndroid";
    private static final String TAG = "okHttpResponse";

    TextView tvOkHttp;
    private static final String Base_url_gitrepos = "http://api.github.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOkHttp = (TextView) findViewById(R.id.tvOkhttpResponse);
    }


    public void makingRestCalls(View view) {

        switch (view.getId()) {

            case R.id.btnHttp:

                Intent intent = new Intent(this, HttpIntentService.class);
                startService(intent);
                break;

            case R.id.btnOkHttp:

                final OkHttpClient client = new OkHttpClient();

                final Request request = new Request.Builder()
                        .url(BASE_URL)
                        .build();

                //making synchronous call using okhttp

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            String result = client.newCall(request).execute().body().string();
                            Gson gson = new Gson();
                            GithubProfile githubProfile = gson.fromJson(result, GithubProfile.class);
                            Log.d(TAG, "run: " + githubProfile.getName());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                        Log.d(TAG, "onFailure: " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {


                        Gson gson = new Gson();
                        GithubProfile githubProfile = gson.fromJson(response.body().string(), GithubProfile.class);
                        Log.d(TAG, "onResponse: " + githubProfile.getName());
//                        Handler handler = new Handler(Looper.getMainLooper());
//
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvOkHttp.setText(responseResult);
//                            }
//                        });

                    }


                });

            case R.id.btnRetrofit:

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Base_url_gitrepos)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();


                GithubService githubService = retrofit.create(GithubService.class);
                retrofit2.Call<List<GithubRepo>> callToGetRepos = githubService.callProfle("manroopAndroid");

                callToGetRepos.enqueue(new retrofit2.Callback<List<GithubRepo>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<GithubRepo>> call, retrofit2.Response<List<GithubRepo>> response) {


                        for (int i = 0; i < 5; i++) {
                            Log.d(TAG, "onResponse: " + response.body().get(i).getName());

                        }

                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<GithubRepo>> call, Throwable t) {

                    }
                });

                break;

            //making rest calls using Retrofit helper class
            case R.id.btnRetrofit2:


                retrofit2.Call<Weatherdata> weatherdataCall = RetrofitHelper.callWeatherData();
                weatherdataCall.enqueue(new retrofit2.Callback<Weatherdata>() {
                    @Override
                    public void onResponse(retrofit2.Call<Weatherdata> call, retrofit2.Response<Weatherdata> response) {
                        Log.d(TAG, "onResponse: " + response.body().getCity().getName());
                        Toast.makeText(MainActivity.this, response.body().getCity().getName(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onFailure(retrofit2.Call<Weatherdata> call, Throwable t) {

                        Log.d(TAG, "onFailure: " + t.toString());
                    }
                });

                break;

            case R.id.btnRxJava:

                Observable<Weatherdata> weatherdataObservable = RetrofitHelper.getWeatherDataObs();
                weatherdataObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<Weatherdata>() {
                            @Override
                            public void onCompleted() {
                                Log.d(TAG, "onCompleted: ");
                            }

                            @Override
                            public void onError(Throwable e) {

                                Log.d(TAG, "onError: "+ e.toString());
                            }

                            @Override
                            public void onNext(Weatherdata weatherdata) {
                                Log.d(TAG, "onNext: " + weatherdata.getCity().getName());
                            }
                        });


                break;

            case R.id.btnGooglePlaces:


                retrofit2.Call<ClosePlacesPojo> call = RetrofitHelper.callGooglePlaces();
                call.enqueue(new retrofit2.Callback<ClosePlacesPojo>() {
                    @Override
                    public void onResponse(retrofit2.Call<ClosePlacesPojo> call, retrofit2.Response<ClosePlacesPojo> response) {
                        Log.d(TAG, "onResponse: " + response.body().getResults().get(0).getName());
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ClosePlacesPojo> call, Throwable t) {

                    }
                });


                break;


        }


    }
}













