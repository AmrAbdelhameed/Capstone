package com.example.amr.capstone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amr.capstone.Models.MainResponse;
import com.example.amr.capstone.RetrofitAPIs.APIService;
import com.example.amr.capstone.RetrofitAPIs.ApiUtils;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {

    APIService mAPIService;
    Gson gson;
    String BooksData = "";
    List<MainResponse.ItemsBean> itemsBeans;
    ProgressDialog pdialog;
    Button bTnSave;
    EditText txtBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FirebaseCrash.log(getString(R.string.activityCreated));

        txtBook = findViewById(R.id.txtBook);
        bTnSave = findViewById(R.id.bTnSave);

        if (savedInstanceState != null)
            txtBook.setText(savedInstanceState.getString("txtBook"));

        mAPIService = ApiUtils.getAPIService();
        gson = new Gson();

        pdialog = new ProgressDialog(StartActivity.this);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage(getString(R.string.plzwait));

        bTnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String BooksType = txtBook.getText().toString();
                if (!BooksType.isEmpty())
                    getBooksGET(BooksType);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
        Boolean SaveData = sharedPreferences.getBoolean("SaveData", false);
        String BooksData = sharedPreferences.getString("BooksData", "");

        if (SaveData) {
            Intent i = new Intent(StartActivity.this, MainActivity.class);
            Bundle b = new Bundle();
            b.putString("BooksData", BooksData);
            i.putExtras(b);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    public void getBooksGET(final String BooksType) {
        pdialog.show();
        mAPIService.getMovies(BooksType, "books").enqueue(new Callback<MainResponse>() {

            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {

                if (response.isSuccessful()) {

                    MainResponse mainResponse = response.body();
                    itemsBeans = mainResponse.getItems();

                    BooksData = gson.toJson(itemsBeans);
                    SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("SaveData", true);
                    editor.putString("BooksType", BooksType);
                    editor.putString("BooksData", BooksData);
                    editor.apply();

                    Intent i = new Intent(StartActivity.this, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putString("BooksData", BooksData);
                    i.putExtras(b);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();

                }
                pdialog.dismiss();
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                Toast.makeText(StartActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();
                pdialog.dismiss();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtBook", txtBook.getText().toString());
    }
}
