package com.example.firabasecrud

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mensaje(var id:String?=null,
                   var id_emisor:String?=null,
                   var id_receptor:String?=null,
                   var imagen_emisor:Int = 0,
                   var contenido:String?=null,
                   var fecha_hora:String?=null): Parcelable