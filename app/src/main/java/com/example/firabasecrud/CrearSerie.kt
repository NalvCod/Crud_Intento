package com.example.firabasecrud

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CrearSerie : AppCompatActivity() {
    private lateinit var nombre : EditText
    private lateinit var fechaInicio : EditText
    private lateinit var fechaFin : EditText
    private lateinit var genero : EditText
    private lateinit var anadir : Button
    private lateinit var volver : ImageView
    private lateinit var anadirImagen : ImageView

    //Firebase
    private lateinit var database: DatabaseReference

    //private lateinit var storage: StorageReference
    private var url_imagen: Uri? = null

    //AppWriteStorage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_serie)

        nombre = findViewById(R.id.nombre)
        fechaInicio = findViewById(R.id.fecha_inicio)
        fechaFin = findViewById(R.id.fecha_fin)
        genero = findViewById(R.id.genero)
        anadir = findViewById(R.id.modificar)
        volver = findViewById(R.id.volver)
        anadirImagen = findViewById(R.id.anadir_imagen)

        //firebase
        database = FirebaseDatabase.getInstance().reference

        //AppWriteStorage
        id_projecto ="675b02ce0004439f752e"
        id_bucket = "675b02ce0004439f752e"

        val client = Client().setEndpoint("https://cloud.appwrite.io/v1").setProject(id_projecto)
        val storage = Storage(client)

        anadirImagen.setOnClickListener{
            accesoGaleria.launch("image/*")
        }
        var lista_series = Util.obtenerListaSeries(database, this)
        anadir.setOnClickListener {
            //Comprobamos que los datos introducidos sean válidos

            //En caso de que esté algo vacío
            if (nombre.text.isEmpty() || fechaInicio.text.isEmpty()
                || fechaFin.text.isEmpty() || genero.text.isEmpty() || url_imagen == null){
                Toast.makeText(this, "Rellena todos los campos o selecione una imagen", Toast.LENGTH_SHORT).show()
            } else if (fechaInicio.text.toString().toInt() > 2024 || fechaInicio.text.toString().toInt() < 1900 || fechaFin.text.toString()
                    .toInt() < fechaInicio.text.toString().toInt()) {
                Toast.makeText(this, "Año de inicio no válido", Toast.LENGTH_SHORT).show()
            } else if (Util.existeSerie(lista_series, nombre.text.toString())) {
                Toast.makeText(this, "La serie ya existe", Toast.LENGTH_SHORT).show()
            } else {

                val identificador_serie = database.child("null").child("series").push().key

                GlobalScope.launch(Dispatchers.IO) {
                    val inputStream = contentResolver.openInputStream(url_imagen!!)
                    val identificadorAppWrite = identificador_serie!!.substring(1, 20)
                    //var escudo = Util.guardarEscudo(storage,identificador_club!!,url_escudo!!)
                    val fileImpostor = inputStream.use { input ->
                        val tempFile = kotlin.io.path.createTempFile(identificadorAppWrite).toFile()
                        if (input != null) {
                            tempFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        InputFile.fromFile(tempFile) // tenemos un archivo temporal con la imagen
                    }
                    val identificadorFile = ID.unique()
                    val file = storage.createFile(
                        bucketId = id_bucket,
                        fileId = identificadorFile,
                        file = fileImpostor,
                    )

                    var imagen = "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto"

                    val serie = Serie(
                        identificador_serie,
                        nombre.text.toString(),
                        fechaInicio.text.toString(),
                        fechaFin.text.toString(),
                        imagen,
                        identificadorFile
                    )
                    Util.escribirSerie(database, identificador_serie, serie)

                    Util.toastCorrutina(
                        this@CrearSerie, applicationContext,
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
            anadirImagen.setImageURI(url_imagen)
        }
    }
}

