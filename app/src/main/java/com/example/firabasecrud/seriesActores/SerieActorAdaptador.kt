package com.example.firabasecrud.seriesActores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.R
import com.example.firabasecrud.actor.Actor

class SerieActorAdaptador {

    private lateinit var contexto: Context
    private lateinit var actorActual: Actor

    inner class SerieActorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val miniatura: ImageView = itemView.findViewById(R.id.foto_perfil)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val fecha_estreno: TextView = itemView.findViewById(R.id.fecha_estreno)

    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieActorViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_actor, parent, false)
        contexto = parent.context
        return SerieActorViewHolder(vista_item)

    }


}