package com.martin.cakes.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

//TODO would normally map the dto to a more ui oriented Cake class before use
data class CakeDto @JsonCreator constructor(
    @JsonProperty("title")
    val title: String,

    @JsonProperty("desc")
    val desc: String,

    @JsonProperty("image")
    val image: String
)