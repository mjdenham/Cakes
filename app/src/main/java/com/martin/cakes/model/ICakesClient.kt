package com.martin.cakes.model

import retrofit2.Response

interface ICakesClient {
    fun getCakes(): Response<List<CakeDto>>
}
