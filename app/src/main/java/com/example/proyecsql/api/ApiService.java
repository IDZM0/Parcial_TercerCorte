package com.example.proyecsql.api;

import com.example.proyecsql.Nota;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/notas")
    Call<List<Nota>> obtenerNotas();

    @POST("/notas")
    Call<Nota> crearNota(@Body Nota nota);

    @PUT("/notas/{id}")
    Call<Nota> actualizarNota(@Path("id") int id, @Body Nota nota);

    @DELETE("/notas/{id}")
    Call<Void> eliminarNota(@Path("id") int id);
}