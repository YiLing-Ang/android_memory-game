package iss.ca.memgame;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private Thread bkgdThread;
    InputStream in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button fetch_btn = findViewById(R.id.btnFetch);

        Resources r = getResources();
        String name = getPackageName();

        //instantiate music service
        Intent intentMusic = new Intent(MainActivity.this, iss.ca.memgame.MusicService.class);
        intentMusic.putExtra("class","main");
        startService(intentMusic);

        fetch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.inputTxt);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                TextView progressText = findViewById(R.id.progressText);

                String inputURL = "https://" + editText.getText().toString();
                progressBar.setProgress(0);

                if (bkgdThread != null) {
                    bkgdThread.interrupt();
                    for(int i=1; i<=20; i++)
                    {
                        ImageView img = findViewById(r.getIdentifier("image" + i, "id", name));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                img.setImageResource(R.drawable.cross);
                            }
                        });
                    }
                }

                bkgdThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            //using Jsoup to crawl the given url for a list of 20 image links
                            Document doc = Jsoup.connect(inputURL).get();
                            Elements elements = doc.getElementsByTag("img");
                            List <String> urls = elements.stream().map(x->x.absUrl("src"))
                                    .filter(x->!x.endsWith(".svg"))
                                    .limit(20).collect(Collectors.toList());

                            //loop through list of image URLs and setting bitmap images to ImageView in layout grid
                            for (int i=1; i<=20; i++) {
                                if (Thread.interrupted())
                                    return;
                                URL url = new URL(urls.get(i-1));
                                URLConnection urlConn = url.openConnection();
                                HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                                httpConn.connect();

                                //raw data from image URL
                                in = httpConn.getInputStream();

                                //converted to bitmap
                                Bitmap bmpimg = BitmapFactory.decodeStream(in);

                                //taking advantage of simple naming convention of ImageView to allow for looping
                                ImageView img = findViewById(r.getIdentifier("image" + i, "id", name));

                                //UI thread started to allow setting of images
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        img.setImageBitmap(bmpimg);
                                        progressBar.incrementProgressBy(1);
                                        progressText.setText("Downloading " + progressBar.getProgress() + " of 20 images");
                                    }
                                });
                                Thread.sleep(500);
                            }
                            progressText.setText("Download complete");
                        }
                        catch (
                                MalformedURLException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });bkgdThread.start();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent intentMusic = new Intent(MainActivity.this, iss.ca.memgame.MusicService.class);
        intentMusic.putExtra("class","main");
        startService(intentMusic);
    }
}