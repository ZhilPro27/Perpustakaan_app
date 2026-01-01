package com.example.perpustakaan_app.uicontroller.route.anggota

object DestinasiEditAnggota: DestinasiNavigasiAnggota {
    override val route = "edit_anggota"
    override val tittleRes = "Edit Anggota"
    const val idAnggotaArg = "id_anggota"
    val routeWithArgs = "$route/{$idAnggotaArg}"
}