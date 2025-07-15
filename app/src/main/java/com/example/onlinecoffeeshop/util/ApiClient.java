package com.example.onlinecoffeeshop.util;

import com.example.onlinecoffeeshop.service.AddressApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://production.cas.so/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static AddressApiService getAddressApiService() {
        return getClient().create(AddressApiService.class);
    }
}
