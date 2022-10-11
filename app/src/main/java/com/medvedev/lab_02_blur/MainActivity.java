package com.medvedev.lab_02_blur;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    SurfaceView surfaceView;

    Spinner threadSpinner;
    public int thread;
    public int blurLv = 3;

    TextView elapsedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.surfaceView);
        String[] countOfThreads = {"1","2","3","4","5","6","7","8"};
        threadSpinner = findViewById(R.id.ThreadSpinner);
        ArrayAdapter<Integer> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countOfThreads);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        threadSpinner.setAdapter(adapter);
        SeekBar seek = findViewById(R.id.BlurSeek);
        TextView textBlur = findViewById(R.id.textBlur);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textBlur.setText("Blur level: " + String.valueOf(i));
                blurLv = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    class Worker implements Runnable
    {
        public int y0;
        public int y1;

        public  int w;
        public int h;

        public Bitmap bmp;
        public Bitmap res;

        public void run()
        {
            for(int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int red = 0;
                    int green = 0;
                    int blue = 0;

                    for (int v = 0; v < blurLv; v++)
                        for (int u = 0; u < blurLv; u++) {
                            int px = u + x - blurLv / 2;
                            int py = v + y - blurLv / 2;

                            if (px < 0) px = 0;
                            if (py < 0) py = 0;
                            if (px >= w) px = w - 1;
                            if (py >= h) py = h - 1;

                            int c = bmp.getPixel(px, py);

                            red += Color.red(c);
                            green += Color.green(c);
                            blue += Color.blue(c);
                        }

                    red /= blurLv * blurLv;
                    green /= blurLv * blurLv;
                    blue /= blurLv * blurLv;

                    res.setPixel(x, y, Color.rgb(red, green, blue));
                }
            }
        }
    }

    public void onClick(View v)
    {
        thread = threadSpinner.getSelectedItemPosition() + 1;

        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.windows);

        bmp = Bitmap.createScaledBitmap(bmp,256,256,false);
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        Bitmap res = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Thread[] thr = new Thread[thread];
        Worker[] wor = new Worker[thread];

        int s = h / thr.length;

        for (int i = 0; i < thread; i++)
        {
            wor[i] = new Worker();
            wor[i].bmp = bmp;
            wor[i].res = res;
            wor[i].w = w;
            wor[i].h = h;
            wor[i].y0 = s * i;
            wor[i].y1 = wor[i].y0 + s;
            thr[i] = new Thread(wor[i]);
            thr[i].start();
        }

        for (int i = 0; i < thread; i++)
        {
            try {
                thr[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        surfaceView.setForeground(new BitmapDrawable(res));
    }
}