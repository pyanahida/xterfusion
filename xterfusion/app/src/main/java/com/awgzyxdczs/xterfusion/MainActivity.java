package com.awgzyxdczs.xterfusion;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView tv;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final StringBuilder buffer = new StringBuilder();
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);

        // 打开时立刻播放音频
        player = MediaPlayer.create(this, R.raw.xterfusion);
        if (player != null) {
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    player = null;
                }
            });
            player.start();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                playAll();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    /** 对应 Python 的 nl() */
    private void nl() {
        buffer.append('\n');
        flush();
    }

    /** 对应 Python 的 print(" ", end="") */
    private void space() {
        buffer.append(' ');
        flush();
    }

    private void flush() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                tv.setText(buffer.toString());
            }
        });
    }

    /**
     * 对应 Python 的 unit()
     */
    private void unit(boolean isX, double bpm, double stay, double h, boolean dot) {
        double sleepFor = 240.0 / (bpm * h);
        double realSleep = sleepFor * stay * (dot ? 1.5 : 1.0);

        buffer.append(isX ? 'X' : 'O');
        flush();

        try {
            Thread.sleep((long) (realSleep * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void unit(boolean isX, double bpm, double stay) {
        unit(isX, bpm, stay, 16, false);
    }

    private void unit(boolean isX, double bpm, double stay, double h) {
        unit(isX, bpm, stay, h, false);
    }

    // ===== 各段 =====

    private void a1(boolean t) {
        unit(t, 130, 4);
        unit(t, 130, 4);
        unit(t, 130, 2);
        unit(t, 130, 2);
        unit(t, 130, 4);
        nl();
    }

    private void a2(boolean t) {
        unit(t, 130, 2);
        unit(t, 130, 3);
        unit(t, 130, 3);
        unit(t, 130, 2);
        unit(t, 130, 2);
        unit(t, 130, 4);
        nl();
    }

    private void a3(boolean t) {
        unit(t, 135, 3);
        unit(t, 135, 3);
        unit(t, 135, 2);
        unit(t, 135, 1, 12);
        unit(t, 135, 1, 12);
        unit(t, 135, 1, 12);
        unit(t, 135, 4);
        nl();
    }

    private void a4(boolean t) {
        double x = t ? 140 : 145;
        unit(t, x, 1);
        unit(t, x, 2);
        unit(t, x, 1);
        unit(t, x, 2);
        unit(t, x, 1);
        unit(t, x, 1);
        unit(t, x, 2);
        unit(t, x, 2);
        unit(t, x, 4);
        nl();
    }

    private void b1(boolean t) {
        unit(t, 150, 2);
        unit(t, 150, 2);
        unit(t, 150, 4);
        nl();
    }

    private void b2(boolean t) {
        unit(t, 150, 1);
        unit(t, 150, 1);
        unit(t, 150, 2);
        unit(t, 150, 4);
        nl();
    }

    private void b3(boolean t) {
        for (int i = 0; i < 2; i++) {
            unit(t, 160, 1);
            unit(t, 160, 1);
            unit(t, 160, 2);
        }
        nl();
    }

    private void b4(boolean t) {
        for (int i = 0; i < 4; i++) {
            unit(t, 160, 1, 12);
        }
        unit(t, 160, 2, 12);
        nl();
    }

    private void b5(boolean t) {
        for (int i = 0; i < 6; i++) {
            unit(t, 170, 1);
        }
        unit(t, 170, 2);
        nl();
    }

    private void b6(boolean t) {
        unit(t, 170, 1, 32);
        unit(t, 170, 1, 32);
        unit(t, 170, 1);
        unit(t, 170, 2);
        unit(t, 170, 2);
        unit(t, 170, 2);
        nl();
    }

    private void c1(boolean t, double bpm) {
        unit(t, bpm, 2);
        unit(t, bpm, 2);
        space();
    }

    private void playAll() {
        Runnable[] order = new Runnable[] {
            new Runnable() { public void run() { a1(true); a1(false); } },
            new Runnable() { public void run() { a2(true); a2(false); } },
            new Runnable() { public void run() { a3(true); a3(false); } },
            new Runnable() { public void run() { a4(true); a4(false); } },
            new Runnable() { public void run() { b1(true); b1(false); } },
            new Runnable() { public void run() { b2(true); b2(false); } },
            new Runnable() { public void run() { b3(true); b3(false); } },
            new Runnable() { public void run() { b4(true); b4(false); } },
            new Runnable() { public void run() { b5(true); b5(false); } },
            new Runnable() { public void run() { b6(true); b6(false); } }
        };

        for (Runnable r : order) {
            r.run();
        }

        for (double bpm : new double[] {180, 190}) {
            for (int i = 0; i < 2; i++) {
                c1(true, bpm);
                c1(false, bpm);
            }
        }
        nl();

        for (double bpm = 200; bpm < 260; bpm += 10) {
            for (int i = 0; i < 4; i++) {
                unit(true, bpm, 2);
                unit(false, bpm, 2);
            }
            space();
        }
        nl();

        for (int i = 0; i < 31; i++) {
            unit(true, 256, 1);
        }
        nl();
    }
}
