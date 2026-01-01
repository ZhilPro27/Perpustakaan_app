package com.example.perpustakaan_app.viewmodel.anggota

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DataAnggota
import com.example.perpustakaan_app.repostitori.RepositoryDataAnggota
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface AnggotaUiState{
    data class Success(val anggota: List<DataAnggota>) : AnggotaUiState
    object Error : AnggotaUiState
    object Loading : AnggotaUiState
}

class AnggotaViewModel(private val repositoryDataAnggota: RepositoryDataAnggota) : ViewModel() {
    var anggotaUiState : AnggotaUiState by mutableStateOf(AnggotaUiState.Loading)
        private set

    var searchQuery by mutableStateOf("")
        private set

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    fun searchAnggota(keyword: String = searchQuery) {
        viewModelScope.launch {
            anggotaUiState = AnggotaUiState.Loading
            anggotaUiState = try {
                if (keyword.isBlank()) {
                    AnggotaUiState.Success(repositoryDataAnggota.getAnggota())
                } else {
                    AnggotaUiState.Success(repositoryDataAnggota.getAnggotaByNama(keyword))
                }
            } catch (e: IOException) {
                AnggotaUiState.Error
            } catch (e: Exception) {
                AnggotaUiState.Error
            }
        }
    }

    fun getAnggota() {
        viewModelScope.launch {
            anggotaUiState = AnggotaUiState.Loading
            anggotaUiState = try {
                AnggotaUiState.Success(repositoryDataAnggota.getAnggota())
            } catch (e: IOException) {
                AnggotaUiState.Error
            } catch (e: Exception) {
                AnggotaUiState.Error
            }
        }
    }

    fun deleteAnggota(id_anggota: Int) {
        viewModelScope.launch {
            try {
                repositoryDataAnggota.deleteAnggota(id_anggota)
                getAnggota()
                _pesanChannel.send("Berhasil menghapus anggota")
            } catch (e: Exception) {
                _pesanChannel.send("Gagal menghapus anggota: ${e.message}")
            }
        }
    }
}