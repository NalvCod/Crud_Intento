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
    private lateinit var lista: MutableList<Any> // Lista que puede contener tanto Productoras como Series
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: ProductorasSeriesAdapter // Adaptador para manejar Productoras y Series
    private lateinit var buscar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_productoras_series)

        // Inicialización de vistas
        volver = findViewById(R.id.volver)
        recycler = findViewById(R.id.lista_productoras)
        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().reference
        buscar = findViewById(R.id.buscar_serie)

        // Lista filtrada para búsqueda y ordenación
        var lista_filtrada = mutableListOf<Any>()

        // Filtrar la lista según el texto de búsqueda
        buscar.doOnTextChanged { text, _, _, _ ->
            lista_filtrada = lista.filter { item ->
                when (item) {
                    is Serie -> item.nombre!!.contains(text.toString(), ignoreCase = true)
                    is Productora -> item.nombre!!.contains(text.toString(), ignoreCase = true)
                    else -> false
                }
            }.toMutableList()
            adaptador = ProductorasSeriesAdapter(lista_filtrada, applicationContext)
            recycler.adapter = adaptador
        }

        // Cargar las productoras y series desde Firebase
        db_ref.child("productoras").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo ->
                    val productora = hijo.getValue(Productora::class.java)
                    productora?.let { lista.add(it) }
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        // Cargar las series desde Firebase
        db_ref.child("series").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { hijo ->
                    val serie = hijo.getValue(Serie::class.java)
                    serie?.let { lista.add(it) }
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        // Inicializar el adaptador y asignarlo al RecyclerView
        adaptador = ProductorasSeriesAdapter(lista, applicationContext)
        recycler.adapter = adaptador
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(applicationContext)

        // Acción del botón "volver"
        volver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}