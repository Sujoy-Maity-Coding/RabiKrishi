package com.sujoy.smartfarm.Domain.model

data class Crop(

    var cropId: String = "",

    var cropName: String = "",

    var scientificName: String = "",

    var season: String = "",

    var soilTypes: List<String> = emptyList(),

    var districts: List<String> = emptyList(),

    var months: List<Int> = emptyList(),

    var category: String = "",

    var waterRequirement: String = "",

    var imageUrl: String = "",

    var description: String = "",

    var recommendationScore: Int = 0,

    var sowingTime: String = "",

    var harvestingTime: String = "",

    var expectedYield: String = "",

    var marketDemand: String = "",

    var farmingTips: String = ""
)