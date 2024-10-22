package com.example.weatherappcodingchallenge.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherappcodingchallenge.model.WeatherResponse
import com.example.weatherappcodingchallenge.network.ApiCallState
import com.example.weatherappcodingchallenge.network.WeatherDataRepository
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherDataRepository) : ViewModel() {

    private val _weatherDataResponse = MutableLiveData<ApiCallState<WeatherResponse>>()
    val weatherData : LiveData<ApiCallState<WeatherResponse>>
        get() = _weatherDataResponse


    fun getWeatherFromApi(location: String){
        viewModelScope.launch {
            _weatherDataResponse.value = ApiCallState.Loading()
            val response = repository.getWeatherData(location)
            if (response.isSuccessful) {
                response.body().let { weatherData ->
                    _weatherDataResponse.value = ApiCallState.Success(weatherData!!)
                }
            } else {
                _weatherDataResponse.value = ApiCallState.Error(response.message())

            }
        }
    }

    fun getWeatherFromApiWithCoordinates(latitude: String, longitude : String){
        viewModelScope.launch {
            _weatherDataResponse.value = ApiCallState.Loading()
            val response = repository.getWeatherDataWithCoordinates(latitude, longitude)
            if (response.isSuccessful) {
                response.body().let { weatherData ->
                    _weatherDataResponse.value = ApiCallState.Success(weatherData!!)
                }
            } else {
                _weatherDataResponse.value = ApiCallState.Error(response.message())

            }
        }
    }

}