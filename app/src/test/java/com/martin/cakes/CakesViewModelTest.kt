package com.martin.cakes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.martin.cakes.model.CakeDto
import com.martin.cakes.model.ICakesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class CakesViewModelTest {

    @get:Rule
    val instantCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantLiveDataRule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CakesViewModel

    @Before
    fun setUp() {
        viewModel = CakesViewModel(testClient, Dispatchers.Main)
    }

    @Test
    fun cake_duplicates_should_be_removed() = runTest {
        val liveData = viewModel.getCakes()
        val value = liveData.value
        assertTrue("Expected Success", value is CakesResponse.Success)
        val success = liveData.value as CakesResponse.Success
        val cakes = success.data
        assertEquals("Duplicate values not removed", 3, cakes.size)
    }

    @Test
    fun cakes_should_be_sorted_by_name() = runTest {
        val liveData = viewModel.getCakes()
        val value = liveData.value
        assertTrue("Expected Success", value is CakesResponse.Success)
        val success = liveData.value as CakesResponse.Success
        val cakes = success.data
        assertEquals("Banana cake should be sorted first", cakes[0], BANANA_CAKE)
    }

    private val testClient = object : ICakesClient {
        override fun getCakes(): Response<List<CakeDto>> {
            return Response.success(
                listOf(
                    OTHER_CAKE,
                    BANANA_CAKE,
                    BANANA_CAKE,
                    YET_ANOTHER_CAKE
                )
            )
        }
    }

    companion object {
        private val BANANA_CAKE = CakeDto("Banana cake", "Donkey kongs favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg")
        private val OTHER_CAKE = CakeDto("Other cake", "Marios favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg")
        private val YET_ANOTHER_CAKE = CakeDto("Yet another cake", "Marios favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg")
    }
}