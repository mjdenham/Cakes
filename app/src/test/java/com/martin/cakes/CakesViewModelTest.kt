package com.martin.cakes

import com.martin.cakes.model.CakeDto
import com.martin.cakes.model.ICakesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CakesViewModelTest {

    @get:Rule
    val instantCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CakesViewModel

    @Before
    fun setUp() {
        viewModel = CakesViewModel(testClient, Dispatchers.Main)
    }

    @Test
    fun cake_duplicates_should_be_removed() = runTest {
        val value = viewModel.cakes.drop(1).first()
        assertTrue("Expected Success", value is CakesResponse.Success)
        val success = value as CakesResponse.Success
        val cakes = success.data
        assertEquals("Duplicate values not removed", 3, cakes.size)
    }

    @Test
    fun cakes_should_be_sorted_by_name() = runTest {
        val value = viewModel.cakes.drop(1).first()
        assertTrue("Expected Success", value is CakesResponse.Success)
        val success = value as CakesResponse.Success
        val cakes = success.data
        assertEquals("Banana cake should be sorted first", cakes[0], BANANA_CAKE)
    }

    @Test
    fun cakes_error() = runTest {
        val errorViewModel = CakesViewModel(errorTestClient, Dispatchers.Main)
        val value = errorViewModel.cakes.drop(1).first()
        assertTrue("Expected Error", value is CakesResponse.Error)
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

    private val errorTestClient = object : ICakesClient {
        override fun getCakes(): Response<List<CakeDto>> {
            throw IOException("Simulated error")
        }
    }

    companion object {
        private val BANANA_CAKE = CakeDto("Banana cake", "Donkey kongs favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg")
        private val OTHER_CAKE = CakeDto("Other cake", "Marios favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg")
        private val YET_ANOTHER_CAKE = CakeDto("Yet another cake", "Marios favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg")
    }
}