package com.martin.cakes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martin.cakes.model.CakeDto
import com.martin.cakes.model.CakesClient
import com.martin.cakes.model.ICakesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CakesViewModel(private val cakesClient: ICakesClient = CakesClient()): ViewModel() {

    //TODO migrate to MutableStateFlow eventually
    private val cakes: MutableLiveData<List<CakeDto>> by lazy {
        MutableLiveData<List<CakeDto>>().also {
            loadCakes()
        }
    }

    fun getCakes(): LiveData<List<CakeDto>> {
        return cakes
    }

    private fun loadCakes() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = cakesClient.getCakes()
            if (response.isSuccessful) {
                cakes.postValue(response.body())
            } else {
                Log.e(TAG, "Error querying repo ${response.message()}")
            }
        }
    }

    companion object {
        private const val TAG = "CakesViewModel"
    }
}