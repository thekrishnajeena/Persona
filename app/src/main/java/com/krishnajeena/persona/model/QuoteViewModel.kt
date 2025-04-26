package com.krishnajeena.persona.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.DailyQuote
import com.krishnajeena.persona.network.RetrofitClientQuote
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class QuoteViewModel () : ViewModel() {
    var quoteText by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var showDialog by mutableStateOf(false)

     fun loadQuote(context: Context) {
        val today = LocalDate.now().toString()
        val saved = getSavedQuote(context)

        if (saved != null && saved.date == today) {
            quoteText = saved.text
            showDialog = true
        } else {
            viewModelScope.launch {
                isLoading = true
                val quoteresponse = RetrofitClientQuote.instance.getQuoteOfTheDay()
                val newQuote = quoteresponse.body()?.quote ?: "No quote available"
                val dailyQuote = DailyQuote(newQuote, today)
                saveQuoteLocally(context, dailyQuote)
                quoteText = newQuote
                isLoading = false
                showDialog = true
            }
        }
    }
}

fun saveQuoteLocally(context: Context, quote: DailyQuote) {
    val prefs = context.getSharedPreferences("daily_quote", Context.MODE_PRIVATE)
    prefs.edit().apply {
        putString("quote_text", quote.text)
        putString("quote_date", quote.date)
        apply()
    }
}

fun getSavedQuote(context: Context): DailyQuote? {
    val prefs = context.getSharedPreferences("daily_quote", Context.MODE_PRIVATE)
    val date = prefs.getString("quote_date", null)
    val text = prefs.getString("quote_text", null)
    return if (date != null && text != null) DailyQuote(text, date) else null
}
