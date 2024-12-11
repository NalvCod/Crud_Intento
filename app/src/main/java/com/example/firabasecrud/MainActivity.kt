package com.example.firabasecrud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



private lateinit var botonCrear: Button
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


        botonCrear = findViewById(R.id.crear)
        botonListar = findViewById(R.id.listar)


        botonCrear.setOnClickListener {
            val intent = Intent(this, CrearClub::class.java)
            startActivity(intent)
        }

        botonListar.setOnClickListener {
            val intent = Intent(this, VerClubs::class.java)
            startActivity(intent)
        }

    }
}