package com.example.firabasecrud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



private lateinit var anadir: Button
private lateinit var botonListar: Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        anadir = findViewById(R.id.crear)
        botonListar = findViewById(R.id.listar)


        anadir.setOnClickListener {
            val intent = Intent(this, CrearSerie::class.java)
            startActivity(intent)
        }

        botonListar.setOnClickListener {
            val intent = Intent(this, VerSeries::class.java)
            startActivity(intent)
        }

//        referencia = FirebaseDatabase.getInstance().getReference()
//        var identificador = referencia.child("series").push().key!!
//
//        var nuevaSerie = Serie("1234", "Shingeki", "2012 a 2023")
//
//        referencia.child("series").child(identificador).push()
//            .setValue(nuevaSerie)


    }
}