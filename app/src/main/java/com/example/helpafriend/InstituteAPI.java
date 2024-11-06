package com.example.helpafriend;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface InstituteAPI {
    @GET("emergencyHotline.php") // Make sure this matches your actual endpoint URL
    Call<List<Institute>> getInstitutes();
}
