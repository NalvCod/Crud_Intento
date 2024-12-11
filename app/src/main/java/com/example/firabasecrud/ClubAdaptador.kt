package com.example.firabasecrud

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ClubAdaptador(private val lista_club: MutableList<Club>) :
    RecyclerView.Adapter<ClubAdaptador.ClubViewHolder>() {

    private lateinit var contexto: Context
    private var lista_filtrada = lista_club

    inner class ClubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val ciudad: TextView = itemView.findViewById(R.id.item_ciudad)
        val fundacion: TextView = itemView.findViewById(R.id.item_fundacion)
        val editar: ImageView = itemView.findViewById(R.id.item_editar)
        val borrar: ImageView = itemView.findViewById(R.id.item_borrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_club,parent,false)
        contexto=parent.context
        return ClubViewHolder(vista_item)
    }

    override fun getItemCount() = lista_filtrada.size

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club_actual = lista_filtrada[position]
        holder.nombre.text = club_actual.nombre
        holder.ciudad.text = club_actual.ciudad
        holder.fundacion.text = club_actual.fundacion.toString()

        val URL:String?=when(club_actual.url_escudo){
            ""->null //Para que active imagen de fallback
            else->club_actual.url_escudo
        }
        Log.d("URL",URL.toString())

        Glide.with(contexto)
            .load(URL)
            .apply(Util.opcionesGlide(contexto))
            .transition(Util.transicion)
            .into(holder.miniatura)

        holder.editar.setOnClickListener{
            val intent= Intent(contexto,EditarClub::class.java)
            intent.putExtra("club",club_actual)
            contexto.startActivity(intent)
        }

        holder.borrar.setOnClickListener{
            val db_ref= FirebaseDatabase.getInstance().reference
            //val storage_ref= FirebaseStorage.getInstance().reference
            val id_projecto = "674762dd002af7924291"
            val id_bucket = "674762fb002a63512c24"

            val client = Client()
                .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
                .setProject(id_projecto)

            val storage = Storage(client)
            GlobalScope.launch(Dispatchers.IO) {
                storage.deleteFile(
                    bucketId = id_bucket,
                    fileId = club_actual.id_escudo!!
                )
            }

            lista_filtrada.removeAt(position)
            //storage_ref.child("nba").child("clubs").child(club_actual.id!!).delete()
            db_ref.child("nba").child("clubs").child(club_actual.id!!).removeValue()
            Toast.makeText(contexto,"Club borrado",Toast.LENGTH_SHORT).show()
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,lista_filtrada.size)

        }

    }


}