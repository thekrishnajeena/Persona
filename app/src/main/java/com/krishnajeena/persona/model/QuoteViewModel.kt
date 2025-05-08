package com.krishnajeena.persona.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.DailyQuote
import com.krishnajeena.persona.network.RetrofitClientQuote
import com.krishnajeena.persona.other.QuoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val repository: QuoteRepository
) : ViewModel() {

    var quoteText by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var showDialog by mutableStateOf(false)

    fun loadQuote() {
        viewModelScope.launch {
            isLoading = true
            val quote = repository.fetchQuote()
            quoteText = quote.text
            showDialog = true
            isLoading = false
        }
    }
}
