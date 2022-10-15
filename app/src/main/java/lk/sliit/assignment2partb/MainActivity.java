package lk.sliit.assignment2partb;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Button loadImage;
    ImageView picture;
    ProgressBar progressBar;
    EditText searchKey;
    RecyclerView recyclerView;
    Button singleColumn, doubleColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        loadImage = findViewById(R.id.loadImage);
        recyclerView = findViewById(R.id.imageRecycler);
        progressBar = findViewById(R.id.progressBarId);
        searchKey = findViewById(R.id.inputSearch);
        progressBar.setVisibility(View.INVISIBLE);
        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.INVISIBLE);
                searchImage();
            }
        });


    }

    public void searchImage(){
        Toast.makeText(MainActivity.this, "Searching starts", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        SearchTask searchTask = new SearchTask(MainActivity.this);
        searchTask.setSearchkey(searchKey.getText().toString());
        Single<String> searchObservable = Single.fromCallable(searchTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());
        searchObservable.subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@NonNull String s) {
                Toast.makeText(MainActivity.this, "Searching Ends", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                loadImage(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(MainActivity.this, "Searching Error", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    public void loadImage(String response){
        ImageRetrievalTask imageRetrievalTask = new ImageRetrievalTask(MainActivity.this);
        imageRetrievalTask.setData(response);
        Toast.makeText(MainActivity.this, "Image loading starts", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
        Single<ArrayList<Bitmap>> searchObservable = Single.fromCallable(imageRetrievalTask);
        searchObservable = searchObservable.subscribeOn(Schedulers.io());
        searchObservable = searchObservable.observeOn(AndroidSchedulers.mainThread());
        searchObservable.subscribe(new SingleObserver<ArrayList<Bitmap>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ArrayList<Bitmap> bitmaps) {
                Toast.makeText(MainActivity.this, "Image loading Ends", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2,RecyclerView.VERTICAL,false));
                recyclerView.setAdapter(new ImageAdapter(bitmaps));
                System.out.println("\n\n\nPrinting bitmaps from main\n"+bitmaps);
                //picture.setImageBitmap(bitmap);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(MainActivity.this, "Image loading error, search again", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }
}