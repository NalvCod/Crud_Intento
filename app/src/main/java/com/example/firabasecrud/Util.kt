package com.example.firabasecrud

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.Data
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.firabasecrud.actor.Actor
import com.example.firabasecrud.actor.EditarActor
import com.example.firabasecrud.productoras.Productora
import com.example.firabasecrud.series.Serie
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class Util {
    companion object {

        fun existeSerie(series: List<Serie>, nombre: String): Boolean {
            return series.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }

        fun existeActor(actores: List<Actor>, nombre:String): Boolean{
            return actores.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }

        fun obtenerListaSeries(db_ref: DatabaseReference, contexto: Context): MutableList<Serie> {
            val lista_series = mutableListOf<Serie>()

            db_ref.child("series").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { serie ->
                        val serie_act = serie.getValue(Serie::class.java)
                        lista_series.add(serie_act!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(contexto, "Error al obtener las series", Toast.LENGTH_SHORT)
                        .show()
                }

            })
            return lista_series
        }

        fun escribirSerie(db_ref: DatabaseReference, id: String, serie: Serie) {
            db_ref.child("series").child(id).setValue(serie)
        }

        fun introducir_nombre_usuario(db_ref: DatabaseReference, nombre:String){
            var id = db_ref.child("nombre_usuario").push().key
            db_ref.child("nombre_usuario").child(id!!).setValue(nombre)

        }

        fun escribirActor(db_ref: DatabaseReference, id: String, actor: Actor) {
            db_ref.child("actores").child(id).setValue(actor)
        }

        //PRODUCTORA
        fun existeProductora(productoras: List<Productora>, nombre: String): Boolean {
            return productoras.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }

        fun obtenerListaProductoras(db_ref: DatabaseReference, contexto: Context): MutableList<Productora> {
            val lista_productoras = mutableListOf<Productora>()

            db_ref.child("productoras").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { productora ->
                        val productora_act = productora.getValue(Productora::class.java)
                        productora_act?.let {
                            lista_productoras.add(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(contexto, "Error al obtener las productoras", Toast.LENGTH_SHORT).show()
                }
            })
            return lista_productoras
        }

        fun escribirProductora(db_ref: DatabaseReference, id: String, productora: Productora) {
            db_ref.child("productoras").child(id).setValue(productora)
        }

        suspend fun guardarImagen(almacen: StorageReference, id: String, escudo: Uri): String {
            var urlAlmacen: Uri
            urlAlmacen =
                almacen.child("imagen").child(id).putFile(escudo).await()
                    .storage.downloadUrl.await()

            return urlAlmacen.toString()
        }

        fun toastCorrutina(activity: AppCompatActivity, contexto: Context, texto: String) {
            activity.runOnUiThread {
                Toast.makeText(contexto, texto, Toast.LENGTH_SHORT).show()
            }
        }

        fun animacion_carga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()

            return animacion
        }

        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.fotogaleria)
                .error(R.drawable.error_404)
            return options
        }

        fun obtenerListaActores(db_ref: DatabaseReference, contexto: EditarActor): MutableList<Actor> {
            val lista_actores = mutableListOf<Actor>()

            db_ref.child("series").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { serie ->
                        val actor = serie.getValue(Actor::class.java)
                        lista_actores.add(actor!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(contexto, "Error al obtener los actores", Toast.LENGTH_SHORT)
                        .show()
                }

            })
            return lista_actores


        }


    }
}