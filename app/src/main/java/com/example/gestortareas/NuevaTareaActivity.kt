package com.example.gestortareas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gestortareas.basedatos.TareasDBHelper
import com.example.gestortareas.modelos.Tarea
import com.example.gestortareas.notificaciones.NotificacionHelper
import com.example.gestortareas.utilidades.DateTimeUtils
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class NuevaTareaActivity : AppCompatActivity() {

    // Variables para los elementos de la interfaz
    private lateinit var etTitulo: TextInputEditText
    private lateinit var etDescripcion: TextInputEditText
    private lateinit var btnFecha: Button
    private lateinit var btnHora: Button
    private lateinit var tvFechaHora: TextView
    private lateinit var spinnerRecordatorio: Spinner
    private lateinit var btnGuardar: Button

    // Variables para manejar la fecha y hora
    private val calendario = Calendar.getInstance()
    private var fechaSeleccionada = false
    private var horaSeleccionada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_tarea)

        // Verificar permiso de alarmas exactas al iniciar
        val notificacionHelper = NotificacionHelper(this)
        notificacionHelper.verificarPermisoAlarmasExactas()

        inicializarVistas()
        configurarSpinnerRecordatorio()
        configurarListeners()
    }

    // Enlazamos las vistas del layout con las variables
    private fun inicializarVistas() {
        etTitulo = findViewById(R.id.et_titulo_tarea)
        etDescripcion = findViewById(R.id.et_descripcion_tarea)
        btnFecha = findViewById(R.id.btn_fecha)
        btnHora = findViewById(R.id.btn_hora)
        tvFechaHora = findViewById(R.id.tv_fecha_hora)
        spinnerRecordatorio = findViewById(R.id.spinner_recordatorio)
        btnGuardar = findViewById(R.id.btn_guardar_tarea)
    }

    // Configuramos el spinner con las opciones de recordatorio
    private fun configurarSpinnerRecordatorio() {
        val opciones = resources.getStringArray(R.array.opciones_recordatorio)
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRecordatorio.adapter = adaptador
    }

    // Configuramos los listeners de los botones
    private fun configurarListeners() {
        btnFecha.setOnClickListener {
            mostrarSelectorFecha()
        }

        btnHora.setOnClickListener {
            mostrarSelectorHora()
        }

        btnGuardar.setOnClickListener {
            guardarTarea()
        }
    }

    // Muestra el selector de fecha
    private fun mostrarSelectorFecha() {
        val year = calendario.get(Calendar.YEAR)
        val month = calendario.get(Calendar.MONTH)
        val day = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Guardamos la fecha seleccionada en el calendario
                calendario.set(Calendar.YEAR, selectedYear)
                calendario.set(Calendar.MONTH, selectedMonth)
                calendario.set(Calendar.DAY_OF_MONTH, selectedDay)
                fechaSeleccionada = true
                actualizarTextoFechaHora()
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    // Muestra el selector de hora
    private fun mostrarSelectorHora() {
        val hour = calendario.get(Calendar.HOUR_OF_DAY)
        val minute = calendario.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Guardamos la hora seleccionada en el calendario
                calendario.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendario.set(Calendar.MINUTE, selectedMinute)
                calendario.set(Calendar.SECOND, 0)
                horaSeleccionada = true
                actualizarTextoFechaHora()
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    // Actualiza el texto que muestra la fecha y hora seleccionadas
    private fun actualizarTextoFechaHora() {
        if (fechaSeleccionada && horaSeleccionada) {
            tvFechaHora.text = DateTimeUtils.formatoFechaHora(calendario.timeInMillis)
        } else if (fechaSeleccionada) {
            tvFechaHora.text = "Fecha: ${DateTimeUtils.formatoFecha(calendario.timeInMillis)}"
        } else if (horaSeleccionada) {
            tvFechaHora.text = "Hora: ${DateTimeUtils.formatoHora(calendario.timeInMillis)}"
        }
    }

    // Guarda la tarea en la base de datos
    private fun guardarTarea() {
        val titulo = etTitulo.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()

        // Validamos que el título no esté vacío
        if (titulo.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        // Validamos que se haya seleccionado fecha y hora
        if (!fechaSeleccionada || !horaSeleccionada) {
            Toast.makeText(this, "Debes seleccionar fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        val tiempoLimite = calendario.timeInMillis
        val posicionRecordatorio = spinnerRecordatorio.selectedItemPosition

        // Creamos el objeto tarea
        val tarea = Tarea(
            id = 0, // Se asignará automáticamente
            titulo = titulo,
            descripcion = descripcion,
            fechaLimite = tiempoLimite,
            tipoRecordatorio = posicionRecordatorio,
            completada = 0,
            fechaCompletada = 0
        )

        // Guardamos la tarea en la base de datos
        val db = TareasDBHelper(this)
        val id = db.insertarTarea(tarea)

        if (id > 0) {
            tarea.id = id

            // Si hay recordatorio, lo programamos
            if (posicionRecordatorio > 0) {
                programarRecordatorio(tarea)
            }

            Toast.makeText(this, "Tarea guardada correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar la tarea", Toast.LENGTH_SHORT).show()
        }
    }

    // Programa la notificación para el recordatorio
    private fun programarRecordatorio(tarea: Tarea) {
        val notificacionHelper = NotificacionHelper(this)
        notificacionHelper.programarNotificacion(tarea)
    }
}