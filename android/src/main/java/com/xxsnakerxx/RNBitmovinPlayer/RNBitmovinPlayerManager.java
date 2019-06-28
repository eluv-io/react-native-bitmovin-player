package com.xxsnakerxx.RNBitmovinPlayer;

import com.bitmovin.player.api.event.data.ErrorEvent;
import com.bitmovin.player.api.event.data.FullscreenEnterEvent;
import com.bitmovin.player.api.event.data.FullscreenExitEvent;
import com.bitmovin.player.api.event.data.MutedEvent;
import com.bitmovin.player.api.event.data.PausedEvent;
import com.bitmovin.player.api.event.data.PlayEvent;
import com.bitmovin.player.api.event.data.PlaybackFinishedEvent;
import com.bitmovin.player.api.event.data.ReadyEvent;
import com.bitmovin.player.api.event.data.RenderFirstFrameEvent;
import com.bitmovin.player.api.event.data.SeekEvent;
import com.bitmovin.player.api.event.data.SeekedEvent;
import com.bitmovin.player.api.event.data.StallEndedEvent;
import com.bitmovin.player.api.event.data.StallStartedEvent;
import com.bitmovin.player.api.event.data.TimeChangedEvent;
import com.bitmovin.player.api.event.data.UnmutedEvent;
import com.bitmovin.player.api.event.listener.OnReadyListener;
import com.bitmovin.player.api.event.listener.OnPlayListener;
import com.bitmovin.player.api.event.listener.OnPausedListener;
import com.bitmovin.player.api.event.listener.OnTimeChangedListener;
import com.bitmovin.player.api.event.listener.OnStallStartedListener;
import com.bitmovin.player.api.event.listener.OnStallEndedListener;
import com.bitmovin.player.api.event.listener.OnPlaybackFinishedListener;
import com.bitmovin.player.api.event.listener.OnRenderFirstFrameListener;
import com.bitmovin.player.api.event.listener.OnErrorListener;
import com.bitmovin.player.api.event.listener.OnMutedListener;
import com.bitmovin.player.api.event.listener.OnUnmutedListener;
import com.bitmovin.player.api.event.listener.OnSeekListener;
import com.bitmovin.player.api.event.listener.OnSeekedListener;
import com.bitmovin.player.api.event.listener.OnFullscreenEnterListener;
import com.bitmovin.player.api.event.listener.OnFullscreenExitListener;
import com.bitmovin.player.config.*;
import com.bitmovin.player.config.network.*;
import com.bitmovin.player.config.drm.*;
import com.bitmovin.player.config.media.*;
import com.bitmovin.player.UnsupportedDrmException;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
import com.bitmovin.player.ui.FullscreenHandler;
import com.bitmovin.player.ui.FullscreenUtil;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Map;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.*;
import java.nio.charset.StandardCharsets;
import android.content.res.Resources;
import android.net.Uri;
import android.content.res.AssetManager;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Looper;
import android.os.Handler;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.annotation.SuppressLint;

public class RNBitmovinPlayerManager extends SimpleViewManager<BitmovinPlayerView> implements FullscreenHandler, LifecycleEventListener {

    public static final String REACT_CLASS = "RNBitmovinPlayer";

