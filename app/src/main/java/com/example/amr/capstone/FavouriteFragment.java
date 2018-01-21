package com.example.amr.capstone;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amr.capstone.Adapters.FavouriteAdapter;
import com.example.amr.capstone.DataBase.BookProvider;
import com.example.amr.capstone.Models.Favourite;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment {

    Favourite resultsBean;
    RecyclerView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    boolean landscapeMood = false;
    int noItems = 3;
    List<Favourite> favouriteList;
    FavouriteAdapter favouriteAdapter;
    TabletMoodFavourite mListener;

    void setNameListener(TabletMoodFavourite TabletMoodFavourite) {
        this.mListener = TabletMoodFavourite;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_favourite, container, false);

        recycler_view = (RecyclerView) fragment.findViewById(R.id.recycler_view);

        landscapeMood = getResources().getBoolean(R.bool.landscapeMood);
        if (landscapeMood)
            noItems = 5;
        else
            noItems = 3;

        favouriteList = new ArrayList<Favourite>();

        Uri bookUri = BookProvider.MOVIE_CONTENT_URI;
        Cursor movieCursor = getActivity().getContentResolver().query(bookUri, new String[]{"idd", "imageposter", "imageback", "title", "subtitle", "rate", "year", "overview"}, null, null, null);
        while (movieCursor.moveToNext()) {
            resultsBean = new Favourite();

            String mmid = movieCursor.getString(0);
            String imagemovie1 = movieCursor.getString(1);
            String imagemovie2 = movieCursor.getString(2);
            String movietitle = movieCursor.getString(3);
            String moviesubtitle = movieCursor.getString(4);
            Double movierate = movieCursor.getDouble(5);
            String movieyear = movieCursor.getString(6);
            String mmoverview = movieCursor.getString(7);

            resultsBean.setID(mmid);
            resultsBean.setImage1(imagemovie1);
            resultsBean.setImage2(imagemovie2);
            resultsBean.setTitle(movietitle);
            resultsBean.setSubTitle(moviesubtitle);
            resultsBean.setRate(movierate);
            resultsBean.setYear(movieyear);
            resultsBean.setDescription(mmoverview);

            favouriteList.add(resultsBean);
        }
        movieCursor.close();

        favouriteAdapter = new FavouriteAdapter(getActivity(), favouriteList);
        mLayoutManager = new GridLayoutManager(getActivity(), noItems);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(favouriteAdapter);

        recycler_view.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        String ID = favouriteList.get(position).getID();
                        String Title = favouriteList.get(position).getTitle();
                        String SubTitle = favouriteList.get(position).getSubTitle();
                        String Year = favouriteList.get(position).getYear();
                        Double Rate = favouriteList.get(position).getRate();
                        String Overview = favouriteList.get(position).getDescription();
                        String Image1 = favouriteList.get(position).getImage1();
                        String Image2 = favouriteList.get(position).getImage2();

                        mListener.setSelectedName(ID, Title, SubTitle, Year, Rate, Overview, Image1, Image2);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        return fragment;
    }
}
