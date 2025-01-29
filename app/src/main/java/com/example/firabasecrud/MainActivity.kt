package com.example.firabasecrud

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firabasecrud.actor.CrearActor
import com.example.firabasecrud.actor.VerActores
import com.example.firabasecrud.productoras.CrearProductora
import com.example.firabasecrud.productoras.VerProductoras
import com.example.firabasecrud.series.CrearSerie
import com.example.firabasecrud.series.VerSeries
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var anadir: Button
    private lateinit var botonListar: Button
    private lateinit var anadirActor : Button
    private lateinit var botonListarActores : Button
    private lateinit var botonVerProductoras : Button
    private lateinit var botonCrearProductoras : Button
    private lateinit var botonMensajes : ImageView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance().reference

        botonMensajes = findViewById(R.id.mensajes)
        botonMensajes.setOnClickListener {
            //alert dialog
            val editText = EditText(this)
            editText.hint = "Ingresa tu nombre de usuario"

            // Crear el AlertDialog
            val dialog = AlertDialog.Builder(this)
                .setTitle("Nombre de Usuario")
                .setMessage("Por favor, ingresa tu nombre de usuario:")
                .setView(editText) // Establecer el EditText dentro del AlertDialog
                .setPositiveButton("Aceptar") { _, _ ->
                    // Al presionar Aceptar, obtener el texto ingresado
                    val username = editText.text.toString()
                    if (username.isNotEmpty()) {
                        // Si el campo no está vacío, mostrar un mensaje
                        Toast.makeText(this, "Nombre de usuario ingresado: $username", Toast.LENGTH_SHORT).show()
                        Util.introducir_nombre_usuario(database, username)

                        val intent = Intent(this, MensajeActivity::class.java)
                        intent.putExtra("usuario", username)
                        startActivity(intent)
                    } else {
                        // Si el campo está vacío, mostrar un error
                        Toast.makeText(this, "Por favor ingresa un nombre de usuario", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    // Si el usuario presiona Cancelar, cerrar el diálogo
                    dialog.dismiss()
                }
                .create()

            dialog.show()

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