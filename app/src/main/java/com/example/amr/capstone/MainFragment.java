package com.example.amr.capstone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amr.capstone.Adapters.MainAdapter;
import com.example.amr.capstone.Models.MainResponse;
import com.example.amr.capstone.RetrofitAPIs.APIService;
import com.example.amr.capstone.RetrofitAPIs.ApiUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

    APIService mAPIService;
    RecyclerView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    boolean landscapeMood = false;
    int noItems = 3;
    Gson gson;
    String BooksData = "";
    List<MainResponse.ItemsBean> itemsBeans;
    MainAdapter mainAdapter;
    TabletMood mListener;
    ProgressDialog pdialog;

    void setNameListener(TabletMood tabletMood) {
        this.mListener = tabletMood;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_main, container, false);

        mAPIService = ApiUtils.getAPIService();
        gson = new Gson();

        pdialog = new ProgressDialog(getActivity());
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");

        recycler_view = (RecyclerView) fragment.findViewById(R.id.recycler_view);

        landscapeMood = getResources().getBoolean(R.bool.landscapeMood);
        if (landscapeMood)
            noItems = 5;
        else
            noItems = 3;

        itemsBeans = new ArrayList<MainResponse.ItemsBean>();
        getMoviesGET();

        return fragment;
    }

    public void getMoviesGET() {
        pdialog.show();
        mAPIService.getMovies("love", "books").enqueue(new Callback<MainResponse>() {

            @Override
            public void onResponse(Call<MainResponse> call, Response<MainResponse> response) {

                if (response.isSuccessful()) {

                    MainResponse mainResponse = response.body();
                    itemsBeans = mainResponse.getItems();

                    BooksData = gson.toJson(itemsBeans);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferences_name", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("BooksData", BooksData);
                    editor.apply();

                    mainAdapter = new MainAdapter(getActivity(), itemsBeans);
                    mLayoutManager = new GridLayoutManager(getActivity(), noItems);
                    recycler_view.setLayoutManager(mLayoutManager);
                    recycler_view.setItemAnimator(new DefaultItemAnimator());
                    recycler_view.setAdapter(mainAdapter);

                    recycler_view.addOnItemTouchListener(
                            new RecyclerItemClickListener(getActivity(), recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    String ID = itemsBeans.get(position).getId();
                                    String Title = itemsBeans.get(position).getVolumeInfo().getTitle();
                                    String SubTitle = itemsBeans.get(position).getVolumeInfo().getSubtitle();
                                    String Year = itemsBeans.get(position).getVolumeInfo().getPublishedDate();
                                    Double Rate = itemsBeans.get(position).getVolumeInfo().getAverageRating();
                                    String Overview = itemsBeans.get(position).getVolumeInfo().getDescription();
                                    String Image1 = itemsBeans.get(position).getVolumeInfo().getImageLinks().getThumbnail();
                                    String Image2 = itemsBeans.get(position).getVolumeInfo().getImageLinks().getSmallThumbnail();

                                    mListener.setSelectedName(ID, Title, SubTitle, Year, Rate, Overview, Image1, Image2);
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
                Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferences_name", Context.MODE_PRIVATE);
                BooksData = sharedPreferences.getString("BooksData", "");

                Type type = new TypeToken<List<MainResponse.ItemsBean>>() {
                }.getType();
                itemsBeans = gson.fromJson(BooksData, type);

                mainAdapter = new MainAdapter(getActivity(), itemsBeans);
                mLayoutManager = new GridLayoutManager(getActivity(), noItems);
                recycler_view.setLayoutManager(mLayoutManager);
                recycler_view.setItemAnimator(new DefaultItemAnimator());
                recycler_view.setAdapter(mainAdapter);

                recycler_view.addOnItemTouchListener(
                        new RecyclerItemClickListener(getActivity(), recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String ID = itemsBeans.get(position).getId();
                                String Title = itemsBeans.get(position).getVolumeInfo().getTitle();
                                String SubTitle = itemsBeans.get(position).getVolumeInfo().getSubtitle();
                                String Year = itemsBeans.get(position).getVolumeInfo().getPublishedDate();
                                Double Rate = itemsBeans.get(position).getVolumeInfo().getAverageRating();
                                String Overview = itemsBeans.get(position).getVolumeInfo().getDescription();
                                String Image1 = itemsBeans.get(position).getVolumeInfo().getImageLinks().getThumbnail();
                                String Image2 = itemsBeans.get(position).getVolumeInfo().getImageLinks().getSmallThumbnail();

                                mListener.setSelectedName(ID, Title, SubTitle, Year, Rate, Overview, Image1, Image2);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                // do whatever
                            }
                        })
                );
                pdialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favourite) {
            Intent i = new Intent(getActivity(), FavouriteActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
