package com.example.firabasecrud.productoras

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.firabasecrud.R
import com.example.firabasecrud.Util
import com.example.firabasecrud.databinding.ActivityEditarActorBinding
import com.example.firabasecrud.databinding.ActivityEditarProductoraBinding
import com.example.firabasecrud.series.VerSeries
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditarProductora : AppCompatActivity() {

    private lateinit var binding: ActivityEditarProductoraBinding
    private lateinit var database: DatabaseReference
    private var url_imagen: Uri? = null
    private lateinit var productora: Productora
    private var lista_productoras: MutableList<Productora> = mutableListOf()

    // AppWriteStorage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarProductoraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = FirebaseDatabase.getInstance().reference
        productora = intent.getSerializableExtra("productora actual") as Productora
        // Asignar valores de la productora a los campos
        binding.nombre.setText(productora.nombre)
        binding.anhoFundacion.setText(productora.anhoFundacion)

        id_projecto = "675b02ce0004439f752e"
        id_bucket = "675b02ce0004439f752e"

        // Configurar botón Volver
        binding.volver.setOnClickListener {
            val intent = Intent(this, VerProductoras::class.java)
            startActivity(intent)
        }

        val client = Client()
            .setEndpoint("https://cloud.appwrite.io/v1")    // Tu API Endpoint
            .setProject(id_projecto)
        val storage = Storage(client)


        lista_productoras = Util.obtenerListaProductoras(database, this)

        binding.modificarProductora.setOnClickListener {
            // En caso de que esté algo vacío
            if (binding.nombre.text.isEmpty() || binding.anhoFundacion.text.isEmpty() || url_imagen == null) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else if (binding.anhoFundacion.text.toString().toInt() < 1900 || binding.anhoFundacion.text.toString().toInt() > 2024) {
                Toast.makeText(this, "Año de fundación no válido", Toast.LENGTH_SHORT).show()
            } else if (Util.existeProductora(lista_productoras, binding.nombre.text.toString())) {
                Toast.makeText(this, "La productora ya existe", Toast.LENGTH_SHORT).show()
            } else {
                // Creamos la productora
                val identificador_productora = database.child("null").child("productoras").push().key

                GlobalScope.launch(Dispatchers.IO) {
                    var mimeType = ""
                    var nombreArchivo = ""
                    val inputStream = contentResolver.openInputStream(url_imagen!!)
                    val aux = contentResolver.query(url_imagen!!, null, null, null, null)
                    aux.use {
                        if (it!!.moveToFirst()) {
                            // Obtener el nombre del archivo
                            val nombreIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (nombreIndex != -1) {
                                nombreArchivo = it.getString(nombreIndex)
                            }
                        }
                    }
                    mimeType = contentResolver.getType(url_imagen!!).toString()

                    val fileInput = InputFile.fromBytes(
                        bytes = inputStream?.readBytes() ?: byteArrayOf(),
                        filename = nombreArchivo,
                        mimeType = mimeType
                    )

                    val identificadorFile = ID.unique()
                    val file = storage.createFile(
                        bucketId = id_bucket,
                        fileId = identificadorFile,
                        file = fileInput,
                    )
                    Log.d("ESTOY AQUI", "HOLAAAAA")

                    var imagen =
                        "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto&output=jpg"

                    val productora = Productora(
                        identificador_productora,
                        binding.nombre.text.toString(),
                        binding.anhoFundacion.text.toString(),
                    )
                    Util.escribirProductora(database, identificador_productora.toString(), productora)

                    Util.toastCorrutina(
                        this@EditarProductora, applicationContext,
                        "Imagen descargada con éxito"
                    )
                }
                finish()
            }
        }
    }
}