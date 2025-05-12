package com.example.gestortareas.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestortareas.R
import com.example.gestortareas.modelos.Tarea
import com.example.gestortareas.utilidades.DateTimeUtils

// Adaptador para mostrar la lista de tareas (tanto pendientes como completadas)
class TareaAdapter(private val onTareaClick: (Tarea) -> Unit) :
    RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    // Lista de tareas que muestra el adaptador
    private var tareas: List<Tarea> = emptyList()

    // ViewHolder que contiene las vistas para cada elemento
    class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tv_titulo_tarea)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tv_descripcion_tarea)
        val tvFechaHora: TextView = itemView.findViewById(R.id.tv_fecha_hora_tarea)
    }

    // Crea un nuevo ViewHolder cuando se necesita
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    // Vincula los datos de una tarea a un ViewHolder
    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]

        // Asigna el título, descripción y fecha límite a las vistas
        holder.tvTitulo.text = tarea.titulo
        holder.tvDescripcion.text = tarea.descripcion
        holder.tvFechaHora.text = DateTimeUtils.formatoFechaHora(tarea.fechaLimite)

        // Configura el evento de clic en el elemento para abrir los detalles
        holder.itemView.setOnClickListener {
            onTareaClick(tarea)
        }
    }

    // Devuelve el número total de elementos
    override fun getItemCount(): Int = tareas.size

    // Actualiza la lista de tareas y notifica al adaptador
    fun actualizarTareas(nuevasTareas: List<Tarea>) {
        tareas = nuevasTareas
        notifyDataSetChanged()
    }
}