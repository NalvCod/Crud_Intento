package com.example.firabasecrud

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class UtilBackup {
    companion object {

        fun existeClub(clubs: List<Club>, nombre: String): Boolean {
            return clubs.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }


        fun obtenerListaCLubs(db_ref: DatabaseReference, contexto: Context): MutableList<Club> {
            val lista_clubs = mutableListOf<Club>()

            db_ref.child("nba").child("clubs")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { club ->
                            val club_actual = club.getValue(Club::class.java)
                            lista_clubs.add(club_actual!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(contexto, "Error al obtener los clubs", Toast.LENGTH_SHORT)
                            .show()
                    }

                })
            return lista_clubs
        }

        fun escribirClub(db_ref: DatabaseReference, id: String, club: Club) {
            db_ref.child("nba").child("clubs").child(id).setValue(club)
        }

        //LO CAMBIAREMOS
        suspend fun guardarEscudo(almacen: StorageReference, id: String, escudo: Uri): String {
            var urlAlmacen: Uri
            urlAlmacen =
                almacen.child("escudos").child(id).putFile(escudo).await()
                    .storage.downloadUrl.await()

            return urlAlmacen.toString()
        }

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto: String){
            activity.runOnUiThread{
                Toast.makeText(contexto,texto, Toast.LENGTH_SHORT).show()
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
    }
}
