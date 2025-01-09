package com.krishnajeena.persona.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.components.HomeEvent
import com.krishnajeena.persona.other.Resource
import com.krishnajeena.persona.usecases.AddMediaItemsUseCase
import com.krishnajeena.persona.usecases.PlaySongUseCase
import com.krishnajeena.persona.ui_states.HomeUiState
import com.krishnajeena.persona.usecases.GetSongsUseCase
import com.krishnajeena.persona.usecases.PauseSongUseCase
import com.krishnajeena.persona.usecases.ResumeSongUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val addMediaItemsUseCase: AddMediaItemsUseCase,
    private val playSongUseCase: PlaySongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
) : ViewModel(){

    var homeUiState by mutableStateOf(HomeUiState())
        private set

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.PlaySong -> playSong()

            HomeEvent.PauseSong -> pauseSong()

            HomeEvent.ResumeSong -> resumeSong()

            HomeEvent.FetchSong -> getSong()

            is HomeEvent.OnSongSelected -> homeUiState =
                homeUiState.copy(selectedSong = event.selectedSong)

        }
    }

    private fun getSong() {
        homeUiState = homeUiState.copy(loading = true)

        viewModelScope.launch {
            getSongsUseCase().catch {
                homeUiState = homeUiState.copy(
                    loading = false,
                    errorMessage = it.message
                )
            }.collect {
                homeUiState = when (it) {
                    is Resource.Success -> {
                        it.data?.let { songs ->
                            addMediaItemsUseCase(songs)
                        }

                        homeUiState.copy(
                            loading = false,
                            songs = it.data
                        )
                    }

                    is Resource.Loading -> {
                        homeUiState.copy(
                            loading = true,
                            errorMessage = null
                        )
                    }

                    is Resource.Error -> {
                        homeUiState.copy(
                            loading = false,
                            errorMessage = it.message
                        )
                    }
                }
            }
        }
    }

    private fun playSong() {
        homeUiState.apply {
            songs?.indexOf(selectedSong)?.let { song ->
                playSongUseCase(song)
            }
        }
    }

    private fun pauseSong() = pauseSongUseCase()

    private fun resumeSong() = resumeSongUseCase()

}