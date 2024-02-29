package fr.isen.thibaud.androiderestaurant.network

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.isen.thibaud.androiderestaurant.R
import fr.isen.thibaud.androiderestaurant.model.MenuItem

class ImageAPI {
    companion object {
        val IMG_HEIGHT = 80.dp
        val IMG_SINGLE_HEIGHT = 200.dp
    }

    @Composable
    fun LoadImage(item: MenuItem) {
        item.images.forEach {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .build(),
                null,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(80.dp)
                    .height(IMG_HEIGHT)
                    .clip(RoundedCornerShape(5))
            )
        }
    }

    @Composable
    fun LoadSingleImage(item: MenuItem, page: Int) {
        var it = item.images[page]
        if(it.isEmpty()) {
            it = if (page + 1 < item.images.size) {
                item.images[page + 1]
            } else {
                item.images[0]
            }
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(it)
                .build(),
            null,
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(IMG_SINGLE_HEIGHT)
        )
    }
}