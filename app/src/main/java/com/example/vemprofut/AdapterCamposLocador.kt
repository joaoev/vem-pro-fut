package com.example.vemprofut

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterCamposLocador(private val emplist: ArrayList<CampoData>,  private val itemClickListener: OnItemClickListener?) :
    RecyclerView.Adapter<AdapterCamposLocador.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(campoId: Int, userId: Int)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome_campo_locador: TextView = itemView.findViewById(R.id.textViewNomeCampo)
        val preco_hora: TextView = itemView.findViewById(R.id.textViewPrecoHoraCampo)
        val endereco: TextView = itemView.findViewById(R.id.textViewEnderecoCampo)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_campo, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentEmp = emplist[position]
        val precoHora = currentEmp.hourly_rate
        holder.nome_campo_locador.text = currentEmp.local_name
        holder.preco_hora.text = "R$ $precoHora"
        holder.endereco.text = currentEmp.address

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(currentEmp.id ?: -1, currentEmp.id_locators)
        }
    }

    override fun getItemCount(): Int {
        return emplist.size
    }


}