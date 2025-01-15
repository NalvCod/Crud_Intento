package com.example.firabasecrud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.firabasecrud.databinding.ActivityVerActoresBinding
import com.example.firabasecrud.MainActivity
import com.example.firabasecrud.Actor

class VerActores : AppCompatActivity() {

    private lateinit var binding: ActivityVerActoresBinding
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: ActorAdaptador
    private var lista: MutableList<Actor> = mutableListOf()
    private lateinit var volver: ImageView
    private lateinit var recycler: RecyclerView
    private lateinit var buscar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar el binding
        binding = ActivityVerActoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración de la interfaz para ViewCompat
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de vistas
        volver = binding.volver
        recycler = binding.listaActores
        ordenar = binding.ordenarRanking
        buscar = binding.buscarActor

        // Lista filtrada para mostrar
        var listaFiltrada = mutableListOf<Actor>()

        // Inicialización de la base de datos de Firebase
        db_ref = FirebaseDatabase.getInstance().reference

        // Configuración del botón "ordenar"
        ordenar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listaFiltrada = lista.sortedBy { it.puntuacion }.toMutableList()
            } else {
                listaFiltrada = lista
            }
            adaptador = ActorAdaptador(listaFiltrada)
            adaptador.notifyDataSetChanged()
            recycler.adapter = adaptador
        }

        // Configuración de la barra de búsqueda
        buscar.doOnTextChanged { text, _, _, _ ->
            listaFiltrada = lista.filter { actor ->
                actor.nombre!!.contains(text.toString(), ignoreCase = true)
            }.toMutableList()
            adaptador = ActorAdaptador(listaFiltrada)
            recycler.adapter = adaptador
        }

        // Listener para cargar datos desde Firebase
        db_ref.child("actores").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoActor = hijo?.getValue(Actor::class.java)
                    if (pojoActor != null) {
                        lista.add(pojoActor)
                        Log.d("Actor", pojoActor.toString())
                    }
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        // Configuración del adaptador para el RecyclerView
        adaptador = ActorAdaptador(lista)
        recycler.adapter = adaptador
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(applicationContext)

        // Acción al hacer clic en el botón "volver"
        volver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
