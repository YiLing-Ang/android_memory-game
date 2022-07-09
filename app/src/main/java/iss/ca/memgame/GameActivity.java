package iss.ca.memgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        System.out.println("start memory game");
        Intent intent = getIntent();
        Resources r = getResources();
        String name = getPackageName();
        List<String> imgList = (List<String>) getIntent().getSerializableExtra("imgList");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //loop through list of image URLs and setting bitmap images to ImageView in layout grid
                    for (int i = 1; i <= 6; i++) {

                        //converted to bitmap
                        Bitmap bmpimg = ImageDownload.downloadImg(imgList, i);

                        //taking advantage of simple naming convention of ImageView to allow for looping
                        ImageView img = findViewById(r.getIdentifier("img" + i, "id", name));
                        ImageView img1 = findViewById(r.getIdentifier("img" + (i+6), "id", name));

                        //UI thread started to allow setting of images
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                img.setImageBitmap(bmpimg);
                                img1.setImageBitmap(bmpimg);
                            }
                        });
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}