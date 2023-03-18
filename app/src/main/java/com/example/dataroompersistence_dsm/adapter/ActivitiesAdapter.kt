package com.example.dataroompersistence_dsm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.dataroompersistence_dsm.R
import com.example.dataroompersistence_dsm.model.Activities
import java.text.SimpleDateFormat
import java.util.*

class ActivitiesAdapter(var activitiesList: MutableList<Activities>,
                        val listener: AdapterListener ):
    RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_activity, parent, false)
        return ViewHolder(view)
    }



    fun actualizarLista(nuevaLista: MutableList<Activities>) {
        activitiesList=nuevaLista
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = activitiesList[position]
        holder.titulo.text=activity.title
        holder.descrip.text=activity.description
        holder.lugar.text=activity.location
        // convertir fecha a texto en formato "dd/MM/yyyy"
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaTexto = formatter.format(activity.date)

        holder.fecha.text = fechaTexto
        holder.horas.text=activity.hours.toString()

        holder.cvTitulo.setOnClickListener {
            listener.onEditItemClick(activity)
        }
        holder.btnBorrar.setOnClickListener {
             listener.onDeleteItemClick(activity)
        }

    }

    override fun getItemCount(): Int {
        return activitiesList.size
    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cvTitulo = itemView.findViewById<CardView>(R.id.cvTitulo)
        val titulo = itemView.findViewById<TextView>(R.id.titulo)
        val descrip = itemView.findViewById<TextView>(R.id.descripcion)
        val fecha = itemView.findViewById<TextView>(R.id.fecha)
        val horas = itemView.findViewById<TextView>(R.id.horas)
        val lugar = itemView.findViewById<TextView>(R.id.lugar)
        val btnBorrar = itemView.findViewById<Button>(R.id.btnBorrar)

    }

}