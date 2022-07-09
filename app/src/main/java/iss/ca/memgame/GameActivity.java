package iss.ca.memgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

public class GameActivity extends AppCompatActivity {

    TextView timerText;
    Button btn;
    Timer timer;
    int seconds = 0;
    boolean running;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_game);
//
//        running=true;
//        runTimer();
////
////        btn = findViewById(R.id.finishBtn);
////
////        btn.setOnClickListener(new View.OnClickListener() {
////
////            @Override
////            public void onClick(View view) {
////
////                running = false;
////                seconds = 0;
////                finish();
////            }
////        });
//
//        System.out.println("start memory game");
//        Intent intent = getIntent();
//        Resources r = getResources();
//        String name = getPackageName();
//        List<String> imgList = (List<String>) getIntent().getSerializableExtra("imgList");
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //loop through list of image URLs and setting bitmap images to ImageView in layout grid
//                    for (int i = 1; i <= 6; i++) {
//
//                        //converted to bitmap
//                        Bitmap bmpimg = ImageDownload.downloadImg(imgList, i);
//
//                        //taking advantage of simple naming convention of ImageView to allow for looping
//                        ImageView img = findViewById(r.getIdentifier("img" + i, "id", name));
//                        ImageView img1 = findViewById(r.getIdentifier("img" + (i+6), "id", name));
//
//                        //UI thread started to allow setting of images
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                img.setImageBitmap(bmpimg);
//                                img1.setImageBitmap(bmpimg);
//                            }
//                        });
//                    }
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    TextView tv_p1, tv_p2,txtNumOfMatches;

    ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12;

    //array for images
    Integer[] cardsArray = {101, 102, 103, 104, 105, 106, 201, 202, 203, 204, 205, 206};

    //actual images
    int image101, image102, image103, image104, image105, image106, image201, image202, image203, image204, image205, image206;

    int firstCard, secondCard;
    int clickedFirst, clickedSecond;
    int cardNumber = 1;

    int turn = 1;
    int playerPoints = 0, cpuPoints = 0;
    int totalPoints = playerPoints+cpuPoints;

    InputStream in = null;
    private Thread bkgdThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        System.out.println("start memory game");
        Intent intent = getIntent();

        Resources r = getResources();
        String name = getPackageName();
        ArrayList<String> numbersList = (ArrayList<String>) getIntent().getSerializableExtra("imgList");
        if (bkgdThread != null) {
            bkgdThread.interrupt();
            for(int i=1; i<=12; i++)
            {
                ImageView img = findViewById(r.getIdentifier("img" + i, "id", name));
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
                try {
                    //loop through list of image URLs and setting bitmap images to ImageView in layout grid
                    for (int i = 1; i <= 6; i++) {
                        if (Thread.interrupted())
                            return;
                        URL url = new URL(numbersList.get(i - 1));
                        URLConnection urlConn = url.openConnection();
                        HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                        httpConn.connect();

                        //raw data from image URL
                        in = httpConn.getInputStream();

                        //converted to bitmap
                        Bitmap bmpimg = BitmapFactory.decodeStream(in);

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

        tv_p1 = (TextView) findViewById(R.id.tv_p1);
        tv_p2 = (TextView) findViewById(R.id.tv_p2);
        txtNumOfMatches = (TextView)findViewById(R.id.txtNumOfMatches);


        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        img5 = (ImageView) findViewById(R.id.img5);
        img6 = (ImageView) findViewById(R.id.img6);
        img7 = (ImageView) findViewById(R.id.img7);
        img8 = (ImageView) findViewById(R.id.img8);
        img9 = (ImageView) findViewById(R.id.img9);
        img10 = (ImageView) findViewById(R.id.img10);
        img11 = (ImageView) findViewById(R.id.img11);
        img12 = (ImageView) findViewById(R.id.img12);

        img1.setTag("0");
        img2.setTag("1");
        img3.setTag("2");
        img4.setTag("3");
        img5.setTag("4");
        img6.setTag("5");
        img7.setTag("6");
        img8.setTag("7");
        img9.setTag("8");
        img10.setTag("9");
        img11.setTag("10");
        img12.setTag("11");

        //load the card images
        frontOfCardResources();

        //shuffle the images
        Collections.shuffle(Arrays.asList(cardsArray));

        //changing the color of second player when he is inactive
        tv_p2.setTextColor(Color.GRAY);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img1, theCard);

            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img2, theCard);

            }
        });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img3, theCard);

            }
        });

        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img4, theCard);

            }
        });

        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img5, theCard);

            }
        });

        img6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img6, theCard);

            }
        });

        img7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img7, theCard);

            }
        });

        img8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img8, theCard);

            }
        });

        img9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img9, theCard);

            }
        });

        img10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img10, theCard);

            }
        });

        img11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img11, theCard);

            }
        });

        img12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int theCard = Integer.parseInt((String) view.getTag());
                doStuff(img12, theCard);

            }
        });
    }
    private void doStuff(ImageView iv, int card) {
        //set the correct image to the imageview
        if (cardsArray[card] == 101) {
            iv.setImageResource(image101);
        } else if (cardsArray[card] == 102) {
            iv.setImageResource(image102);
        } else if (cardsArray[card] == 103) {
            iv.setImageResource(image103);
        } else if (cardsArray[card] == 104) {
            iv.setImageResource(image104);
        } else if (cardsArray[card] == 105) {
            iv.setImageResource(image105);
        } else if (cardsArray[card] == 106) {
            iv.setImageResource(image106);
        } else if (cardsArray[card] == 201) {
            iv.setImageResource(image201);
        } else if (cardsArray[card] == 202) {
            iv.setImageResource(image202);
        } else if (cardsArray[card] == 203) {
            iv.setImageResource(image203);
        } else if (cardsArray[card] == 204) {
            iv.setImageResource(image204);
        } else if (cardsArray[card] == 205) {
            iv.setImageResource(image205);
        } else if (cardsArray[card] == 206) {
            iv.setImageResource(image206);
        }

        //check which image is selected and save it to temporary variable
        if (cardNumber == 1) {
            firstCard = cardsArray[card];
            if (firstCard > 200) {
                firstCard = firstCard - 100;
            }
            cardNumber = 2;
            clickedFirst = card;

            iv.setEnabled(false);
        } else if (cardNumber == 2) {
            secondCard = cardsArray[card];
            if (secondCard > 200) {
                secondCard = secondCard - 100;
            }
            cardNumber = 1;
            clickedSecond = card;

            img1.setEnabled(false);
            img2.setEnabled(false);
            img3.setEnabled(false);
            img4.setEnabled(false);
            img5.setEnabled(false);
            img6.setEnabled(false);
            img7.setEnabled(false);
            img8.setEnabled(false);
            img9.setEnabled(false);
            img10.setEnabled(false);
            img11.setEnabled(false);
            img12.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //check if the selected images are equal
                    calculate();
                }
            }, 1000);
        }
    }

    private void calculate(){
        //if images are equal remove the cards and add points
        if(firstCard==secondCard){
            if(clickedFirst==0){
                img1.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==1){
                img2.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==2){
                img3.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==3){
                img4.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==4){
                img5.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==5){
                img6.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==6){
                img7.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==7){
                img8.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==8){
                img9.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==9){
                img10.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==10){
                img11.setVisibility(View.INVISIBLE);
            }else if(clickedFirst==11){
                img12.setVisibility(View.INVISIBLE);
            }

            if(clickedSecond==0){
                img1.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==1){
                img2.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==2){
                img3.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==3){
                img4.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==4){
                img5.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==5){
                img6.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==6){
                img7.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==7){
                img8.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==8){
                img9.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==9){
                img10.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==10){
                img11.setVisibility(View.INVISIBLE);
            }else if(clickedSecond==11){
                img12.setVisibility(View.INVISIBLE);
            }

            //add points to the correct player


            if(turn==1){
                playerPoints++;
                tv_p1.setText("Player 1: "+playerPoints);
                totalPoints++;
                txtNumOfMatches.setText(totalPoints+" out of 6 matches");
            } else if(turn==2){
                cpuPoints++;
                tv_p2.setText("Player 2: "+cpuPoints);
                totalPoints++;
                txtNumOfMatches.setText(totalPoints+" out of 6 matches");
            }

        }else{
            img1.setImageResource(R.drawable.back);
            img2.setImageResource(R.drawable.back);
            img3.setImageResource(R.drawable.back);
            img4.setImageResource(R.drawable.back);
            img5.setImageResource(R.drawable.back);
            img6.setImageResource(R.drawable.back);
            img7.setImageResource(R.drawable.back);
            img8.setImageResource(R.drawable.back);
            img9.setImageResource(R.drawable.back);
            img10.setImageResource(R.drawable.back);
            img11.setImageResource(R.drawable.back);
            img12.setImageResource(R.drawable.back);

            //change the player turn
            if(turn ==1){
                turn=2;
                tv_p1.setTextColor(Color.GRAY);
                tv_p2.setTextColor(Color.BLACK);
            }else if (turn==2){
                turn=1;
                tv_p2.setTextColor(Color.GRAY);
                tv_p1.setTextColor(Color.BLACK);
            }
        }
        img1.setEnabled(true);
        img2.setEnabled(true);
        img3.setEnabled(true);
        img4.setEnabled(true);
        img5.setEnabled(true);
        img6.setEnabled(true);
        img7.setEnabled(true);
        img8.setEnabled(true);
        img9.setEnabled(true);
        img10.setEnabled(true);
        img11.setEnabled(true);
        img12.setEnabled(true);

        //check if the game has ended
        checkEnd();
    }

    private void checkEnd(){
        if(img1.getVisibility()==View.INVISIBLE&&
                img2.getVisibility()==View.INVISIBLE&&
                img3.getVisibility()==View.INVISIBLE&&
                img4.getVisibility()==View.INVISIBLE&&
                img5.getVisibility()==View.INVISIBLE&&
                img6.getVisibility()==View.INVISIBLE&&
                img7.getVisibility()==View.INVISIBLE&&
                img8.getVisibility()==View.INVISIBLE&&
                img9.getVisibility()==View.INVISIBLE&&
                img10.getVisibility()==View.INVISIBLE&&
                img11.getVisibility()==View.INVISIBLE&&
                img12.getVisibility()==View.INVISIBLE){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
            alertDialogBuilder
                    .setMessage("GAME OVER!\nPlayer 1:"+playerPoints+ "\nPlayer 2:"+cpuPoints)
                    .setCancelable(false)
                    .setPositiveButton("NEW", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    })
                    .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void frontOfCardResources(){
        image101=R.drawable.ic_image101;
        image102=R.drawable.ic_image102;
        image103=R.drawable.ic_image103;
        image104=R.drawable.ic_image104;
        image105=R.drawable.ic_image105;
        image106=R.drawable.ic_image106;
        image201=R.drawable.ic_image201;
        image202=R.drawable.ic_image202;
        image203=R.drawable.ic_image203;
        image204=R.drawable.ic_image204;
        image205=R.drawable.ic_image205;
        image206=R.drawable.ic_image206;
    }

    private void runTimer() {
        timerText = findViewById(R.id.txtTime);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%d:%02d:%02d", hours, minutes, secs);
                timerText.setText(time);
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

}