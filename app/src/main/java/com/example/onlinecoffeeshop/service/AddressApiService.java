package com.example.onlinecoffeeshop.service;

import com.example.onlinecoffeeshop.model.ProvinceResponse;
import com.example.onlinecoffeeshop.model.CommuneResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AddressApiService {

    @GET("address-kit/2025-07-01/provinces")
    Call<ProvinceResponse> getProvinces();

    @GET("address-kit/2025-07-01/provinces/{provinceCode}/communes")
    Call<CommuneResponse> getCommunes(@Path("provinceCode") String provinceCode);
}
