package com.martin.cakes.ui.screen.cakes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martin.cakes.model.CakeDto
import com.martin.cakes.model.CakesClient
import com.martin.cakes.model.ICakesClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CakesViewModel(private val cakesClient: ICakesClient = CakesClient(), private val dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    private val _cakes = MutableStateFlow<CakesResponse>(CakesResponse.Loading)
    val cakes: Flow<CakesResponse> = _cakes

    private val _refreshing = MutableStateFlow<Boolean>(false)
    val refreshing: Flow<Boolean> = _refreshing

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        Log.e(TAG, "Error loading cakes", throwable)
        viewModelScope.launch {
            updateCakes(CakesResponse.Error)
        }
    }

    init {
        loadCakes()
    }

    fun refresh() {
        loadCakes()
    }

    private fun loadCakes() {
        Log.d(TAG, "Loading cakes")
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            updateCakes(CakesResponse.Loading)

            val response = cakesClient.getCakes()
            if (response.isSuccessful) {
                val cakeList: List<CakeDto> = response.body() ?: emptyList()
                val organisedCakeList = cakeList.distinct()
                    .sortedBy { it.title }

                updateCakes(CakesResponse.Success(organisedCakeList))
            } else {
                Log.e(TAG, "Error querying repo ${response.message()}")
                updateCakes(CakesResponse.Error)
            }
        }
    }

    private suspend fun updateCakes(response: CakesResponse) {
        _cakes.emit(response)
        _refreshing.emit(response == CakesResponse.Loading)
    }

    companion object {
        private const val TAG = "CakesViewModel"
    }
}