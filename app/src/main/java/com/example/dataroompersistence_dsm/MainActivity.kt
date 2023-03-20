package com.example.dataroompersistence_dsm

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.dataroompersistence_dsm.adapter.ActivitiesAdapter
import com.example.dataroompersistence_dsm.adapter.AdapterListener
import com.example.dataroompersistence_dsm.context.DBContext
import com.example.dataroompersistence_dsm.databinding.ActivityMainBinding
import com.example.dataroompersistence_dsm.model.Activities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class MainActivity : AppCompatActivity() , AdapterListener{


    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ActivitiesAdapter
    private lateinit var room: DBContext
    private var activitiesList: MutableList<Activities> = mutableListOf()
    private lateinit var activities: Activities



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.entryList.layoutManager = LinearLayoutManager(this)
        // Inicializar base de datos
        room = Room.databaseBuilder(this, DBContext::class.java, "activities_database").build()

        // Obtener actividades de la base de datos
        getActivities(room)

        adapter = ActivitiesAdapter(activitiesList, this@MainActivity)
        binding.entryList.adapter = adapter

        //mostrar date picker

        binding.etDate.setOnClickListener{
            mostrarDatePicker()
        }


        //esconder teclado cuando termine de agregar y le de en flechita
      binding.etHoras.setOnEditorActionListener { _, actionId, _ ->

          if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
              val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
              imm.hideSoftInputFromWindow(binding.etHoras.windowToken, 0)
              true
          } else {
              false
          }
      }


     // ocultar el teclado

        binding.root.setOnTouchListener { v, event ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }

        // Configurar botón de agregar nueva entrada
        binding.btnAddUpdate.setOnClickListener {

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken ,0)



            // Verificar que esten llenos
            if (fieldsAreEmpty()) {
                Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(binding.btnAddUpdate.text.equals("agregar")){
                val titulo = binding.etTitulo.text.toString()
                val descrip = binding.etDescrip.text.toString()
                val lugar = binding.etLugar.text.toString()
                val fechaStr = binding.etDate.text.toString()
                val format = SimpleDateFormat("dd-MM-yyyy")
                val fecha = format.parse(fechaStr) ?: Date()

                val hourText = binding.etHoras.text.toString()
                val decimalHour = hourText.toFloat()


                val activity = Activities(0, title = titulo, description = descrip,location = lugar, date = fecha,  hours = decimalHour)
                setActivities(room,activity)


            }
            else if (binding.btnAddUpdate.text.equals("actualizar")){

              activities.title = binding.etTitulo.text.toString()
                activities.description = binding.etDescrip.text.toString()
                activities.location =  binding.etLugar.text.toString()
                val fechaStr = binding.etDate.text.toString()
                val format = SimpleDateFormat("dd-MM-yyyy")
                val fecha = format.parse(fechaStr) ?: Date()
                activities.date=fecha
                Log.d("My tag", "onCreate: ${fecha}")
                activities.hours = binding.etHoras.text.toString().toFloat()
                updateActivity(room,activities)
            
            }
        }

        binding.btnDeleteAll.setOnClickListener {
            deleteAllActivities(room)
        }


    }
    // Verificar si hay EditText vacios
    private fun fieldsAreEmpty(): Boolean {
        return binding.etTitulo.text.isNullOrEmpty() || binding.etLugar.text.isNullOrEmpty() ||
                binding.etDescrip.text.isNullOrEmpty() || binding.etHoras.text.isNullOrEmpty() || binding.etDate.text.isNullOrEmpty()
    }
    // Se confiura el calendario
    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]
        val datePickerDialog = DatePickerDialog(this,
            { view, year, month, dayOfMonth -> // Actualizar el valor del EditText con la fecha seleccionada
                val SelectedDate = dayOfMonth.toString() + "-" + (month + 1) + "-" + year
                binding.etDate.setText(SelectedDate)

            }, year, month, day
        )
        datePickerDialog.show()
    }
    private fun getActivities(room: DBContext){
        lifecycleScope.launch{
            try {
                val activitiesList = withContext(Dispatchers.IO) { // Cambiar al contexto IO para operaciones de base de datos
                    room.activitiesDao().getAllActivities()

                }
                adapter.actualizarLista(activitiesList)

            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error al obtener actividades: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setActivities(room: DBContext, activities: Activities){
        lifecycleScope.launch{
            try {
                    room.activitiesDao().insert(activities)
                    getActivities(room)
                    clearFields()
                Toast.makeText(this@MainActivity, "Actividad agregada ", Toast.LENGTH_LONG).show()



            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error al agregar actividad: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("MyTag", "Activity ID: ${e.message} + ${e.localizedMessage}")
            }
        }
    }
    private fun deleteAllActivities(room: DBContext){
        lifecycleScope.launch {
           try{
            room.activitiesDao().deleteAllActivities()
            getActivities(room)
            Toast.makeText(this@MainActivity, "Todas las actividades se eliminaron ", Toast.LENGTH_LONG).show()
        }
        catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Error al eliminar actividades: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        }
    }

    private fun clearFields(){

        binding.etTitulo.setText("")
        binding.etDescrip.setText("")
        binding.etLugar.setText("")
        binding.etDate.setText("")
        binding.etHoras.setText("")

        if(binding.btnAddUpdate.text.equals("actualizar")){
            binding.btnAddUpdate.setText("agregar")
            binding.etTitulo.isEnabled = true
        }
    }
    private fun updateActivity(room: DBContext, activities: Activities){
        lifecycleScope.launch{
            try {

                Log.d("MyTag", "Activity ID: ${activities.date}")
            room.activitiesDao().updateActivity(activities.title, activities.description, activities.location,activities.date, activities.hours)
            getActivities(room)

            clearFields()
            Toast.makeText(this@MainActivity, "Actividad actualizada ", Toast.LENGTH_LONG).show()
            }
            catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error al actualizar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onEditItemClick(activities: Activities) {

        binding.btnAddUpdate.setText("actualizar")
        binding.etTitulo.isEnabled = false
        this.activities = activities
        binding.etTitulo.setText(activities.title)
        binding.etDescrip.setText(activities.description)
        binding.etLugar.setText(activities.location)


        val format = SimpleDateFormat("dd-MM-yyyy")
        binding.etDate.setText(format.format(activities.date))
        binding.etHoras.setText(activities.hours.toString())

    }

    override fun onDeleteItemClick(activities: Activities) {
        lifecycleScope.launch{
           try {
               room.activitiesDao().deleteActivity(activities.title)
               Log.d("MyTag", "Activity ID: ${activities.id}")
               getActivities(room)
               clearFields()
               Toast.makeText(this@MainActivity, "Actividad eliminada ", Toast.LENGTH_LONG).show()
           }
           catch (e: Exception) {
               Toast.makeText(this@MainActivity, "Error al borrar actividad: ${e.message}", Toast.LENGTH_SHORT).show()
           }
        }
    }

}


