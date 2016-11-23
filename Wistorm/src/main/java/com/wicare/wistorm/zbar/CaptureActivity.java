package com.wicare.wistorm.zbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.wicare.wistorm.R;
import com.wicare.wistorm.zbar.camera.CameraManager;
import com.wicare.wistorm.zbar.decode.CaptureActivityHandler;
import com.wicare.wistorm.zbar.decode.InactivityTimer;
import com.wicare.wistorm.zbar.view.ViewfinderView;

import java.io.IOException;

public class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback,View.OnClickListener {

    private Button btnBack;
    private InactivityTimer inactivityTimer;
    private CaptureActivityHandler handler;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private boolean flag = true;
    private boolean hasSurface = false;
    private ViewfinderView viewfinderView;
    private ScreenOffReceiver mScreenOffReceiver = new ScreenOffReceiver();


    private static final float BEEP_VOLUME = 0.20f;
    private static final long VIBRATE_DURATION = 200L;

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this);
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }


    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public void handleDecode(String result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        Intent intent = new Intent();
        intent.putExtra("result", result);
        Message message = Message.obtain(getHandler(), R.id.return_scan_result, intent);
        message.sendToTarget();
    }

//    public void light() {
//        if (this.flag) {
//            CameraManager.get().openLight();
//            this.flag = false;
//            this.iv_flashlight.setImageDrawable(getResources().getDrawable(R.drawable.icon_light_hover));
//            return;
//        }
//        CameraManager.get().offLight();
//        this.flag = true;
//        this.iv_flashlight.setImageDrawable(getResources().getDrawable(R.drawable.icon_light));
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ws_zbar_capture);
        CameraManager.init(getApplication());
        this.viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		this.btnBack = (Button) findViewById(R.id.btn_back);
		this.btnBack.setOnClickListener(this);
        this.hasSurface = false;
        this.inactivityTimer = new InactivityTimer(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(this.mScreenOffReceiver, intentFilter);
//        initView();
    }

//    /**
//     * 标题和返回箭头
//     */
//    private void initView() {
//        ActionBar mActionBar = getSupportActionBar();
//        mActionBar.setHomeButtonEnabled(true);
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        this.finish();
//        return super.onSupportNavigateUp();
//    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().offLight();
        inactivityTimer.onPause();
        CameraManager.get().closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        handler = null;
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        inactivityTimer.onResume();
        vibrate = true;
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        unregisterReceiver(this.mScreenOffReceiver);
        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
//            light();
            CaptureActivity.this.finish();
        }
    }

    private class ScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Log.d("CaptureActivity", "CaptureActivity receive screen off command ++");
            CaptureActivity.this.finish();
        }
    }
}