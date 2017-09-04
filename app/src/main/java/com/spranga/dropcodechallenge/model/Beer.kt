package com.spranga.dropcodechallenge.model

/**
 * Created by nietzsche on 02/09/17.
 */
data class BeerModel(val id: Int,
    val name: String,
    val description: String,
    val abv: Float,
    val ibu: Float?,
    val image_url: String,
    val method: MethodModel,
    val ingredients: IngredientsModel
)


data class MethodModel(val mash_temp: List<MashTempModel>, val fermentation: FermentationModel,
    val twist: String?)

data class IngredientsModel(val malt: List<MaltModel>,
    val hops: List<HopModel>,
    val yeast: String)

data class MashTempModel(val temp: TemperatureModel, val duration: Int?)
data class FermentationModel(val temp: TemperatureModel)
data class MaltModel(val name: String, val amount: AmountModel)
data class HopModel(val name: String, val amount: AmountModel, val add: String,
    val attribute: String)

data class TemperatureModel(val value: Float, val unit: String)
data class AmountModel(val value: Float, val unit: String)