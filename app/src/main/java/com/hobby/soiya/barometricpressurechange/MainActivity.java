package com.hobby.soiya.barometricpressurechange;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.hobby.soiya.barometricpressurechange.data.WeatherContainer;
import com.hobby.soiya.barometricpressurechange.data.WeatherDayList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PascalsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);

        // RecyclerViewに表示するViewにアプリケーション固有のデータセットをバインドする
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        // 区切り線の追加
        // LinearLayoutManager.VERTICAL -> 縦スクロール
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        // 基本的なアニメーションの追加
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // 上に引っ張ったら更新するやつ
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 引っ張って離した時に呼ばれます。
                showPascal();
            }
        });

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

                    // リストへ追加
                    @Override
                    public void onNext(@NonNull WeatherContainer weatherContainer) {
                        List<Pascal> pascalList = new ArrayList<>();
                        mAdapter = new PascalsAdapter(pascalList);
                        java.util.List<WeatherDayList> weatherDayLists = weatherContainer.getWeatherDayList();

                        for(WeatherDayList wd : weatherDayLists){
                            long unixtime;
                            Date day;

                            unixtime = wd.getDt().longValue()*1000L;
                            day = new Date(unixtime);
                            Pascal pascal = new Pascal(wd.getTemperatureEtc().getGrndLevel(), day);
                            pascalList.add(pascal);
                        }
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        Log.d("recyclerView", String.valueOf(mAdapter.getItemCount()));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("通信 -> ", "失敗" + e.toString());
                    }

                    @Override
                    public void onComplete() {
                        // 更新のぐるぐるを止める
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

    }
}
