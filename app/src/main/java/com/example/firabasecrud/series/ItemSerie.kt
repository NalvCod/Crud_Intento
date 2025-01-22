package com.example.firabasecrud.series

import android.os.Bundle
import android.widget.ImageView
import android.widget.RatingBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.firabasecrud.R

class ItemSerie : AppCompatActivity() {

    private lateinit var ratingBar: RatingBar
    private lateinit var editar : ImageView
    private lateinit var eliminar : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.item_serie)

        ratingBar = findViewById(R.id.item_rating)
        editar = findViewById(R.id.editar)
        eliminar = findViewById(R.id.borrar)


    }
}