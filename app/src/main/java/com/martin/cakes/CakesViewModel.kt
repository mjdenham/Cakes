package com.martin.cakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martin.cakes.model.CakeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CakesViewModel: ViewModel() {

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
            cakes.postValue(getCakeData())
//            val response = service.getCakes("mralexgray")
//            if (response.isSuccessful) {
//                cakes.postValue(response.body())
//            } else {
//                Log.e(TAG, "Error querying repo ${response.message()}")
//            }
        }
    }

    private fun getCakeData(): List<CakeDto> {
        return listOf(
            CakeDto("Banana cake", "Donkey kongs favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg"),
            CakeDto("Other cake", "Marios favourite", "https://s3-eu-west-1.amazonaws.com/doesntexist.jpg"),
            CakeDto("Yet another cake", "Marios favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg"),
        )
    }

    companion object {
        private const val TAG = "CakesViewModel"
    }
}