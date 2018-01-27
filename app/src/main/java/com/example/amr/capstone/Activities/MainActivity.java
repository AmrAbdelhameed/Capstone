package com.example.amr.capstone.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.amr.capstone.Fragments.DetailsFragment;
import com.example.amr.capstone.Fragments.MainFragment;
import com.example.amr.capstone.R;
import com.example.amr.capstone.TabletMood;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

public class MainActivity extends AppCompatActivity implements TabletMood {
    private static final String TAG = "MainActivity";
    boolean mIsTwoPane = false;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FirebaseCrash.log(getString(R.string.activityCreated));

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
        String BooksType = sharedPreferences.getString("BooksType", "");
        setTitle(BooksType);

        if (savedInstanceState == null) {
            Intent sentIntent = getIntent();
            Bundle sentBundle = sentIntent.getExtras();
            MainFragment mMainFragment = new MainFragment();
            mMainFragment.setNameListener(MainActivity.this);
            mMainFragment.setArguments(sentBundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.flMain, mMainFragment, "").commit();
        }
        if (null != findViewById(R.id.flDetails)) {
            mIsTwoPane = true;
        }
    }

    @Override
    public void setSelectedName(String ID, String Title, String SubTitle, String Year, Double Rate, String Overview, String Image1, String Image2, String publisher) {

        if (!mIsTwoPane) {
            Intent i = new Intent(this, DetailsActivity.class);

            Bundle b = new Bundle();
            b.putBoolean("Favourite", false);
            b.putString("ID", ID);
            b.putBoolean("Toolbar", true);
            b.putString("Title", Title);
            b.putString("SubTitle", SubTitle);
            b.putString("Year", Year);
            b.putDouble("Rate", Rate);
            b.putString("Overview", Overview);
            b.putString("Image1", Image1);
            b.putString("Image2", Image2);
            b.putString("publisher", publisher);
            i.putExtras(b);

            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            DetailsFragment mDetailsFragment = new DetailsFragment();

            Bundle b = new Bundle();
            b.putBoolean("Favourite", false);
            b.putString("ID", ID);
            b.putBoolean("Toolbar", false);
            b.putString("Title", Title);
            b.putString("SubTitle", SubTitle);
            b.putString("Year", Year);
            b.putDouble("Rate", Rate);
            b.putString("Overview", Overview);
            b.putString("Image1", Image1);
            b.putString("Image2", Image2);
            b.putString("publisher", publisher);
            mDetailsFragment.setArguments(b);

            getSupportFragmentManager().beginTransaction().replace(R.id.flDetails, mDetailsFragment, "").commit();
        }
    }
}
