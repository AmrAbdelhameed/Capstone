package com.example.amr.capstone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amr.capstone.DataBase.DBHelper;
import com.example.amr.capstone.RetrofitAPIs.APIService;
import com.example.amr.capstone.RetrofitAPIs.ApiUtils;
import com.squareup.picasso.Picasso;

public class DetailsFragment extends Fragment {
    TextView title_movie, yearView, rate, DescriptionView, makeAsFavourite;
    ImageView imageView;
    APIService mAPIService;
    Button Btn_makeAsFavourite;
    String ID, Title, SubTitle, Year, Overview, Image1, Image2;
    boolean toolbarExist, Favourite;
    Double Rate;
    DBHelper dbHelper;
    ProgressDialog pdialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsFragment = inflater.inflate(R.layout.fragment_details, container, false);

        dbHelper = new DBHelper(getContext());

        mAPIService = ApiUtils.getAPIService();

        pdialog = new ProgressDialog(getActivity());
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");

        makeAsFavourite = (TextView) detailsFragment.findViewById(R.id.makeAsFavourite);
        Btn_makeAsFavourite = (Button) detailsFragment.findViewById(R.id.Btn_makeAsFavourite);
        title_movie = (TextView) detailsFragment.findViewById(R.id.title_book);
        yearView = (TextView) detailsFragment.findViewById(R.id.date_book);
        rate = (TextView) detailsFragment.findViewById(R.id.rate_book);
        DescriptionView = (TextView) detailsFragment.findViewById(R.id.description_book);
        imageView = (ImageView) detailsFragment.findViewById(R.id.Image_Poster);

        Bundle sentBundle = getArguments();
        Favourite = sentBundle.getBoolean("Favourite");
        ID = sentBundle.getString("ID");
        Title = sentBundle.getString("Title");
        SubTitle = sentBundle.getString("SubTitle");
        toolbarExist = sentBundle.getBoolean("Toolbar");

        if (toolbarExist) {
            ((DetailsActivity) getActivity()).setTitle(Title);
        } else {
            title_movie.setText(Title);
            title_movie.setVisibility(View.VISIBLE);
        }
        Year = sentBundle.getString("Year");
        yearView.setText("Date : " + Year);
        Rate = sentBundle.getDouble("Rate");
        rate.setText("Rate : " + String.valueOf(Rate) + "/5");
        Overview = sentBundle.getString("Overview");
        DescriptionView.setText("Description: " + Overview);
        Image1 = sentBundle.getString("Image1");
        Image2 = sentBundle.getString("Image2");

        Picasso.with(getActivity()).load(Image2).into(imageView);

        if (dbHelper.checkmovie(ID).getCount() == 0) {
            Btn_makeAsFavourite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
            makeAsFavourite.setText("Make As Favourite");
        } else {
            Btn_makeAsFavourite.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            makeAsFavourite.setText("Make As UnFavourite");
        }

        Btn_makeAsFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHelper.checkmovie(ID).getCount() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("UnFavourite it ?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dbHelper.deleterow(ID);
                                    Toast.makeText(getActivity(), "UnFavourite Successfully", Toast.LENGTH_SHORT).show();
                                    Btn_makeAsFavourite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                                    makeAsFavourite.setText("Make As Favourite");
                                    if (toolbarExist) {
                                        Intent intent = new Intent();
                                        getActivity().setResult(Activity.RESULT_OK, intent);
                                        ((DetailsActivity) getActivity()).finish();
                                    } else {
                                        ((FavouriteActivity) getActivity()).finish();
                                    }

                                }
                            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog d = builder.create();
                    d.setTitle("Are you sure");
                    d.show();
                } else {
                    if (dbHelper.checkmovie(ID).getCount() == 0) {
                        dbHelper.addMovie(ID, Image1, Image2, Title, SubTitle, Rate, Year, Overview);
                        Toast.makeText(getActivity(), "Favourite Successfully", Toast.LENGTH_SHORT).show();
                        Btn_makeAsFavourite.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                        makeAsFavourite.setText("Make As UnFavourite");
                    }
                }
            }
        });
        return detailsFragment;
    }
}
