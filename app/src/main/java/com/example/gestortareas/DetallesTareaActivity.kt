package com.example.gestortareas

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gestortareas.basedatos.TareasDBHelper
import com.example.gestortareas.modelos.Tarea
import com.example.gestortareas.notificaciones.NotificacionHelper
import com.example.gestortareas.utilidades.DateTimeUtils
import java.util.Calendar

class DetallesTareaActivity : AppCompatActivity() {

    // Variables para los elementos de la interfaz
    private lateinit var tvTitulo: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvFechaHora: TextView
    private lateinit var tvRecordatorio: TextView
    private lateinit var btnMarcarCompletada: Button
    private lateinit var btnEliminar: Button

    // Variables para gestionar la tarea
    private var tareaId: Long = 0
    private lateinit var tarea: Tarea
    private lateinit var dbHelper: TareasDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles_tarea)

        // Obtenemos el ID de la tarea desde el intent
        tareaId = intent.getLongExtra("TAREA_ID", -1)
        if (tareaId == -1L) {
            // Si no hay ID válido, mostramos error y cerramos la actividad
            Toast.makeText(this, "Error: Tarea no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inicializamos los componentes y cargamos los datos
        inicializarVistas()
        cargarDatosTarea()
        configurarListeners()
    }

    // Enlazamos las vistas del layout con las variables
    private fun inicializarVistas() {
        tvTitulo = findViewById(R.id.tv_titulo_tarea)
        tvDescripcion = findViewById(R.id.tv_descripcion_tarea)
        tvFechaHora = findViewById(R.id.tv_fecha_hora_tarea)
        tvRecordatorio = findViewById(R.id.tv_recordatorio_tarea)
        btnMarcarCompletada = findViewById(R.id.btn_marcar_completada)
        btnEliminar = findViewById(R.id.btn_eliminar_tarea)
    }

    // Cargamos los datos de la tarea desde la base de datos
    private fun cargarDatosTarea() {
        dbHelper = TareasDBHelper(this)
        tarea = dbHelper.obtenerTareaPorId(tareaId)

        // Mostramos los datos en la interfaz
        tvTitulo.text = tarea.titulo
        tvDescripcion.text = tarea.descripcion
        tvFechaHora.text = DateTimeUtils.formatoFechaHora(tarea.fechaLimite)

        // Configuramos el texto del recordatorio según el tipo
        val opcionesRecordatorio = resources.getStringArray(R.array.opciones_recordatorio)
        tvRecordatorio.text = opcionesRecordatorio[tarea.tipoRecordatorio]

        // Cambiamos el texto del botón según el estado de la tarea
        if (tarea.completada == 1) {
            btnMarcarCompletada.text = "Marcar como pendiente"
        } else {
            btnMarcarCompletada.text = "Marcar como completada"
        }
    }

    // Configuramos los listeners de los botones
    private fun configurarListeners() {
        btnMarcarCompletada.setOnClickListener {
            cambiarEstadoTarea()
        }

        btnEliminar.setOnClickListener {
            confirmarEliminarTarea()
        }
    }

    // Cambia el estado de la tarea entre completada y pendiente
    private fun cambiarEstadoTarea() {
        if (tarea.completada == 0) {
            // Si está pendiente, la marcamos como completada
            tarea.completada = 1
            tarea.fechaCompletada = Calendar.getInstance().timeInMillis

            // Cancelamos la notificación si existe
            if (tarea.tipoRecordatorio > 0) {
                val notificacionHelper = NotificacionHelper(this)
                notificacionHelper.cancelarNotificacion(tarea.id)
            }

            Toast.makeText(this, "Tarea marcada como completada", Toast.LENGTH_SHORT).show()
        } else {
            // Si está completada, la marcamos como pendiente
            tarea.completada = 0
            tarea.fechaCompletada = 0

            // Reprogramamos el recordatorio si es necesario y la fecha es futura
            if (tarea.tipoRecordatorio > 0 && tarea.fechaLimite > Calendar.getInstance().timeInMillis) {
                val notificacionHelper = NotificacionHelper(this)
                notificacionHelper.programarNotificacion(tarea)
            }

            Toast.makeText(this, "Tarea marcada como pendiente", Toast.LENGTH_SHORT).show()
        }

        // Actualizamos la tarea en la base de datos y la interfaz
        dbHelper.actualizarTarea(tarea)
        cargarDatosTarea()
    }

    // Muestra diálogo de confirmación antes de eliminar
    private fun confirmarEliminarTarea() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar tarea")
            .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarTarea()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Elimina la tarea y cierra la actividad
    private fun eliminarTarea() {
        // Cancelamos la notificación si existe
        if (tarea.tipoRecordatorio > 0) {
            val notificacionHelper = NotificacionHelper(this)
            notificacionHelper.cancelarNotificacion(tarea.id)
        }

        // Eliminamos la tarea de la base de datos
        dbHelper.eliminarTarea(tarea.id)
        Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show()
        finish()
    }
}