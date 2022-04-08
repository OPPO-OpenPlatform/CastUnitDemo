/*
 * Copyright (c) 2022 OPPO.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * File: - MainActivity.java
 * Description:
 *     N/A
 *
 * Version: 1.0.0
 * Date: 2022-01-06
 * Owner: Wei Zhang
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~ Revision History ~~~~~~~~~~~~~~~~~~~~~~~
 * <author>             <date>           <version>              <desc>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Wei Zhang           2022-01-06           1.0.0         project init
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.example.castsdkdemo;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.oplus.ocs.base.common.ConnectionResult;
import com.oplus.ocs.base.common.api.OnConnectionFailedListener;
import com.oplus.ocs.base.common.api.OnConnectionSucceedListener;
import com.oplus.ocs.cast.CastUnit;
import com.oplus.ocs.cast.CastUnitClient;
import com.oplus.ocs.cast.sdk.api.CrossScreenClientManagerDelegate;
import com.oplus.ocs.cast.sdk.api.RemotePlayListener;
import com.oplus.ocs.cast.sdk.cmd.StreamPlayAddVolCmd;
import com.oplus.ocs.cast.sdk.cmd.StreamPlayDisconnCmd;
import com.oplus.ocs.cast.sdk.cmd.StreamPlayMuteCmd;
import com.oplus.ocs.cast.sdk.cmd.StreamPlayPauseCmd;
import com.oplus.ocs.cast.sdk.cmd.StreamPlayPlayCmd;
import com.oplus.ocs.cast.sdk.cmd.StreamPlaySeekCmd;
import com.oplus.ocs.cast.sdk.cmd.StreamPlaySetUriCmd;
import com.oplus.ocs.cast.sdk.cmd.StreamPlaySetVolCmd;
import com.oplus.ocs.cast.sdk.cmd.StreamPlayStopCmd;
import com.oplus.ocs.cast.sdk.cmd.StreamPlaySubVolCmd;
import com.oplus.ocs.cast.sdk.config.DeviceInfo;
import com.oplus.ocs.cast.sdk.config.PlayInfo;
import com.oplus.ocs.cast.sdk.config.PlayState;

import static com.example.castsdkdemo.Config.DB_NAME;
import static com.example.castsdkdemo.Config.RELAY_STATUS_KEY;
import static com.example.castsdkdemo.Config.TABLE_NAME;

/**
 * CastSDK Demo main activity.
 */
