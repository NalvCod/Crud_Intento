package com.example.firabasecrud.series

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firabasecrud.R
import com.example.firabasecrud.Util
import com.example.firabasecrud.productoras.Productora
import com.example.firabasecrud.seriesActores.SerieDetalle
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SerieAdaptador(
    private val lista_series: MutableList<Serie>,
    private val productora: Productora? = null,
    private val esFiltrado: Boolean = false
) : RecyclerView.Adapter<SerieAdaptador.SerieViewHolder>() {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_series


    inner class SerieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val genero: TextView = itemView.findViewById(R.id.genero)
        val fecha_estreno: TextView = itemView.findViewById(R.id.fecha_estreno)
        val fecha_fin: TextView = itemView.findViewById(R.id.fecha_finalizacion)
        val puntuacion: RatingBar = itemView.findViewById(R.id.item_rating)
        val editar: ImageView = itemView.findViewById(R.id.editar)
        val borrar: ImageView = itemView.findViewById(R.id.borrar)
    }


    fun listarProductoraFiltro(){
        if (esFiltrado && productora != null) {
            Log.d("Productora", productora.toString())
            lista_filtrada = lista_series.filter { serie ->
                productora.series.split(",").contains(serie.nombre)
            }.toMutableList()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_serie, parent, false)
        contexto = parent.context
        return SerieViewHolder(vista_item)
    }

    override fun getItemCount() = lista_filtrada.size


    override fun onBindViewHolder(holder: SerieViewHolder, position: Int) {
        val serie_actual = lista_filtrada[position]
        holder.nombre.text = serie_actual.nombre
        holder.fecha_estreno.text = serie_actual.fechaInicio
        holder.fecha_fin.text = serie_actual.fechaFin
        holder.genero.text = serie_actual.genero
        holder.puntuacion.rating = serie_actual.puntuacion

        val URL: String? = when (serie_actual.url_imagen) {
            "" -> null // Para que active imagen de fallback
            else -> serie_actual.url_imagen
        }
        Log.d("URL", URL.toString())

        Glide.with(contexto)
            .load(URL)
            .apply(Util.opcionesGlide(contexto))
            .transition(Util.transicion)
            .into(holder.miniatura)

        holder.editar.setOnClickListener {
            val intent = Intent(contexto, EditarSerie::class.java)
            intent.putExtra("serie", serie_actual)
            contexto.startActivity(intent)
        }

        //Para acceder a los actores que se encuentran en una serie
        holder.miniatura.setOnClickListener{
            val intent = Intent(contexto, SerieDetalle::class.java)
            intent.putExtra("serie", serie_actual)
            contexto.startActivity(intent)
        }

        holder.borrar.setOnClickListener {
            if (esFiltrado){
                Log.v("AAA", "ENTRA")
                val listaNueva = productora?.series?.split(",")?.filter { it != serie_actual.nombre }?.joinToString { "," }
                productora?.series = listaNueva.toString()
                // Guardamos la actualización de la productora en Firebase
                val db_ref = FirebaseDatabase.getInstance().reference
                db_ref.child("productoras").child(productora?.id ?: "").child("series").setValue(listaNueva.toString())
                    .addOnSuccessListener {
                        Log.v("AAA", "Productora actualizada en Firebase")
                        Toast.makeText(contexto, "Serie eliminada de la productora", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("AAA", "Error al actualizar la productora en Firebase", e)
                        Toast.makeText(contexto, "Error al actualizar la productora", Toast.LENGTH_SHORT).show()
                    }
            }else{
                lista_filtrada.removeAt(position)
                val db_ref = FirebaseDatabase.getInstance().reference
                val id_projecto = "674762dd002af7924291"
                val id_bucket = "674762fb002a63512c24"

                val client = Client()
                    .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
                    .setProject(id_projecto)

                val storage = Storage(client)
                GlobalScope.launch(Dispatchers.IO) {
                    storage.deleteFile(
                        bucketId = id_bucket,
                        fileId = serie_actual.id_imagen!!
                    )
                }
                db_ref.child("serie").child(serie_actual.id!!).removeValue()
                Toast.makeText(contexto, "Serie eliminada", Toast.LENGTH_SHORT).show()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, lista_filtrada.size)
            }

        }
    }
}