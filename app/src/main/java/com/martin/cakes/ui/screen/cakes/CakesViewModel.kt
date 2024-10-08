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

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
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
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            updateCakes(CakesResponse.Loading)

            val response = cakesClient.getCakes()
            if (response.isSuccessful) {
                val cakeList: List<CakeDto> = response.body() ?: emptyList()
                val organisedCakeList = cakeList.distinct()
                    .sortedBy { it.title }

                updateCakes(CakesResponse.Success(organisedCakeList))
            } else {
                updateCakes(CakesResponse.Error)
            }
        }
    }

    private suspend fun updateCakes(response: CakesResponse) {
        _cakes.emit(response)
    }

    companion object {
        private const val TAG = "CakesViewModel"
    }
}