package com.example.firabasecrud

import android.annotation.SuppressLint
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditarClub : AppCompatActivity() {

    private lateinit var escudo: ImageView
    private lateinit var nombre: EditText
    private lateinit var ciudad: EditText
    private lateinit var fundacion: EditText
    private lateinit var boton_modificar: Button
    private lateinit var boton_volver: Button

    private lateinit var database: DatabaseReference
    //private lateinit var storage: StorageReference
    private var url_escudo: Uri? = null
    private lateinit var club: Club
    private lateinit var lista_clubs: MutableList<Club>

    //AppWriteStorage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_club)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        escudo = findViewById(R.id.escudo)
        nombre = findViewById(R.id.nombre)
        ciudad = findViewById(R.id.ciudad)
        fundacion = findViewById(R.id.fundacion)
        boton_modificar = findViewById(R.id.boton_modificar)
        boton_volver = findViewById(R.id.boton_volver)
        database = FirebaseDatabase.getInstance().reference
        //storage = FirebaseStorage.getInstance().reference

        club = intent.getSerializableExtra("club") as Club

        nombre.setText(club.nombre)
        ciudad.setText(club.ciudad)
        fundacion.setText(club.fundacion.toString())


        id_projecto = "674762dd002af7924291"
        id_bucket = "674762fb002a63512c24"

        val client = Client()
            .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
            .setProject(id_projecto)

        val storage = Storage(client)

        var activity = this


        Glide.with(applicationContext)
            .load(club.url_escudo)
            .apply(Util.opcionesGlide(applicationContext))
            .transition(Util.transicion)
            .into(escudo)



        escudo.setOnClickListener {
            accesoGaleria.launch("image/*")
        }

        lista_clubs = Util.obtenerListaCLubs(database, this)
        boton_modificar.setOnClickListener {
            if (nombre.text.isEmpty() || ciudad.text.isEmpty()
                || fundacion.text.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Rellene todos los campos o selecione una imagen",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (fundacion.text.toString().toInt() > 2023 || fundacion.text.toString()
                    .toInt() < 1500
            ) {
                Toast.makeText(this, "Año de fundación no válido", Toast.LENGTH_SHORT).show()
            } else if (Util.existeClub(
                    lista_clubs,
                    nombre.text.toString()
                ) && nombre.text.toString().lowercase() != club.nombre!!.lowercase()
            ) {
                Toast.makeText(this, "Club ya existe", Toast.LENGTH_SHORT).show()
            } else {

                val identificador_club = club.id

                GlobalScope.launch(Dispatchers.IO) {

                    if (url_escudo != null){
                        Log.d("URL",url_escudo.toString())
                        //var escudo = Util.guardarEscudo(storage,identificador_club!!,url_escudo!!)
                        val identificadorFile = ID.unique()
                        Log.d("IDDDDD",identificadorFile)
                        Log.d("IDANTIGUA",club.id_escudo!!)
                        storage.deleteFile(
                            bucketId = id_bucket,
                            fileId = club.id_escudo!!
                        )
                        val inputStream = contentResolver.openInputStream(url_escudo!!)
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
                        club.id_escudo = identificadorFile

                        var escudo =
                            "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto"
                        club.url_escudo = escudo
                    }else if (nombre.text.toString()!=club.nombre || ciudad.text.toString()!=club.ciudad
                        || fundacion.text.toString().toInt()!=club.fundacion!!){
                        val club = Club(
                            identificador_club,
                            nombre.text.toString(),
                            ciudad.text.toString(),
                            fundacion.text.toString().toInt(),
                            club.url_escudo,
                            club.id_escudo
                        )
                        Util.escribirClub(database, identificador_club!!, club)

                        Util.tostadaCorrutina(activity,applicationContext,
                            "Club actualizado con éxito")
                    }
                }
                finish()
            }

        }

        boton_volver.setOnClickListener {
            finish()
        }


    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            url_escudo = uri
            escudo.setImageURI(url_escudo)
        }
    }
}