    private BitmovinPlayerView _playerView;
    private BitmovinPlayer _player;
    private boolean _fullscreen;
    private ThemedReactContext _reactContext;
    private ExecutorService executor
          = Executors.newSingleThreadExecutor();
    private Activity _activity;
    private View _decorView;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "onReady",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onReady")))
                .put(
                        "onPlay",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onPlay")))
                .put(
                        "onPaused",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onPaused")))
                .put(
                        "onTimeChanged",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onTimeChanged")))
                .put(
                        "onStallStarted",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onStallStarted")))
                .put(
                        "onStallEnded",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onStallEnded")))
                .put(
                        "onPlaybackFinished",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onPlaybackFinished")))
                .put(
                        "onRenderFirstFrame",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onRenderFirstFrame")))
                .put(
                        "onError",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "_onPlayerError")))
                .put(
                        "onMuted",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onMuted")))
                .put(
                        "onUnmuted",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onUnmuted")))
                .put(
                        "onSeek",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onSeek")))
                .put(
                        "onSeeked",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onSeeked")))
                .put(
                        "onFullscreenEnter",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onFullscreenEnter")))
                .put(
                        "onFullscreenExit",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onFullscreenExit")))
                .build();
    }

    @Override
    public BitmovinPlayerView createViewInstance(ThemedReactContext context) {
        _reactContext = context;
        _playerView = new BitmovinPlayerView(context);
        _player = _playerView.getPlayer();
        _fullscreen = false;
        _activity = _reactContext.getCurrentActivity();
        _decorView = _activity.getWindow().getDecorView();

        setListeners();

        return _playerView;
    }

    @Override
    public void onDropViewInstance(BitmovinPlayerView view) {
        _playerView.onDestroy();

        super.onDropViewInstance(view);

        _player = null;
        _playerView = null;
    }

    @ReactProp(name = "configuration")
    public void setConfiguration(BitmovinPlayerView view, ReadableMap config) throws Exception{
        JSONObject obj = JsonConvert.reactToJSON(config);
        PlayerConfiguration configuration = PlayerConfiguration.fromJSON(obj.toString());

        ReadableMap sourceMap = null;
        String token = "";
        if (config.hasKey("Source")) {
            sourceMap = config.getMap("Source");
        }else{
          return;
        }

        String sourceUrl = "";
        if(sourceMap.hasKey("dash")){
          sourceUrl = sourceMap.getString("dash");
        }else if(sourceMap.hasKey("hls")){
          sourceUrl = sourceMap.getString("dash");
        }else{
          System.out.println("Could not find dash or hls in Source.");
          return;
        }

        ReadableMap networkMap = null;
        if (config.hasKey("token")) {
            token = config.getString("token");
        }
        //TODO: source playback configurations
        configuration.getPlaybackConfiguration().setAutoplayEnabled(true);

        //Listing assets
        /*
        final AssetManager assets = _reactContext.getBaseContext().getAssets();
        final String[] names = assets.list( "" );

        System.out.println("Assets: ");
        for(int i = 0; i < names.length; i++){
          System.out.println(names[i]);
        }
        */

        if (config.hasKey("style")) {
            ReadableMap styleMap = config.getMap("style");
            if (styleMap.hasKey("uiEnabled") && !styleMap.getBoolean("uiEnabled")) {
                configuration.getStyleConfiguration().setUiEnabled(false);
            }
            if (styleMap.hasKey("fullscreenIcon") && styleMap.getBoolean("fullscreenIcon")) {
                _playerView.setFullscreenHandler(this);
            }
            if (styleMap.hasKey("uiCss") && styleMap.getString("uiCss") != null) {
                configuration.getStyleConfiguration().setPlayerUiCss("file:///android_asset/" + styleMap.getString("uiCss"));
            }

            if (styleMap.hasKey("supplementalUiCss") && styleMap.getString("supplementalUiCss") != null) {
                configuration.getStyleConfiguration().setSupplementalPlayerUiCss("file:///android_asset/" + styleMap.getString("supplementalUiCss"));
            }

            if (styleMap.hasKey("uiJs") && styleMap.getString("uiJs") != null) {
                configuration.getStyleConfiguration().setPlayerUiJs("file:///android_asset/" + styleMap.getString("uiJs"));
            }
        }

        _player.setup(configuration);
        SourceItem sourceItem =  new SourceItem(sourceUrl);
        SourceConfiguration sourceConfiguration = new SourceConfiguration();

        if(sourceMap.hasKey("poster")){
          String poster = sourceMap.getString("poster");
          sourceItem.setPosterSource(poster);
        }

        ReadableMap drm = null;
        if (sourceMap.hasKey("drm")) {
            drm = sourceMap.getMap("drm");
            //System.out.println("XXX -- has drm.");
            if(drm.hasKey("widevine")){
                //System.out.println("XXX -- has widevine.");
                ReadableMap widevine = drm.getMap("widevine");
                if(widevine.hasKey("LA_URL")) {
                    String widevineUrl = widevine.getString("LA_URL");
                    //System.out.println("Widevine url: " + widevineUrl);
                    if(widevine.hasKey("headers")){
                      ReadableMap headers = widevine.getMap("headers");
                      if(headers.hasKey("Authorization")){
                        token = headers.getString("Authorization");
                        //System.out.println("XXX -- proxy token: " + token);
                      }
                    }

                    WidevineConfiguration widevineConfiguration =
                            (WidevineConfiguration) new DRMConfiguration.Builder()
                                    .uuid(WidevineConfiguration.UUID)
                                    .licenseUrl(widevineUrl)
                                    .putHttpHeader("Authorization", token)
                                    .build();

                    sourceItem.addDRMConfiguration(widevineConfiguration);
                    System.out.println("Added widevine config.");
            }
          }
        }

        sourceConfiguration.addSourceItem(sourceItem);
        //System.out.println("Created configuration with sourceItem: " + sourceConfiguration.getFirstSourceItem().getDashSource().getUrl());
        _player.load(sourceConfiguration);
        //System.out.println("Player setup.");
    }

    private void handleFullscreen(boolean fullscreen)
    {
        System.out.println("XXX: handleFullscreen. " + fullscreen);
        this._fullscreen = fullscreen;
        // this.doRotation(fullscreen);
        this.doSystemUiVisibility(fullscreen);
        this.doLayoutChanges(fullscreen);
    }

    private void doRotation(boolean fullScreen)
    {
        int rotation = this._activity.getWindowManager().getDefaultDisplay().getRotation();

        if (fullScreen)
        {
            System.out.println("XXX: doRotation fullscreen.");
            switch (rotation)
            {
                case Surface.ROTATION_270:
                    this._activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;

                default:
                    this._activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        else
        {
            System.out.println("XXX: doRotation no fullscreen.");
            this._activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void doSystemUiVisibility(final boolean fullScreen)
    {
        this._decorView.post(new Runnable()
        {
            @Override
            public void run()
            {
                int uiParams = FullscreenUtil.getSystemUiVisibilityFlags(fullScreen, true);
                _decorView.setSystemUiVisibility(uiParams);
                /*
                if(fullScreen){
                  System.out.println("XXX: doSystemUiVisibility fullscreen.");
                  _decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
               }else{
                  System.out.println("XXX: doSystemUiVisibility not fullscreen.");
                  _decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
               }
               */
            }
        });
    }

    private void doLayoutChanges(final boolean fullscreen)
    {
        // System.out.println("XXX: doLayoutChanges.");
        Looper mainLooper = Looper.getMainLooper();
        boolean isMainLooperAlready = Looper.myLooper() == mainLooper;


        UpdateLayoutRunnable updateLayoutRunnable = new UpdateLayoutRunnable(this._activity, fullscreen);

        if (isMainLooperAlready)
        {
            updateLayoutRunnable.run();
        }
        else
        {
            Handler handler = new Handler(mainLooper);
            handler.post(updateLayoutRunnable);
        }
    }

    @Override
    public boolean isFullScreen() {
        return _fullscreen;
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void onDestroy() {}

    private void triggerEvent(String eventName, WritableMap data) {
        _reactContext.getJSModule(RCTEventEmitter.class)
            .receiveEvent(_playerView.getId(), eventName, data);
    }

    @Override
    public void onFullscreenRequested() {
        System.out.println("onFullscreenRequested");
        this.handleFullscreen(true);
        WritableMap map = Arguments.createMap();
        triggerEvent("onFullscreenEnter",map);
    }

    @Override
    public void onFullscreenExitRequested() {
        System.out.println("onFullscreenExitRequested");
        this.handleFullscreen(false);
        WritableMap map = Arguments.createMap();
        triggerEvent("onFullscreenExit",map);
    }

    @Override
    public void onHostResume() {
        _playerView.onResume();
    }

    @Override
    public void onHostPause() {
        _playerView.onPause();
    }

    @Override
    public void onHostDestroy() {
        _playerView.onDestroy();
    }

    private void setListeners() {
        _player.addEventListener(new OnReadyListener() {
            public void onReady(ReadyEvent event) {
                WritableMap map = Arguments.createMap();
                triggerEvent("onReady",map);
            }
        });

        _player.addEventListener(new OnPlayListener() {
            public void onPlay(PlayEvent event) {
                WritableMap map = Arguments.createMap();

                map.putDouble("time", event.getTime());
                triggerEvent("onPlay",map);
            }
        });

        _player.addEventListener(new OnPausedListener() {
            @Override
            public void onPaused(PausedEvent event) {
                WritableMap map = Arguments.createMap();

                map.putDouble("time", event.getTime());
                triggerEvent("onPaused",map);
            }
        });

        _player.addEventListener(new OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimeChangedEvent event) {
                WritableMap map = Arguments.createMap();

                map.putDouble("time", event.getTime());
                triggerEvent("onTimeChanged",map);
            }
        });

        _player.addEventListener(new OnStallStartedListener() {
            @Override
            public void onStallStarted(StallStartedEvent event) {
                WritableMap map = Arguments.createMap();
                triggerEvent("onStallStarted",map);
            }
        });

        _player.addEventListener(new OnStallEndedListener() {
            @Override
            public void onStallEnded(StallEndedEvent event) {
                WritableMap map = Arguments.createMap();

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onStallEnded",
                        map);
            }
        });

        _player.addEventListener(new OnPlaybackFinishedListener() {
            @Override
            public void onPlaybackFinished(PlaybackFinishedEvent event) {
                WritableMap map = Arguments.createMap();

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onPlaybackFinished",
                        map);
            }
        });

        _player.addEventListener(new OnRenderFirstFrameListener() {
            @Override
            public void onRenderFirstFrame(RenderFirstFrameEvent event) {
                WritableMap map = Arguments.createMap();

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onRenderFirstFrame",
                        map);
            }
        });

        _player.addEventListener(new OnErrorListener() {
            @Override
            public void onError(ErrorEvent event) {
                WritableMap map = Arguments.createMap();
                WritableMap errorMap = Arguments.createMap();

                errorMap.putInt("code", event.getCode());
                errorMap.putString("message", event.getMessage());

                map.putMap("error", errorMap);

                System.out.println("onError: " + event.getMessage());

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onError",
                        map);
            }
        });

        _player.addEventListener(new OnMutedListener() {
            @Override
            public void onMuted(MutedEvent event) {
                WritableMap map = Arguments.createMap();

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onMuted",
                        map);
            }
        });

        _player.addEventListener(new OnUnmutedListener() {
            @Override
            public void onUnmuted(UnmutedEvent event) {
                WritableMap map = Arguments.createMap();

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onUnmuted",
                        map);
            }
        });

        _player.addEventListener(new OnSeekListener() {
            @Override
            public void onSeek(SeekEvent event) {
                WritableMap map = Arguments.createMap();

                map.putDouble("seekTarget", event.getSeekTarget());
                map.putDouble("position", event.getPosition());

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onSeek",
                        map);
            }
        });

        _player.addEventListener(new OnSeekedListener() {
            @Override
            public void onSeeked(SeekedEvent event) {
                WritableMap map = Arguments.createMap();

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onSeeked",
                        map);
            }
        });

        _player.addEventListener(new OnFullscreenEnterListener() {
            @Override
            public void onFullscreenEnter(FullscreenEnterEvent event) {
                System.out.println("XXX: ANDROID fullscreen event.");
                WritableMap map = Arguments.createMap();

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onFullscreenEnter",
                        map);
            }
        });

        _player.addEventListener(new OnFullscreenExitListener() {
            @Override
            public void onFullscreenExit(FullscreenExitEvent event) {
                WritableMap map = Arguments.createMap();

                _reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        _playerView.getId(),
                        "onFullscreenExit",
                        map);
            }
        });
    }

    private class UpdateLayoutRunnable implements Runnable
    {
        private Activity activity;
        private boolean fullscreen;

        private UpdateLayoutRunnable(Activity activity, boolean fullscreen)
        {
            this.activity = activity;
            this.fullscreen = fullscreen;
        }

        @Override
        @SuppressLint("RestrictedApi")
        public void run()
        {
          /*
            if (RNBitmovinPlayerManager.this.toolbar != null)
            {
                if (this.fullscreen)
                {
                    RNBitmovinPlayerManager.this.toolbar.setVisibility(View.GONE);
                }
                else
                {
                    RNBitmovinPlayerManager.this.toolbar.setVisibility(View.VISIBLE);
                }
            }
            */

            if (RNBitmovinPlayerManager.this._playerView.getParent() instanceof ViewGroup)
            {
                ViewGroup parentView = (ViewGroup) RNBitmovinPlayerManager.this._playerView.getParent();

                for (int i = 0; i < parentView.getChildCount(); i++)
                {
                    View child = parentView.getChildAt(i);
                    if (child != _playerView)
                    {
                        child.setVisibility(fullscreen ? View.GONE : View.VISIBLE);
                    }
                }
            }
        }
    }
}
