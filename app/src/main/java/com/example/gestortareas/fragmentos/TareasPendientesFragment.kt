package com.example.gestortareas.fragmentos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestortareas.DetallesTareaActivity
import com.example.gestortareas.R
import com.example.gestortareas.adaptadores.TareaAdapter
import com.example.gestortareas.basedatos.TareasDBHelper
import com.example.gestortareas.modelos.Tarea

// Fragmento que muestra la lista de tareas pendientes
class TareasPendientesFragment : Fragment() {

    // Referencias a las vistas del fragmento
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvSinTareas: TextView
    private lateinit var adapter: TareaAdapter
    private lateinit var dbHelper: TareasDBHelper

    // Infla la vista del fragmento
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tareas_pendientes, container, false)

        // Inicializa las referencias a las vistas
        recyclerView = view.findViewById(R.id.recycler_tareas_pendientes)
        tvSinTareas = view.findViewById(R.id.tv_sin_tareas_pendientes)

        inicializarRecyclerView()

        return view
    }

    // Cuando el fragmento vuelve a ser visible, recargamos los datos
    override fun onResume() {
        super.onResume()
        cargarTareasPendientes()
    }

    // Configura el RecyclerView con su adaptador y el evento de clic
    private fun inicializarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TareaAdapter { tarea ->
            abrirDetallesTarea(tarea)
        }
        recyclerView.adapter = adapter
    }

    // Carga las tareas pendientes desde la base de datos
    private fun cargarTareasPendientes() {
        dbHelper = TareasDBHelper(requireContext())
        val tareas = dbHelper.obtenerTareasPendientes()

        // Muestra un mensaje si no hay tareas pendientes
        if (tareas.isEmpty()) {
            tvSinTareas.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvSinTareas.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.actualizarTareas(tareas)
        }
    }

    // Abre la pantalla de detalles para la tarea seleccionada
    private fun abrirDetallesTarea(tarea: Tarea) {
        val intent = Intent(activity, DetallesTareaActivity::class.java)
        intent.putExtra("TAREA_ID", tarea.id)
        startActivity(intent)
    }
}