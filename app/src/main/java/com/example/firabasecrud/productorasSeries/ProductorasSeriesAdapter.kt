package com.example.firabasecrud.productorasSeries

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firabasecrud.R
import com.example.firabasecrud.Util
import com.example.firabasecrud.actor.Actor
import com.example.firabasecrud.productoras.Productora
import com.example.firabasecrud.series.Serie
import com.example.firabasecrud.series.SerieAdaptador
import com.example.firabasecrud.series.SerieAdaptador.SerieViewHolder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProductorasSeriesAdapter(
    private val lista_productoras_series: MutableList<Any>,  // Lista que puede contener tanto Productoras como Series
    private val contexto: Context
) : RecyclerView.Adapter<ProductorasSeriesAdapter.ProductorasSeriesViewHolder>() {

    private lateinit var database: DatabaseReference

    inner class ProductorasSeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val anadirSerie: Button = itemView.findViewById(R.id.anadir_serie)
        val fecha_estreno: TextView = itemView.findViewById(R.id.fecha_estreno)
        val fecha_fin: TextView = itemView.findViewById(R.id.fecha_finalizacion)
        val productora: TextView = itemView.findViewById(R.id.nombre_productora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductorasSeriesViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_productora_serie, parent, false)
        database = FirebaseDatabase.getInstance().reference
        return ProductorasSeriesViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: ProductorasSeriesViewHolder, position: Int) {
        val item = lista_productoras_series[position]

        when (item) {
            is Serie -> {
                // Mostrar datos de Serie
                holder.nombre.text = item.nombre
                holder.fecha_estreno.text = item.fechaInicio
                holder.fecha_fin.text = item.fechaFin
                holder.productora.visibility = View.GONE // No mostrar la productora

                // Cargar imagen de la serie
                val URL = if (item.url_imagen.isNullOrEmpty()) null else item.url_imagen
                Glide.with(contexto)
                    .load(URL)
                    .apply(Util.opcionesGlide(contexto))
                    .transition(Util.transicion)
                    .into(holder.miniatura)

                // Acción para añadir la serie
                holder.anadirSerie.setOnClickListener {
                    Toast.makeText(contexto, "Se ha añadido la serie ${item.nombre}", Toast.LENGTH_SHORT).show()
                    // Aquí podrías agregar la lógica de añadir la serie a una lista de la productora
                }

            }
            is Productora -> {
                // Mostrar datos de Productora
                holder.nombre.text = item.nombre
                holder.fecha_estreno.text = item.anhoFundacion
                holder.fecha_fin.visibility = View.GONE // No mostrar fecha de finalización en la productora
                holder.anadirSerie.visibility = View.GONE // No mostrar botón de añadir serie

                // Mostrar nombre de la productora
                holder.productora.text = item.nombre


            }
        }
    }

    override fun getItemCount(): Int = lista_productoras_series.size
}