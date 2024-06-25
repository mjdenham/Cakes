package com.martin.cakes.model

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class CakesClient :  ICakesClient {
    override fun getCakes() = service.getCakes().execute()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(JacksonConverterFactory.create())
        .baseUrl("https://gist.githubusercontent.com/")
        .build()

    private val service: CakesService = retrofit.create(CakesService::class.java)
}