package com.example.amr.capstone.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amr.capstone.Models.MainResponse;
import com.example.amr.capstone.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

    private Context mContext;
    private List<MainResponse.ItemsBean> favourites;

    public MainAdapter(Context mContext, List<MainResponse.ItemsBean> favourites) {
        this.mContext = mContext;
        this.favourites = favourites;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        if (favourites.get(position) != null)
            if (favourites.get(position).getVolumeInfo() != null)
                if (favourites.get(position).getVolumeInfo().getImageLinks() != null)
                    if (favourites.get(position).getVolumeInfo().getImageLinks().getThumbnail() != null) {
                        String imag = favourites.get(position).getVolumeInfo().getImageLinks().getThumbnail();
                        if (!imag.isEmpty())
                            Picasso.with(mContext).load(imag).into(holder.image);
                    }

        holder.title.setText(favourites.get(position).getVolumeInfo().getTitle());
        holder.subtitle.setText(favourites.get(position).getVolumeInfo().getSubtitle());
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
            image = (ImageView) view.findViewById(R.id.grid_item_image_in_Main);
        }
    }
}