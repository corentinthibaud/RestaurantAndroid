package fr.isen.thibaud.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.thibaud.androiderestaurant.model.BasketModel
import fr.isen.thibaud.androiderestaurant.network.ImageAPI
import fr.isen.thibaud.androiderestaurant.model.MenuCategory
import fr.isen.thibaud.androiderestaurant.model.MenuModel
import fr.isen.thibaud.androiderestaurant.network.NetworkConstants
import fr.isen.thibaud.androiderestaurant.ui.theme.AndroidERestaurantTheme
import fr.isen.thibaud.androiderestaurant.utils.formattedPrice.Companion.formattedPrice
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
class MenuActivity : ComponentActivity() {
    companion object {
        const val CATEGORY_KEY = "categoryKey"
    }

    private val TAB_HEIGHT = 48.dp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("lifeCycle", "Menu Activity on Create")
        var category = (intent.getSerializableExtra(CATEGORY_KEY) as Categories) ?: Categories.ENTREE

        setContent {
            var refreshPage = false
            var tabIndex by remember { mutableIntStateOf(0) }
            AndroidERestaurantTheme {
                val context = this
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                                titleContentColor = MaterialTheme.colorScheme.onBackground,
                            ),
                            title = {
                                Text("Menu",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center)
                            },
                            actions = {
                                IconButton(onClick = {
                                    val intent = Intent(this@MenuActivity, BasketActivity::class.java)
                                    startActivity(intent)
                                }) {
                                    BadgedBox(badge = {
                                        if(BasketModel.current(context).countQuantities() > 0) {
                                            Badge {
                                                Text(BasketModel.current(context).countQuantities().toString())
                                            }
                                        }
                                    }) {
                                        Icon(painterResource(R.drawable.baseline_shopping_cart_24), stringResource(R.string.app_menu_cart))
                                    }
                                }
                            }
                        )
                    }
                ) {
                    tabIndex = Categories.entries.indexOf(category)
                    TabRow(selectedTabIndex = tabIndex,
                        modifier = Modifier
                            .padding(top = it.calculateTopPadding())
                            .height(48.dp)
                            .fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        tabs = {
                            Categories.entries.forEachIndexed {index, it ->
                                Tab(tabIndex == index,
                                    {
                                        category = it
                                        refreshPage = true
                                        tabIndex = index
                                    }
                                ) {
                                    Text(
                                        text = it.display,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                        }
                    })
                    if(category.API.isEmpty()) {
                        Log.e("MenuActivity", "No category has been found")
                        Text(text = "No category has been found",
                            modifier = Modifier.padding(top = it.calculateTopPadding() + TAB_HEIGHT),
                            color = MaterialTheme.colorScheme.error)
                    } else {
                        val resultsFromAPI = remember { mutableStateOf<MenuCategory?>(null) }
                        if(resultsFromAPI.value == null || refreshPage) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .padding(top = it.calculateTopPadding() + TAB_HEIGHT)
                                    .fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primaryContainer
                            )
                            APICall(category, resultsFromAPI)
                            refreshPage = false
                        }
                        LazyColumn (
                            Modifier.padding(top = it.calculateTopPadding() + TAB_HEIGHT)
                        ) {
                            resultsFromAPI.value?.let { it ->
                                items(it.items) {
                                    var priceMin = ""
                                    ListItem(
                                        modifier = Modifier.clickable(enabled = true, role = Role.Button) {
                                            val intent = Intent(this@MenuActivity, DetailActivity::class.java)
                                            intent.putExtra(DetailActivity.DISH_KEY, it)
                                            startActivity(intent)
                                            },
                                        headlineContent = { Text(
                                            text = it.nameFr,
                                            style = MaterialTheme.typography.titleMedium
                                        ) },
                                        supportingContent = {
                                            if(it.prices.count() > 1) {
                                                var textSupporting = "Tailles disponibles:"
                                                it.prices.forEach {
                                                    if(priceMin.isEmpty() || it.price < priceMin) {
                                                        priceMin = it.price
                                                    }
                                                    if(!textSupporting.endsWith(":")) {
                                                        textSupporting += ","
                                                    }
                                                    textSupporting += " " + it.size
                                                }
                                                Text(text = textSupporting)
                                            }
                                        }, trailingContent = {
                                            if(priceMin.isEmpty()) {
                                                priceMin = it.prices.first().price
                                            }
                                            priceMin = formattedPrice(priceMin)
                                            Text(
                                                text = "$priceMin €",
                                                style = MaterialTheme.typography.labelMedium)
                                        }, leadingContent = {
                                            val imgAPI = ImageAPI()
                                            imgAPI.LoadImage(it)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        Log.d("lifeCycle", "Menu Activity on Pause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Menu Activity on Resume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Menu Activity on Destroy")
        super.onDestroy()
    }
}

@Composable
private fun APICall(category: Categories, resultsToSendToParent: MutableState<MenuCategory?>) {
    val context = LocalContext.current
    val queue = Volley.newRequestQueue(context)

    val params = JSONObject()
    params.put(NetworkConstants.ID_SHOP, "1")

    val request = JsonObjectRequest(NetworkConstants.method, NetworkConstants.URL, params,
        { response ->
            val result = recipesFromAPI(parseAPIResponse(response.toString()), category)
            resultsToSendToParent.value = result
        },
        { error ->
            Log.e("APIError", error.toString())
        })

    queue.add(request)
}

private fun recipesFromAPI(response: MenuModel, category: Categories): MenuCategory {
    return response.data.first {
        it.nameFr == category.API
    }
}

private fun parseAPIResponse(response: String): MenuModel {
    val gson = Gson()
    return gson.fromJson(response, MenuModel::class.java)
}