package com.example.gestortareas.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gestortareas.basedatos.TareasDBHelper
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {

    // Se ejecuta cuando se inicia el dispositivo
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reprogramamos las notificaciones al reiniciar el dispositivo
            reprogramarNotificaciones(context)
        }
    }

    // Reprograma todas las notificaciones pendientes
    private fun reprogramarNotificaciones(context: Context) {
        val dbHelper = TareasDBHelper(context)
        val tareasPendientes = dbHelper.obtenerTareasPendientes()
        val notificacionHelper = NotificacionHelper(context)
        val ahora = Calendar.getInstance().timeInMillis

        // Solo reprogramamos las tareas pendientes con fecha futura
        for (tarea in tareasPendientes) {
            if (tarea.tipoRecordatorio > 0 && tarea.fechaLimite > ahora) {
                notificacionHelper.programarNotificacion(tarea)
            }
        }
    }
}