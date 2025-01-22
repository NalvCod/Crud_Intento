package com.example.firabasecrud.seriesActores

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.MainActivity
import com.example.firabasecrud.R
import com.example.firabasecrud.actor.Actor
import com.example.firabasecrud.series.Serie
import com.example.firabasecrud.series.SerieAdaptador
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class VerSeriesActores : AppCompatActivity() {

    private lateinit var volver: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Serie>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: SerieAdaptador
    private lateinit var ordenar: CheckBox
    private lateinit var buscar: EditText
    private lateinit var anadirActor: Button
    private lateinit var actorActual: Actor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_series)

        volver = findViewById(R.id.volver)
        recycler = findViewById(R.id.lista_series)
        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().reference
        ordenar = findViewById(R.id.ordenar_ranking)
        buscar = findViewById(R.id.buscar_serie)

        var lista_filtrada = mutableListOf<Serie>()

        ordenar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lista_filtrada = lista.sortedBy { it.puntuacion }.toMutableList()
            } else {
                lista_filtrada = lista
            }
            adaptador = SerieAdaptador(lista_filtrada)
            adaptador.notifyDataSetChanged()
            recycler.adapter=adaptador
        }

        buscar.doOnTextChanged{
                text, _, _, _ ->
            lista_filtrada = lista.filter { serie ->
                serie.nombre!!.contains(text.toString(), ignoreCase = true)
            }.toMutableList()
            adaptador = SerieAdaptador(lista_filtrada)
            recycler.adapter=adaptador
        }



        db_ref.child("series").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoSerie = hijo?.getValue(Serie::class.java)
                    lista.add(pojoSerie!!)
                    Log.d("Serie", pojoSerie.toString())
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
        adaptador = SerieAdaptador(lista)
        recycler.adapter = adaptador
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(applicationContext)

        volver.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        anadirActor.setOnClickListener{
            val intent = Intent(this, VerSeriesActores::class.java)
            startActivity(intent)
            //quiero guardarme el actor que estoy pasando
            intent.putExtra("com/example/firabasecrud/actor", actorActual)

        }
    }
}