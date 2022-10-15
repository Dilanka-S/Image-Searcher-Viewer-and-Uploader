package lk.sliit.assignment2partb;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.BinderThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.singleImageView);
    }
    public void bind(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
    }
}
