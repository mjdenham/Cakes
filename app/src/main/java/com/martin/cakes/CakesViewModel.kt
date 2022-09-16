package com.martin.cakes

import androidx.lifecycle.ViewModel
import com.martin.cakes.model.CakeDto

class CakesViewModel: ViewModel() {

    fun getCake(): CakeDto {
        return CakeDto("Banana cake", "Donkey kongs favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg")
    }
    fun cakeSelected() {

    }
}