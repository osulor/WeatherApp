package com.example.weatherappcodingchallenge.network

import com.example.weatherappcodingchallenge.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("data/2.5/weather")
    suspend fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
    ): Response<WeatherResponse>

    @GET("data/2.5/weather")
    suspend fun getWeatherDataWithCoordinates(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") apiKey: String,
    ): Response<WeatherResponse>

}