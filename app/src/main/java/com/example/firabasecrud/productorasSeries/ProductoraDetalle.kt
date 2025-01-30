package com.example.firabasecrud.productorasSeries

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.databinding.ActivityProductoraDetalleBinding
import com.example.firabasecrud.productoras.Productora
import com.example.firabasecrud.productoras.VerProductoras
import com.example.firabasecrud.series.Serie
import com.example.firabasecrud.series.SerieAdaptador
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductoraDetalle : AppCompatActivity() {
    // binding
    private lateinit var binding: ActivityProductoraDetalleBinding
    private lateinit var productoraActual: Productora
    private lateinit var adaptador: SerieAdaptador
    private lateinit var db_ref : DatabaseReference
    private lateinit var recycler: RecyclerView
    private var listaSeries: MutableList<Serie> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductoraDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productoraActual = intent.getSerializableExtra("productora actual") as Productora
        recycler = binding.recyclerSeries
        db_ref = FirebaseDatabase.getInstance().reference
        listaSeries = mutableListOf()

        val recyclerView = binding.recyclerSeries
        recyclerView.layoutManager = LinearLayoutManager(this)

        adaptador = SerieAdaptador(listaSeries, productoraActual, true)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)

        binding.volver.setOnClickListener {
            val intent = Intent(this, VerProductoras::class.java)
            startActivity(intent)
        }
        binding.titulo.text = productoraActual.nombre

        db_ref.child("series").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaSeries.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoSerie = hijo?.getValue(Serie::class.java)
                    listaSeries.add(pojoSerie!!)
                    Log.d("Serie", pojoSerie.toString())
                }

                adaptador.listarProductoraFiltro()
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })
    }
}
