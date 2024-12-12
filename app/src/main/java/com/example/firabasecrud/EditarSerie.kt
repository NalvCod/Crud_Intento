package com.example.firabasecrud

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditarSerie : AppCompatActivity() {

    private lateinit var nombre: EditText
    private lateinit var fecha_inicio: EditText
    private lateinit var fecha_fin: EditText
    private lateinit var genero: EditText
    private lateinit var modificar: Button
    private lateinit var anadirImagen: ImageView
    private lateinit var volver : Button

    //Firebase
    private lateinit var database: DatabaseReference

    //private lateinit var storage: StorageReference
    private var url_imagen: Uri? = null
    private lateinit var serie: Serie
    private lateinit var lista_series: MutableList<Serie>

    //AppWriteStorage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    private lateinit var imagen: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_serie)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nombre = findViewById(R.id.nombre)
        fecha_inicio = findViewById(R.id.fecha_inicio)
        fecha_fin = findViewById(R.id.fecha_fin)
        genero = findViewById(R.id.genero)
        modificar = findViewById(R.id.modificar)
        anadirImagen = findViewById(R.id.anadir_imagen)
        volver = findViewById(R.id.volver)

        var serie = intent.getSerializableExtra("serie") as Serie

        nombre.setText(serie.nombre)
        fecha_inicio.setText(serie.fechaInicio)
        fecha_fin.setText(serie.fechaFin)

        id_projecto = "675b02ce0004439f752e"
        id_bucket = "675b02ce0004439f752e"

        val client = Client()
            .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
            .setProject(id_projecto)

        val storage = Storage(client)

        var activity = this


        Glide.with(applicationContext).load(serie.url_imagen)
            .apply(Util.opcionesGlide(applicationContext)).transition(Util.transicion)
            .into(anadirImagen)



        anadirImagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }

        lista_series = Util.obtenerListaSeries(database, this)
        modificar.setOnClickListener {
            if (nombre.text.isEmpty() || fecha_inicio.text.isEmpty()
                || fecha_fin.text.isEmpty() || genero.text.isEmpty() || url_imagen == null
            ) {
                Toast.makeText(
                    this,
                    "RellenA todos los campos o selecione una imagen",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (fecha_inicio.text.toString().toInt() > 2023 || fecha_inicio.text.toString()
                    .toInt() < 1900 || fecha_fin.text.toString()
                    .toInt() < 2025
            ) {
                Toast.makeText(this, "Año de fundación no válido", Toast.LENGTH_SHORT).show()
            } else if (Util.existeSerie(
                    lista_series,
                    nombre.text.toString()
                )
            ) {
                Toast.makeText(this, "Club ya existe", Toast.LENGTH_SHORT).show()
            } else {

                val identificador_serie = database.child("nba").child("clubs").push().key

                GlobalScope.launch(Dispatchers.IO) {

                    if (url_imagen != null) {
                        Log.d("URL", url_imagen.toString())
                        //var escudo = Util.guardarEscudo(storage,identificador_club!!,url_escudo!!)
                        val identificadorFile = ID.unique()
                        Log.d("IDDDDD", identificadorFile)
                        Log.d("IDANTIGUA", serie.id_imagen!!)
                        storage.deleteFile(
                            bucketId = id_bucket,
                            fileId = serie.id_imagen!!
                        )
                        val inputStream = contentResolver.openInputStream(url_imagen!!)
                        val fileImpostor = inputStream.use { input ->
                            val tempFile = kotlin.io.path.createTempFile(identificadorFile).toFile()
                            if (input != null) {
                                tempFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            InputFile.fromFile(tempFile) // tenemos un archivo temporal con la imagen
                        }

                        val file = storage.createFile(
                            bucketId = id_bucket,
                            fileId = identificadorFile,
                            file = fileImpostor,
                        )
                        serie.id_imagen = identificadorFile

                        imagen =
                            "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto"
                        serie.url_imagen = imagen
                    } else if (nombre.text.toString() != serie.nombre ||
                        fecha_inicio.text.toString().toInt() != serie.fechaInicio?.toInt() ||
                        fecha_fin.text.toString().toInt() != serie.fechaFin?.toInt()
                    ) {
                        val serie = Serie(
                            identificador_serie,
                            nombre.text.toString(),
                            fecha_inicio.text.toString(),
                            fecha_fin.text.toString(),
                            imagen,
                            serie.id_imagen
                        )
                        Util.escribirSerie(database, identificador_serie!!, serie)

                        Util.toastCorrutina(
                            this@EditarSerie, applicationContext,
                            "Imagen descargada con éxito"
                        )
                    }
                    finish()
                }

            }

            volver.setOnClickListener {
                finish()
            }


        }

    }
    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            url_imagen = uri
            anadirImagen.setImageURI(url_imagen)
        }
    }
}