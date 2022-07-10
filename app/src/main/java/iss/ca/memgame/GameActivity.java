package iss.ca.memgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity{

    //values assigned to images set to ImageView
    private Integer[] cardsArray = {101, 102, 103, 104, 105, 106, 201, 202, 203, 204, 205, 206};

    private TextView tv_p1, tv_p2, txtNumOfMatches, timerText;

    //tracks if game is currently in progress
    private boolean running;

    //variables for getting ImageView object names in a loop
    private Resources r;
    private String name;

    //for storing downloaded images from received URLs
    private Bitmap[] bitmapArray = new Bitmap[6];
    private ImageView[] imgViews = new ImageView[12];
    //to exclude matched pairs from being flipped back again
    private ImageView[] matchedImg = new ImageView[12];


    private int seconds = 0;
    //tracks image set to the selected card
    private int firstCardImg, secondCardImg;
    //tracks ImageView of the selected card
    private int firstCardTag, secondCardTag;
    //tracks if 1, 2 or no cards opened currently
    private int cardNumber = 1;
    //tracks current player's turn
    private int turn = 1;
    private int playerPoints = 0, cpuPoints = 0;
    private int totalPoints = playerPoints+cpuPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //instantiating for other methods in class to use
        r = getResources();
        name = getPackageName();

        //downloading images, setting to ImageViews and setting onClickListeners
        loadImages();

        running=true;
        runTimer();

        tv_p1 = (TextView) findViewById(R.id.tv_p1);
        tv_p2 = (TextView) findViewById(R.id.tv_p2);
        txtNumOfMatches = (TextView)findViewById(R.id.txtNumOfMatches);

        //shuffle the images
        Collections.shuffle(Arrays.asList(cardsArray));

        //changing the color of second player when he is inactive
        tv_p2.setTextColor(Color.GRAY);
    }

    public void loadImages(){
        List<String> imgList = (List<String>) getIntent().getSerializableExtra("imgList");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //loop through list of image URLs and setting bitmap images to ImageView in layout grid
                    for (int i = 1; i <= 6; i++) {
                        bitmapArray[i-1] = ImageDownload.downloadImg(imgList, i);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i=1; i<=12; i++){
                                ImageView img = findViewById(r.getIdentifier("img" + i, "id", name));
                                img.setTag(String.valueOf(i-1));
                                imgViews[i-1]=img;
                                img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        playGame(img, Integer.parseInt((String) v.getTag()));
                                    }
                                });
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void playGame(ImageView iv, int tag) {
        int card_i = cardsArray[tag]%100;
        iv.setImageBitmap(bitmapArray[card_i-1]);

        //check if there's already an open card, assigns selected card parameters (image identifier + ImageView tag)
        //to a temporary variable for further processing
        if (cardNumber == 1) {
            cardNumber = 2;
            firstCardImg = card_i;
            firstCardTag = tag;

            iv.setEnabled(false);
        }

        else if (cardNumber == 2) {
            secondCardImg = card_i;
            cardNumber = 1;
            secondCardTag = tag;

            //disables all unmatched cards before calculate() runs
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
        //if images are equal, keep the cards and add points
        if(firstCardImg == secondCardImg){
            matchedFollowUp(firstCardTag);
            matchedFollowUp(secondCardTag);

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
        }
        else
        {
            //flips all non-matched cards back to placeholder
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

        for (int i = 0; i < imgViews.length; i++){
            imgViews[i].setEnabled(true);
        }

        //check if the game has ended
        checkEnd();
    }

    public void matchedFollowUp(int i){
        //disables specified ImageViews permanently and adds to list for exclusion from flipping back to placeholder
        ImageView img = findViewById(r.getIdentifier("img" + (i+1), "id", name));
        img.setClickable(false);
        matchedImg[i]=img;
    }

    private void checkEnd(){
        if(totalPoints==6)
        {
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