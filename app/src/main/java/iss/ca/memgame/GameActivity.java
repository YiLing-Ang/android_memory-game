package iss.ca.memgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
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

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    TextView timerText;
    Button btn;
    Timer timer;
    int seconds = 0;
    boolean running;

    TextView tv_p1, tv_p2, txtNumOfMatches;

    ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12;
    ImageView[] imgViews = new ImageView[12];
    ImageView[] matchedImg = new ImageView[12];

    //array for images
    Integer[] cardsArray = {101, 102, 103, 104, 105, 106, 201, 202, 203, 204, 205, 206};

    //actual images
    int image101, image102, image103, image104, image105, image106,
            image201, image202, image203, image204, image205, image206;

    int firstCard, secondCard;
    int clickedFirst, clickedSecond;
    int cardNumber = 1;

    int turn = 1;
    int playerPoints = 0, cpuPoints = 0;
    int totalPoints = playerPoints+cpuPoints;

    public Bitmap dlimg1, dlimg2, dlimg3, dlimg4, dlimg5, dlimg6;
    InputStream in = null;
    List<String> imgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        running=true;
        runTimer();

        Intent intent = getIntent();
        Resources r = getResources();
        String name = getPackageName();
        imgList = (List<String>) getIntent().getSerializableExtra("imgList");

        loadImages();

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

        imgViews[0] = img1;
        imgViews[1] = img2;
        imgViews[2] = img3;
        imgViews[3] = img4;
        imgViews[4] = img5;
        imgViews[5] = img6;
        imgViews[6] = img7;
        imgViews[7] = img8;
        imgViews[8] = img9;
        imgViews[9] = img10;
        imgViews[10] = img11;
        imgViews[11] = img12;

        //load the card images
        frontOfCardResources();

        //shuffle the images
        Collections.shuffle(Arrays.asList(cardsArray));

        //changing the color of second player when he is inactive
        tv_p2.setTextColor(Color.GRAY);

        for(int i=0; i< imgViews.length;i++){
            imgViews[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        int theCard;
        switch(id){
            case R.id.img1:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img1, theCard);
                break;
            case R.id.img2:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img2, theCard);
                break;
            case R.id.img3:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img3, theCard);
                break;
            case R.id.img4:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img4, theCard);
                break;
            case R.id.img5:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img5, theCard);
                break;
            case R.id.img6:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img6, theCard);
                break;
            case R.id.img7:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img7, theCard);
                break;
            case R.id.img8:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img8, theCard);
                break;
            case R.id.img9:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img9, theCard);
                break;
            case R.id.img10:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img10, theCard);
                break;
            case R.id.img11:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img11, theCard);
                break;
            case R.id.img12:
                theCard = Integer.parseInt((String) v.getTag());
                playGame(img12, theCard);
                break;
        }
    }

    private void playGame(ImageView iv, int card) {
        //set the correct image to the imageview
        switch(cardsArray[card]){
            case 101:
            case 201:
                iv.setImageBitmap(dlimg1);
                break;
            case 102:
            case 202:
                iv.setImageBitmap(dlimg2);
                break;
            case 103:
            case 203:
                iv.setImageBitmap(dlimg3);
                break;
            case 104:
            case 204:
                iv.setImageBitmap(dlimg4);
                break;
            case 105:
            case 205:
                iv.setImageBitmap(dlimg5);
                break;
            case 106:
            case 206:
                iv.setImageBitmap(dlimg6);
                break;
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

            for(int i = 0; i < imgViews.length; i++){
                imgViews[i].setEnabled(false);
            }

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
            checkMatch(clickedFirst);
            checkMatch(clickedSecond);

            MediaPlayer correctSound = MediaPlayer.create(GameActivity.this, R.raw.correct);
            correctSound.start();

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
            for(int i = 0; i < imgViews.length; i++){
                if(matchedImg[i] == null)
                {
                    imgViews[i].setImageResource(R.drawable.back);
                }
            }

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

        for(int i = 0; i < imgViews.length; i++){
            imgViews[i].setEnabled(true);
        }

        //check if the game has ended
        checkEnd();
    }

    private void checkEnd(){
        if(!img1.isClickable() &&
                !img2.isClickable() &&
                !img3.isClickable() &&
                !img4.isClickable() &&
                !img5.isClickable() &&
                !img6.isClickable() &&
                !img7.isClickable() &&
                !img8.isClickable() &&
                !img9.isClickable() &&
                !img10.isClickable() &&
                !img11.isClickable() &&
                !img12.isClickable()){

            running = false;

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
            alertDialogBuilder
                    .setMessage("GAME OVER!\nPlayer 1: "+playerPoints+ "\nPlayer 2: "+cpuPoints)
                    .setCancelable(false)
                    .setPositiveButton("NEW GAME", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
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

    public void loadImages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //loop through list of image URLs and setting bitmap images to ImageView in layout grid
                    for (int i = 1; i <= 6; i++) {
                        if (Thread.interrupted())
                            return;
                        URL url = new URL(imgList.get(i - 1));
                        URLConnection urlConn = url.openConnection();
                        HttpURLConnection httpConn = (HttpURLConnection) urlConn;
                        httpConn.connect();

                        //raw data from image URL
                        in = httpConn.getInputStream();
                        switch (i){
                            case 1:dlimg1 = BitmapFactory.decodeStream(in);
                                break;
                            case 2:dlimg2 = BitmapFactory.decodeStream(in);
                                break;
                            case 3:dlimg3 = BitmapFactory.decodeStream(in);
                                break;
                            case 4:dlimg4 = BitmapFactory.decodeStream(in);
                                break;
                            case 5:dlimg5 = BitmapFactory.decodeStream(in);
                                break;
                            case 6:dlimg6 = BitmapFactory.decodeStream(in);
                                break;
                        }
                        /*                       Thread.sleep(100);*/
                    }
                }
                catch (MalformedURLException e)
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
        }).start();
    }

    public void checkMatch(int i){
        switch(i){
            case 0:
                img1.setClickable(false);
                matchedImg[0] = img1;
                break;
            case 1:
                img2.setClickable(false);
                matchedImg[1] = img2;
                break;
            case 2:
                img3.setClickable(false);
                matchedImg[2] = img3;
                break;
            case 3:
                img4.setClickable(false);
                matchedImg[3] = img4;
                break;
            case 4:
                img5.setClickable(false);
                matchedImg[4] = img5;
                break;
            case 5:
                img6.setClickable(false);
                matchedImg[5] = img6;
                break;
            case 6:
                img7.setClickable(false);
                matchedImg[6] = img7;
                break;
            case 7:
                img8.setClickable(false);
                matchedImg[7] = img8;
                break;
            case 8:
                img9.setClickable(false);
                matchedImg[8] = img9;
                break;
            case 9:
                img10.setClickable(false);
                matchedImg[9] = img10;
                break;
            case 10:
                img11.setClickable(false);
                matchedImg[10] = img11;
                break;
            case 11:
                img12.setClickable(false);
                matchedImg[11] = img12;
                break;
        }
    }
}