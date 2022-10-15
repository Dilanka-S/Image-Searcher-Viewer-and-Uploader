package lk.sliit.assignment2partb;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> {
    private ArrayList<Bitmap> bitmaps;

    public ImageAdapter(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_image,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(bitmaps.get(position));

    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }
}
