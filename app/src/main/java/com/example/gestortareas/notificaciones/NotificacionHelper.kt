package com.example.gestortareas.notificaciones

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.gestortareas.modelos.Tarea

class NotificacionHelper(private val context: Context) {

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Programa una notificación para una tarea
    fun programarNotificacion(tarea: Tarea) {
        // Si no hay recordatorio o la tarea ya está completada, no programamos nada
        if (tarea.tipoRecordatorio == 0 || tarea.completada == 1) {
            return
        }

        // Calculamos el tiempo para la notificación según el tipo de recordatorio
        val tiempo = calcularTiempoRecordatorio(tarea.fechaLimite, tarea.tipoRecordatorio)

        // Creamos intent para el receptor de alarma
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TAREA_ID, tarea.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tarea.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // A partir de Android 12 (S), necesitamos verificar el permiso SCHEDULE_EXACT_ALARM
        if (Build.VERSION.SDK_INT >= 31) { // Build.VERSION_CODES.S = 31
            if (alarmManager.canScheduleExactAlarms()) {
                // Usamos setAlarmClock que es exacto y muestra un icono en la barra de estado
                val infoIntent = PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, context.javaClass),
                    PendingIntent.FLAG_IMMUTABLE
                )

                val alarmClockInfo = AlarmManager.AlarmClockInfo(tiempo, infoIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            } else {
                // Sin permiso para alarmas exactas, usamos una inexacta
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tiempo,
                    pendingIntent
                )
            }
        } else {
            // Para versiones anteriores a Android 12
            // Usamos setAndAllowWhileIdle que funciona bien incluso en modo Doze
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                tiempo,
                pendingIntent
            )
        }
    }

    // Verifica si tenemos permiso para programar alarmas exactas en Android 12+
    fun verificarPermisoAlarmasExactas(): Boolean {
        if (Build.VERSION.SDK_INT >= 31) { // Build.VERSION_CODES.S = 31
            if (!alarmManager.canScheduleExactAlarms()) {
                // Si no tenemos permiso, redirigimos a la configuración
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
                return false
            }
        }
        return true
    }

    // Cancela una notificación programada
    fun cancelarNotificacion(tareaId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tareaId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    // Calcula el tiempo para el recordatorio según el tipo
    private fun calcularTiempoRecordatorio(fechaLimite: Long, tipoRecordatorio: Int): Long {
        val tiempoRecordatorio = when (tipoRecordatorio) {
            1 -> 5 * 60 * 1000 // 5 minutos antes
            2 -> 15 * 60 * 1000 // 15 minutos antes
            3 -> 30 * 60 * 1000 // 30 minutos antes
            4 -> 60 * 60 * 1000 // 1 hora antes
            5 -> 2 * 60 * 60 * 1000 // 2 horas antes
            6 -> 24 * 60 * 60 * 1000 // 1 día antes
            else -> 0
        }

        return fechaLimite - tiempoRecordatorio
    }
}