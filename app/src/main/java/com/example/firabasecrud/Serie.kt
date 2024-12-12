package com.example.firabasecrud

import java.io.Serializable

data class Serie(
    var id: String? = "",
    var nombre: String? = "",
    var fechaInicio: String? = "",
    var fechaFin:String?="",
    var url_imagen: String? = "",
    var id_imagen: String? = ""
): Serializable