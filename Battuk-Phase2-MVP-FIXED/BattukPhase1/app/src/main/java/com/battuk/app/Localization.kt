package com.battuk.app

import androidx.compose.material3.Text as M3Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

object BattukLocale {
    var language by mutableStateOf("en")
}

private val mr = mapOf(
    "Welcome to Battuk" to "Battuk मध्ये स्वागत आहे",
    "Track your home expenses. Understand your spending patterns." to "घरातील खर्च नोंदवा आणि खर्चाची पद्धत समजा.",
    "Name" to "नाव", "DOB (optional)" to "जन्मतारीख (ऐच्छिक)", "Add profile photo" to "प्रोफाइल फोटो जोडा",
    "Photo selected" to "फोटो निवडला", "Get Started →" to "सुरू करा →", "Your Family" to "तुमचे कुटुंब",
    "Add family members who spend or earn." to "खर्च किंवा उत्पन्न असलेले कुटुंब सदस्य जोडा.",
    "Add Family Member" to "कुटुंब सदस्य जोडा", "Relation" to "नाते", "Date of Birth (Optional)" to "जन्मतारीख (ऐच्छिक)",
    "Sound effects" to "ध्वनी प्रभाव", "Settings" to "सेटिंग्ज", "Theme" to "थीम", "Light" to "लाईट", "Dark" to "डार्क", "System" to "सिस्टम",
    "Home" to "मुख्यपृष्ठ", "Reports" to "अहवाल", "Calendar" to "दिनदर्शिका", "More" to "अधिक",
    "Expenses" to "खर्च", "Income" to "उत्पन्न", "Savings" to "बचत", "Quick actions" to "जलद कृती",
    "Add Expense" to "खर्च जोडा", "Add Income" to "उत्पन्न जोडा", "Bulk Entry" to "एकत्र नोंद", "Remember" to "खरेदी यादी",
    "Calendar" to "दिनदर्शिका", "Top categories" to "प्रमुख श्रेणी", "Recent activity" to "अलीकडील कृती",
    "Good day! 🌿" to "शुभ दिवस! 🌿", "Track today • Better tomorrow" to "आज नोंदवा • उद्याचे नियोजन करा",
    "Single Entry" to "एकल नोंद", "Item / description" to "वस्तू / वर्णन", "Price (₹)" to "किंमत (₹)",
    "Quantity" to "प्रमाण", "Unit" to "एकक", "Category" to "श्रेणी", "Family member" to "कुटुंब सदस्य",
    "Date" to "दिनांक", "Add Receipt Photo (Optional)" to "पावतीचा फोटो जोडा (ऐच्छिक)", "Receipt Selected" to "पावती निवडली",
    "Open Calculator" to "कॅल्क्युलेटर उघडा", "Save Expense" to "खर्च जतन करा", "Add multiple items from one market/store run." to "एका बाजार/दुकान फेरीतील अनेक वस्तू जोडा.",
    "Store name" to "दुकानाचे नाव", "Attach Trip Receipt" to "फेरीची पावती जोडा", "Trip Receipt Selected" to "फेरीची पावती निवडली",
    "Save Bulk Entry" to "एकत्र नोंद जतन करा", "Remember the Things" to "खरेदीच्या गोष्टी लक्षात ठेवा", "Plan before you shop. Tick items at the market, then convert them into Bulk Entry." to "खरेदीपूर्वी यादी करा. बाजारात वस्तूंवर खूण करा आणि नंतर एकत्र नोंदीत बदला.",
    "New list name" to "नवीन यादीचे नाव", "Add item" to "वस्तू जोडा", "No receipts saved yet. Attach one while adding an expense." to "अजून पावत्या जतन केलेल्या नाहीत. खर्च नोंदवताना एक जोडा.",
    "Categories" to "श्रेणी", "Choose an item" to "वस्तू निवडा", "Choose an item" to "वस्तू निवडा", "For this category, add any item/description from Single Entry." to "या श्रेणीसाठी एकल नोंदीत वस्तू/वर्णन जोडा.",
    "Item Price History" to "वस्तू किंमत इतिहास", "Price trend" to "किंमत कल", "Current" to "सध्याची",
    "Mini Calendar" to "लघु दिनदर्शिका", "Days with expenses are highlighted." to "ज्या दिवसांना खर्च आहे ते ठळक केले आहेत.",
    "Day Expenses" to "दिवसाचा खर्च", "No expenses logged for today yet." to "आजचा खर्च अजून नोंदवलेला नाही.",
    "Calculator" to "कॅल्क्युलेटर", "Expression" to "गणितीय उदाहरण", "History" to "इतिहास", "Copy Result" to "निकाल कॉपी करा", "Paste to Previous Amount" to "मागील रकमेवर पेस्ट करा",
    "Expenses by category" to "श्रेणीनुसार खर्च", "Family member spend" to "कुटुंब सदस्याचा खर्च", "Open Visual Dashboard" to "व्हिज्युअल डॅशबोर्ड उघडा",
    "Visual Dashboard" to "व्हिज्युअल डॅशबोर्ड", "Your spending patterns at a glance" to "तुमच्या खर्चाची झटपट माहिती",
    "Spend per family member" to "प्रति कुटुंब सदस्य खर्च", "This week vs previous week" to "या आठवड्याची मागील आठवड्याशी तुलना", "Inflation insights" to "महागाईची माहिती",
    "Inflation Calculator" to "महागाई कॅल्क्युलेटर", "Product" to "वस्तू", "Calculate" to "गणना करा", "Price change" to "किंमत बदल",
    "Calculated from your recorded purchase history." to "तुमच्या नोंदवलेल्या खरेदी इतिहासावरून गणना.", "Receipts Gallery" to "पावती संग्रह", "Browse local receipt photos" to "स्थानिक पावती फोटो पहा",
    "Currency" to "चलन", "Indian Rupees (₹) — fixed" to "भारतीय रुपये (₹) — निश्चित", "Family Members" to "कुटुंब सदस्य", "Manage your family" to "कुटुंब व्यवस्थापन",
    "Receipts Gallery" to "पावती संग्रह", "Year Management" to "वर्ष व्यवस्थापन", "Export CSV" to "CSV निर्यात", "Archive Year" to "वर्ष संग्रहित करा",
    "Budgets" to "अर्थसंकल्प", "Loans / EMI" to "कर्ज / EMI", "Streaks & Badges" to "सातत्य व बॅज", "Inflation Model" to "महागाई मॉडेल", "Cloud Backup & Sync" to "क्लाउड बॅकअप व सिंक", "Language" to "भाषा",
    "Phase 2" to "फेज २", "Advanced Features" to "अॅडव्हान्स फीचर्स", "Monthly budget" to "मासिक बजेट", "Budget limit" to "बजेट मर्यादा",
    "Add Budget" to "बजेट जोडा", "Budget saved." to "बजेट जतन केले.", "Budget exceeded" to "बजेट मर्यादा ओलांडली", "EMI" to "EMI",
    "Add Loan / EMI" to "कर्ज / EMI जोडा", "Due day" to "देय दिवस", "Next due" to "पुढील देय", "Mark paid / next month" to "भरले / पुढील महिना",
    "Current streak" to "सध्याचे सातत्य", "Best streak" to "सर्वोत्तम सातत्य", "Badges" to "बॅज", "days" to "दिवस",
    "Cloud sync is provider-neutral." to "क्लाउड सिंक सेवा-निरपेक्ष आहे.", "Export sync package" to "सिंक पॅकेज निर्यात करा", "Import sync package" to "सिंक पॅकेज आयात करा",
    "Provider not configured" to "सेवा पुरवठादार कॉन्फिगर केलेला नाही", "Save the package to Drive or another cloud storage." to "पॅकेज Drive किंवा इतर क्लाउड स्टोरेजमध्ये जतन करा.",
    "Weighted average" to "भारित सरासरी", "Seasonal adjustment" to "हंगामी समायोजन", "Product or category" to "वस्तू किंवा श्रेणी", "Time range" to "कालावधी",
    "Advanced Features" to "अॅडव्हान्स फीचर्स", "Build stronger money habits with budgets, reminders, insights and backup." to "अर्थसंकल्प, स्मरणपत्रे, माहिती आणि बॅकअपसह आर्थिक सवयी मजबूत करा.",
    "Set category limits and get alerts" to "श्रेणी मर्यादा ठेवा आणि सूचना मिळवा", "Schedule due-date reminders" to "देय तारखेची स्मरणपत्रे ठेवा", "Reward consistent daily logging" to "दररोजच्या सातत्यपूर्ण नोंदीसाठी बक्षीस मिळवा", "Weighted prices with seasonal view" to "भारित किमती व हंगामी दृश्य", "Save/import a complete sync package" to "पूर्ण सिंक पॅकेज जतन/आयात करा", "English / Marathi for the whole UI" to "संपूर्ण UI साठी इंग्रजी / मराठी",
    "Schedule EMI due-date reminders on this device." to "या डिव्हाइसवर EMI देय तारखेची स्मरणपत्रे ठेवा.", "Loan / EMI name" to "कर्ज / EMI नाव", "Lender" to "कर्जदाता", "Principal (₹)" to "मूळ रक्कम (₹)", "EMI (₹)" to "EMI (₹)", "Note (optional)" to "नोंद (ऐच्छिक)", "Scheduled EMIs" to "शेड्यूल केलेले EMI", "Pause" to "थांबवा", "Resume" to "पुन्हा सुरू करा",
    "Current month budgets" to "चालू महिन्याचे बजेट", "No category budgets" to "या महिन्यासाठी कोणतेही श्रेणी बजेट नाही", "Month (YYYY-MM)" to "महिना (YYYY-MM)", "Budget limit (₹)" to "बजेट मर्यादा (₹)",
    "Streaks use the dates of your logged expenses only; no account or cloud is required." to "सातत्य फक्त नोंदवलेल्या खर्चाच्या तारखांवर आधारित आहे; खाते किंवा क्लाउड आवश्यक नाही.",
    "Not enough purchase history in this period." to "या कालावधीत पुरेसा खरेदी इतिहास नाही.", "Not enough item-level history in this category." to "या श्रेणीमध्ये पुरेसा वस्तू-स्तरीय इतिहास नाही.",
    "From (YYYY-MM-DD)" to "पासून (YYYY-MM-DD)", "To (YYYY-MM-DD)" to "पर्यंत (YYYY-MM-DD)",
    "Choose the app interface language." to "अॅपची इंटरफेस भाषा निवडा.", "The UI switches immediately between English and Marathi." to "UI इंग्रजी आणि मराठीमध्ये त्वरित बदलतो.", "Produce names remain bilingual in either mode." to "दोन्ही मोडमध्ये फळे-भाज्यांची नावे द्विभाषिक राहतात.",
    "English" to "इंग्रजी", "Marathi" to "मराठी",
    "Weekly Market" to "साप्ताहिक बाजार", "Kirana Store" to "किराणा दुकान", "Clothes" to "कपडे", "Shoes" to "बूट", "Toiletries" to "वैयक्तिक वापर", "Milk Products" to "दूध उत्पादने", "Fast Food" to "फास्ट फूड", "Entertainment" to "मनोरंजन", "Loan/EMI" to "कर्ज/EMI", "Petrol/Diesel" to "पेट्रोल/डिझेल", "Other" to "इतर",
    "Father" to "वडील", "Mother" to "आई", "Son" to "मुलगा", "Daughter" to "मुलगी", "Brother" to "भाऊ", "Sister" to "बहीण", "Grandmother" to "आजी", "Grandfather" to "आजोबा", "Uncle" to "काका/मामा",
    "Day" to "दिवस", "Week" to "आठवडा", "Month" to "महिना", "6 Months" to "६ महिने", "Year" to "वर्ष", "This Month" to "हा महिना", "This week" to "हा आठवडा", "Previous" to "मागील", "Expense" to "खर्च"
)

