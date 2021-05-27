package com.vladkor.dactyltranslator.list;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vladkor.dactyltranslator.R;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    ArrayList<ItemTopPlace> topPlaces;
    private Context context;
    Resources res;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTop;
        public TextView levelTop;
        public ImageView avatarTop;
        public ImageView imagePlaceTop;
        public ProgressBar progressBarTop;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTop = itemView.findViewById(R.id.nameTop);
            levelTop = itemView.findViewById(R.id.levelTop);
            avatarTop = itemView.findViewById(R.id.imageTop);
            imagePlaceTop = itemView.findViewById(R.id.placeTopImage);
            progressBarTop = itemView.findViewById(R.id.progressBar);
            res = itemView.getResources();
        }
    }
    public MyRecyclerViewAdapter(Context context, ArrayList<ItemTopPlace> array){
        topPlaces = array;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_place, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemTopPlace topPlace = topPlaces.get(position);
        holder.nameTop.setText(topPlace.getName());
        holder.levelTop.setText(Integer.toString(topPlace.getLevel()));
        Picasso.get().load(topPlace.getImageProfile()).into(holder.avatarTop);
        switch (position){
            case 0:
                holder.imagePlaceTop.setImageDrawable(res.getDrawable(R.drawable.ic_prize1, res.newTheme()));
                break;
            case 1:
                holder.imagePlaceTop.setImageDrawable(res.getDrawable(R.drawable.ic_prize2, res.newTheme()));
                break;
            case 2:
                holder.imagePlaceTop.setImageDrawable(res.getDrawable(R.drawable.ic_prize3, res.newTheme()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return topPlaces.size();
    }


}
