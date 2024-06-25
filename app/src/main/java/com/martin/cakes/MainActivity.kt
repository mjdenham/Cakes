package com.martin.cakes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
            val cakes = viewModel.getCakes()
            val currentSelectedItem = remember { mutableStateOf(cakes[0]) }
            val showDialog = remember { mutableStateOf(false) }

            CakesTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Cakes(cakes) { cake ->
                        currentSelectedItem.value = cake
                        showDialog.value = true
                    }

                    if (showDialog.value) {
                        ShowCakeDetail(currentSelectedItem.value) { showDialog.value = false }
                    }
                }
            }
        }
    }
}

@Composable
fun Cakes(cakes: List<CakeDto>, onClick: (CakeDto) -> Unit) {
    LazyColumn {
        items(cakes) { cake ->
            Cake(cake) { onClick(cake) }
        }
    }
}

@Composable
fun Cake(cake: CakeDto, onClick: () -> Unit) {
    Row(modifier = Modifier.clickable { onClick() }) {
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

        Column( modifier = Modifier.fillMaxWidth(1F)) {
            Text(text = "${cake.title}")
        }
    }
}

@Composable
fun ShowCakeDetail(cake: CakeDto, hideCakeDetail: () -> Unit) {
    Column {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
                hideCakeDetail()
            },
            title = {
                Text(text = cake.title)
            },
            text = {
                Text(cake.desc)
            },
            confirmButton = {
                Button(
                    onClick = {
                        hideCakeDetail()
                    }) {
                    Text(stringResource(id = R.string.okay))
                }
            }
        )
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
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    CakesTheme {
        ShowCakeDetail(
            CakeDto("Banana cake", "Donkey kongs favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg"),
        ) {}
    }
}