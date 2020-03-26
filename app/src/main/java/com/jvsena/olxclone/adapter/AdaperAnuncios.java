package com.jvsena.olxclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvsena.olxclone.R;
import com.jvsena.olxclone.model.Anuncio;

import java.util.List;

public class AdaperAnuncios extends RecyclerView.Adapter<AdaperAnuncios.MyViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;

    public AdaperAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio,parent,false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

       TextView titulo;
       TextView valor;
       TextView foto;

       public MyViewHolder(View itemView){
           super(itemView);

           titulo = itemView.findViewById(R.id.textTitulo);
           valor = itemView.findViewById(R.id.textValor);
           foto = itemView.findViewById(R.id.imageAnuncio);
       }
   }
}
