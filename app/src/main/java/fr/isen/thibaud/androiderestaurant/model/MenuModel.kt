package fr.isen.thibaud.androiderestaurant.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class MenuItem(
    @SerializedName("id") val id: String,
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("id_category") val categoryId: String,
    @SerializedName("categ_name_fr") val categoryNameFr: String,
    @SerializedName("categ_name_en") val categoryNameEn: String,
    val images: List<String>,
    val ingredients: List<Ingredient>,
    val prices: List<Price>
): Serializable

data class Ingredient(
    @SerializedName("id") val id: String,
    @SerializedName("id_shop") val shopId: String,
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("name_en") val nameEn: String,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("update_date") val updateDate: String,
    @SerializedName("id_pizza") val pizzaId: String
): Serializable

data class Price(
    @SerializedName("id") val id: String,
    @SerializedName("id_pizza") val pizzaId: String,
    @SerializedName("id_size") val sizeId: String,
    @SerializedName("price") val price: String,
    @SerializedName("create_date") val createDate: String,
    @SerializedName("update_date") val updateDate: String,
    @SerializedName("size") val size: String
): Serializable

data class MenuCategory(
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("name_en") val nameEn: String,
    val items: List<MenuItem>
): Serializable

data class MenuModel(
    val data: List<MenuCategory>
): Serializable