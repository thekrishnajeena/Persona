package com.krishnajeena.persona.other

import android.content.Context
import com.krishnajeena.persona.data_layer.DailyQuote
import com.krishnajeena.persona.network.RetrofitClientQuote
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuoteRepository @Inject constructor(
    @ApplicationContext private val context: Context // Use ApplicationContext with Hilt
) {

    private val prefs = context.getSharedPreferences("daily_quote", Context.MODE_PRIVATE)

    suspend fun fetchQuote(): DailyQuote {
        val today = LocalDate.now().toString()
        val saved = getSavedQuote()

        return if (saved?.date == today) {
            saved
        } else {
            val response = RetrofitClientQuote.getInstance().getQuoteOfTheDay()
            val newQuote = response.body()?.quote ?: "No quote available"
            val dailyQuote = DailyQuote(newQuote, today)
            saveQuote(dailyQuote)
            dailyQuote
        }
    }

    private fun saveQuote(quote: DailyQuote) {
        prefs.edit().apply {
            putString("quote_text", quote.text)
            putString("quote_date", quote.date)
            apply()
        }
    }

    private fun getSavedQuote(): DailyQuote? {
        val date = prefs.getString("quote_date", null)
        val text = prefs.getString("quote_text", null)
        return if (date != null && text != null) DailyQuote(text, date) else null
    }
}
