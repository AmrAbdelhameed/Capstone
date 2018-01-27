package com.example.amr.capstone.Fragments;

import android.content.Intent;
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

import com.example.amr.capstone.Activities.FavouriteActivity;
import com.example.amr.capstone.Activities.MainActivity;
import com.example.amr.capstone.Activities.SecondActivity;
import com.example.amr.capstone.Adapters.MainAdapter;
import com.example.amr.capstone.Models.MainResponse;
import com.example.amr.capstone.R;
import com.example.amr.capstone.RecyclerItemClickListener;
import com.example.amr.capstone.TabletMood;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {

    public static int index = -1;
    public static int top = -1;
    RecyclerView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    boolean landscapeMood = false;
    int noItems = 3;
    Gson gson;
    String BooksData = "";
    List<MainResponse.ItemsBean> itemsBeans;
    MainAdapter mainAdapter;
    TabletMood mListener;

    public void setNameListener(TabletMood tabletMood) {
        this.mListener = tabletMood;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_main, container, false);

        recycler_view = (RecyclerView) fragment.findViewById(R.id.recycler_view);
        landscapeMood = getResources().getBoolean(R.bool.landscapeMood);
        if (landscapeMood)
            noItems = 5;
        else
            noItems = 3;

        Bundle sentBundle = getArguments();
        BooksData = sentBundle.getString("BooksData");

        gson = new Gson();
        Type type = new TypeToken<List<MainResponse.ItemsBean>>() {
        }.getType();
        itemsBeans = gson.fromJson(BooksData, type);

        mainAdapter = new MainAdapter(getActivity(), itemsBeans);
        mLayoutManager = new GridLayoutManager(getActivity(), noItems);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setHasFixedSize(true);
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
                        String publisher = itemsBeans.get(position).getVolumeInfo().getPublisher();

                        mListener.setSelectedName(ID, Title, SubTitle, Year, Rate, Overview, Image1, Image2, publisher);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        return fragment;
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
        if (id == R.id.action_setting) {
            Intent i = new Intent(getActivity(), SecondActivity.class);
            startActivityForResult(i, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                ((MainActivity) getActivity()).finish();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //read current recyclerview position
        index = mLayoutManager.getItemCount();
        View v = recycler_view.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - recycler_view.getPaddingTop());
    }

    @Override
    public void onResume() {
        super.onResume();
        //set recyclerview position
        if (index != -1) {
            mLayoutManager.scrollToPosition(index);
        }
    }
}
