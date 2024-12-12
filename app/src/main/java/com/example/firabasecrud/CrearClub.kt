package com.example.firabasecrud

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope


import kotlinx.coroutines.launch


class CrearClub : AppCompatActivity() {


//    private lateinit var nombre: EditText
//    private lateinit var ciudad: EditText
//    private lateinit var fundacion: EditText
//    private lateinit var escudo: ImageView
//    private lateinit var botonCrear: Button
//    private lateinit var botonVolver: Button
//
//    //Firebase
//    private lateinit var database: DatabaseReference
//
//    //private lateinit var storage: StorageReference
//    private var url_escudo: Uri? = null
//
//    //AppWriteStorage
//    private lateinit var id_projecto: String
//    private lateinit var id_bucket: String
//
//
//    @SuppressLint("NewApi")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_crear_club)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        var activity = this
//
//        nombre = findViewById(R.id.nombre)
//        ciudad = findViewById(R.id.ciudad)
//        fundacion = findViewById(R.id.fundacion)
//        escudo = findViewById(R.id.escudo)
//        botonCrear = findViewById(R.id.boton_crear)
//        botonVolver = findViewById(R.id.boton_volver)
//
//        //firebase
//        database = FirebaseDatabase.getInstance().reference
//        //storage = FirebaseStorage.getInstance().reference
//
//        //AppWriteStorage
//        id_projecto = "675b02ce0004439f752e"
//        id_bucket = "675b02ce0004439f752e"
//
//        val client = Client()
//            .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
//            .setProject(id_projecto)
//
//        val storage = Storage(client)
//
//        escudo.setOnClickListener {
//            accesoGaleria.launch("image/*")
//        }
//        var lista_series = Util.obtenerListaSeries(database, this)
//        botonCrear.setOnClickListener {
//
//            if (nombre.text.isEmpty() || ciudad.text.isEmpty()
//                || fundacion.text.isEmpty() || url_escudo == null
//            ) {
//                Toast.makeText(
//                    this,
//                    "Rellene todos los campos o selecione una imagen",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else if (fundacion.text.toString().toInt() > 2023 || fundacion.text.toString()
//                    .toInt() < 1500
//            ) {
//                Toast.makeText(this, "Año de fundación no válido", Toast.LENGTH_SHORT).show()
//            } else if (Util.existeSerie(
//                    lista_series,
//                    nombre.text.toString()
//                )
//            ) {
//                Toast.makeText(this, "Club ya existe", Toast.LENGTH_SHORT).show()
//            } else {
//
//                val identificador_club = database.child("nba").child("clubs").push().key
//
//                GlobalScope.launch(Dispatchers.IO) {
//                    val inputStream = contentResolver.openInputStream(url_escudo!!)
//                    val identificadorAppWrite = identificador_club!!.substring(1, 20)
//                    //var escudo = Util.guardarEscudo(storage,identificador_club!!,url_escudo!!)
//                    val fileImpostor = inputStream.use { input ->
//                        val tempFile = kotlin.io.path.createTempFile(identificadorAppWrite).toFile()
//                        if (input != null) {
//                            tempFile.outputStream().use { output ->
//                                input.copyTo(output)
//                            }
//                        }
//                        InputFile.fromFile(tempFile) // tenemos un archivo temporal con la imagen
//                    }
//                    val identificadorFile = ID.unique()
//                    val file = storage.createFile(
//                        bucketId = id_bucket,
//                        fileId = identificadorFile,
//                        file = fileImpostor,
//                    )
//
//                    var escudo =
//                        "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto"
//
//                    val club = Club(
//                        identificador_club,
//                        nombre.text.toString(),
//                        ciudad.text.toString(),
//                        fundacion.text.toString().toInt(),
//                        escudo,
//                        identificadorFile
//                    )
//                    Util.escribirSerie(database, identificador_club, club)
//
//                    Util.toastCorrutina(
//                        activity, applicationContext,
//                        "Escudo descargado con éxito"
//                    )
//                }
//                finish()
//            }
//
//
//        }
//        botonVolver.setOnClickListener {
//            finish()
//        }
//
//    }
//
//    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
//    { uri: Uri? ->
//        if (uri != null) {
//            url_escudo = uri
//            escudo.setImageURI(url_escudo)
//        }
//    }
}



