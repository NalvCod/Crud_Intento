package com.example.firabasecrud.seriesActores


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.actor.Actor
import com.example.firabasecrud.actor.ActorAdaptador
import com.example.firabasecrud.databinding.ActivitySerieDetalleBinding
import com.example.firabasecrud.series.Serie
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SerieDetalle : AppCompatActivity() {
    // binding
    private lateinit var binding: ActivitySerieDetalleBinding
    private lateinit var serie: Serie
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptadorActores: ActorAdaptador
    private lateinit var recycler: RecyclerView

    // Lista para almacenar los actores de la serie
    private var listaActores: MutableList<Actor> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflamos el layout
        binding = ActivitySerieDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperamos el objeto Serie desde el Intent
        serie = intent.getSerializableExtra("serie") as Serie

        // Inicializamos Firebase Database
        db_ref = FirebaseDatabase.getInstance().reference

        // Configuramos el RecyclerView para los actores
        recycler = binding.recyclerActores
        recycler.layoutManager = LinearLayoutManager(this)
        adaptadorActores = ActorAdaptador(listaActores)
        recycler.adapter = adaptadorActores

        // Acción al hacer click en el botón de "volver"
        binding.volver.setOnClickListener {
            finish()  // Finaliza la actividad y vuelve a la anterior
        }
        binding.titulo.text = serie.nombre
        cargarActoresDeSerie()
    }

    private fun cargarActoresDeSerie() {
        Log.v("aa", "aAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        db_ref.child("actores").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaActores.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoActores = hijo?.getValue(Actor::class.java)
                    listaActores.add(pojoActores!!)
                    Log.d("Serie", pojoActores.toString())
                }

                adaptadorActores.listarActoresDeSerie()
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
    }
}