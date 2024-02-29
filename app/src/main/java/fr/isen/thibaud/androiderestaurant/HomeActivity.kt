package fr.isen.thibaud.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.isen.thibaud.androiderestaurant.ui.theme.AndroidERestaurantTheme

enum class Categories (val display: String, val API: String) {
    ENTREE("Entrée", "Entrées"),
    PLAT("Plat", "Plats"),
    DESSERT("Dessert", "Desserts")
}

@OptIn(ExperimentalMaterial3Api::class)
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                titleContentColor = MaterialTheme.colorScheme.onBackground,
                            ),
                            title = {
                                Text("Menu Restaurant Android", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        )
                    },
                    content = {
                        ScreenContent(it)
                    }
                )
            }
        }
    }

    @Composable
    fun ScreenContent(paddingValues: PaddingValues) {
        LazyColumn(modifier = Modifier.padding(top = paddingValues.calculateTopPadding()), content = {
            items(items = Categories.entries.toTypedArray(), key = { it.hashCode() }) {
                ItemRow(it)
            }
        })
    }

    @Composable
    private fun ItemRow(item: Categories) {
        val intent = Intent(this, MenuActivity::class.java)
        TextButton(
            onClick = {
                intent.putExtra(MenuActivity.CATEGORY_KEY, item)
                Log.d("Debug", "Starting activity")
                startActivity(intent) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp),
            content = {
                Text(
                    text = item.display,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )
    }
}

