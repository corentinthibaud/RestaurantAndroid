package fr.isen.thibaud.androiderestaurant.utils

import java.text.DecimalFormat

class formattedPrice {
    companion object {
        fun formattedPrice(priceMin: String): String {
            var price = priceMin
            if(!priceMin.matches(Regex("\\d+\\.\\d{2}"))) {
                if(!priceMin.contains(".")) {
                    price += ".00"
                } else {
                    val formattedPrice = DecimalFormat("#." + "0".repeat(2)).format(priceMin.toDouble())
                    price = formattedPrice
                }
            }
            return price
        }
    }
}