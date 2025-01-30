package com.example.firabasecrud.productorasSeries

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
import com.example.firabasecrud.productoras.Productora
import com.example.firabasecrud.series.Serie
import com.example.firabasecrud.series.SerieAdaptador
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerProductorasSeries : AppCompatActivity() {

    private lateinit var volver: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Serie>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: ProductorasSeriesAdapter
    private lateinit var buscar: EditText
    private lateinit var productoraActual: Productora

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_productoras_series)

        volver = findViewById(R.id.volver)
        recycler = findViewById(R.id.lista_productoras)
        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().reference
        buscar = findViewById(R.id.buscar_serie)
        productoraActual = intent.getSerializableExtra("productora actual") as Productora

        var lista_filtrada = mutableListOf<Serie>()

        buscar.doOnTextChanged { text, _, _, _ ->
            lista_filtrada = lista.filter { serie ->
                serie.nombre!!.contains(text.toString(), ignoreCase = true)
            }.toMutableList()
            adaptador = ProductorasSeriesAdapter(lista_filtrada, productoraActual)
            recycler.adapter = adaptador
        }

        // Get series data from the Firebase database
        db_ref.child("series").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoSerie = hijo?.getValue(Serie::class.java)
                    pojoSerie?.let {
                        lista.add(it)
                    }
                    Log.d("Serie", pojoSerie.toString())
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        adaptador = ProductorasSeriesAdapter(lista, productoraActual)
        recycler.adapter = adaptador
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(applicationContext)

        // Back button logic
        volver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}