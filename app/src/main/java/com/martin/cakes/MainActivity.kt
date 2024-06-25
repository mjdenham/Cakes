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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
            val cakesResponse: State<CakesResponse> = viewModel.getCakes().observeAsState(CakesResponse.Loading)
            val currentSelectedItem: MutableState<CakeDto?> = remember { mutableStateOf(null) }

            CakesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    cakesResponse.value.let { response ->
                        if (response is CakesResponse.Success) {

                            val isRefreshing: Boolean? by viewModel.isRefreshing().observeAsState()

                            SwipeRefresh(
                                state = rememberSwipeRefreshState(isRefreshing ?: false),
                                onRefresh = { viewModel.refresh() },
                            ) {
                                Cakes(response.data) { cake ->
                                    currentSelectedItem.value = cake
                                }
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

@Composable
fun Cakes(cakes: List<CakeDto>, onClick: (CakeDto) -> Unit) {
    LazyColumn {
        items(cakes) { cake ->
            Cake(cake) { onClick(cake) }
            Divider(color = MaterialTheme.colors.primary)
        }
    }
}

@Composable
fun Cake(cake: CakeDto, onClick: () -> Unit) {
    Row(modifier = Modifier.clickable { onClick() }) {
        Column(modifier = Modifier.padding(6.dp)) {
            //TODO handle image loading errors
            AsyncImage(
                model = cake.image,
                contentDescription = "Cake image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column( modifier = Modifier.fillMaxWidth(1F)) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "${cake.title}",
                color = MaterialTheme.colors.primary
            )
        }
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
                    color = MaterialTheme.colors.primary
                )
            },
            text = {
                Text(text = cake.desc)
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