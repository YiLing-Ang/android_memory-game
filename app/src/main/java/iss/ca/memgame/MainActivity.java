package iss.ca.memgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private Thread bkgdThread;
    ArrayList<String> sixImages = new ArrayList<String>();
    List<Integer> pickedImageViews = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources r = getResources();
        String name = getPackageName();

        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sixImages.size() == 6) {
                    System.out.println("start intent");

                    Intent intent = new Intent(MainActivity.this, GameActivity.class);

                    intent.putExtra("imgList", sixImages);
                    startActivity(intent);

                    System.out.println("intent okay");
                }
            }
        });

        Button fetch_btn = findViewById(R.id.btnFetch);
        fetch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.inputTxt);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                TextView progressText = findViewById(R.id.progressText);

                String inputURL = "https://" + editText.getText().toString();
                progressBar.setProgress(0);
                clearSelection();

                if (bkgdThread != null) {
                    bkgdThread.interrupt();
                    for(int i=1; i<=20; i++)
                    {
                        ImageView img = findViewById(r.getIdentifier("image" + i, "id", name));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                img.setImageResource(R.drawable.cross);
                                img.setBackground(null);
                            }
                        });
                    }
                }

                bkgdThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //using Jsoup to crawl the given url for a list of 20 image links
                            Document doc = Jsoup.connect(inputURL).get();
                            Elements elements = doc.getElementsByTag("img");
                            List<String> urls = elements.stream().map(x -> x.absUrl("src"))
                                    .filter(x -> !x.endsWith(".svg"))
                                    .limit(20).collect(Collectors.toList());

                            //loop through list of image URLs and setting bitmap images to ImageView in layout grid
                            for (int i = 1; i <= 20; i++) {
                                if (Thread.interrupted())
                                    return;
                                URL url = new URL(urls.get(i - 1));

                                //bitmap output
                                Bitmap bmpimg = ImageDownload.downloadImg(urls, i);

                                //taking advantage of simple naming convention of ImageView to allow for looping
                                ImageView img = findViewById(r.getIdentifier("image" + i, "id", name));

                                //UI thread started to allow setting of images
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        img.setImageBitmap(bmpimg);
                                        img.setBackground(null);
                                        progressBar.incrementProgressBy(1);
                                        progressText.setText("Downloading " + progressBar.getProgress() + " of 20 images");
                                        if (progressBar.getProgress()==20)
                                        {
                                            progressText.setText("Download complete");
                                        }

                                        if (img != null) {
                                            img.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    imageSelect(url, img.getId());
                                                }
                                            });
                                        }
                                    }
                                });
                                Thread.sleep(500);
                            }
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

    protected void imageSelect(URL url, int id)
    {
        Resources r = getResources();
        String name = getPackageName();
        ImageView imageView = findViewById(id);

        if (sixImages.size()<6) {
            System.out.println("click success");
            System.out.println(sixImages.size());
            sixImages.add(url.toString());
            pickedImageViews.add(id);
            imageView.setBackground(getResources().getDrawable(R.drawable.button_border));
            if (sixImages.size()==6)
            {
                findViewById(R.id.btnSubmit).setEnabled(true);
            }
        }

        else
        {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Only 6 images may be selected.")
                    .setMessage("Would you like to reset your selection?")

                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            clearSelection();
                        }
                    })

                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    protected void clearSelection()
    {
        for (Integer i: pickedImageViews)
        {
            ImageView imageView = findViewById(i);
            imageView.setBackground(null);
        }
        sixImages.clear();
        findViewById(R.id.btnSubmit).setEnabled(false);
    }
}