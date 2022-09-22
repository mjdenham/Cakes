package com.martin.cakes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.martin.cakes.model.CakeDto
import com.martin.cakes.ui.theme.CakesTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CakesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val cakesResponse: CakesResponse = viewModel.cakes.collectAsState(initial = CakesResponse.Loading).value
            val currentSelectedItem: MutableState<CakeDto?> = remember { mutableStateOf(null) }

            CakesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    cakesResponse.let { response ->
                        val isRefreshing: Boolean = viewModel.refreshing.collectAsState(initial = false).value

                        SwipeRefresh(
                            state = rememberSwipeRefreshState(isRefreshing),
                            onRefresh = { viewModel.refresh() },
                        ) {
                            if (response is CakesResponse.Success) {
                                    Cakes(response.data) { cake ->
                                    currentSelectedItem.value = cake
                                }

                                currentSelectedItem.value?.let { selectedCake ->
                                    ShowCakeDetail(selectedCake) { currentSelectedItem.value = null }
                                }
                            } else if (response is CakesResponse.Error) {
                                ShowErrorMessage {viewModel.refresh()}
                            }
                        }
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
            Divider()
        }
    }
}

@Composable
fun Cake(cake: CakeDto, onClick: () -> Unit) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxSize(1F),
        ) {
        //TODO handle image loading errors
        AsyncImage(
            model = cake.image,
            contentDescription = "Cake image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(10.dp)
                .size(45.dp)
                .clip(CircleShape)
        )

        Text(
            text = "${cake.title}",
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun ShowCakeDetail(cake: CakeDto, hideCakeDetail: () -> Unit) {
    Column {
        AlertDialog(
            onDismissRequest = {
                hideCakeDetail()
            },
            title = {
                Text(
                    text = cake.title,
                    color = MaterialTheme.typography.h5.color,
                    fontSize = MaterialTheme.typography.h5.fontSize
                )
            },
            text = {
                Text(
                    text = cake.desc,
                    color = MaterialTheme.typography.body1.color,
                    fontSize = MaterialTheme.typography.body1.fontSize
                )
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

@Composable
fun ShowErrorMessage(retry: () -> Unit) {
    Column {
        AlertDialog(
            onDismissRequest = {
                retry()
            },
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = MaterialTheme.colors.primary
                )
            },
            text = {
                Text(text = stringResource(id = R.string.network_error))
            },
            confirmButton = {
                Button(
                    onClick = {
                        retry()
                    }) {
                    Text(stringResource(id = R.string.retry))
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