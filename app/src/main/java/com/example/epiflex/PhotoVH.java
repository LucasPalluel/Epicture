package com.example.epiflex;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PhotoVH extends RecyclerView.ViewHolder {
    public ImageView photo;
    public TextView title;

    public PhotoVH(View itemView) {
        super(itemView);
    }
}

