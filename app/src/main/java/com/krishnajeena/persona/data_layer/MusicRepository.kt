package com.krishnajeena.persona.data_layer

import com.krishnajeena.persona.other.Resource
import kotlinx.coroutines.flow.Flow
interface MusicRepository {
    fun getSongs(): Flow<Resource<List<Song>>>
}
