package com.example.music_player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv,currentTimeTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay,nextBtn,previousBtn,musicIcon,fastBtn,backBtn,shuffleBtn, repeatBtn;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x;
    boolean isShuffle = false;
    boolean isRepeat = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv= findViewById(R.id.total_time);
        seekBar= findViewById(R.id.seek_bar);
        pausePlay= findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        fastBtn = findViewById(R.id.fast_next);
        backBtn = findViewById(R.id.fast_previous);
        shuffleBtn = findViewById(R.id.shuffle);
        repeatBtn = findViewById(R.id.repeat);

        titleTv.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    }else{
                        pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    void setResourcesWithMusic(){
        currentSong =songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());

        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());
        fastBtn.setOnClickListener(v -> fastForward());
        backBtn.setOnClickListener(v -> fastBackward());

        // Shuffle button click listener
        shuffleBtn.setOnClickListener(v -> {
            isShuffle = !isShuffle;
            shuffleBtn.setImageResource(isShuffle ? R.drawable.baseline_shuffle_on_24 : R.drawable.baseline_shuffle_24);
        });

        // Repeat button click listener
        repeatBtn.setOnClickListener(v -> {
            isRepeat = !isRepeat;
            repeatBtn.setImageResource(isRepeat ? R.drawable.baseline_repeat_on_24 : R.drawable.baseline_repeat_24);
        });

        playMusic();
    }


//    private void playMusic(){
//
//        mediaPlayer.reset();
//        try {
//            mediaPlayer.setDataSource(currentSong.getPath());
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            seekBar.setProgress(0);
//            seekBar.setMax(mediaPlayer.getDuration());
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//    }

    private void playMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());

            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeat) {
                    playMusic();
                } else {
                    playNextSong();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    private void playNextSong(){
//
//        if(MyMediaPlayer.currentIndex == songsList.size()-1)
//            return;
//        MyMediaPlayer.currentIndex +=1;
//        mediaPlayer.reset();
//        setResourcesWithMusic();
//
//    }


    private void playNextSong() {
        if (isShuffle) {
            Random random = new Random();
            MyMediaPlayer.currentIndex = random.nextInt(songsList.size());
        } else {
            if (MyMediaPlayer.currentIndex == songsList.size() - 1) {
                if (isRepeat) {
                    MyMediaPlayer.currentIndex = 0;
                } else {
                    return;
                }
            } else {
                MyMediaPlayer.currentIndex += 1;
            }
        }
        mediaPlayer.reset();
        setResourcesWithMusic();
    }


//    private void playPreviousSong(){
//
//        if(MyMediaPlayer.currentIndex == 0)
//            return;
//        MyMediaPlayer.currentIndex -=1;
//        mediaPlayer.reset();
//        setResourcesWithMusic();
//    }

    private void playPreviousSong() {
        if (isShuffle) {
            Random random = new Random();
            MyMediaPlayer.currentIndex = random.nextInt(songsList.size());
        } else {
            if (MyMediaPlayer.currentIndex == 0) {
                if (isRepeat) {
                    MyMediaPlayer.currentIndex = songsList.size() - 1;
                } else {
                    return;
                }
            } else {
                MyMediaPlayer.currentIndex -= 1;
            }
        }
        mediaPlayer.reset();
        setResourcesWithMusic();
    }


    private void pausePlay(){

        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();

    }

    private void fastForward(){

        if(mediaPlayer.isPlaying())
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);

    }

    private void fastBackward(){

        if(mediaPlayer.isPlaying())
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
    }


    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}