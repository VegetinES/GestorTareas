package com.example.gestortareas

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gestortareas.fragmentos.EstadisticasFragment
import com.example.gestortareas.fragmentos.TareasCompletadasFragment
import com.example.gestortareas.fragmentos.TareasPendientesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuramos la barra de navegación inferior
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            navegar(item)
        }

        // Configuramos el botón flotante para añadir tareas
        val fabNuevaTarea = findViewById<FloatingActionButton>(R.id.fab_nueva_tarea)
        fabNuevaTarea.setOnClickListener {
            val intent = Intent(this, NuevaTareaActivity::class.java)
            startActivity(intent)
        }

        // Cargamos el fragmento inicial de tareas pendientes
        if (savedInstanceState == null) {
            cargarFragmento(TareasPendientesFragment())
            bottomNavigation.selectedItemId = R.id.menu_pendientes
        }
    }

    // Maneja la navegación entre fragmentos
    private fun navegar(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_pendientes -> {
                cargarFragmento(TareasPendientesFragment())
                return true
            }
            R.id.menu_completadas -> {
                cargarFragmento(TareasCompletadasFragment())
                return true
            }
            R.id.menu_estadisticas -> {
                cargarFragmento(EstadisticasFragment())
                return true
            }
        }
        return false
    }

    // Carga un fragmento en el contenedor
    private fun cargarFragmento(fragmento: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragmento)
            .commit()
    }

    // Actualizamos el fragmento actual cuando volvemos a la actividad
    override fun onResume() {
        super.onResume()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction()
                .detach(currentFragment)
                .attach(currentFragment)
                .commit()
        }
    }
}