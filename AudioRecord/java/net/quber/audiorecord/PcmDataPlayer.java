package net.quber.audiorecord;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static net.quber.audiorecord.MainActivity.mPCMDataPath;

public class PcmDataPlayer {
    public boolean isPlaying = false;
    public boolean isRepeat = false;
    private Context mContext;
    private AudioTrack mAudioTrack = null;
    private int mBufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);

    PcmDataPlayer(Context context) {
        mContext = context;
    }

    public void pcmDataPlay() {
        if (isPlaying) {
            isPlaying = false;
        } else {
            File file = new File(mPCMDataPath + "music.pcm");
            if (!file.exists()) {
                Toast.makeText(mContext, mPCMDataPath + "music.pcm not found", Toast.LENGTH_SHORT).show();
                ((Button) ((Activity) mContext).findViewById(R.id.btn_play)).setText("PLAY");
                return;
            }
            isPlaying = true;


            if (mAudioTrack == null) {
                if (false) {
                    mAudioTrack = new AudioTrack.Builder()
                            .setAudioAttributes(new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_ASSISTANT)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .build())
                            .setAudioFormat(new AudioFormat.Builder()
                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                    .setSampleRate(16000)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO/*CHANNEL_OUT_STEREO CHANNEL_OUT_MONO*/)
                                    .build())
                            .setBufferSizeInBytes(mBufferSize)
                            .build();
                } else {
                    int mStreamType = 3;
                    mAudioTrack = new AudioTrack(mStreamType, 16000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                            mBufferSize, AudioTrack.MODE_STREAM);
                }
            }
            Toast.makeText(mContext, "REPEAT(" + isRepeat + ")", Toast.LENGTH_SHORT).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] writeData = new byte[mBufferSize];
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(mPCMDataPath + "music.pcm");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    DataInputStream dis = new DataInputStream(fis);
                    mAudioTrack.play();

                    while (isPlaying) {
                        try {
                            int ret = dis.read(writeData, 0, mBufferSize);
                            if (ret <= 0) {
                                fis = new FileInputStream(mPCMDataPath + "music.pcm");
                                dis = new DataInputStream(fis);
                                if (isRepeat) {
                                    Thread.sleep(300);
                                    continue;
                                }

                                ((MainActivity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isPlaying = false;
                                        isRepeat = false;
                                    }
                                });
                                break;
                            }
                            mAudioTrack.write(writeData, 0, ret);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    mAudioTrack.stop();
                    mAudioTrack.release();
                    mAudioTrack = null;
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ((Button) ((Activity) mContext).findViewById(R.id.btn_play)).setText("PLAY");
                        }
                    });

                    try {
                        dis.close();
                        assert fis != null;
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
