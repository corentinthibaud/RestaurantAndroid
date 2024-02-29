package fr.isen.thibaud.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.isen.thibaud.androiderestaurant.MenuActivity.Companion.CATEGORY_KEY
import fr.isen.thibaud.androiderestaurant.model.BasketItemModel
import fr.isen.thibaud.androiderestaurant.model.BasketModel.Companion.current
import fr.isen.thibaud.androiderestaurant.network.ImageAPI
import fr.isen.thibaud.androiderestaurant.model.MenuItem
import fr.isen.thibaud.androiderestaurant.network.ImageAPI.Companion.IMG_SINGLE_HEIGHT
import fr.isen.thibaud.androiderestaurant.ui.theme.AndroidERestaurantTheme

@OptIn(ExperimentalMaterial3Api::class)
class DetailActivity : ComponentActivity() {
    companion object {
        const val DISH_KEY = "dish_key"
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dish = intent.getSerializableExtra(DISH_KEY) as? MenuItem
        setContent {
            val quantity = remember { mutableIntStateOf(1) }
            val basketItems = remember { mutableStateListOf<BasketItemModel>() }
            val context = LocalContext.current
            AndroidERestaurantTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                titleContentColor = MaterialTheme.colorScheme.onBackground,
                            ),
                            title = {
                                dish?.nameFr?.let {
                                    Text(
                                        it,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    val intent = Intent(this@DetailActivity, MenuActivity::class.java)
                                    intent.putExtra(CATEGORY_KEY, dish?.categoryId)
                                    startActivity(intent)
                                }) {
                                    Icon(painterResource(R.drawable.baseline_arrow_back_24), stringResource(R.string.app_menu_back))
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    val intent = Intent(this@DetailActivity, BasketActivity::class.java)
                                    startActivity(intent)
                                }) {
                                    BadgedBox(badge = {
                                        if(current(context).countQuantities() > 0) {
                                            Badge {
                                                Text(current(context).countQuantities().toString())
                                            }
                                        }
                                    }) {
                                        Icon(painterResource(R.drawable.baseline_shopping_cart_24), stringResource(R.string.app_menu_cart))
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomAppBar {
                            Button(onClick = {
                                if (dish != null) {
                                    current(context).add(dish, quantity.intValue, context)
                                    basketItems.clear()
                                    basketItems.addAll(current(context).items)
                                }
                            }, Modifier.fillMaxWidth()) {
                                Text(text = "Ajouter au panier")
                            }
                        }
                    }
                ) {paddingValues ->
                    dish?.let {
                        val pagerState = rememberPagerState{it.images.size}
                        val imgAPI = ImageAPI()
                        val ingredientsState = remember { mutableStateOf(false) }
                        HorizontalPager(pagerState, Modifier
                            .padding(top = paddingValues.calculateTopPadding())
                            .height(IMG_SINGLE_HEIGHT)
                        ) { page ->
                            imgAPI.LoadSingleImage(it, page)
                        }
                        Row(Modifier
                            .clickable {
                                ingredientsState.value = !ingredientsState.value
                            }
                            .fillMaxWidth()
                            .padding(top = paddingValues.calculateTopPadding() + IMG_SINGLE_HEIGHT)
                            .height(64.dp)
                        ) {
                            Text(
                                "Liste des ingrédients",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                            if (ingredientsState.value) {
                                Icon(
                                    painterResource(R.drawable.baseline_keyboard_arrow_up_24),
                                    stringResource(R.string.app_hide_ingredients),
                                    Modifier
                                        .fillMaxHeight()
                                        .padding(8.dp)
                                )
                            } else {
                                Icon(
                                    painterResource(R.drawable.baseline_keyboard_arrow_down_24),
                                    stringResource(R.string.app_show_ingredients),
                                    Modifier
                                        .fillMaxHeight()
                                        .padding(8.dp)
                                )
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding() + IMG_SINGLE_HEIGHT + 32.dp)
                        ) {
                            if(ingredientsState.value) {
                                items(dish.ingredients) {
                                    ListItem(
                                        headlineContent = {
                                            Text(it.nameFr.replaceFirstChar { char -> char.uppercase() })
                                        }
                                    )
                                }
                            }
                            item(it.nameFr) {
                                Row (
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 32.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(
                                        onClick = { if (quantity.intValue > 1) quantity.intValue-- },
                                        modifier = Modifier.padding(8.dp),
                                        enabled = quantity.intValue > 1,

                                        ) {
                                        Icon(
                                            painterResource(R.drawable.baseline_remove_24),
                                            stringResource(R.string.app_remove_quantity)
                                        )
                                    }
                                    TextField(
                                        value = quantity.intValue.toString(),
                                        modifier = Modifier.width(64.dp),
                                        onValueChange = {},
                                        readOnly = true
                                    )
                                    IconButton(
                                        onClick = { quantity.intValue++; },
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.baseline_add_24),
                                            stringResource(R.string.app_add_quantity)
                                        )
                                    }
                                }
                            }
                        }
                        Text("Total : ${it.prices[0].price.toFloat() * quantity.intValue} €",
                            Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}