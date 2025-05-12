package com.example.gestortareas.basedatos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gestortareas.modelos.Tarea

// Clase auxiliar para gestionar la base de datos SQLite
class TareasDBHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BASE_DATOS, null, VERSION_BASE_DATOS) {

    companion object {
        private const val NOMBRE_BASE_DATOS = "tareas.db"
        private const val VERSION_BASE_DATOS = 1

        // Sentencia SQL para crear la tabla de tareas
        private const val SQL_CREAR_TABLA_TAREAS =
            "CREATE TABLE ${TareasContract.TareaEntry.TABLA_NOMBRE} (" +
                    "${TareasContract.TareaEntry.COLUMNA_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${TareasContract.TareaEntry.COLUMNA_TITULO} TEXT NOT NULL," +
                    "${TareasContract.TareaEntry.COLUMNA_DESCRIPCION} TEXT," +
                    "${TareasContract.TareaEntry.COLUMNA_FECHA_LIMITE} INTEGER," +
                    "${TareasContract.TareaEntry.COLUMNA_TIPO_RECORDATORIO} INTEGER," +
                    "${TareasContract.TareaEntry.COLUMNA_COMPLETADA} INTEGER DEFAULT 0," +
                    "${TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA} INTEGER DEFAULT 0)"
    }

    // Se llama cuando se crea la base de datos por primera vez
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREAR_TABLA_TAREAS)
    }

    // Se llama cuando la base de datos necesita ser actualizada
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${TareasContract.TareaEntry.TABLA_NOMBRE}")
        onCreate(db)
    }

    // Inserta una nueva tarea en la base de datos
    fun insertarTarea(tarea: Tarea): Long {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(TareasContract.TareaEntry.COLUMNA_TITULO, tarea.titulo)
            put(TareasContract.TareaEntry.COLUMNA_DESCRIPCION, tarea.descripcion)
            put(TareasContract.TareaEntry.COLUMNA_FECHA_LIMITE, tarea.fechaLimite)
            put(TareasContract.TareaEntry.COLUMNA_TIPO_RECORDATORIO, tarea.tipoRecordatorio)
            put(TareasContract.TareaEntry.COLUMNA_COMPLETADA, tarea.completada)
            put(TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA, tarea.fechaCompletada)
        }

        return db.insert(TareasContract.TareaEntry.TABLA_NOMBRE, null, values)
    }

    // Actualiza una tarea existente en la base de datos
    fun actualizarTarea(tarea: Tarea): Int {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(TareasContract.TareaEntry.COLUMNA_TITULO, tarea.titulo)
            put(TareasContract.TareaEntry.COLUMNA_DESCRIPCION, tarea.descripcion)
            put(TareasContract.TareaEntry.COLUMNA_FECHA_LIMITE, tarea.fechaLimite)
            put(TareasContract.TareaEntry.COLUMNA_TIPO_RECORDATORIO, tarea.tipoRecordatorio)
            put(TareasContract.TareaEntry.COLUMNA_COMPLETADA, tarea.completada)
            put(TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA, tarea.fechaCompletada)
        }

        val seleccion = "${TareasContract.TareaEntry.COLUMNA_ID} = ?"
        val seleccionArgs = arrayOf(tarea.id.toString())

        return db.update(
            TareasContract.TareaEntry.TABLA_NOMBRE,
            values,
            seleccion,
            seleccionArgs
        )
    }

    // Elimina una tarea de la base de datos por su ID
    fun eliminarTarea(id: Long): Int {
        val db = writableDatabase

        val seleccion = "${TareasContract.TareaEntry.COLUMNA_ID} = ?"
        val seleccionArgs = arrayOf(id.toString())

        return db.delete(
            TareasContract.TareaEntry.TABLA_NOMBRE,
            seleccion,
            seleccionArgs
        )
    }

    // Obtiene una tarea por su ID
    fun obtenerTareaPorId(id: Long): Tarea {
        val db = readableDatabase

        val proyeccion = arrayOf(
            TareasContract.TareaEntry.COLUMNA_ID,
            TareasContract.TareaEntry.COLUMNA_TITULO,
            TareasContract.TareaEntry.COLUMNA_DESCRIPCION,
            TareasContract.TareaEntry.COLUMNA_FECHA_LIMITE,
            TareasContract.TareaEntry.COLUMNA_TIPO_RECORDATORIO,
            TareasContract.TareaEntry.COLUMNA_COMPLETADA,
            TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA
        )

        val seleccion = "${TareasContract.TareaEntry.COLUMNA_ID} = ?"
        val seleccionArgs = arrayOf(id.toString())

        val cursor = db.query(
            TareasContract.TareaEntry.TABLA_NOMBRE,
            proyeccion,
            seleccion,
            seleccionArgs,
            null,
            null,
            null
        )

        cursor.moveToFirst()

        // Crea un objeto Tarea con los datos obtenidos
        val tarea = Tarea(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_ID)),
            titulo = cursor.getString(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_TITULO)),
            descripcion = cursor.getString(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_DESCRIPCION)),
            fechaLimite = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_FECHA_LIMITE)),
            tipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_TIPO_RECORDATORIO)),
            completada = cursor.getInt(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_COMPLETADA)),
            fechaCompletada = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA))
        )

        cursor.close()
        return tarea
    }

    // Obtiene todas las tareas pendientes
    fun obtenerTareasPendientes(): List<Tarea> {
        val tareas = mutableListOf<Tarea>()
        val db = readableDatabase

        // Consulta las tareas donde completada = 0
        val seleccion = "${TareasContract.TareaEntry.COLUMNA_COMPLETADA} = ?"
        val seleccionArgs = arrayOf("0")
        val ordenamiento = "${TareasContract.TareaEntry.COLUMNA_FECHA_LIMITE} ASC"

        val cursor = db.query(
            TareasContract.TareaEntry.TABLA_NOMBRE,
            null,
            seleccion,
            seleccionArgs,
            null,
            null,
            ordenamiento
        )

        // Recorre el cursor para obtener todas las tareas
        while (cursor.moveToNext()) {
            val tarea = Tarea(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_ID)),
                titulo = cursor.getString(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_TITULO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_DESCRIPCION)),
                fechaLimite = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_FECHA_LIMITE)),
                tipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_TIPO_RECORDATORIO)),
                completada = cursor.getInt(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_COMPLETADA)),
                fechaCompletada = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA))
            )

            tareas.add(tarea)
        }

        cursor.close()
        return tareas
    }

    // Obtiene todas las tareas completadas
    fun obtenerTareasCompletadas(): List<Tarea> {
        val tareas = mutableListOf<Tarea>()
        val db = readableDatabase

        // Consulta las tareas donde completada = 1
        val seleccion = "${TareasContract.TareaEntry.COLUMNA_COMPLETADA} = ?"
        val seleccionArgs = arrayOf("1")
        val ordenamiento = "${TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA} DESC"

        val cursor = db.query(
            TareasContract.TareaEntry.TABLA_NOMBRE,
            null,
            seleccion,
            seleccionArgs,
            null,
            null,
            ordenamiento
        )

        // Recorre el cursor para obtener todas las tareas
        while (cursor.moveToNext()) {
            val tarea = Tarea(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_ID)),
                titulo = cursor.getString(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_TITULO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_DESCRIPCION)),
                fechaLimite = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_FECHA_LIMITE)),
                tipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_TIPO_RECORDATORIO)),
                completada = cursor.getInt(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_COMPLETADA)),
                fechaCompletada = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA))
            )

            tareas.add(tarea)
        }

        cursor.close()
        return tareas
    }

    // Cuenta el número total de tareas
    fun contarTotalTareas(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${TareasContract.TareaEntry.TABLA_NOMBRE}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    // Cuenta el número de tareas pendientes
    fun contarTareasPendientes(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${TareasContract.TareaEntry.TABLA_NOMBRE} WHERE ${TareasContract.TareaEntry.COLUMNA_COMPLETADA} = 0",
            null
        )
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    // Cuenta el número de tareas completadas
    fun contarTareasCompletadas(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${TareasContract.TareaEntry.TABLA_NOMBRE} WHERE ${TareasContract.TareaEntry.COLUMNA_COMPLETADA} = 1",
            null
        )
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    // Obtiene las tareas completadas más recientes (limitadas por la cantidad)
    fun obtenerTareasRecientemente(limite: Int): List<Tarea> {
        val tareas = mutableListOf<Tarea>()
        val db = readableDatabase

        // Consulta las tareas completadas ordenadas por fecha de completado
        val seleccion = "${TareasContract.TareaEntry.COLUMNA_COMPLETADA} = ?"
        val seleccionArgs = arrayOf("1")
        val ordenamiento = "${TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA} DESC"

        val cursor = db.query(
            TareasContract.TareaEntry.TABLA_NOMBRE,
            null,
            seleccion,
            seleccionArgs,
            null,
            null,
            ordenamiento,
            limite.toString()
        )

        // Recorre el cursor para obtener las tareas recientes
        while (cursor.moveToNext()) {
            val tarea = Tarea(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_ID)),
                titulo = cursor.getString(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_TITULO)),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_DESCRIPCION)),
                fechaLimite = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_FECHA_LIMITE)),
                tipoRecordatorio = cursor.getInt(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_TIPO_RECORDATORIO)),
                completada = cursor.getInt(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_COMPLETADA)),
                fechaCompletada = cursor.getLong(cursor.getColumnIndexOrThrow(TareasContract.TareaEntry.COLUMNA_FECHA_COMPLETADA))
            )

            tareas.add(tarea)
        }

        cursor.close()
        return tareas
    }
}