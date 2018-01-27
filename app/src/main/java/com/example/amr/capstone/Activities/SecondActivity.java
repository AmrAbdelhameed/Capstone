package com.example.amr.capstone.Activities;

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
import com.example.amr.capstone.R;
import com.example.amr.capstone.RetrofitAPIs.APIService;
import com.example.amr.capstone.RetrofitAPIs.ApiUtils;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_second);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FirebaseCrash.log(getString(R.string.activityCreated));

        setTitle(getString(R.string.updateType));

        txtBook = findViewById(R.id.txtBook);
        bTnSave = findViewById(R.id.bTnSave);

        if (savedInstanceState != null)
            txtBook.setText(savedInstanceState.getString("txtBook"));

        mAPIService = ApiUtils.getAPIService();
        gson = new Gson();

        pdialog = new ProgressDialog(SecondActivity.this);
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

    public void getBooksGET(final String BooksType) {
        pdialog.show();
        mAPIService.getMovies(BooksType, getString(R.string.typeFilter)).enqueue(new Callback<MainResponse>() {

            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {

                if (response.isSuccessful()) {

                    MainResponse mainResponse = response.body();
                    itemsBeans = mainResponse.getItems();

                    BooksData = gson.toJson(itemsBeans);
                    SharedPreferences sharedPreferences = SecondActivity.this.getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("SaveData", true);
                    editor.putString("BooksType", BooksType);
                    editor.putString("BooksData", BooksData);
                    editor.apply();

                    Intent i = new Intent(SecondActivity.this, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putString("BooksData", BooksData);
                    i.putExtras(b);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();

                }
                pdialog.dismiss();
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                Toast.makeText(SecondActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();
                pdialog.dismiss();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtBook", txtBook.getText().toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
