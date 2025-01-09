package com.krishnajeena.persona.usecases

import com.krishnajeena.persona.data_layer.MusicRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(private val musicRepository: MusicRepository) {
    operator fun invoke()= musicRepository.getSongs()
}