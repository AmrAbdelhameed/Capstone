package com.example.amr.capstone.Widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.amr.capstone.Adapters.MainAdapter;
import com.example.amr.capstone.Models.MainResponse;
import com.example.amr.capstone.R;
import com.example.amr.capstone.RecyclerItemClickListener;
import com.example.amr.capstone.RetrofitAPIs.APIService;
import com.example.amr.capstone.RetrofitAPIs.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The configuration screen for the {@link AppWidget AppWidget} AppWidget.
 */
public class AppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.amr.capstone.Widget.AppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    APIService mAPIService;
    RecyclerView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    boolean landscapeMood = false;
    int noItems = 3;
    Gson gson;
    String BooksData = "";
    List<MainResponse.ItemsBean> itemsBeans;
    MainAdapter mainAdapter;
    ProgressDialog pdialog;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public AppWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.app_widget_configure);

        mAPIService = ApiUtils.getAPIService();
        gson = new Gson();

        pdialog = new ProgressDialog(AppWidgetConfigureActivity.this);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage(getString(R.string.plzwait));

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
        BooksData = sharedPreferences.getString("BooksData", "");

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);

        landscapeMood = getResources().getBoolean(R.bool.landscapeMood);
        if (landscapeMood)
            noItems = 5;
        else
            noItems = 3;

        if (BooksData.equals(""))
            getBooksGET();
        else {
            gson = new Gson();
            Type type = new TypeToken<List<MainResponse.ItemsBean>>() {
            }.getType();
            itemsBeans = gson.fromJson(BooksData, type);

            mainAdapter = new MainAdapter(AppWidgetConfigureActivity.this, itemsBeans);
            mLayoutManager = new GridLayoutManager(AppWidgetConfigureActivity.this, noItems);
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(mainAdapter);

            recycler_view.addOnItemTouchListener(
                    new RecyclerItemClickListener(AppWidgetConfigureActivity.this, recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            String ID = itemsBeans.get(position).getId();
                            String Title = itemsBeans.get(position).getVolumeInfo().getTitle();
                            String SubTitle = itemsBeans.get(position).getVolumeInfo().getSubtitle();
                            String Year = itemsBeans.get(position).getVolumeInfo().getPublishedDate();
                            Double Rate = itemsBeans.get(position).getVolumeInfo().getAverageRating();
                            String Overview = itemsBeans.get(position).getVolumeInfo().getDescription();
                            String Publisher = itemsBeans.get(position).getVolumeInfo().getPublisher();

                            String widgetText = "";
                            // When the button is clicked, store the string locally
                            widgetText += "Title: " + Title + "\n" + "SubTitle: " + SubTitle + "\n" + "Publisher: " + Publisher + "\n" + "Publish Date: " + Year + "\n" + "Rate: " + String.valueOf(Rate) + "/5" + "\n" + "Description: " + Overview;
                            saveTitlePref(AppWidgetConfigureActivity.this, mAppWidgetId, widgetText);

                            // It is the responsibility of the configuration activity to update the app widget
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(AppWidgetConfigureActivity.this);
                            AppWidget.updateAppWidget(AppWidgetConfigureActivity.this, appWidgetManager, mAppWidgetId);

                            // Make sure we pass back the original appWidgetId
                            Intent resultValue = new Intent();
                            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                            setResult(RESULT_OK, resultValue);
                            finish();
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );
        }

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    public void getBooksGET() {
        pdialog.show();
        mAPIService.getMovies(getString(R.string.DefaultFilter), getString(R.string.typeFilter)).enqueue(new Callback<MainResponse>() {

            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {

                if (response.isSuccessful()) {

                    MainResponse mainResponse = response.body();
                    itemsBeans = mainResponse.getItems();

                    mainAdapter = new MainAdapter(AppWidgetConfigureActivity.this, itemsBeans);
                    mLayoutManager = new GridLayoutManager(AppWidgetConfigureActivity.this, noItems);
                    recycler_view.setLayoutManager(mLayoutManager);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                    recycler_view.setAdapter(mainAdapter);

                    recycler_view.addOnItemTouchListener(
                            new RecyclerItemClickListener(AppWidgetConfigureActivity.this, recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    String ID = itemsBeans.get(position).getId();
                                    String Title = itemsBeans.get(position).getVolumeInfo().getTitle();
                                    String SubTitle = itemsBeans.get(position).getVolumeInfo().getSubtitle();
                                    String Year = itemsBeans.get(position).getVolumeInfo().getPublishedDate();
                                    Double Rate = itemsBeans.get(position).getVolumeInfo().getAverageRating();
                                    String Overview = itemsBeans.get(position).getVolumeInfo().getDescription();
                                    String Publisher = itemsBeans.get(position).getVolumeInfo().getPublisher();

                                    String widgetText = "";
                                    // When the button is clicked, store the string locally
                                    widgetText += "Title: " + Title + "\n" + "SubTitle: " + SubTitle + "\n" + "Publisher: " + Publisher + "\n" + "Publish Date: " + Year + "\n" + "Rate: " + String.valueOf(Rate) + "/5" + "\n" + "Description: " + Overview;
                                    saveTitlePref(AppWidgetConfigureActivity.this, mAppWidgetId, widgetText);

                                    // It is the responsibility of the configuration activity to update the app widget
                                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(AppWidgetConfigureActivity.this);
                                    AppWidget.updateAppWidget(AppWidgetConfigureActivity.this, appWidgetManager, mAppWidgetId);

                                    // Make sure we pass back the original appWidgetId
                                    Intent resultValue = new Intent();
                                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                                    setResult(RESULT_OK, resultValue);
                                    finish();
                                }

                                @Override
                                public void onLongItemClick(View view, int position) {
                                    // do whatever
                                }
                            })
                    );
                }
                pdialog.dismiss();
            }

            @Override
            public void onFailure(Call<MainResponse> call, Throwable t) {
                Toast.makeText(AppWidgetConfigureActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();
                pdialog.dismiss();
            }
        });
    }
}

