package com.example.gestortareas.utilidades

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Objeto singleton para formatear fechas y horas
object DateTimeUtils {

    // Formatos predefinidos para fechas y horas
    private val formatoFechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())

    // Convierte timestamp a formato fecha y hora (dd/MM/yyyy HH:mm)
    fun formatoFechaHora(timestamp: Long): String {
        return formatoFechaHora.format(Date(timestamp))
    }

    // Convierte timestamp a formato solo fecha (dd/MM/yyyy)
    fun formatoFecha(timestamp: Long): String {
        return formatoFecha.format(Date(timestamp))
    }

    // Convierte timestamp a formato solo hora (HH:mm)
    fun formatoHora(timestamp: Long): String {
        return formatoHora.format(Date(timestamp))
    }
}