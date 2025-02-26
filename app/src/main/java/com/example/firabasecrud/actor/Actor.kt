package com.example.firabasecrud.actor

import com.example.firabasecrud.series.fechaCreacionFormateada
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Actor(var id : String? = "", var nombre: String? = "", var fechaNacimiento: String? = fechaCreacionFormateada(), var url_imagen: String? = "", var id_imagen: String? = "", var seriesActor: String? = ""): Serializable{

    fun fechaCreacionFormateada(): String {
        val fechgaFormato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return fechgaFormato.format(Date())
    }
}
