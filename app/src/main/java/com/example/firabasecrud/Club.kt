package com.example.firabasecrud

import java.io.Serializable

data class Club(
    var id: String? = "",
    var nombre: String? = "",
    var ciudad: String? = "",
    var fundacion: Int? = 0,
    var url_escudo: String? = "",
    var id_escudo: String? = ""
):Serializable
