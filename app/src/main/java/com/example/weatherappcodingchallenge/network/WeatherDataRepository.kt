package com.example.weatherappcodingchallenge.network

import com.example.weatherappcodingchallenge.model.WeatherResponse
import com.example.weatherappcodingchallenge.network.Constants.API_KEY
import retrofit2.Response

class WeatherDataRepository {

    private val api = RetrofitInstance.api

    suspend fun getWeatherData(location: String): Response<WeatherResponse>{
        return api.getWeatherData(location, API_KEY)
    }

suspend fun getWeatherDataWithCoordinates(latitude: String, longitude : String): Response<WeatherResponse>{
        return api.getWeatherDataWithCoordinates(latitude, longitude, API_KEY)
    }
}