package com.example.proyecsql;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecsql.api.ApiClient;
import com.example.proyecsql.api.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostrarNotasFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotaAdapter adapter;
    private NotasDatabaseHelper dbHelper;
    private List<Nota> listaNotas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mostrar_notas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNotas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new NotasDatabaseHelper(getContext());

        adapter = new NotaAdapter(listaNotas, new NotaAdapter.OnNotaEliminarListener() {
            @Override
            public void onEliminarClick(Nota nota) {
                ApiService api = ApiClient.getApiService();
                api.eliminarNota(nota.getId()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            eliminarNotaLocalmente(nota);
                        } else {
                            Toast.makeText(getContext(), "Error al eliminar en el servidor", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Servidor no disponible. Se eliminará localmente.", Toast.LENGTH_SHORT).show();
                        Log.e("ELIMINAR", "Error: " + t.getMessage());
                        eliminarNotaLocalmente(nota);
                    }
                });
            }

            private void eliminarNotaLocalmente(Nota nota) {
                int filas = dbHelper.eliminarNotaPorId(nota.getId());
                if (filas > 0) {
                    listaNotas.remove(nota);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Nota eliminada localmente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar localmente", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);

        Button btnVolver = view.findViewById(R.id.buttonVolverNotas);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        sincronizarNotasDesdeServidor();

        return view;
    }

    private void sincronizarNotasDesdeServidor() {
        ApiService api = ApiClient.getApiService();
        api.obtenerNotas().enqueue(new Callback<List<Nota>>() {
            @Override
            public void onResponse(Call<List<Nota>> call, Response<List<Nota>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Nota> notasServidor = response.body();

                    dbHelper.getWritableDatabase().delete(NotasDatabaseHelper.TABLE_NOTAS, null, null);
                    for (Nota nota : notasServidor) {
                        dbHelper.insertarNotaSinSincronizar(nota.getId(), nota.getTexto());
                    }

                    listaNotas.clear();
                    listaNotas.addAll(dbHelper.getAllNotas());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al sincronizar (código: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Nota>> call, Throwable t) {
                Toast.makeText(getContext(), "No se pudo conectar al servidor", Toast.LENGTH_SHORT).show();
                Log.e("SYNC", "Fallo: " + t.getMessage());
            }
        });
    }
}
