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
    var puntuacion : Float = 0.0f,
    var fechaCreacionSerieBD: String = fechaCreacionFormateada()
): Serializable

fun fechaCreacionFormateada(): String {
    val fechgaFormato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return fechgaFormato.format(Date())
}

