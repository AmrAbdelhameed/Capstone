package com.example.amr.capstone.RetrofitAPIs;

import com.example.amr.capstone.Models.MainResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("books/v1/volumes")
    Call<MainResponse> getMovies(@Query("q") String q, @Query("printType") String printType);
}