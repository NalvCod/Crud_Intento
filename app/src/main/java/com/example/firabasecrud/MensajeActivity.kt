package com.example.firabasecrud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.databinding.ActivityMensajeBinding
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MensajeActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Mensaje>
    private lateinit var db_ref: DatabaseReference
    private lateinit var mensaje_enviado: EditText
    private lateinit var boton_enviar: Button
    private var last_pos: Int = 0
    private lateinit var binding: ActivityMensajeBinding
    private lateinit var usuarioActual: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding
        binding = ActivityMensajeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        last_pos = intent.getIntExtra("LAST_POS", 100000)
        Log.d("LASTTT_POS_LLEGAMOS", last_pos.toString())
        db_ref = FirebaseDatabase.getInstance().getReference()
        lista = mutableListOf()
        mensaje_enviado = binding.textoMensaje
        boton_enviar = binding.botonEnviar

        boton_enviar.setOnClickListener {
            last_pos = 1
            val mensaje = mensaje_enviado.text.toString().trim()

            if (mensaje.trim() != "") {
                val hoy: Calendar = Calendar.getInstance()
                val formateador: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                val fecha_hora = formateador.format(hoy.getTime());

                val id_mensaje = db_ref.child("chat").child("mensajes").push().key!!
                val nuevo_mensaje = Mensaje(
                    id_mensaje,
                    usuarioActual,
                    "",
                    R.drawable.yo,
                    mensaje,
                    fecha_hora
                )
                db_ref.child("chat").child("mensajes").child(id_mensaje).setValue(nuevo_mensaje)
                mensaje_enviado.setText("")
            } else {
                Toast.makeText(applicationContext, "Escribe algo", Toast.LENGTH_SHORT).show()
            }
        }



        db_ref.child("chat").child("mensajes").addChildEventListener(object : ChildEventListener {
            //cada vez que se añade un mensaje
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //corrutina
                GlobalScope.launch(Dispatchers.IO) {
                    val pojo_mensaje = snapshot.getValue(Mensaje::class.java)
                    pojo_mensaje!!.id_receptor = usuarioActual
                    if (pojo_mensaje.id_receptor == pojo_mensaje.id_emisor) {
                        //quiero ponerle el drawable yo
                        pojo_mensaje.imagen_emisor = R.drawable.yo
                    } else {
                        pojo_mensaje.imagen_emisor = R.drawable.otro
                    }

                    runOnUiThread {
                        lista.add(pojo_mensaje)
                        lista.sortBy { it.fecha_hora }
                        recycler.adapter!!.notifyDataSetChanged()
                        if (last_pos < lista.size && last_pos != 1 && last_pos != 100000) {
                            recycler.scrollToPosition((last_pos))
                        } else {
                            recycler.scrollToPosition((lista.size - 1))
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        recycler = binding.recyclerMensajes
        recycler.adapter = MensajeAdaptador(lista, last_pos)
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        val actividad = Intent(applicationContext, MainActivity::class.java)
        last_pos = lista.size
        actividad.putExtra("LAST_POS", last_pos)
        Log.d("LASTTT_POS_ATRAS", last_pos.toString())
        startActivity(actividad)
    }

    override fun onResume() {
        super.onResume()
        usuarioActual = intent.getStringExtra("usuario").toString()
    }

}