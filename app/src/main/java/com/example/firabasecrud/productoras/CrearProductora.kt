package com.example.firabasecrud.productoras

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firabasecrud.R
import com.example.firabasecrud.Util
import com.example.firabasecrud.databinding.ActivityCrearProductoraBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CrearProductora : AppCompatActivity() {

    //binding
    private lateinit var binding: ActivityCrearProductoraBinding

    //Firebase
    private lateinit var database: DatabaseReference

    //AppWriteStorage
    private var url_imagen: Uri? = null
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_productora)

        binding = ActivityCrearProductoraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //firebase
        database = FirebaseDatabase.getInstance().reference

        //AppWriteStorage
        id_projecto = "675b01650006c779a329"
        id_bucket = "675b02ce0004439f752e"

        val client = Client().setEndpoint("https://cloud.appwrite.io/v1").setProject(id_projecto)
        val storage = Storage(client)



        binding.anadirProductora.setOnClickListener {
            // En caso de que algún campo esté vacío
            if (binding.nombre.text.isEmpty() || binding.anhoFundacion.text.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else if (!(binding.anhoFundacion.text.toString().substring(0, 4)
                    .toInt() in 1901..2024
                        )) {
                Toast.makeText(this, "Año de fundación no válido", Toast.LENGTH_SHORT).show()
            } else {
                // Creamos la productora
                val identificador_productora = database.child("null").child("productoras").push().key

                GlobalScope.launch(Dispatchers.IO) {


                    val productora = Productora(
                        identificador_productora,
                        binding.nombre.text.toString(),
                        binding.anhoFundacion.text.toString()
                    )
                    Util.escribirProductora(database, identificador_productora.toString(), productora)

                    Util.toastCorrutina(
                        this@CrearProductora, applicationContext,
                        "Productora creada con éxito"
                    )
                }
                finish()
            }
        }
    }
}