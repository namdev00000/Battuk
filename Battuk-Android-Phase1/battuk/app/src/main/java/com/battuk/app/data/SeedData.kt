package com.battuk.app.data

import com.battuk.app.data.entity.Category
import com.battuk.app.data.entity.CategoryType
import com.battuk.app.data.entity.Item
import com.battuk.app.data.entity.ItemSubtype

/**
 * Default categories and the bilingual (English / Marathi) Weekly Market produce list,
 * inserted once when the database is first created.
 */
object SeedData {

    fun defaultCategories(): List<Category> = listOf(
        Category(name = "Weekly Market", type = CategoryType.WEEKLY_MARKET, iconKey = "market"),
        Category(name = "Kirana Store", type = CategoryType.KIRANA_STORE, iconKey = "kirana"),
        Category(name = "Clothes", type = CategoryType.CLOTHES, iconKey = "clothes"),
        Category(name = "Shoes", type = CategoryType.SHOES, iconKey = "shoes"),
        Category(name = "Toiletries", type = CategoryType.TOILETRIES, iconKey = "toiletries"),
        Category(name = "Milk Products", type = CategoryType.MILK_PRODUCTS, iconKey = "milk"),
        Category(name = "Fast Food", type = CategoryType.FAST_FOOD, iconKey = "fastfood"),
        Category(name = "Entertainment", type = CategoryType.ENTERTAINMENT, iconKey = "entertainment"),
        Category(name = "Loan / EMI", type = CategoryType.LOAN_EMI, iconKey = "loan"),
        Category(name = "Petrol / Diesel", type = CategoryType.PETROL_DIESEL, iconKey = "fuel"),
        Category(name = "Other", type = CategoryType.OTHER, iconKey = "other")
    )

    /** Call once category rows exist, passing the Weekly Market category's generated id. */
    fun weeklyMarketItems(weeklyMarketCategoryId: Long): List<Item> {
        fun veg(en: String, mr: String, unit: String = "kg") =
            Item(nameEnglish = en, nameMarathi = mr, subtype = ItemSubtype.VEGETABLE, defaultUnit = unit, categoryId = weeklyMarketCategoryId)

        fun leafy(en: String, mr: String, unit: String = "bundle") =
            Item(nameEnglish = en, nameMarathi = mr, subtype = ItemSubtype.LEAFY_GREEN, defaultUnit = unit, categoryId = weeklyMarketCategoryId)

        fun fruit(en: String, mr: String, unit: String = "kg") =
            Item(nameEnglish = en, nameMarathi = mr, subtype = ItemSubtype.FRUIT, defaultUnit = unit, categoryId = weeklyMarketCategoryId)

        return listOf(
            // Vegetables
            veg("Onion", "कांदा"),
            veg("Potato", "बटाटा"),
            veg("Tomato", "टोमॅटो"),
            veg("Brinjal", "वांगी"),
            veg("Okra", "भेंडी"),
            veg("Bitter Gourd", "कारले"),
            veg("Bottle Gourd", "दूधी भोपळा"),
            veg("Cabbage", "कोबी", unit = "piece"),
            veg("Cauliflower", "फ्लॉवर", unit = "piece"),
            veg("Coriander Leaves", "कोथिंबीर", unit = "bundle"),
            veg("Cucumber", "काकडी"),
            veg("Capsicum", "ढोबळी मिरची"),
            // Leafy greens
            leafy("Fenugreek Leaves", "मेथी"),
            leafy("Spinach", "पालक"),
            leafy("Amaranth Leaves", "राजगिरा / तांदुळजा"),
            leafy("Sorrel Leaves", "अंबाडी"),
            leafy("Dill Leaves", "शेपू"),
            leafy("Colocasia Leaves", "अळूची पाने"),
            leafy("Radish Greens", "मुळ्याचा पाला"),
            leafy("Mint Leaves", "पुदिना"),
            leafy("Curry Leaves", "कढीपत्ता"),
            leafy("Spring Onion", "कांद्याची पात"),
            // Fruits
            fruit("Mango", "आंबा"),
            fruit("Banana", "केळी", unit = "dozen"),
            fruit("Guava", "पेरू"),
            fruit("Jackfruit", "फणस", unit = "piece"),
            fruit("Pomegranate", "डाळिंब"),
            fruit("Custard Apple", "सीताफळ"),
            fruit("Watermelon", "कलिंगड", unit = "piece"),
            fruit("Papaya", "पपई", unit = "piece"),
            fruit("Grapes", "द्राक्षे"),
            fruit("Orange", "संत्री"),
            fruit("Chikoo", "चिकू"),
            fruit("Jambhul", "जांभूळ")
        )
    }
}
