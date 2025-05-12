package com.example.gestortareas.fragmentos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestortareas.R
import com.example.gestortareas.adaptadores.EstadisticasAdapter
import com.example.gestortareas.basedatos.TareasDBHelper

// Fragmento que muestra estadísticas generales sobre las tareas
class EstadisticasFragment : Fragment() {

    // Referencias a vistas del fragmento
    private lateinit var tvTotalTareas: TextView
    private lateinit var tvTareasPendientes: TextView
    private lateinit var tvTareasCompletadas: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EstadisticasAdapter
    private lateinit var dbHelper: TareasDBHelper

    // Infla la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadisticas, container, false)

        // Inicializa las referencias a las vistas
        tvTotalTareas = view.findViewById(R.id.tv_total_tareas)
        tvTareasPendientes = view.findViewById(R.id.tv_tareas_pendientes)
        tvTareasCompletadas = view.findViewById(R.id.tv_tareas_completadas)
        recyclerView = view.findViewById(R.id.recycler_estadisticas)

        inicializarRecyclerView()

        return view
    }

    // Cuando el fragmento vuelve a ser visible, recargamos los datos
    override fun onResume() {
        super.onResume()
        cargarEstadisticas()
    }

    // Configura el RecyclerView con su adaptador
    private fun inicializarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = EstadisticasAdapter()
        recyclerView.adapter = adapter
    }

    // Carga las estadísticas desde la base de datos y actualiza la interfaz
    private fun cargarEstadisticas() {
        dbHelper = TareasDBHelper(requireContext())

        // Obtiene contadores de tareas
        val totalTareas = dbHelper.contarTotalTareas()
        val tareasPendientes = dbHelper.contarTareasPendientes()
        val tareasCompletadas = dbHelper.contarTareasCompletadas()

        // Actualiza los TextViews con los datos obtenidos
        tvTotalTareas.text = "Total tareas: $totalTareas"
        tvTareasPendientes.text = "Tareas pendientes: $tareasPendientes"
        tvTareasCompletadas.text = "Tareas completadas: $tareasCompletadas"

        // Carga las 5 tareas completadas más recientes para mostrarlas
        val tareasRecientes = dbHelper.obtenerTareasRecientemente(5)
        adapter.actualizarTareas(tareasRecientes)
    }
}