package com.martin.cakes.ui.screen.cake

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.martin.cakes.R
import com.martin.cakes.model.CakeDto
import com.martin.cakes.ui.theme.CakesTheme

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
                    color = MaterialTheme.typography.headlineSmall.color,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
            },
            text = {
                Text(
                    text = cake.desc,
                    color = MaterialTheme.typography.bodyLarge.color,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
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

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    CakesTheme {
        ShowCakeDetail(
            CakeDto("Banana cake", "Donkey kongs favourite", "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg"),
        ) {}
    }
}