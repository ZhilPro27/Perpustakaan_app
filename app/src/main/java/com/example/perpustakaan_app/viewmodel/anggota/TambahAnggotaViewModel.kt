package com.example.perpustakaan_app.viewmodel.anggota

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.perpustakaan_app.modeldata.DetailAnggota
import com.example.perpustakaan_app.modeldata.UIStateAnggota
import com.example.perpustakaan_app.modeldata.toDataAnggota
import com.example.perpustakaan_app.repostitori.RepositoryDataAnggota
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class TambahAnggotaViewModel(private val repositoryDataAnggota: RepositoryDataAnggota) : ViewModel() {
    var uiStateAnggota: UIStateAnggota by mutableStateOf(UIStateAnggota())
        private set

    fun updateUiState(detailAnggota: DetailAnggota) {
        uiStateAnggota = UIStateAnggota(
            detailAnggota = detailAnggota,
            isEntryValid = validasiInput(detailAnggota)
        )
    }

    private val _pesanChannel = Channel<String>()
    val pesanChannel = _pesanChannel.receiveAsFlow()

    suspend fun simpanAnggota(context: Context): Boolean {
        if (!uiStateAnggota.isEntryValid) {
            _pesanChannel.send("Gagal: Data tidak lengkap")
            return false
        }
        return try {
            repositoryDataAnggota.postAnggota(
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