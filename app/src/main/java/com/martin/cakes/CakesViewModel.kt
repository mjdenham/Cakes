package com.martin.cakes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martin.cakes.model.CakeDto
import com.martin.cakes.model.CakesClient
import com.martin.cakes.model.ICakesClient
import com.martin.cakes.ui.theme.CakesResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CakesViewModel(private val cakesClient: ICakesClient = CakesClient(), private val dispatcher: CoroutineDispatcher = Dispatchers.IO): ViewModel() {

    //TODO migrate to MutableStateFlow eventually
    private val cakes = MutableLiveData<CakesResponse>()
    private val refreshing = MutableLiveData<Boolean>(false)

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        Log.e(TAG, "Error loading cakes", throwable)
        cakes.postValue(CakesResponse.Error)
    }

    init {
        loadCakes()
    }

    fun getCakes(): LiveData<CakesResponse> {
        return cakes
    }

    fun isRefreshing(): LiveData<Boolean> = refreshing

    fun refresh() {
        loadCakes()
    }

    private fun loadCakes() {
        Log.d(TAG, "Loading cakes")
        viewModelScope.launch(dispatcher + coroutineExceptionHandler) {
            cakes.postValue(CakesResponse.Loading)
            refreshing.postValue(true)
            val response = cakesClient.getCakes()
            if (response.isSuccessful) {
                val cakeList: List<CakeDto> = response.body() ?: emptyList()
                val organisedCakeList = cakeList.distinct()
                    .sortedBy { it.title }

                cakes.postValue(CakesResponse.Success(organisedCakeList))
            } else {
                Log.e(TAG, "Error querying repo ${response.message()}")
                cakes.postValue(CakesResponse.Error)
            }
            refreshing.postValue(false)
        }
    }

    companion object {
        private const val TAG = "CakesViewModel"
    }
}