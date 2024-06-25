package com.martin.cakes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.martin.cakes.model.CakeDto
import com.martin.cakes.ui.screen.CakesScreen
import com.martin.cakes.ui.screen.cake.ShowCakeDetail
import com.martin.cakes.ui.theme.CakesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CakesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val currentSelectedItem: MutableState<CakeDto?> = remember { mutableStateOf(null) }

                    CakesScreen(showCakeDetail = { cake -> currentSelectedItem.value = cake})

                    currentSelectedItem.value?.let { selectedCake ->
                        ShowCakeDetail(selectedCake) { currentSelectedItem.value = null }
                    }
                }
            }
        }
    }
}