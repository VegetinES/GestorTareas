package com.example.gestortareas.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.gestortareas.DetallesTareaActivity
import com.example.gestortareas.basedatos.TareasDBHelper

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CANAL_ID = "canal_recordatorios"
        const val EXTRA_TAREA_ID = "extra_tarea_id"
    }

    // Se ejecuta cuando se recibe la alarma
    override fun onReceive(context: Context, intent: Intent) {
        val tareaId = intent.getLongExtra(EXTRA_TAREA_ID, -1)

        if (tareaId != -1L) {
            // Obtenemos los detalles de la tarea
            val dbHelper = TareasDBHelper(context)
            val tarea = dbHelper.obtenerTareaPorId(tareaId)

            // Solo mostramos notificación si la tarea sigue pendiente
            if (tarea.completada == 0) {
                mostrarNotificacion(context, tarea.id, tarea.titulo, tarea.descripcion)
            }
        }
    }

    // Muestra la notificación del recordatorio
    private fun mostrarNotificacion(context: Context, tareaId: Long, titulo: String, descripcion: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Creamos canal de notificación para Android Oreo y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Recordatorios de tareas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Canal para recordatorios de tareas pendientes"
            }
            notificationManager.createNotificationChannel(canal)
        }

        // Intent para abrir los detalles de la tarea al pulsar la notificación
        val intent = Intent(context, DetallesTareaActivity::class.java).apply {
            putExtra("TAREA_ID", tareaId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            tareaId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construimos la notificación
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Recordatorio: $titulo")
            .setContentText(descripcion)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostramos la notificación
        notificationManager.notify(tareaId.toInt(), notificacion)
    }
}