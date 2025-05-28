package com.example.proyecsql;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {

    private List<Nota> listaNotas;
    private OnNotaEliminarListener eliminarListener;

    // Interfaz para eliminar
    public interface OnNotaEliminarListener {
        void onEliminarClick(Nota nota);
    }

    public NotaAdapter(List<Nota> listaNotas, OnNotaEliminarListener listener) {
        this.listaNotas = listaNotas;
        this.eliminarListener = listener;
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_nota, parent, false);
        return new NotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        Nota nota = listaNotas.get(position);
        holder.textViewNotaTexto.setText(nota.getTexto());

        holder.buttonEliminar.setOnClickListener(v -> {
            if (eliminarListener != null) {
                eliminarListener.onEliminarClick(nota);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    public static class NotaViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNotaTexto;
        public Button buttonEliminar;

        public NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNotaTexto = itemView.findViewById(R.id.textViewNotaTexto);
            buttonEliminar = itemView.findViewById(R.id.buttonEliminar);
        }
    }
}
