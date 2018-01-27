package com.example.amr.capstone.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.example.amr.capstone.Activities.DetailsActivity;
import com.example.amr.capstone.Activities.FavouriteActivity;
import com.example.amr.capstone.DataBase.DBHelper;
import com.example.amr.capstone.R;
import com.squareup.picasso.Picasso;

public class DetailsFragment extends Fragment {
    TextView title_movie, yearView, rate, DescriptionView, makeAsFavourite, publisher_book;
    ImageView imageView;
    Button Btn_makeAsFavourite;
    String ID, Title, SubTitle, Year, Overview, Image1, Image2, publisher;
    boolean toolbarExist, Favourite;
    Double Rate;
    DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailsFragment = inflater.inflate(R.layout.fragment_details, container, false);

        dbHelper = new DBHelper(getContext());

        makeAsFavourite = (TextView) detailsFragment.findViewById(R.id.makeAsFavourite);
        Btn_makeAsFavourite = (Button) detailsFragment.findViewById(R.id.Btn_makeAsFavourite);
        title_movie = (TextView) detailsFragment.findViewById(R.id.title_book);
        yearView = (TextView) detailsFragment.findViewById(R.id.date_book);
        rate = (TextView) detailsFragment.findViewById(R.id.rate_book);
        publisher_book = (TextView) detailsFragment.findViewById(R.id.publisher_book);
        DescriptionView = (TextView) detailsFragment.findViewById(R.id.description_book);
        imageView = (ImageView) detailsFragment.findViewById(R.id.Image_Poster);

        Bundle sentBundle = getArguments();
        Favourite = sentBundle.getBoolean(getString(R.string.fav));
        ID = sentBundle.getString(getString(R.string.id));
        Title = sentBundle.getString(getString(R.string.title));
        SubTitle = sentBundle.getString(getString(R.string.subtitle));
        publisher = sentBundle.getString(getString(R.string.publisher2));
        publisher_book.setText(getString(R.string.pubOut) + publisher);
        toolbarExist = sentBundle.getBoolean(getString(R.string.toolbar));

        if (toolbarExist) {
            ((DetailsActivity) getActivity()).setTitle(Title);
        } else {
            title_movie.setText(Title);
            title_movie.setVisibility(View.VISIBLE);
        }
        Year = sentBundle.getString(getString(R.string.year));
        yearView.setText(getString(R.string.dateOut) + Year);
        Rate = sentBundle.getDouble(getString(R.string.rate));
        rate.setText(getString(R.string.rateOut) + String.valueOf(Rate) + getString(R.string.rateTo));
        Overview = sentBundle.getString(getString(R.string.overview));
        DescriptionView.setText(getString(R.string.descOut) + Overview);
        Image1 = sentBundle.getString(getString(R.string.img1));
        Image2 = sentBundle.getString(getString(R.string.img2));

        Picasso.with(getActivity()).load(Image2).into(imageView);

        if (dbHelper.checkmovie(ID).getCount() == 0) {
            Btn_makeAsFavourite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
            makeAsFavourite.setText(R.string.makeasfav);
        } else {
            Btn_makeAsFavourite.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            makeAsFavourite.setText(R.string.makeasunfav);
        }

        Btn_makeAsFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHelper.checkmovie(ID).getCount() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.unfav_ques)
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dbHelper.deleterow(ID);
                                    Toast.makeText(getActivity(), R.string.unfavSuccess, Toast.LENGTH_SHORT).show();
                                    Btn_makeAsFavourite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                                    makeAsFavourite.setText(R.string.makeasfav);
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
                    d.setTitle(getString(R.string.msgConfirm));
                    d.show();
                } else {
                    if (dbHelper.checkmovie(ID).getCount() == 0) {
                        dbHelper.addBook(ID, Image1, Image2, Title, SubTitle, Rate, Year, Overview, publisher);
                        Toast.makeText(getActivity(), R.string.favSuccess, Toast.LENGTH_SHORT).show();
                        Btn_makeAsFavourite.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                        makeAsFavourite.setText(R.string.makeasunfav);
                    }
                }
            }
        });
        return detailsFragment;
    }
}
