package com.example.vemprofut

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val emplist: ArrayList<CampoData>, private val itemClickListener: Adapter.OnItemClickListener?)
    : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(campoId: Int, userId: Int)
    }
    // This class defines the ViewHolder object for each item in the RecyclerView
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeCampo: TextView = itemView.findViewById(R.id.tvNomeCampo)
        val precoHora: TextView = itemView.findViewById(R.id.tvPrecoHora)
        val endereco: TextView = itemView.findViewById(R.id.tvEnderecoCampo)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cards_adapater, parent, false)
        return MyViewHolder(itemView)
    }

    // This method returns the total
    // number of items in the data set
    override fun getItemCount(): Int {
        return emplist.size
    }

    // This method binds the data to the ViewHolder object
    // for each item in the RecyclerView
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentEmp = emplist[position]
        val precoHora = currentEmp.hourly_rate
        holder.nomeCampo.text = currentEmp.local_name
        holder.precoHora.text = "R$ $precoHora"
        holder.endereco.text = currentEmp.address

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(currentEmp.id ?: -1, currentEmp.id_locators)
        }
    }


}