package com.martin.cakes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martin.cakes.model.CakesClient
import com.martin.cakes.model.ICakesClient
import com.martin.cakes.ui.theme.CakesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CakesViewModel(private val cakesClient: ICakesClient = CakesClient()): ViewModel() {

    //TODO migrate to MutableStateFlow eventually
    private val cakes: MutableLiveData<CakesResponse> by lazy {
        MutableLiveData<CakesResponse>().also {
            loadCakes()
        }
    }

    fun getCakes(): LiveData<CakesResponse> {
        return cakes
    }

    private fun loadCakes() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = cakesClient.getCakes()
            if (response.isSuccessful) {
                cakes.postValue(CakesResponse.Success(response.body() ?: emptyList()))
            } else {
                Log.e(TAG, "Error querying repo ${response.message()}")
                cakes.postValue(CakesResponse.Error)
            }
        }
    }

    companion object {
        private const val TAG = "CakesViewModel"
    }
}