public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "CastSdkDemo";
    /**
     * Broadcast message
     */
    private static final String TRIGGER_INTENT = "crossscreen.intent.action.TRIGGER_PLAY";
    private boolean mBindSDK = false;
    private Button mPlayBtn = null;
    private Button mPauseBtn = null;
    private Button mDisconnectBtn = null;
    private Button mAddVolBtn = null;
    private Button mSubVolBtn = null;
    private Button mSetVolBtn = null;
    private Button mMuteBtn = null;
    private Button mUnMuteBtn = null;
    private Button mPlayNextBtn = null;
    private TextView mCurrProcess = null;
    private TextView mStart = null;
    private TextView mEnd = null;
    private SeekBar mPlayBar = null;
    private TextView mVolumeView = null;
    private SeekBar mVolumeBar = null;
    private Button mGetVersion = null;
    private Button mFeatureSupport = null;
    private TextView mCurStateTxt = null;
    private TextView mGetVersionTxt = null;
    private TextView mFeatureSupportTxt = null;
    private int mPlayProcess = 0;
    private int mVolume = 0;
    private String[] mVideoUrl = {"http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
            "https://www.w3school.com.cn/example/html5/mov_bbb.mp4"};
    private int mVideoIndex = 0;
    private MyBroadcastReceiver mReceiver = null;
    private CrossScreenClientManagerDelegate mManagerDelegate = null;
    private CastUnitClient mClient = null;
    private RelayAbilityDatabaseHelper mDbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * when init, set to 1 to support CastUnit ability
         */
        mDbHelper = new RelayAbilityDatabaseHelper(this, DB_NAME, null, 1);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RELAY_STATUS_KEY, 1);
        db.insert(TABLE_NAME, null, values);

        init();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                onClick(v);
                break;
            case MotionEvent.ACTION_CANCEL:
                onClick(v);
                v.refreshDrawableState();
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * BroadcastReceiver.
     */
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "BroadcastReceiver.onReceive().action=" + intent.getAction());
            if (TRIGGER_INTENT.equals(intent.getAction())) {
                initHeyCast();
            }
        }
    }

    /**
     * initialize when create view.
     */
    private void init() {
        mCurStateTxt = findViewById(R.id.connect_state_txt);
        mGetVersion = findViewById(R.id.cur_version);
        mGetVersion.setOnTouchListener(this);
        mFeatureSupport = findViewById(R.id.feature_support);
        mFeatureSupport.setOnTouchListener(this);
        mGetVersionTxt = findViewById(R.id.cur_version_txt);
        mFeatureSupportTxt = findViewById(R.id.feature_support_txt);
        boolean res = getSupportFeature();
        getVersion();
        if (res) {
            mFeatureSupportTxt.setText("Feature Support: true");
        } else {
            Log.e(TAG, "init current phone is not support cast");
            mFeatureSupportTxt.setText("Feature Support: false");
            return;
        }
        mPlayBtn = findViewById(R.id.tv_play);
        mPlayBtn.setOnTouchListener(this);
        mPauseBtn = findViewById(R.id.tv_pause);
        mPauseBtn.setOnTouchListener(this);
        mDisconnectBtn = findViewById(R.id.tv_disconnect);
        mDisconnectBtn.setOnTouchListener(this);
        mAddVolBtn = findViewById(R.id.tv_volume_add);
        mAddVolBtn.setOnTouchListener(this);
        mSubVolBtn = findViewById(R.id.tv_volume_sub);
        mSubVolBtn.setOnTouchListener(this);
        mSetVolBtn = findViewById(R.id.tv_volume_10);
        mSetVolBtn.setOnTouchListener(this);
        mMuteBtn = findViewById(R.id.tv_mute);
        mMuteBtn.setOnTouchListener(this);
        mUnMuteBtn = findViewById(R.id.tv_un_mute);
        mUnMuteBtn.setOnTouchListener(this);
        mPlayNextBtn = findViewById(R.id.tv_play_next);
        mPlayNextBtn.setOnTouchListener(this);

        mCurrProcess = findViewById(R.id.currentprocess);
        mStart = findViewById(R.id.start);
        mEnd = findViewById(R.id.end);
        mPlayBar = findViewById(R.id.process);
        mVolumeView = findViewById(R.id.volume);
        mVolumeBar = findViewById(R.id.seekBar);
        mVolumeBar.setMax(100);

        mPlayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "Process seekbar onProgressChanged:" + progress);
                mPlayProcess = progress;
                mCurrProcess.setText("current process: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "Process seekbar onStopTrackingTouch process:" + mPlayProcess);
                setCurVideoSeek(mPlayProcess);
            }
        });

        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mVolume = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                StreamPlaySetVolCmd setVolCmd = new StreamPlaySetVolCmd.Builder()
                        .setCallerAppName(getApplicationContext().getPackageName())
                        .setVolume(mVolume)
                        .build();
                mClient.sendStreamPlay(setVolCmd, null);
            }
        });

        mBindSDK = false;
        mReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TRIGGER_INTENT);
        registerReceiver(mReceiver, intentFilter);

        mManagerDelegate = new CrossScreenClientManagerDelegate() {
            @Override
            public void onOpen() {
                Log.e(TAG, "CrossScreenClientManagerDelegate onOpen--------------");
                mCurStateTxt.setText("Current connect state: true");
                setCurVideoUri();
                play();
            }

            @Override
            public void onClose() {
                Log.e(TAG, "CrossScreenClientManagerDelegate onClose--------------");
            }
        };
        Log.d(TAG, "init end");
    }

    /**
     * initialize castunit service.
     */
    private void initHeyCast() {
        synchronized (this) {
            Log.e(TAG, "-------initHeyCast----");
            closeHeyCast();

            mClient = CastUnit.getCastClient(getApplicationContext())
                    .addOnConnectionSucceedListener(new OnConnectionSucceedListener() {
                        @Override
                        public void onConnectionSucceed() {
                            Log.e(TAG, "CastUnitClient onConnectionSucceed--");
                            CastUnit.getCastClient(getApplicationContext()).registerDelegate(mManagerDelegate);
                            CastUnit.getCastClient(getApplicationContext()).registerRemotePlayListener(mRemotePlayListener);
                            CastUnit.getCastClient(getApplicationContext()).open();
                            mBindSDK = true;
                        }
                    }, null).addOnConnectionFailedListener(new OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Log.e(TAG, "CastUnitClient onConnectionFailed--");
                        }
                    }, null);
        }
    }

    /**
     * close heycast service.
     */
    private void closeHeyCast() {
        synchronized (this) {
            if (mBindSDK) {
                mClient.unregisterDelegate(mManagerDelegate);
                mClient.unregisterRemotePlayListener(mRemotePlayListener);
                mClient.close();
                mBindSDK = false;
                Log.e(TAG, "-------closeHeyCast----");
            }
        }
    }

    /**
     * remote play listener.
     */
    private RemotePlayListener mRemotePlayListener = new RemotePlayListener() {
        @Override
        public void onRemoteConnectStatus(int i, long l) {
            Log.e(TAG, "------------onRemoteConnectStatus: i=" + i);

            if (i == 1) {
                mCurStateTxt.setText("Current connect state: false");
                closeHeyCast();
            } else {
                mCurStateTxt.setText("Current connect state: true");
            }
        }

        @Override
        public void onPositionUpdate(long l, long l1, long l2) {
            Log.e(TAG, "-------------onPositionUpdate: l=" + l + ", l1=" + l1 + ", l2=" + l2);
        }

        @Override
        public void onRemotePlayState(PlayState playState, long l) {
            Log.e(TAG, "-----------onRemotePlayState: playState=" + playState + ", l=" + l);
        }

        @Override
        public void onSetUri(long l) {
            Log.e(TAG, "------------onSetUri l: " + l);
        }

        @Override
        public void onGetUri(String s, long l) {
            Log.e(TAG, "------------onGetUri l: " + l + ", s: " + s);
        }

        @Override
        public void onGetTriggerType(int i, long l) {
            Log.e(TAG, "------------onGetTriggerType l: " + l + ", i: " + i);
        }

        @Override
        public void onPlay(long l) {
            Log.e(TAG, "------------onPlay l: " + l);
        }

        @Override
        public void onPause(long l) {
            Log.e(TAG, "------------onPause l: " + l);
        }

        @Override
        public void onSeek(long l) {
            Log.e(TAG, "------------onSeek l: " + l);
        }

        @Override
        public void onStop(long l) {
            Log.e(TAG, "------------onStop l: " + l);
        }

        @Override
        public void onVolumeChanged(int i, long l) {
            Log.e(TAG, "------------onVolumeChanged l: " + l);
            mVolumeView.setText("volume:" + i);
            mVolumeBar.setProgress(i);
        }

        @Override
        public void onVolumeMuted(boolean b, long l) {
            Log.e(TAG, "------------onVolumeMuted l: " + l + ", b: " + b);
        }

        @Override
        public void onCurrentPlayInfo(PlayInfo playInfo) {
            Log.e(TAG, "------------onCurrentPlayInfo: " + playInfo);
            mEnd.setText("end:" + playInfo.getDuration() / 1000);
            mCurrProcess.setText("current process:" + playInfo.getPosition() / 1000);
            mPlayBar.setMax(playInfo.getDuration() / 1000);
            mPlayBar.setProgress(playInfo.getPosition() / 1000);
            mVolumeView.setText("volume:" + playInfo.getVolume());
            mVolumeBar.setProgress(playInfo.getVolume());
        }

        @Override
        public void onRemoteAppMessage(String s) {
            Log.e(TAG, "------------onRemoteAppMessage s: " + s);
        }

        @Override
        public void onRemoteCustomMsg(String s, long l) {
            Log.e(TAG, "------------onRemoteCustomMsg l: " + l + ", s: " + s);
        }

        @Override
        public void onQueryResult(PlayInfo playInfo, long l) {
            Log.e(TAG, "------------onQueryResult l: " + l + ", " + playInfo.toString());
        }

        @Override
        public void onCurrentPlayer(String s, long l) {
            Log.e(TAG, "------------onCurrentPlayer l: " + l + ", s: " + s);
        }

        @Override
        public void onCustomResult(String s, long l) {
            Log.e(TAG, "------------onRemoteAppMessage s: " + s + ", l: " + l);
        }

        @Override
        public void onGetDeviceInfo(DeviceInfo deviceInfo, long l) {
            Log.e(TAG, "------------onGetDeviceInfo l: " + l + ", " + deviceInfo.toString());
        }

        @Override
        public void onError(String s, long l) {
            Log.e(TAG, "------------onError l: " + l + ", s: " + s);
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "------------------unregisterReceiver");
        unregisterReceiver(mReceiver);
        closeHeyCast();
        super.onDestroy();
    }

    /**
     * set uri command.
     */
    private void setCurVideoUri() {
        try {
            String uriStr = Uri.encode(mVideoUrl[mVideoIndex]);
            String title = "";  // video title
            String mediaType = "video";  // video media type
            String creator = ""; //video creator
            mVideoIndex = (mVideoIndex + 1) % mVideoUrl.length;
            Log.e(TAG, "setCurVideoUri: mVideoIndex=" + mVideoIndex);
            StreamPlaySetUriCmd.Builder builder = new StreamPlaySetUriCmd.Builder();
            StreamPlaySetUriCmd.MetaData meta = new StreamPlaySetUriCmd.MetaData(
                    mediaType, title, creator, uriStr
            );

            int seek = Math.toIntExact(0);
            StreamPlaySetUriCmd setUriCmd = builder.setCallerAppName(getApplicationContext().getPackageName())
                    .setPlayUri(uriStr)
                    .setPlayPosition(seek)
                    .setMetaData(meta)
                    .build();
            mClient.sendStreamPlay(setUriCmd, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * seek video command.
     */
    private void setCurVideoSeek(int process) {
        Log.d(TAG, "seek to" + process);
        StreamPlaySeekCmd seekCmd = new StreamPlaySeekCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .setPosition(process * 1000)
                .build();
        mClient.sendStreamPlay(seekCmd, null);
    }

    /**
     * play video command.
     */
    private void play() {
        if (mClient == null) {
            Log.e(TAG, "play mClient == null");
            return;
        }
        StreamPlayPlayCmd playCmd = new StreamPlayPlayCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .build();
        mClient.sendStreamPlay(playCmd, null);
    }

    /**
     * pause video command.
     */
    private void pause() {
        if (mClient == null) {
            Log.e(TAG, "pause mClient == null");
            return;
        }
        StreamPlayPauseCmd pauseCmd = new StreamPlayPauseCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .build();
        mClient.sendStreamPlay(pauseCmd, null);
    }

    /**
     * disconnect command.
     */
    private void disconnect() {
        if (mClient == null) {
            Log.e(TAG, "disconnect mClient == null");
            return;
        }
        StreamPlayDisconnCmd disconnCmd = new StreamPlayDisconnCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .build();
        mClient.sendStreamPlay(disconnCmd, null);
        mCurStateTxt.setText("Current connect state: false");
    }

    /**
     * add volume command.
     */
    private void addVol() {
        if (mClient == null) {
            Log.e(TAG, "addVol mClient == null");
            return;
        }
        StreamPlayAddVolCmd addVolCmd = new StreamPlayAddVolCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .build();
        mClient.sendStreamPlay(addVolCmd, null);
    }

    /**
     * sub volume command.
     */
    private void subVol() {
        if (mClient == null) {
            Log.e(TAG, "subVol mClient == null");
            return;
        }
        StreamPlaySubVolCmd subVolCmd = new StreamPlaySubVolCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .build();
        mClient.sendStreamPlay(subVolCmd, null);
    }

    /**
     * set volume command.
     */
    private void setVol(int vol) {
        if (mClient == null) {
            Log.e(TAG, "setVol mClient == null");
            return;
        }
        StreamPlaySetVolCmd setVolCmd = new StreamPlaySetVolCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .setVolume(vol)
                .build();
        mClient.sendStreamPlay(setVolCmd, null);
    }

    /**
     * mute command.
     */
    private void mute() {
        if (mClient == null) {
            Log.e(TAG, "mute mClient == null");
            return;
        }
        StreamPlayMuteCmd muteCmd = new StreamPlayMuteCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .setMute(true)
                .build();
        mClient.sendStreamPlay(muteCmd, null);
    }

    /**
     * unmute command.
     */
    private void unMute() {
        if (mClient == null) {
            Log.e(TAG, "unMute mClient == null");
            return;
        }
        StreamPlayMuteCmd unMuteCmd = new StreamPlayMuteCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .setMute(false)
                .build();
        mClient.sendStreamPlay(unMuteCmd, null);
    }

    /**
     * play next video scenario.
     */
    private void playNext() {
        if (mClient == null) {
            Log.e(TAG, "playNext mClient == null");
            return;
        }
        StreamPlayStopCmd stopCmd = new StreamPlayStopCmd.Builder()
                .setCallerAppName(getApplicationContext().getPackageName())
                .build();
        mClient.sendStreamPlay(stopCmd, null);
        setCurVideoUri();
        play();
    }

    private void getVersion() {
        int version = CastUnit.getCastClient(getApplicationContext()).getVersion();
        Log.d(TAG, "getVersion: " + version);
        mGetVersionTxt.setText("Current Version: " + version);
    }

    private boolean getSupportFeature() {
        boolean res = CastUnit.getCastClient(getApplicationContext()).hasFeature("");
        Log.d(TAG, "getSupportFeature: " + res);
        if (res) {
            mFeatureSupportTxt.setText("Feature Support: true");
        } else {
            mFeatureSupportTxt.setText("Feature Support: false");
        }
        return res;
    }

    private void onClick(View v) {
        Log.d(TAG, "onClick: " + v.getId());
        if (v.getId() == R.id.cur_version) {
            getVersion();
            return;
        } else if (v.getId() == R.id.feature_support) {
            getSupportFeature();
            return;
        }
        if (mClient == null || !mBindSDK) {
            Log.e(TAG, "onClick mClient == null, mBindSDK=" + mBindSDK);
            return;
        }
        switch (v.getId()) {
            case R.id.tv_play:
                Log.d(TAG, "R.id.tv_play");
                play();
                break;
            case R.id.tv_pause:
                Log.d(TAG, "R.id.tv_pause");
                pause();
                break;
            case R.id.tv_disconnect:
                Log.d(TAG, "R.id.tv_disconnect");
                disconnect();
                break;
            case R.id.tv_volume_add:
                Log.d(TAG, "R.id.tv_volume_add");
                addVol();
                break;
            case R.id.tv_volume_sub:
                Log.d(TAG, "R.id.tv_volume_sub");
                subVol();
                break;
            case R.id.tv_volume_10:
                Log.d(TAG, "R.id.tv_volume_10");
                setVol(10);
                break;
            case R.id.tv_mute:
                Log.d(TAG, "R.id.tv_mute");
                mute();
                break;
            case R.id.tv_un_mute:
                Log.d(TAG, "R.id.tv_un_mute");
                unMute();
                break;
            case R.id.tv_play_next:
                Log.d(TAG, "R.id.tv_play_next");
                playNext();
                break;
            default:
                break;
        }
    }
}