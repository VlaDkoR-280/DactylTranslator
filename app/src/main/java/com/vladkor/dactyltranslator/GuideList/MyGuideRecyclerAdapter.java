package com.vladkor.dactyltranslator.GuideList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vladkor.dactyltranslator.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyGuideRecyclerAdapter extends RecyclerView.Adapter<MyGuideRecyclerAdapter.MyViewHolfer> {

    private ArrayList<ItemWord> words;
    public MyGuideRecyclerAdapter(ArrayList<ItemWord> words){
        this.words = words;
    }

    public class MyViewHolfer extends RecyclerView.ViewHolder {

        public TextView wordName;
        public ImageView wordImage;
        public MyViewHolfer(@NonNull View itemView) {
            super(itemView);
            wordImage = itemView.findViewById(R.id.image_word);
            wordName = itemView.findViewById(R.id.name_word);
        }
    }

    @NonNull
    @Override
    public MyViewHolfer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolfer(LayoutInflater.from(parent.getContext()).inflate(R.layout.word_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolfer holder, int position) {
        holder.wordName.setText(words.get(position).getName());
        Picasso.get().load(words.get(position).getUri()).into(holder.wordImage);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }





}
