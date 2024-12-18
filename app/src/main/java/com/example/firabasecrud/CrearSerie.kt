package com.example.firabasecrud

import android.media.Rating
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
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
    private lateinit var puntuacion : RatingBar

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
        puntuacion = findViewById(R.id.puntuacion)

        //firebase
        database = FirebaseDatabase.getInstance().reference

        //AppWriteStorage
        id_projecto ="675b01650006c779a329"
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
            } else if (fechaFin.text.toString().toInt() < fechaInicio.text.toString().toInt()){
                Toast.makeText(this, "Año de fin no válido", Toast.LENGTH_SHORT).show()
            }else if (fechaInicio.text.toString().toInt() > 2024 || fechaInicio.text.toString().toInt() < 1900) {
                Toast.makeText(this, "Año de inicio no válido", Toast.LENGTH_SHORT).show()
            } else if (Util.existeSerie(lista_series, nombre.text.toString())) {
                Toast.makeText(this, "La serie ya existe", Toast.LENGTH_SHORT).show()
            } else {

                val identificador_serie = database.child("null").child("series").push().key

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

                    var imagen = "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto&output=jpg"

                    val serie = Serie(
                        identificador_serie,
                        nombre.text.toString(),
                        fechaInicio.text.toString(),
                        fechaFin.text.toString(),
                        genero.text.toString(),
                        imagen,
                        identificadorFile,
                        puntuacion.rating
                    )
                    Util.escribirSerie(database, identificador_serie.toString(), serie)

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

