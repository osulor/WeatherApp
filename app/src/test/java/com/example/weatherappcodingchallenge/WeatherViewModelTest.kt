package com.example.weatherappcodingchallenge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weatherappcodingchallenge.model.Coord
import com.example.weatherappcodingchallenge.model.Main
import com.example.weatherappcodingchallenge.model.Weather
import com.example.weatherappcodingchallenge.model.WeatherResponse
import com.example.weatherappcodingchallenge.network.ApiCallState
import com.example.weatherappcodingchallenge.network.WeatherDataRepository
import com.example.weatherappcodingchallenge.viewmodel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.kotlin.*
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import retrofit2.Response

@ExperimentalCoroutinesApi
class WeatherViewModelTest {

    // Rule to handle LiveData synchronization for unit tests
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Mockito rule to initialize mocks
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    // Test coroutine dispatcher to control coroutine execution
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: WeatherDataRepository

    @Mock
    private lateinit var observer: Observer<ApiCallState<WeatherResponse>>

    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)  // Set the test dispatcher
        viewModel = WeatherViewModel(repository)
        viewModel.weatherData.observeForever(observer)  // Observe LiveData during tests
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()  // Reset the dispatcher after the test
    }

    @Test
    fun `getWeatherFromApi success`() = runTest {
        // Arrange: Mock the repository response
        val main = Main(286.69, 1024, 94, 1024, 1014,
            286.69, 287.43, 286.05)
        val coord = Coord(2.3488, 48.8534)
        val weatherList = listOf(Weather("overcast clouds", "04n", 804, "Clouds"))

        val mockWeatherResponse = WeatherResponse(coord, main, weatherList)
        val successResponse = Response.success(mockWeatherResponse)

        whenever(repository.getWeatherData("London")).thenReturn(successResponse)

        // Act: Call the function
        viewModel.getWeatherFromApi("London")
        testDispatcher.scheduler.advanceUntilIdle()  // Ensure coroutines complete

        // Assert: Verify that the correct states were emitted
        verify(observer).onChanged(ApiCallState.Loading())
        verify(observer).onChanged(ApiCallState.Success(mockWeatherResponse))
    }


    @Test
    fun `getWeatherFromApi error`() = runTest {
        val errorMessage = "Not Found"
        val errorResponse = Response.error<WeatherResponse>(404, okhttp3.ResponseBody.create(null, ""))

        whenever(repository.getWeatherData("UnknownCity")).thenReturn(errorResponse)

        viewModel.getWeatherFromApi("UnknownCity")
        testDispatcher.scheduler.advanceUntilIdle()  // Ensure coroutines complete

        // Assert: Verify that the correct states were emitted
        verify(observer).onChanged(ApiCallState.Loading())
        verify(observer).onChanged(ApiCallState.Error(errorMessage))
    }
}

