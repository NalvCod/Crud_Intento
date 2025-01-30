package com.example.firabasecrud.productoras

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.MainActivity
import com.example.firabasecrud.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerProductoras : AppCompatActivity() {

    private lateinit var volver: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Productora>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: ProductoraAdaptador
    private lateinit var buscar: EditText
    private lateinit var nombre: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_productoras)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        volver = findViewById(R.id.volver)
        recycler = findViewById(R.id.lista_productoras)
        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().reference
        buscar = findViewById(R.id.buscar_productora)

        var lista_filtrada = mutableListOf<Productora>()

        buscar.doOnTextChanged { text, _, _, _ ->
            lista_filtrada = lista.filter { productora ->
                productora.nombre!!.contains(text.toString(), ignoreCase = true)
            }.toMutableList()
            adaptador = ProductoraAdaptador(lista_filtrada)
            recycler.adapter = adaptador
        }

        db_ref.child("productoras").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoProductora = hijo?.getValue(Productora::class.java)
                    lista.add(pojoProductora!!)
                    Log.d("Productora", pojoProductora.toString())
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        // Configurar el adaptador y el RecyclerView
        adaptador = ProductoraAdaptador(lista)
        recycler.adapter = adaptador
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(applicationContext)

        // Configurar el botón de volver
        volver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}