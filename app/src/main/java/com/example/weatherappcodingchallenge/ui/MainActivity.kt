package com.example.weatherappcodingchallenge.ui

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.weatherappcodingchallenge.R
import com.example.weatherappcodingchallenge.databinding.ActivityMainBinding
import com.example.weatherappcodingchallenge.model.WeatherResponse
import com.example.weatherappcodingchallenge.network.ApiCallState
import com.example.weatherappcodingchallenge.network.WeatherDataRepository
import com.example.weatherappcodingchallenge.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = WeatherViewModel(WeatherDataRepository())

        observeWeatherData()

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        sharedPreferences = getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)

        // Check if there's a last searched city and load it if available
        val lastCity = loadLastSearchedCity()
        if (lastCity != null) {
            getWeatherData(lastCity)
        } else {
            // If no city was found, request location
            requestLocation()
        }

        binding.searchButton.setOnClickListener {
            val city = binding.searchEditText.text.toString()
            if (city.isNotEmpty()){
                getWeatherData(city)
                saveLastSearchedCity(city)
            }
        }
    }

    // Save the last searched city to SharedPreferences
    private fun saveLastSearchedCity(city: String) {
        sharedPreferences.edit().putString("last_city", city).apply()
    }

    // Load the last searched city from SharedPreferences
    private fun loadLastSearchedCity(): String? {
        return sharedPreferences.getString("last_city", null)
    }

    private fun getWeatherData(location: String){
        viewModel.getWeatherFromApi(location)
        observeWeatherData()
    }

    private fun getWeatherDataWithCoordinates(latitude: String, longitude : String){
        viewModel.getWeatherFromApiWithCoordinates(latitude,longitude)
        observeWeatherData()
    }

    private fun observeWeatherData() {
        viewModel.weatherData.observe(this) { apiCallSate ->
            when (apiCallSate) {

                is ApiCallState.Success -> {
                    apiCallSate.data?.let { weatherData ->
                        displayData(weatherData)
                    }
                }

                is ApiCallState.Error -> {
                    binding.progressBar.visibility = View.GONE
                }

                is ApiCallState.Loading -> {
                    binding.apply {
                        progressBar.visibility = View.VISIBLE
                        temp.visibility  = View.GONE
                        mainWeather.visibility = View.GONE
                        weatherDesc.visibility = View.GONE
                        weatherIcon.visibility = View.GONE

                    }

                }

                else -> {}
            }
        }
    }

    private fun displayData(weatherData: WeatherResponse) {
        val city = loadLastSearchedCity()
        if (city != null) {
            binding.temp.text = " It's currently ${weatherData.main.temp}°C in $city "
        }

        binding.mainWeather.text = weatherData.weather[0].main
        binding.weatherDesc.text = weatherData.weather[0].description
        Glide.with(this)
            .load("https://openweathermap.org/img/wn/${weatherData.weather[0].icon}@2x.png")
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_launcher_foreground)
            .into(binding.weatherIcon)

      setVisibilityOn()
    }

    private fun setVisibilityOn(){
        binding.apply {
            temp.visibility = View.VISIBLE
            mainWeather.visibility = View.VISIBLE
            weatherDesc.visibility = View.VISIBLE
            weatherIcon.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }


    private fun requestLocation() {
        // Check if the location permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is granted, get the location
            getLastLocation()
        } else {
            // Request the permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Handle permission result using the ActivityResultLauncher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLastLocation() // Permission granted, get location
                showPermissionGrantedMessage()
            } else {
                // Handle the case when permission is denied
                showPermissionDeniedMessage()
            }
        }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Use the location data (latitude, longitude)
                    val latitude = location.latitude.toString()
                    val longitude = location.longitude.toString()
                    println("Location: $latitude, $longitude")
                    getWeatherDataWithCoordinates(latitude,longitude)
                } else {
                    Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
    }

    private fun showPermissionGrantedMessage() {
        Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
    }

}