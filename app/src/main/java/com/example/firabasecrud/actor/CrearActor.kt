package com.example.firabasecrud.actor

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firabasecrud.R
import com.example.firabasecrud.Util
import com.example.firabasecrud.databinding.ActivityCrearActorBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CrearActor : AppCompatActivity() {

    //binding
    private lateinit var binding: ActivityCrearActorBinding

    //Firebase
    private lateinit var database: DatabaseReference

    //private lateinit var storage: StorageReference
    private var url_imagen: Uri? = null

    //AppWriteStorage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_actor)

        binding = ActivityCrearActorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //firebase
        database = FirebaseDatabase.getInstance().reference

        //AppWriteStorage
        id_projecto = "675b01650006c779a329"
        id_bucket = "675b02ce0004439f752e"

        val client = Client().setEndpoint("https://cloud.appwrite.io/v1").setProject(id_projecto)
        val storage = Storage(client)

        var lista_actores = Util.obtenerListaSeries(database, this)

        binding.anadirImagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }

        binding.anadirActor.setOnClickListener {
            //En caso de que esté algo vacío
            if (binding.nombre.text.isEmpty() || binding.fechaNacimiento.text.isEmpty() || url_imagen == null) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else if (!(binding.fechaNacimiento.text.toString().substring(0, 4)
                    .toInt() in 1901..2024
            )) {
                Toast.makeText(this, "Año de nacimiento no válido", Toast.LENGTH_SHORT).show()
            } else if (Util.existeSerie(lista_actores, binding.nombre.text.toString())) {
                Toast.makeText(this, "La serie ya existe", Toast.LENGTH_SHORT).show()
            }
            else{
                //Creamos el actor
                val identificador_actor = database.child("null").child("actores").push().key

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

                    var imagen =
                        "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto&output=jpg"

                    val actor = Actor(
                        identificador_actor,
                        binding.nombre.text.toString(),
                        binding.fechaNacimiento.text.toString(),
                        imagen,
                        identificadorFile
                    )
                    Util.escribirActor(database, identificador_actor.toString(), actor)


                    Util.toastCorrutina(
                        this@CrearActor, applicationContext,
                        "Imagen descargada con éxito"
                    )
                }
                finish()
            }
        }
    }
    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            url_imagen = uri
            binding.anadirImagen.setImageURI(url_imagen)
        }
    }
}










