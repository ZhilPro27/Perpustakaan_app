package com.example.perpustakaan_app.viewmodel.anggota

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perpustakaan_app.modeldata.DetailAnggota
import com.example.perpustakaan_app.modeldata.UIStateAnggota
import com.example.perpustakaan_app.modeldata.toDataAnggota
import com.example.perpustakaan_app.modeldata.toUiStateAnggota
import com.example.perpustakaan_app.repostitori.RepositoryDataAnggota
import com.example.perpustakaan_app.uicontroller.route.anggota.DestinasiEditAnggota
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditAnggotaViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoryDataAnggota: RepositoryDataAnggota
) : ViewModel() {
    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()
    private val idAnggota: Int = checkNotNull(savedStateHandle[DestinasiEditAnggota.idAnggotaArg])

    var uiStateAnggota: UIStateAnggota by mutableStateOf(UIStateAnggota())
        private set

    init {
        ambilAnggota()
    }

    private fun ambilAnggota() {
        viewModelScope.launch {
            try {
                val dataAnggota = repositoryDataAnggota.getAnggotaById(idAnggota)
                uiStateAnggota = dataAnggota.toUiStateAnggota(isEntryValid = true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUiState(detailAnggota: DetailAnggota) {
        uiStateAnggota = UIStateAnggota(
            detailAnggota = detailAnggota,
            isEntryValid = validasiInput(detailAnggota)
        )
    }

    suspend fun updateAnggota(context: Context): Boolean {
        if (!uiStateAnggota.isEntryValid) {
            _pesanChannel.send("Gagal: Data tidak lengkap")
            return false
        }
        return try {
            repositoryDataAnggota.putAnggota(
                idAnggota,
                uiStateAnggota.detailAnggota.toDataAnggota()
            )
            _pesanChannel.send("Berhasil memperbarui data anggota")
            true
        } catch (e: Exception) {
            _pesanChannel.send("Gagal update: ${e.message}")
            false
        }
    }

    private fun validasiInput(uiState: DetailAnggota = uiStateAnggota.detailAnggota): Boolean {
        return with(uiState) {
            nama.isNotBlank() && no_hp.isNotBlank() && alamat.isNotBlank()
        }
    }
}
