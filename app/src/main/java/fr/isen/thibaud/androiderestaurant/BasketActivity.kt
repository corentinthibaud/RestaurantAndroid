package fr.isen.thibaud.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.isen.thibaud.androiderestaurant.MenuActivity.Companion.CATEGORY_KEY
import fr.isen.thibaud.androiderestaurant.model.BasketItemModel
import fr.isen.thibaud.androiderestaurant.model.BasketModel.Companion.current
import fr.isen.thibaud.androiderestaurant.network.ImageAPI
import fr.isen.thibaud.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.thibaud.androiderestaurant.utils.formattedPrice.Companion.formattedPrice

@OptIn(ExperimentalMaterial3Api::class)
class BasketActivity: ComponentActivity() {
    private var price = "0.0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidERestaurantTheme {
                BasketView(this)
            }
        }
    }

    @Composable
    fun BasketView(context: ComponentActivity) {
        val basketItems = remember { mutableStateListOf<BasketItemModel>() }
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    title = {
                        Text(
                            "Panier",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            val intent =
                                Intent(this@BasketActivity, MenuActivity::class.java)
                            intent.putExtra(CATEGORY_KEY, Categories.ENTREE)
                            startActivity(intent)
                        }) {
                            Icon(
                                painterResource(R.drawable.baseline_arrow_back_24),
                                stringResource(R.string.app_menu_back)
                            )
                        }
                    }
                )
            }, bottomBar = {
                BottomAppBar {
                    Button(onClick = {/*TODO*/}, Modifier.fillMaxWidth()) {
                        Text(text = "Commander pour " + price + "€")
                    }
                }
            }
        ) {
            LazyColumn(
                Modifier.padding(top = it.calculateTopPadding())
            ) {
                items(basketItems) {it ->
                    BasketItemView(it, context, basketItems)
                }
            }
        }
        val basket = current(context)
        basketItems.addAll(basket.items)
        price = basket.price()
    }

    @Composable
    fun BasketItemView(it: BasketItemModel, context: ComponentActivity, basketItems: MutableList<BasketItemModel>) {
        var priceMin = ""
        ListItem(
            modifier = Modifier.clickable(enabled = true, role = Role.Button) {
                val intent = Intent(this@BasketActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.DISH_KEY, it.dish)
                startActivity(intent)
            },
            headlineContent = {
                Text(
                    text = it.dish.nameFr,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                if (priceMin.isEmpty()) {
                    priceMin = it.dish.prices.first().price
                }
                priceMin = formattedPrice(priceMin)
                Text(
                    text = "$priceMin €",
                    style = MaterialTheme.typography.labelMedium
                )

            },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = it.count.toString(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.width(64.dp)
                    )
                    FilledTonalIconButton(onClick = {
                        current(context).delete(it, context)
                        basketItems.clear()
                        val basket = current(context)
                        basketItems.addAll(basket.items)
                        price = basket.price()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_delete_24),
                            contentDescription = "Supprimer"
                        )
                    }
                }
            }, leadingContent = {
                val imgAPI = ImageAPI()
                imgAPI.LoadImage(it.dish)
            }
        )
    }

}