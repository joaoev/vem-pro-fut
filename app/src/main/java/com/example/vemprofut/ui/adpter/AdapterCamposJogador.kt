package com.example.vemprofut.ui.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.vemprofut.R
import com.example.vemprofut.model.Campo

class AdapterCamposJogador(
    private val camposCadastradosJogador : ArrayList<Campo>,
    val campoSelecionado: (String) -> Unit
): RecyclerView.Adapter<AdapterCamposJogador.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeCampo : TextView = itemView.findViewById(R.id.txtCardCampoNome)
        val precoHora : TextView = itemView.findViewById(R.id.txtCardCampoPrecoHora)
        val endereco : TextView = itemView.findViewById(R.id.txtCardCampoEndereco)
        val imagem: ImageView = itemView.findViewById(R.id.imgCardCampo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterCamposJogador.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_campo, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterCamposJogador.MyViewHolder, position: Int) {
        val campo = camposCadastradosJogador[position]

        holder.nomeCampo.text = campo.local_name
        var preco = campo.hourly_rate.toString()
        holder.precoHora.text = "R$ ${preco}"
        holder.endereco.text = campo.address
        holder.imagem.load(campo.url_image){
            placeholder(R.drawable.add_image_bg_square)
            error(R.drawable.add_image_bg_square)
        }

        holder.itemView.setOnClickListener(){
            campoSelecionado(campo.id)
        }
    }

    override fun getItemCount() = camposCadastradosJogador.size
}