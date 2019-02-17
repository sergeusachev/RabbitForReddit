package com.serge.rabbitforreddit.request

enum class CategoriesEnum(val categoryName: String) {
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    GENERAL("general"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology");
}

enum class CountryEnum(val countryCode: String) {
    RU("ru");
}