fun localize(text: String): String {
    if (BattukLocale.language != "mr") return text
    mr[text]?.let { return it }
    return when {
        text.startsWith("No category budgets set for ") -> "${mr["No category budgets"] ?: "या महिन्यासाठी कोणतेही बजेट नाही"} ${text.removePrefix("No category budgets set for ")}."
        text.startsWith("Remaining ") -> "शिल्लक ${text.removePrefix("Remaining ")}"
        text.startsWith("Log expenses on ") -> "सलग ${text.removePrefix("Log expenses on ").removeSuffix(".")} नोंदवा."
        text.startsWith("Current: ") -> text.replace("Current:", "सध्याची:").replace("change", "बदल")
        else -> text
    }
}

@Composable
fun BattukText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (androidx.compose.ui.text.TextLayoutResult) -> Unit = {},
    style: TextStyle = androidx.compose.material3.LocalTextStyle.current
) {
    M3Text(
        text = localize(text), modifier = modifier, color = color, fontSize = fontSize,
        fontStyle = fontStyle, fontWeight = fontWeight, fontFamily = fontFamily,
        letterSpacing = letterSpacing, textDecoration = textDecoration, textAlign = textAlign,
        lineHeight = lineHeight, overflow = overflow, softWrap = softWrap, maxLines = maxLines,
        minLines = minLines, onTextLayout = onTextLayout, style = style
    )
}
