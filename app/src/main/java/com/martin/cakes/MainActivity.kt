package com.martin.cakes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.martin.cakes.model.CakeDto
import com.martin.cakes.ui.theme.CakesTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CakesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CakesTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Cakes(viewModel.getCakes())
                }
            }
        }
    }
}

@Composable
fun Cakes(cakes: List<CakeDto>) {
    LazyColumn {
        items(cakes) { cake ->
            Cake(cake)
        }
    }
}

@Composable
fun Cake(cake: CakeDto) {
    Row {
        Column {
            //TODO handle image loading errors
            AsyncImage(
                model = cake.image,
                contentDescription = "Cake image",
                modifier = Modifier
                    .size(45.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(text = "${cake.title}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CakesTheme {
        Cakes(
            listOf(
                CakeDto("Banana cake", "Donkey kongs favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg"),
                CakeDto("Other cake", "Marios favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg"),
                CakeDto("Yet another cake", "Marios favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg"),
            )
        )
    }
}