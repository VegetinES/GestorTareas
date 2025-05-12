package com.example.gestortareas.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestortareas.R
import com.example.gestortareas.modelos.Tarea
import com.example.gestortareas.utilidades.DateTimeUtils

// Adaptador para mostrar estadísticas de tareas completadas recientemente
class EstadisticasAdapter : RecyclerView.Adapter<EstadisticasAdapter.EstadisticaViewHolder>() {

    // Lista de tareas que muestra el adaptador
    private var tareas: List<Tarea> = emptyList()

    // ViewHolder que contiene las vistas para cada elemento
    class EstadisticaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tv_titulo_tarea)
        val tvFechaCompletada: TextView = itemView.findViewById(R.id.tv_fecha_completada)
    }

    // Crea un nuevo ViewHolder cuando se necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstadisticaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_estadistica, parent, false)
        return EstadisticaViewHolder(view)
    }

    // Vincula los datos de una tarea a un ViewHolder
    override fun onBindViewHolder(holder: EstadisticaViewHolder, position: Int) {
        val tarea = tareas[position]

        // Asigna el título y fecha de completado a las vistas
        holder.tvTitulo.text = tarea.titulo
        holder.tvFechaCompletada.text = "Completada: ${DateTimeUtils.formatoFechaHora(tarea.fechaCompletada)}"
    }

    // Devuelve el número total de elementos
    override fun getItemCount(): Int = tareas.size

    // Actualiza la lista de tareas y notifica al adaptador
    fun actualizarTareas(nuevasTareas: List<Tarea>) {
        tareas = nuevasTareas
        notifyDataSetChanged()
    }
}