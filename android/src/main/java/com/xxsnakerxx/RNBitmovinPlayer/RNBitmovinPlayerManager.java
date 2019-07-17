package com.xxsnakerxx.RNBitmovinPlayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

import com.bitmovin.player.BitmovinPlayer;
import com.bitmovin.player.BitmovinPlayerView;
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
import com.bitmovin.player.api.event.data.SubtitleAddedEvent;
import com.bitmovin.player.api.event.data.SubtitleChangedEvent;
import com.bitmovin.player.api.event.data.SubtitleRemovedEvent;
import com.bitmovin.player.api.event.data.TimeChangedEvent;
import com.bitmovin.player.api.event.data.UnmutedEvent;
import com.bitmovin.player.api.event.data.WarningEvent;
import com.bitmovin.player.api.event.listener.OnErrorListener;
import com.bitmovin.player.api.event.listener.OnFullscreenEnterListener;
import com.bitmovin.player.api.event.listener.OnFullscreenExitListener;
import com.bitmovin.player.api.event.listener.OnMutedListener;
import com.bitmovin.player.api.event.listener.OnPausedListener;
import com.bitmovin.player.api.event.listener.OnPlayListener;
import com.bitmovin.player.api.event.listener.OnPlaybackFinishedListener;
import com.bitmovin.player.api.event.listener.OnReadyListener;
import com.bitmovin.player.api.event.listener.OnRenderFirstFrameListener;
import com.bitmovin.player.api.event.listener.OnSeekListener;
import com.bitmovin.player.api.event.listener.OnSeekedListener;
import com.bitmovin.player.api.event.listener.OnStallEndedListener;
import com.bitmovin.player.api.event.listener.OnStallStartedListener;
import com.bitmovin.player.api.event.listener.OnSubtitleAddedListener;
import com.bitmovin.player.api.event.listener.OnSubtitleChangedListener;
import com.bitmovin.player.api.event.listener.OnSubtitleRemovedListener;
import com.bitmovin.player.api.event.listener.OnTimeChangedListener;
import com.bitmovin.player.api.event.listener.OnUnmutedListener;
import com.bitmovin.player.api.event.listener.OnWarningListener;
import com.bitmovin.player.config.PlayerConfiguration;
import com.bitmovin.player.config.StyleConfiguration;
import com.bitmovin.player.config.drm.DRMConfiguration;
import com.bitmovin.player.config.drm.WidevineConfiguration;
import com.bitmovin.player.config.media.SourceConfiguration;
import com.bitmovin.player.config.media.SourceItem;
import com.bitmovin.player.config.track.SubtitleTrack;
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

import org.json.JSONObject;

import java.util.Map;

public class RNBitmovinPlayerManager extends SimpleViewManager<BitmovinPlayerView> implements FullscreenHandler, LifecycleEventListener {

    public static final String REACT_CLASS = "RNBitmovinPlayer";

    private BitmovinPlayerView _playerView;
    private BitmovinPlayer _player;
    private boolean _fullscreen;
    private ThemedReactContext _reactContext;
    private View _decorView;
    private boolean _ready;

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
        _decorView = _reactContext.getCurrentActivity().getWindow().getDecorView();
        _ready = false;

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
    public void setConfiguration(BitmovinPlayerView view, ReadableMap config) throws Exception {
        JSONObject obj = JsonConvert.reactToJSON(config);
        PlayerConfiguration configuration = PlayerConfiguration.fromJSON(obj.toString());

        if (!config.hasKey("Source")) {
            System.out.println("RNBitmovinPlayerManager.setConfiguration - config is missing \"Source\"");
            return;
        }
        ReadableMap sourceMap = config.getMap("Source");

        String sourceUrl;
        if (sourceMap.hasKey("dash")) {
            sourceUrl = sourceMap.getString("dash");
        } else if (sourceMap.hasKey("hls")) {
            sourceUrl = sourceMap.getString("hls");
        } else {
            System.out.println("RNBitmovinPlayerManager.setConfiguration - Could not find dash or hls in \"Source\"");
            return;
        }
        System.out.println("RNBitmovinPlayerManager.setConfiguration - sourceUrl=" + sourceUrl);

        String token = "";
        if (config.hasKey("token")) {
            token = config.getString("token");
        }

        if (sourceMap.hasKey("playback")) {
            ReadableMap playbackMap = sourceMap.getMap("playback");
            if (playbackMap.hasKey("autoplay")) {
                Boolean autoplay = playbackMap.getBoolean("autoplay");
                if (autoplay) {
                    configuration.getPlaybackConfiguration().setAutoplayEnabled(true);
                }
            }
        }

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
            StyleConfiguration styleConf = configuration.getStyleConfiguration();
            ReadableMap styleMap = config.getMap("style");
            if (styleMap.hasKey("uiEnabled") && !styleMap.getBoolean("uiEnabled")) {
                styleConf.setUiEnabled(false);
            }
            if (styleMap.hasKey("fullscreenIcon") && styleMap.getBoolean("fullscreenIcon")) {
                _playerView.setFullscreenHandler(this);
            }
            if (styleMap.hasKey("uiCss") && styleMap.getString("uiCss") != null) {
                styleConf.setPlayerUiCss("file:///android_asset/" + styleMap.getString("uiCss"));
            }
            if (styleMap.hasKey("supplementalUiCss") && styleMap.getString("supplementalUiCss") != null) {
                styleConf.setSupplementalPlayerUiCss("file:///android_asset/" + styleMap.getString("supplementalUiCss"));
            }
            if (styleMap.hasKey("uiJs") && styleMap.getString("uiJs") != null) {
                styleConf.setPlayerUiJs("file:///android_asset/" + styleMap.getString("uiJs"));
            }
            configuration.setStyleConfiguration(styleConf);
        }

