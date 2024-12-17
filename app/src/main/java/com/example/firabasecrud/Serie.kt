package com.example.firabasecrud

    import java.io.Serializable
    import java.text.SimpleDateFormat
    import java.util.Date
    import java.util.Locale

    data class Serie(
        var id: String? = "",
        var nombre: String? = "",
        var fechaInicio: String? = "",
        var fechaFin:String?="",
        var genero:String?="",
        var url_imagen: String? = "",
        var id_imagen: String? = "",
        var puntuacion : Int = 0,
        var fechaCreacionSerieBD: String = getCurrentDate()
    ): Serializable

        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

