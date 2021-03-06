package com.example.amr.capstone.RetrofitAPIs;

public class ApiUtils {

    public static final String BASE_URL = "https://www.googleapis.com/";

    private ApiUtils() {
    }

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}