        _player.setup(configuration);

        SourceItem sourceItem = new SourceItem(sourceUrl);
        if (sourceMap.hasKey("poster")) {
            String poster = sourceMap.getString("poster");
            sourceItem.setPosterSource(poster);
        }
        if (sourceMap.hasKey("drm")) {
            ReadableMap drm = sourceMap.getMap("drm");
            if (drm.hasKey("widevine")) {
                ReadableMap widevine = drm.getMap("widevine");
                if (widevine.hasKey("LA_URL")) {
                    String widevineUrl = widevine.getString("LA_URL");
                    if (widevine.hasKey("headers")) {
                        ReadableMap headers = widevine.getMap("headers");
                        if (headers.hasKey("Authorization")) {
                            token = headers.getString("Authorization");
                        }
                    }
                    WidevineConfiguration widevineConfiguration =
                            (WidevineConfiguration) new DRMConfiguration.Builder()
                                    .uuid(WidevineConfiguration.UUID)
                                    .licenseUrl(widevineUrl)
                                    .putHttpHeader("Authorization", token)
                                    .build();
                    sourceItem.addDRMConfiguration(widevineConfiguration);
                }
            }
        }
        SourceConfiguration sourceConf = new SourceConfiguration();
        sourceConf.addSourceItem(sourceItem);

