package com.hobby.soiya.barometricpressurechange;

import com.hobby.soiya.barometricpressurechange.data.WeatherContainer;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OpenWeatherMapApiInterface {

    @GET("/data/2.5/{path}")
    public Observable<WeatherContainer> get(@Path("path") String path, @Query("zip") String zipcode, @Query("appid") String key);
}
