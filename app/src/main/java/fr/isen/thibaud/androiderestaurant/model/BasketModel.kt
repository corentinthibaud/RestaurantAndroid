package fr.isen.thibaud.androiderestaurant.model

import android.content.Context
import com.google.gson.GsonBuilder
import fr.isen.thibaud.androiderestaurant.utils.formattedPrice.Companion.formattedPrice

class BasketModel {
    var items: MutableList<BasketItemModel> = mutableListOf()

    fun add(dish: MenuItem, count: Int, context: Context) {
        val existingItem = items.firstOrNull {it.dish == dish}
        existingItem?.let {
            it.count = it.count + count
        } ?: run {
            items.add(BasketItemModel(count, dish))
        }
        save(context)
    }

    fun delete(item: BasketItemModel, context: Context) {
        items.removeAll {item.dish.nameFr == it.dish.nameFr}.toString()
        save(context)
    }

    private fun save(context: Context) {
        val json = GsonBuilder().create().toJson(this)
        val sharedPreferences = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(BASKET_PREFERENCES_KEY, json)
        editor.apply()
    }

    fun countQuantities(): Int {
        var quantity = 0
        items.forEach {
            quantity += it.count
        }
        return quantity
    }

    fun price(): String {
        var price = 0.toDouble()
        items.forEach {
            price += it.dish.prices[0].price.toDouble() * it.count
        }
        return formattedPrice(price.toString())
    }

    companion object {
        const val USER_PREFERENCES_NAME = "USER_PREFERENCES"
        const val BASKET_PREFERENCES_KEY = "BASKET"

        fun current(context: Context): BasketModel {
            val sharedPreferences = context.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)
            val json = sharedPreferences.getString(BASKET_PREFERENCES_KEY, null)
            if(json != null) {
                return GsonBuilder().create().fromJson(json, BasketModel::class.java)
            }
            return BasketModel()
        }
    }
}
