package com.example.amr.capstone.Fragments;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
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
import android.widget.Toast;

import com.example.amr.capstone.Adapters.FavouriteAdapter;
import com.example.amr.capstone.DataBase.BookProvider;
import com.example.amr.capstone.Models.Favourite;
import com.example.amr.capstone.R;
import com.example.amr.capstone.RecyclerItemClickListener;
import com.example.amr.capstone.TabletMoodFavourite;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static String TAG = "CursorLoader";
    Favourite resultsBean;
    RecyclerView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    boolean landscapeMood = false;
    int noItems = 3;
    List<Favourite> favouriteList;
    FavouriteAdapter favouriteAdapter;
    TabletMoodFavourite mListener;
    LoaderManager loadermanager;
    CursorLoader cursorLoader;

    public void setNameListener(TabletMoodFavourite TabletMoodFavourite) {
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

        loadermanager = getActivity().getLoaderManager();
        loadermanager.initLoader(1, null, this);

        return fragment;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {"idd", "imageposter", "imageback", "title", "subtitle", "rate", "year", "publisher", "overview"};
        cursorLoader = new CursorLoader(getActivity(), BookProvider.Book_CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        while (cursor.moveToNext()) {
            resultsBean = new Favourite();

            String mmid = cursor.getString(0);
            String imagemovie1 = cursor.getString(1);
            String imagemovie2 = cursor.getString(2);
            String movietitle = cursor.getString(3);
            String moviesubtitle = cursor.getString(4);
            Double movierate = cursor.getDouble(5);
            String movieyear = cursor.getString(6);
            String publisher = cursor.getString(7);
            String mmoverview = cursor.getString(8);

            resultsBean.setID(mmid);
            resultsBean.setImage1(imagemovie1);
            resultsBean.setImage2(imagemovie2);
            resultsBean.setTitle(movietitle);
            resultsBean.setSubTitle(moviesubtitle);
            resultsBean.setRate(movierate);
            resultsBean.setYear(movieyear);
            resultsBean.setDescription(mmoverview);
            resultsBean.setPublisher(publisher);

            favouriteList.add(resultsBean);
        }
        cursor.close();

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
                        String publisher = favouriteList.get(position).getPublisher();

                        mListener.setSelectedName(ID, Title, SubTitle, Year, Rate, Overview, Image1, Image2, publisher);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
