package com.example.firabasecrud.productorasSeries

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firabasecrud.databinding.ActivityProductoraDetalleBinding
import com.example.firabasecrud.productoras.Productora
import com.example.firabasecrud.productoras.VerProductoras
import com.example.firabasecrud.series.Serie
import com.example.firabasecrud.series.SerieAdaptador

class ProductoraDetalle : AppCompatActivity() {
    // binding
    private lateinit var binding: ActivityProductoraDetalleBinding
    private lateinit var productoraActual: Productora
    private lateinit var adaptador: SerieAdaptador
    private var listaSeries: MutableList<Serie> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding
        binding = ActivityProductoraDetalleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar la productora
        productoraActual = intent.getSerializableExtra("productora actual") as Productora

        // Configuración del RecyclerView
        val recyclerView = binding.recyclerSeries // Asegúrate de que el id sea el correcto
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configurar el adaptador y asignarlo al RecyclerView
        adaptador = SerieAdaptador(listaSeries)
        recyclerView.adapter = adaptador

        // Añadir un OnClickListener para el botón "volver"
        binding.volver.setOnClickListener {
            val intent = Intent(this, VerProductoras::class.java)
            startActivity(intent)
        }

        // Mostrar el nombre de la productora
        binding.titulo.text = productoraActual.nombre

        // Rellenar la lista de series de la productora y actualizar el RecyclerView
        cargarSeriesDeProductora()
    }

    // Método para cargar las series de la productora
    private fun cargarSeriesDeProductora() {
        // Extraer las series de la productora (almacenadas como un String de nombres)
        val nombresSeries = productoraActual.series.split(",")

        // Convertir los nombres de las series en objetos de tipo Serie
        listaSeries = obtenerSeriesDeNombres(nombresSeries)

        // Notificar al adaptador que la lista ha cambiado
        adaptador.notifyDataSetChanged() // Actualiza el RecyclerView con las series de la productora
    }

    // Método para obtener las series a partir de los nombres
    private fun obtenerSeriesDeNombres(nombres: List<String>): MutableList<Serie> {
        val series = mutableListOf<Serie>()

        // Este es un ejemplo de cómo podrías buscar la serie en tu base de datos o en una lista local
        // Este método debería tomar los nombres de las series y buscarlas en tu base de datos (o de donde las recuperes)
        for (nombre in nombres) {
            // Aquí puedes hacer una búsqueda para obtener la serie por su nombre, si tienes una base de datos
            // Ejemplo ficticio de cómo crear una Serie con el nombre:
            val serie = Serie(nombre = nombre) // Asegúrate de que Serie tiene un constructor adecuado
            series.add(serie)
        }

        return series
    }
}