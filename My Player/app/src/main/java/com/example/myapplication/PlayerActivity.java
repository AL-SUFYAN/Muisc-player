package com.example.myapplication;

import static com.example.myapplication.R.drawable.ic_skip;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.icu.text.CaseMap;
import android.os.Build;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.icu.text.CaseMap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;




import org.junit.internal.runners.statements.Fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


public class PlayerActivity extends AppCompatActivity{
    Button playbtn, btnnext, btnprev, btnff, btnfr;
    TextView txtesname, txtsstart, txtsstop;
    SeekBar seekmusic;
    ImageView imageView;
    int position;
    boolean isPlaying =false;
    CaseMap.Title title;
    String sname;
    public static final String EXTRA_NAME ="sname";
    static MediaPlayer mediaPlayer;
    ArrayList<File> mSongs;
    Thread ubdetseekbar;
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().setTitle("Now Playing");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        btnnext = findViewById(R.id.btnnext);
        btnprev = findViewById(R.id.btnprev);
        btnff = findViewById(R.id.btnff);
        playbtn = findViewById(R.id.playbtn);
        btnfr = findViewById(R.id.btnfr);
        txtsstart = findViewById(R.id.txtsstart);
        txtesname = findViewById(R.id.txtn);
        seekmusic = findViewById(R.id.seekbar);
        txtsstop = findViewById(R.id.txtsstop);
        imageView = findViewById(R.id.imgeview);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }



        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mSongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        txtesname.setSelected(true);
        Uri uri =Uri.parse(mSongs.get(position).toString());
        sname =mSongs.get(position).toString();
        File file = new File(sname);
        txtesname.setText(file.getName());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        ubdetseekbar =new Thread()
        {
            @Override
            public void run() {
                int totalDuration =mediaPlayer.getDuration();
                int currentposition =0;

                while (currentposition<totalDuration)
                {
                    try {
                        sleep(300);
                        currentposition =mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentposition);
                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };



        seekmusic.setMax(mediaPlayer.getDuration());
        ubdetseekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Toast.makeText(getApplicationContext(),":"+progress/1000,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"1111",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
//                Toast.makeText(getApplicationContext(),"1111",Toast.LENGTH_SHORT).show();

            }
        });
        String endTime = crateTime(mediaPlayer.getDuration());
        txtsstop.setText(endTime);

        final Handler handler = new Handler();
        final int delay =1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String crateTime =crateTime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(crateTime);
                String endTime = crateTime(mediaPlayer.getDuration());
                txtsstop.setText(endTime);
                seekmusic.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, delay);

            }
        },delay);


        playbtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (mediaPlayer.isPlaying())
                {
                    playbtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                    seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                    seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                }
                else
                {
                    playbtn.setBackgroundResource(R.drawable.ic_pause);
                    seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                    seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    mediaPlayer.start();
                }

            }
        });
        //////////////
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startNewSuond();
            }
        });

        btnnext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewSuond();

            }
        });

        btnprev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewSuon();

            }
        });
        btnff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        btnfr.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }

        });
    }


    private void startNewSuond(){
        mediaPlayer.stop();
        mediaPlayer.release();
        position =((position+1)%mSongs.size());
        Uri u = Uri.parse(mSongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        sname =mSongs.get(position).getName();
        txtesname.setText(sname);
        seekmusic.setMax(mediaPlayer.getDuration());
        playbtn.setBackgroundResource(R.drawable.ic_pause);
        startAmeg(imageView);
        mediaPlayer.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //btnnext.performClick();
                startNewSuond();
            }
        });
    }

    private void startNewSuon(){
        mediaPlayer.stop();
        mediaPlayer.release();
        position =((position-1)<0)?(mSongs.size()-1):(position-1);
        Uri u = Uri.parse(mSongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        sname =mSongs.get(position).getName();
        seekmusic.setMax(mediaPlayer.getDuration());
        txtesname.setText(sname);
        playbtn.setBackgroundResource(R.drawable.ic_pause);
        startAme(imageView);
        mediaPlayer.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //btnnext.performClick();
                startNewSuon();
            }
        });

    }


    public void startAmeg(View view)
    {
        ObjectAnimator animator =ObjectAnimator.ofFloat(imageView,"rotation", 0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public void startAme(View view)
    {
        ObjectAnimator animator =ObjectAnimator.ofFloat(imageView,"rotation", -0f,-360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String crateTime (int duration)
    {
        String time ="";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";

        if (sec<10)
        {
            time+="0";

        }
        time+=sec;

        return time;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2,menu);
        return true;
    }
    public void info(MenuItem item){
        AlertDialog.Builder alert =new AlertDialog.Builder(PlayerActivity.this);
        alert.setTitle("About");
        alert
                .setMessage(R.string.abuot)
                .setCancelable(false)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alertDialog =alert.create();
        alertDialog.show();
    }
}



