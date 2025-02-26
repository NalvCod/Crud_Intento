package com.example.firabasecrud.actor

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firabasecrud.R
import com.example.firabasecrud.Util
import com.example.firabasecrud.series.Serie
import com.example.firabasecrud.seriesActores.SerieActorAdaptador
import com.example.firabasecrud.seriesActores.VerSeriesActores
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActorAdaptador(
    private val lista_actors: MutableList<Actor>,
    private val serie: Serie? = null,  // Usamos el parámetro 'serie' para el filtrado
    private val esFiltrado: Boolean = false  // Controlamos si se debe aplicar el filtro
) : RecyclerView.Adapter<ActorAdaptador.ActorViewHolder>() {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_actors

    inner class ActorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val miniatura: ImageView = itemView.findViewById(R.id.foto_perfil)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val fecha_nacimiento: TextView = itemView.findViewById(R.id.fecha_estreno)
        val editar: ImageView = itemView.findViewById(R.id.editar)
        val borrar: ImageView = itemView.findViewById(R.id.borrar)
        val anadirSerie: Button = itemView.findViewById(R.id.add_series_actor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_actor, parent, false)
        contexto = parent.context
        return ActorViewHolder(vista_item)
    }

    override fun getItemCount() = lista_filtrada.size

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val actor_actual = lista_filtrada[position]
        holder.nombre.text = actor_actual.nombre
        holder.fecha_nacimiento.text = actor_actual.fechaNacimiento

        // Si no hay URL, cargar imagen por defecto
        val URL: String? = when (actor_actual.url_imagen) {
            "" -> null
            else -> actor_actual.url_imagen
        }
        Log.d("URL", URL.toString())

        Glide.with(contexto)
            .load(URL)
            .apply(Util.opcionesGlide(contexto))
            .transition(Util.transicion)
            .into(holder.miniatura)

        holder.editar.setOnClickListener {
            val intent = Intent(contexto, EditarActor::class.java)
            intent.putExtra("actor actual", actor_actual)
            contexto.startActivity(intent)
        }

        holder.anadirSerie.setOnClickListener{
            Log.v("Ver", "Se ha enviado el actor ${actor_actual.nombre}")
            val intent = Intent(contexto, VerSeriesActores::class.java)
            intent.putExtra("actor actual", actor_actual)
            contexto.startActivity(intent)
        }

        holder.borrar.setOnClickListener {
            val db_ref = FirebaseDatabase.getInstance().reference
            val id_projecto = "674762dd002af7924291"
            val id_bucket = "674762fb002a63512c24"

            val client = Client()
                .setEndpoint("https://cloud.appwrite.io/v1")    // Tu API Endpoint
                .setProject(id_projecto)

            val storage = Storage(client)
            GlobalScope.launch(Dispatchers.IO) {
                storage.deleteFile(
                    bucketId = id_bucket,
                    fileId = actor_actual.id_imagen!!
                )
            }


            lista_filtrada.removeAt(position)
            db_ref.child("com/example/firabasecrud/actor").child(actor_actual.id!!).removeValue()
            Toast.makeText(contexto, "Actor eliminado", Toast.LENGTH_SHORT).show()
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, lista_filtrada.size)
        }
    }

    fun listarActoresDeSerie() {

        Log.d("HOALAAA", serie.toString())
        if (serie != null) {
            lista_filtrada = lista_actors.filter { actor ->
                actor.seriesActor?.split(",")!!.contains(serie.nombre)
            }.toMutableList()
            Log.d("LISTAAA", lista_filtrada.toString())
        }
    }
}