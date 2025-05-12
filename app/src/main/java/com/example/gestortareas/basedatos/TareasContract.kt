package com.example.gestortareas.basedatos

// Contrato que define la estructura de la base de datos
object TareasContract {

    // Define los nombres de columnas y tabla para tareas
    object TareaEntry {
        const val TABLA_NOMBRE = "tareas"

        const val COLUMNA_ID = "id"                          // ID único de la tarea
        const val COLUMNA_TITULO = "titulo"                  // Título de la tarea
        const val COLUMNA_DESCRIPCION = "descripcion"        // Descripción de la tarea
        const val COLUMNA_FECHA_LIMITE = "fecha_limite"      // Fecha límite (timestamp)
        const val COLUMNA_TIPO_RECORDATORIO = "tipo_recordatorio"    // Tipo de recordatorio
        const val COLUMNA_COMPLETADA = "completada"          // Estado (0=pendiente, 1=completada)
        const val COLUMNA_FECHA_COMPLETADA = "fecha_completada"      // Fecha de completado
    }
}