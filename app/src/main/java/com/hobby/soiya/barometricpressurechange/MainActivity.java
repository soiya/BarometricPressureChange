package com.hobby.soiya.barometricpressurechange;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hobby.soiya.barometricpressurechange.data.WeatherContainer;
import com.hobby.soiya.barometricpressurechange.data.WeatherDayList;

import java.util.ArrayList;
import java.util.Iterator;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hobby.soiya.barometricpressurechange.Constants.API_KEY;
import static com.hobby.soiya.barometricpressurechange.Constants.ZIP_CODE;

public class MainActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listView);

        showPascal();
    }

    private void showPascal(){
        // レスポンスデータの取得
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("API LOG", message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                // ログを出す
                .addInterceptor(logging)
                .build();

        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                // エンドポイントの設定
                .baseUrl("http://api.openweathermap.org")
                // jsonライブラリ指定
                .addConverterFactory(GsonConverterFactory.create())
                // okhttpクライアントを追加
                .client(client)
                .build();

        retrofit.create(OpenWeatherMapApiInterface.class)
                .get("forecast", ZIP_CODE, API_KEY)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeatherContainer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull WeatherContainer weatherContainer) {
                        java.util.List<WeatherDayList> weatherDayLists = weatherContainer.getWeatherDayList();
                        java.util.List<Double> pascalList = new ArrayList<>();

                        for(WeatherDayList wd : weatherDayLists){
                            pascalList.add(wd.getTemperatureEtc().getGrndLevel());
                        }

                        ArrayAdapter<Double> pascalArrayAdapter =
                                new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, pascalList);

                        listView.setAdapter(pascalArrayAdapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("通信 -> ", "失敗" + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("API", "complete");
                    }
                });

    }
}
