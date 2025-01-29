package com.example.firabasecrud.productoras

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firabasecrud.R
import com.example.firabasecrud.Util
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProductoraAdaptador(private val lista_productoras: MutableList<Productora>) : RecyclerView.Adapter<ProductoraAdaptador.ProductoraViewHolder>() {
    private lateinit var contexto: Context
    private var lista_filtrada = lista_productoras

    inner class ProductoraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.item_nombre) // Nombre de la productora
        val anhoFundacion: TextView = itemView.findViewById(R.id.fecha_estreno) // Año de fundación
        val editar: ImageView = itemView.findViewById(R.id.editar) // Botón de editar
        val borrar: ImageView = itemView.findViewById(R.id.borrar) // Botón de borrar
        val anadirSerie: Button = itemView.findViewById(R.id.add_series_actor) // Añadir serie
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoraViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_productora, parent, false)
        contexto = parent.context
        return ProductoraViewHolder(vista_item)
    }

    override fun getItemCount() = lista_filtrada.size

    override fun onBindViewHolder(holder: ProductoraViewHolder, position: Int) {
        val productora_actual = lista_filtrada[position]
        holder.nombre.text = productora_actual.nombre
        holder.anhoFundacion.text = productora_actual.anhoFundacion

        holder.editar.setOnClickListener {
            val intent = Intent(contexto, EditarProductora::class.java)
            intent.putExtra("productora actual", productora_actual)
            contexto.startActivity(intent)
        }

        holder.anadirSerie.setOnClickListener{
            Log.v("Ver", "Se ha enviado la productora ${productora_actual.nombre}")
            val intent = Intent(contexto, VerProductoras::class.java)
            intent.putExtra("productora actual", productora_actual)
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
                // Aquí puede que necesites ajustar si la productora tiene imágenes
                // storage.deleteFile(
                //     bucketId = id_bucket,
                //     fileId = productora_actual.id_imagen!!  // Si existe un campo para imagen en Productora
                // )
            }

            lista_filtrada.removeAt(position)
            db_ref.child("com/example/firabasecrud/productora").child(productora_actual.id!!).removeValue()
            Toast.makeText(contexto, "Productora eliminada", Toast.LENGTH_SHORT).show()
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, lista_filtrada.size)
        }
    }
}