package com.martin.cakes.ui.theme

import com.martin.cakes.model.CakeDto

sealed interface CakesResponse {
    data class Success(val data: List<CakeDto>) : CakesResponse
    object Loading : CakesResponse
    object Error : CakesResponse
}