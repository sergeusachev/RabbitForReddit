package com.example.serge.newsstand.request

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