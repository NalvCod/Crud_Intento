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

class ProductorasSeriesAdapter (private val lista_series: MutableList<Serie>, private val productoraActual: Productora) : RecyclerView.Adapter<ProductorasSeriesAdapter.ProductorasSeriesViewHolder>(){

    private lateinit var contexto: Context
    private lateinit var database: DatabaseReference
    private var lista_filtrada = lista_series

    inner class ProductorasSeriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val anadirSerie : Button = itemView.findViewById(R.id.anadir_serie)
        val fecha_estreno: TextView = itemView.findViewById(R.id.fecha_estreno)
        val fecha_fin: TextView = itemView.findViewById(R.id.fecha_finalizacion)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductorasSeriesViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_serie_actor, parent, false)
        contexto = parent.context
        database = FirebaseDatabase.getInstance().reference
        return ProductorasSeriesViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: ProductorasSeriesViewHolder, position: Int) {
        val serie_actual = lista_filtrada[position]
        holder.nombre.text = serie_actual.nombre
        holder.fecha_estreno.text = serie_actual.fechaInicio
        holder.fecha_fin.text = serie_actual.fechaFin
        database = FirebaseDatabase.getInstance().reference

        val URL:String?=when(serie_actual.url_imagen){
            ""->null //Para que active imagen de fallback
            else->serie_actual.url_imagen
        }
        Log.d("URL",URL.toString())

        Glide.with(contexto)
            .load(URL)
            .apply(Util.opcionesGlide(contexto))
            .transition(Util.transicion)
            .into(holder.miniatura)

        holder.anadirSerie.setOnClickListener {
            productoraActual.series += lista_filtrada[position].nombre+","
            Util.escribirProductora(database, productoraActual.id!!, productoraActual)
            Toast.makeText(contexto,"Se ha añadido "+serie_actual.nombre+" a "+productoraActual.nombre, Toast.LENGTH_SHORT).show()
        }


    }

    override fun getItemCount() = lista_filtrada.size
}