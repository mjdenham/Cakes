package com.martin.cakes.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.martin.cakes.R
import com.martin.cakes.model.CakeDto
import com.martin.cakes.ui.screen.cakes.CakesResponse
import com.martin.cakes.ui.screen.cakes.CakesViewModel
import com.martin.cakes.ui.theme.CakesTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CakesScreen(showCakeDetail: (CakeDto) -> Unit, viewModel: CakesViewModel = viewModel()) {
    val cakesResponse: CakesResponse = viewModel.cakes.collectAsStateWithLifecycle(initialValue = CakesResponse.Loading).value
    cakesResponse.let { response ->
        val isRefreshing = response is CakesResponse.Loading
        val pullRefreshState = rememberPullRefreshState(isRefreshing, { viewModel.refresh() })

        Box(Modifier.pullRefresh(pullRefreshState)) {
            if (response is CakesResponse.Success) {
                Cakes(response.data) { cake ->
                    showCakeDetail(cake)
                }

            } else if (response is CakesResponse.Error) {
                ShowErrorMessage { viewModel.refresh() }
            }
            PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun Cakes(cakes: List<CakeDto>, onClick: (CakeDto) -> Unit) {
    LazyColumn {
        items(cakes) { cake ->
            CakeItem(cake) { onClick(cake) }
            Divider()
        }
    }
}

@Composable
private fun CakeItem(cake: CakeDto, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
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
private fun ShowErrorMessage(retry: () -> Unit) {
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