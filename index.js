import React from 'react';
import PropTypes from 'prop-types';

import {
  findNodeHandle,
  UIManager,
  NativeModules,
  ViewPropTypes,
  requireNativeComponent,
  Platform,
  Dimensions
} from 'react-native';

const RNBitmovinPlayerModule = NativeModules.RNBitmovinPlayer;

const EMPTY_FN = () => {};

const DEFAULT_CONFIGURATION = {
  style: {
    uiEnabled: true,
    fullscreenIcon: true,
  },
};

class BitmovinPlayer extends React.Component {
  static propTypes = {
    style: ViewPropTypes.style,
    /*configuration: PropTypes.shape({
      source: PropTypes.shape({
        title: PropTypes.string,
        url: PropTypes.string.isRequired,
      }),
      poster: PropTypes.shape({
        url: PropTypes.string,
        persistent: PropTypes.bool,
      }),
      style: PropTypes.shape({
        uiEnabled: PropTypes.bool,
        systemUI: PropTypes.bool,
        uiCss: PropTypes.string,
        supplementalUiCss: PropTypes.string,
        uiJs: PropTypes.string,
        fullscreenIcon: PropTypes.bool,
      }),
    }).isRequired,*/

    configuration: PropTypes.object,
    onReady: PropTypes.func,
    onPlay: PropTypes.func,
    onPaused: PropTypes.func,
    onTimeChanged: PropTypes.func,
    onStallStarted: PropTypes.func,
    onStallEnded: PropTypes.func,
    onPlaybackFinished: PropTypes.func,
    onRenderFirstFrame: PropTypes.func,
    onPlayerError: PropTypes.func,
    onMuted: PropTypes.func,
    onUnmuted: PropTypes.func,
    onSeek: PropTypes.func,
    onSeeked: PropTypes.func,
    onFullscreenEnter: PropTypes.func,
    onFullscreenExit: PropTypes.func,
    onControlsShow: PropTypes.func,
    onControlsHide: PropTypes.func,
  }

  static defaultProps = {
    style: null,

    onReady: EMPTY_FN,
    onPlay: EMPTY_FN,
    onPaused: EMPTY_FN,
    onTimeChanged: EMPTY_FN,
    onStallStarted: EMPTY_FN,
    onStallEnded: EMPTY_FN,
    onPlaybackFinished: EMPTY_FN,
    onRenderFirstFrame: EMPTY_FN,
    onPlayerError: EMPTY_FN,
    onMuted: EMPTY_FN,
    onUnmuted: EMPTY_FN,
    onSeek: EMPTY_FN,
    onSeeked: EMPTY_FN,
    onFullscreenEnter: EMPTY_FN,
    onFullscreenExit: EMPTY_FN,
    onControlsShow: EMPTY_FN,
    onControlsHide: EMPTY_FN,
  }

  state = {
    maxHeight: null,
  }

  _onReady = () => {
    const {
      onReady,
    } = this.props;

/*
    // this need because video view stretched on initial render (RN 0.55.4)
    // TODO: check in future releases of RN
    if (Platform.OS === 'android') {
      UIManager.measure(
        findNodeHandle(this._player),
        (x, y, w, h) => {
          // trigger resize
          this.setState({
            maxHeight: h - 1,
          }, () => {
            requestAnimationFrame(() => {
              this.setState({
                maxHeight: h,
              });
            });
          });
        },
      );
    }
*/
    onReady();
  }

  _onFullscreenEnter = () => {
    const {
      onFullscreenEnter,
    } = this.props;

    // let h = Dimensions.get("window").height;
    // console.log("HEIGHT: " + h);
    //this.setState({
    //  maxHeight: h,
    //});

    if(onFullscreenEnter){
      onFullscreenEnter();
    }
  }

  _onFullscreenExit = () => {
    const {
      onFullscreenExit,
    } = this.props;

    if(onFullscreenExit){
      onFullscreenExit();
    }
  }

  play = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.play(player);
  }

  pause = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.pause(player);
  }

  seek = (time = 0) => {
    const seekTime = parseFloat(time);

    if (seekTime) {
      let player = findNodeHandle(this._player);
      if(!player){
        return;
      }
      RNBitmovinPlayerModule.seek(player, seekTime);
    }
  }

  mute = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.mute(player);
  }

  unmute = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.unmute(player);
  }

  enterFullscreen = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.enterFullscreen(player);
  }

  exitFullscreen = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.exitFullscreen(player);
  }

  getCurrentTime = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.getCurrentTime(player);
  }

  getDuration = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.getDuration(player);
  }

  getVolume = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.getVolume(player);
  }

  setVolume = (volume = 100) => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.setVolume(player, volume);
  }

  isMuted = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.isMuted(player);
  }

  isPaused = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.isPaused(player);
  }

  isStalled = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.isStalled(player);
  }

  isPlaying = () => {
    let player = findNodeHandle(this._player);
    if(!player){
      return;
    }
    RNBitmovinPlayerModule.isPlaying(player);
  }

  _setRef = (ref) => { this._player = ref; }

  render() {
    const {
      style,
      configuration,
    } = this.props;

    // console.log("From ANDROID render() " + JSON.stringify(this.props));

    const {
      maxHeight,
    } = this.state;

    return (
      <RNBitmovinPlayer
        {...this.props}
        ref={this._setRef}
        onReady={this._onReady}
        onFullscreenEnter={this._onFullscreenEnter}
        onFullscreenExit={this._onFullscreenExit}
        configuration={{
          ...DEFAULT_CONFIGURATION,
          ...configuration,
        }}
        /*
        style={[
          style,
          //maxHeight? {height: maxHeight,} : null,
          {
            backgroundColor: 'black',
          },
        ]}
        */
        style={style}
      />
    );
  }
}

const RNBitmovinPlayer = requireNativeComponent(
  'RNBitmovinPlayer',
  BitmovinPlayer,
  {
    nativeOnly: {
      onSurfaceCreate: true,
      testID: true,
      accessibilityLabel: true,
      accessibilityLiveRegion: true,
      accessibilityComponentType: true,
      importantForAccessibility: true,
      renderToHardwareTextureAndroid: true,
      onLayout: true,
      nativeID: true,
    },
  },
);

export default BitmovinPlayer;
