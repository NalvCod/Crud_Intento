package com.example.firabasecrud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firabasecrud.actor.CrearActor
import com.example.firabasecrud.actor.VerActores
import com.example.firabasecrud.productoras.CrearProductora
import com.example.firabasecrud.productoras.VerProductoras
import com.example.firabasecrud.series.CrearSerie
import com.example.firabasecrud.series.VerSeries

class MainActivity : AppCompatActivity() {

    private lateinit var anadir: Button
    private lateinit var botonListar: Button
    private lateinit var anadirActor : Button
    private lateinit var botonListarActores : Button
    private lateinit var botonVerProductoras : Button
    private lateinit var botonCrearProductoras : Button

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
        anadirActor = findViewById(R.id.crearActores)
        botonListarActores = findViewById(R.id.listarActores)
        botonVerProductoras = findViewById(R.id.listarProductoras)
        botonCrearProductoras = findViewById(R.id.crearProductoras)


        botonVerProductoras.setOnClickListener {
            val intent = Intent(this, VerProductoras::class.java)
            startActivity(intent)

        }

        botonCrearProductoras.setOnClickListener {
            val intent = Intent(this, CrearProductora::class.java)
            startActivity(intent)

        }

        anadir.setOnClickListener {
            val intent = Intent(this, CrearSerie::class.java)
            startActivity(intent)
        }

        botonListar.setOnClickListener {
            val intent = Intent(this, VerSeries::class.java)
            startActivity(intent)
        }

        anadirActor.setOnClickListener {
            val intent = Intent(this, CrearActor::class.java)
            startActivity(intent)

        }

        botonListarActores.setOnClickListener {
            val intent = Intent(this, VerActores::class.java)
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