        // PT - Subtitle settings load correctly if this call is delayed, but the video does not
        //      play properly - only audio plays and only going fullscreen fixes the video. This
        //      behavior is different from the BasicPlayback example.
        //
        // Methods tested (all failed):
        //
        // 1.   Handler handler = new Handler(Looper.myLooper());
        //      handler.post(() -> { _player.load(sourceConf); }); // also postDelayed
        //
        // 2.   final Timer timer = new Timer();
        //      timer.schedule(new TimerTask() {
        //          public void run() {
        //              _reactContext.getCurrentActivity().runOnUiThread(() -> { _player.load(sourceConf); });
        //          }
        //      },100);
        //
        // 3.   _decorView.post(() -> { _player.load(sourceConf); });
        _player.load(sourceConf);
    }

    private void handleFullscreen(boolean fullscreen) {
        this._fullscreen = fullscreen;
        // Fullscreen seems to work even without calling doSystemUiVisibility and doLayoutChanges
        this.doSystemUiVisibility(fullscreen);
        this.doLayoutChanges(fullscreen);
    }

    //private void doRotation(boolean fullScreen) {
    //    int rotation = this._activity.getWindowManager().getDefaultDisplay().getRotation();
    //    if (fullScreen) {
    //        switch (rotation) {
    //            case Surface.ROTATION_270:
    //                this._activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    //                break;
    //
    //            default:
    //                this._activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    //        }
    //    } else {
    //        this._activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    //    }
    //}

    private void doSystemUiVisibility(final boolean fullScreen) {
        _decorView.post(() -> {
            int uiParams = FullscreenUtil.getSystemUiVisibilityFlags(fullScreen, true);
            _decorView.setSystemUiVisibility(uiParams);
        });
    }

    private void doLayoutChanges(final boolean fullscreen) {
        _decorView.post(new UpdateLayoutRunnable(fullscreen));
    }

    @Override
    public boolean isFullScreen() {
        return _fullscreen;
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
    }

    private void triggerEvent(String eventName, WritableMap data) {
        _reactContext.getJSModule(RCTEventEmitter.class)
                .receiveEvent(_playerView.getId(), eventName, data);
    }

    @Override
    public void onFullscreenRequested() {
        this.handleFullscreen(true);
        WritableMap map = Arguments.createMap();
        triggerEvent("onFullscreenEnter", map);
    }

    @Override
    public void onFullscreenExitRequested() {
        this.handleFullscreen(false);
        WritableMap map = Arguments.createMap();
        triggerEvent("onFullscreenExit", map);
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
                Context context = _reactContext.getCurrentActivity();
                SharedPreferences sharedPref = context.getSharedPreferences(
                        "RNBitmovinPlayerManager", Context.MODE_PRIVATE);
                String id = sharedPref.getString("subtitleId", null);
                _player.setSubtitle(id);
                _ready = true;

                System.out.println("RNBitmovinPlayerManager.onReady subtitleId=" + id);

                WritableMap map = Arguments.createMap();
                triggerEvent("onReady", map);
            }
        });

        _player.addEventListener(new OnPlayListener() {
            public void onPlay(PlayEvent event) {
                System.out.println("RNBitmovinPlayerManager.onPlay");
                WritableMap map = Arguments.createMap();
                map.putDouble("time", event.getTime());
                triggerEvent("onPlay", map);
            }
        });

        _player.addEventListener(new OnPausedListener() {
            @Override
            public void onPaused(PausedEvent event) {
                System.out.println("RNBitmovinPlayerManager.onPaused");
                WritableMap map = Arguments.createMap();
                map.putDouble("time", event.getTime());
                triggerEvent("onPaused", map);
            }
        });

        _player.addEventListener(new OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimeChangedEvent event) {
                WritableMap map = Arguments.createMap();
                map.putDouble("time", event.getTime());
                triggerEvent("onTimeChanged", map);
            }
        });

        _player.addEventListener(new OnStallStartedListener() {
            @Override
            public void onStallStarted(StallStartedEvent event) {
                System.out.println("RNBitmovinPlayerManager.onStallStarted");
                WritableMap map = Arguments.createMap();
                triggerEvent("onStallStarted", map);
            }
        });

        _player.addEventListener(new OnStallEndedListener() {
            @Override
            public void onStallEnded(StallEndedEvent event) {
                System.out.println("RNBitmovinPlayerManager.onStallEnded");
                WritableMap map = Arguments.createMap();
                triggerEvent("onStallEnded", map);
            }
        });

        _player.addEventListener(new OnPlaybackFinishedListener() {
            @Override
            public void onPlaybackFinished(PlaybackFinishedEvent event) {
                System.out.println("RNBitmovinPlayerManager.onPlaybackFinished");
                WritableMap map = Arguments.createMap();
                triggerEvent("onPlaybackFinished", map);
            }
        });

        _player.addEventListener(new OnRenderFirstFrameListener() {
            @Override
            public void onRenderFirstFrame(RenderFirstFrameEvent event) {
                System.out.println("RNBitmovinPlayerManager.onRenderFirstFrame");
                WritableMap map = Arguments.createMap();
                triggerEvent("onRenderFirstFrame", map);
            }
        });

        _player.addEventListener(new OnErrorListener() {
            @Override
            public void onError(ErrorEvent event) {
                System.out.println("RNBitmovinPlayerManager.onError: " + event.getMessage()
                        + " (code " + event.getCode() + ")");

                WritableMap errorMap = Arguments.createMap();
                errorMap.putInt("code", event.getCode());
                errorMap.putString("message", event.getMessage());

                WritableMap map = Arguments.createMap();
                map.putMap("error", errorMap);
                triggerEvent("onError", map);
            }
        });

        _player.addEventListener(new OnMutedListener() {
            @Override
            public void onMuted(MutedEvent event) {
                System.out.println("RNBitmovinPlayerManager.onMuted");
                WritableMap map = Arguments.createMap();
                triggerEvent("onMuted", map);
            }
        });

        _player.addEventListener(new OnUnmutedListener() {
            @Override
            public void onUnmuted(UnmutedEvent event) {
                System.out.println("RNBitmovinPlayerManager.onUnmuted");
                WritableMap map = Arguments.createMap();
                triggerEvent("onUnmuted", map);
            }
        });

        _player.addEventListener(new OnSeekListener() {
            @Override
            public void onSeek(SeekEvent event) {
                System.out.println("RNBitmovinPlayerManager.onSeek");
                WritableMap map = Arguments.createMap();
                map.putDouble("seekTarget", event.getSeekTarget());
                map.putDouble("position", event.getPosition());
                triggerEvent("onSeek", map);
            }
        });

        _player.addEventListener(new OnSeekedListener() {
            @Override
            public void onSeeked(SeekedEvent event) {
                System.out.println("RNBitmovinPlayerManager.onSeeked");
                WritableMap map = Arguments.createMap();
                triggerEvent("onSeeked", map);
            }
        });

        _player.addEventListener(new OnFullscreenEnterListener() {
            @Override
            public void onFullscreenEnter(FullscreenEnterEvent event) {
                System.out.println("RNBitmovinPlayerManager.onFullscreenEnter");
                WritableMap map = Arguments.createMap();
                triggerEvent("onFullscreenEnter", map);
            }
        });

        _player.addEventListener(new OnFullscreenExitListener() {
            @Override
            public void onFullscreenExit(FullscreenExitEvent event) {
                System.out.println("RNBitmovinPlayerManager.onFullscreenExit");
                WritableMap map = Arguments.createMap();
                triggerEvent("onFullscreenExit", map);
            }
        });

        _player.addEventListener(new OnSubtitleChangedListener() {
            @Override
            public void onSubtitleChanged(SubtitleChangedEvent event) {
                SubtitleTrack track = event.getNewSubtitleTrack();
                String id = null;
                if (track != null) {
                    id = track.getId();
                    System.out.println("RNBitmovinPlayerManager.onSubtitleChanged - id=" + id
                            + " label=" + track.getLabel() + " language="
                            + track.getLanguage());
                } else {
                    System.out.println("RNBitmovinPlayerManager.onSubtitleChanged - id=null");
                }

                // This _ready flag is required because of a Bitmovin bug where subtitles get turned
                // on automatically after loading the style configuration
                if (_ready) {
                    System.out.println("RNBitmovinPlayerManager.onSubtitleChanged - saving subtitleId");
                    Context context = _reactContext.getCurrentActivity();
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            "RNBitmovinPlayerManager", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("subtitleId", id);
                    editor.commit();
                }
            }
        });

        _player.addEventListener(new OnSubtitleAddedListener() {
            @Override
            public void onSubtitleAdded(SubtitleAddedEvent event) {
                SubtitleTrack track = event.getSubtitleTrack();
                System.out.println("RNBitmovinPlayerManager.onSubtitleAdded - id=" + track.getId()
                        + " label=" + track.getLabel() + " language=" + track.getLanguage());
            }
        });

        _player.addEventListener(new OnSubtitleRemovedListener() {
            @Override
            public void onSubtitleRemoved(SubtitleRemovedEvent event) {
                SubtitleTrack track = event.getSubtitleTrack();
                System.out.println("RNBitmovinPlayerManager.onSubtitleRemoved - id=" + track.getId()
                        + " label=" + track.getLabel() + " language=" + track.getLanguage());
            }
        });

        _player.addEventListener(new OnWarningListener() {
            @Override
            public void onWarning(WarningEvent warningEvent) {
                System.out.println("RNBitmovinPlayerManager.onWarning: " + warningEvent.getMessage()
                        + " (code " + warningEvent.getCode() + ")");
            }
        });
    }

    private class UpdateLayoutRunnable implements Runnable {
        private boolean fullscreen;

        private UpdateLayoutRunnable(boolean fullscreen) {
            this.fullscreen = fullscreen;
        }

        @Override
        @SuppressLint("RestrictedApi")
        public void run() {
            //if (RNBitmovinPlayerManager.this.toolbar != null) {
            //    if (this.fullscreen) {
            //        RNBitmovinPlayerManager.this.toolbar.setVisibility(View.GONE);
            //    } else {
            //        RNBitmovinPlayerManager.this.toolbar.setVisibility(View.VISIBLE);
            //    }
            //}

            if (RNBitmovinPlayerManager.this._playerView.getParent() instanceof ViewGroup) {
                ViewGroup parentView = (ViewGroup) RNBitmovinPlayerManager.this._playerView.getParent();
                for (int i = 0; i < parentView.getChildCount(); i++) {
                    View child = parentView.getChildAt(i);
                    if (child != _playerView) {
                        child.setVisibility(fullscreen ? View.GONE : View.VISIBLE);
                    }
                }
            }
        }
    }
}
