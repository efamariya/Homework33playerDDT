package com.example.homework33playerddt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Runnable {
    //кнопка запуска
    private FloatingActionButton floatingActionButton;
    //кнопка стоп
    private FloatingActionButton floatingActionButton2;//new
    //кнопка следующей композиции
    private ImageButton imageButton;
    // кнопка предыдущей композиции
    private ImageButton imageButton3;
    // Аудио проигрыватель
    private MediaPlayer player = new MediaPlayer();
    AudioManager audioManager;
    // название воспроизведения
    private TextView titleMusic;
    // массив названия песен
    String[] arraySong = new String[]{"ДДТ - Просвистела", "ДДТ - Это всё",};
    // массив музыкальных файлов
    Integer[] arayMusicFile = new Integer[]{R.raw.ddt_prosvistela, R.raw.ddt_eto_vse,};
    // ползунок музыки
    SeekBar seekBar;
    // информация времени музыки ползунка
    TextView textViewSeekBar;
    // громкость тише Button
    private ImageButton imageButton1;
    // громкость громче Button
    private ImageButton imageButton2;
    // громкость SeekBar
    SeekBar seekBar2;
    // интернет вещание
    private Button buttonStream;

    // переменная хранит текущий номер файла, который воспроизводится
    int numberMusic; // = 0 по умолчанию

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        floatingActionButton = findViewById(R.id.play);
        floatingActionButton2 = findViewById(R.id.stop);
        titleMusic = findViewById(R.id.titleMusicId);
        imageButton = findViewById(R.id.imageButton4);
        imageButton3 = findViewById(R.id.imageButton5);
        seekBar = findViewById(R.id.seekBar);
        textViewSeekBar = findViewById(R.id.textViewSeekBar);
        imageButton1 = findViewById(R.id.imageButton6);
        imageButton2 = findViewById(R.id.imageButton7);
        seekBar2 = findViewById(R.id.seekBar2);


        //  ползунок громкости

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar2.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int newVolume, boolean b) {
                // textview.setText("Media Volume : " + newVolume);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        // кнопки громкости
        imageButton2.setOnClickListener((listener -> {
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        }));
        imageButton1.setOnClickListener((listener -> {
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        }));

        // кнопка старт

        floatingActionButton.setOnClickListener(listener -> {

            player = MediaPlayer.create(this, arayMusicFile[numberMusic]);
            player.start();
            titleMusic.setText(arraySong[numberMusic]);
            seekBar.setProgress(0);
            seekBar.setMax(player.getDuration()); // ограничение seekBar длинной трека
            new Thread(this).start(); // движение ползунка
        });

        // кнопка стоп

        floatingActionButton2.setOnClickListener(listener -> {
            player.stop();
        });

        // кнопка следующей композиции

        imageButton.setOnClickListener(listener -> {
            numberMusic++;
            player.stop();
            player = MediaPlayer.create(this, arayMusicFile[numberMusic]);
            player.start();
            titleMusic.setText(arraySong[numberMusic]);
            seekBar.setMax(player.getDuration()); // ограничение seekBar длинной трека
            new Thread(this).start(); // движение ползунка

        });

        // кнопка предыдущей композиции
        imageButton3.setOnClickListener(listener -> {
            numberMusic--;
            player.stop();
            player = MediaPlayer.create(this, arayMusicFile[numberMusic]);
            player.start();
            titleMusic.setText(arraySong[numberMusic]);
            seekBar.setMax(player.getDuration()); // ограничение seekBar длинной трека
            new Thread(this).start(); // движение ползунка
        });


        // создание слушателя изменения seekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // метод при перетаскивании ползунка по шкале,
            // где progress позволяет получить новые значения ползунка(позже progress назначается длина трека в миллесекундах)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSeekBar.setVisibility(View.VISIBLE); // установка видимости textViewSeekBar
                // textViewSeekBar.setVisibility(View.INVISIBLE); // установка не видимости textViewSeekBar
                // Math.ceil() - округление до целого в большую сторону
                int timeTrack = (int) Math.ceil(progress / 1000f); // перевод времени из миллисекунд в секунды

                // вывод на экран времени отсчёта трека
                if (timeTrack < 10) {
                    textViewSeekBar.setText("00:0" + timeTrack);
                }
                if (timeTrack < 60) {
                    textViewSeekBar.setText("00:" + timeTrack);
                }
                if (timeTrack >= 60) {
                    textViewSeekBar.setText("01:" + (timeTrack - 60));
                }
                if (timeTrack >= 120) {
                    textViewSeekBar.setText("02:" + (timeTrack - 120));
                }
                if (timeTrack >= 180) {
                    textViewSeekBar.setText("03:" + (timeTrack - 180));
                }
                if (timeTrack >= 240) {
                    textViewSeekBar.setText("04:" + (timeTrack - 240));
                } else if (timeTrack >= 300) {
                    textViewSeekBar.setText("05:" + (timeTrack - 300));
                }

                // передвижение времени отсчёта трека
                double percentTrack = progress / (double) seekBar.getMax(); // получение процента проигранного трека (проигранное время делится на длину трека)
                // seekBar.getX() - начало seekBar по оси Х
                // seekBar.getWidth() - ширина контейнера seekBar
                // 0.92 - поправочный коэффициент (так как seekBar занимает не всю ширину своего контейнера)
                textViewSeekBar.setX(seekBar.getX() + Math.round(seekBar.getWidth() * percentTrack * 0.92));

                if (progress > 0 && player != null && !player.isPlaying()) { // если mediaPlayer не пустой и mediaPlayer не воспроизводится
                    //  clearMediaPlayer(); // остановка и очиска MediaPlayer
                    // назначение кнопке картинки play
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                    MainActivity.this.seekBar.setProgress(0); // установление seekBar значения 0
                }
            }

            // метод при начале перетаскивания ползунка по шкале
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textViewSeekBar.setVisibility(View.INVISIBLE); // установление видимости seekBarHint
            }

            // метод при завершении перетаскивания ползунка по шкале
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player != null && player.isPlaying()) { // если mediaPlayer не пустой и mediaPlayer воспроизводится
                    player.seekTo(seekBar.getProgress()); // обновление позиции трека при изменении seekBar
                }
            }
        });

    }

    // метод дополнительного потока для обновления SeekBar
    @Override
    public void run() {
        int currentPosition = player.getCurrentPosition(); // считывание текущей позиции трека
        int total = player.getDuration(); // считывание длины трека

        // бесконечный цикл при условии не нулевого mediaPlayer, проигрывания трека и текущей позиции трека меньше длины трека
        while (player != null && player.isPlaying() && currentPosition < total) {
            try {

                Thread.sleep(1000); // засыпание вспомогательного потока на 1 секунду
                currentPosition = player.getCurrentPosition(); // обновление текущей позиции трека

            } catch (InterruptedException e) { // вызывается в случае блокировки данного потока
                e.printStackTrace();
                return; // выброс из цикла
            } catch (Exception e) {
                return;
            }

            seekBar.setProgress(currentPosition); // обновление seekBar текущей позицией трека
        }
    }
}