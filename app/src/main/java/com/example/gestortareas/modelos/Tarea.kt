package com.example.gestortareas.modelos

// La clase Tarea define la estructura de datos para guardar la información de cada tarea
data class Tarea(
    var id: Long,                   // Identificador único de la tarea
    val titulo: String,             // Título de la tarea
    val descripcion: String,        // Descripción detallada de la tarea
    val fechaLimite: Long,          // Fecha límite en milisegundos (timestamp)
    val tipoRecordatorio: Int,      // Tipo de recordatorio: 0: sin recordatorio, 1: 5min, 2: 15min, 3: 30min, 4: 1h, 5: 2h, 6: 1día
    var completada: Int,            // Estado: 0: pendiente, 1: completada
    var fechaCompletada: Long       // Fecha de completado en milisegundos (0 si no está completada)
)