#import "RNBitmovinPlayer.h"
#import <React/RCTLog.h>
#import <React/RCTView.h>

@implementation RNBitmovinPlayer {
    BOOL _fullscreen;
}

@synthesize player = _player;
@synthesize playerView = _playerView;

- (void)dealloc {
    [_player destroy];

    _player = nil;
    _playerView = nil;
}

- (instancetype)init {
    if ((self = [super init])) {
        _fullscreen = NO;
    }
    return self;
}

- (void)setConfiguration:(NSDictionary *)config {
    BMPPlayerConfiguration *configuration = [BMPPlayerConfiguration new];
    NSLog(@"RNBitmovinPlayer SetConfiguration");

    NSDictionary *sourceDict = config[@"Source"];

    if (!sourceDict || !(sourceDict[@"hls"] || sourceDict[@"dash"])) return;

    NSString* sourceUrl = @"";
    if(sourceDict[@"hls"]){
      sourceUrl = sourceDict[@"hls"];
    }else if(sourceDict[@"dash"]){
      sourceUrl = sourceDict[@"dash"];
    }

    // NSLog(@"Source url: %@", sourceUrl);

    [configuration setSourceItemWithString:sourceUrl error:NULL];

    if (sourceDict[@"title"]) {
        configuration.sourceItem.itemTitle = sourceDict[@"title"];
    }

    if (config[@"poster"] && config[@"poster"][@"url"]) {
        configuration.sourceItem.posterSource = [NSURL URLWithString:config[@"poster"][@"url"]];
        configuration.sourceItem.persistentPoster = [config[@"poster"][@"persistent"] boolValue];
    }

    if (![config[@"style"][@"uiEnabled"] boolValue]) {
        configuration.styleConfiguration.uiEnabled = NO;
    }

    if ([config[@"style"][@"systemUI"] boolValue]) {
        configuration.styleConfiguration.userInterfaceType = BMPUserInterfaceTypeSystem;
    }

    if (config[@"style"][@"uiCss"]) {
        NSURL* url = [[NSBundle mainBundle] URLForResource:config[@"style"][@"uiCss"]
                                                withExtension:NULL];
        /*NSString* content = [NSString stringWithContentsOfFile:url
                                        encoding:NSUTF8StringEncoding
                                        error:NULL];
        NSLog(@"CSS: url: %@, contents: \n%@",url, content);
        */

        configuration.styleConfiguration.playerUiCss = url;
    }

    if (config[@"style"][@"supplementalUiCss"]) {
        NSURL* url = [[NSBundle mainBundle] URLForResource:config[@"style"][@"supplementalUiCss"]
                                                withExtension:NULL];
        /*NSString* content = [NSString stringWithContentsOfFile:url
                                        encoding:NSUTF8StringEncoding
                                        error:NULL];
        NSLog(@"supplementalUiCss: url: %@, contents: \n%@",url, content);
        */

        configuration.styleConfiguration.supplementalPlayerUiCss = url;
    }

    if (config[@"style"][@"uiJs"]) {
        NSURL* url = [[NSBundle mainBundle] URLForResource:config[@"style"][@"uiJs"]
                                                withExtension:NULL];
        /*NSString* content = [NSString stringWithContentsOfFile:url
                                        encoding:NSUTF8StringEncoding
                                        error:NULL];
        NSLog(@"uiJs: url: %@, contents: \n%@",url, content);
        */

        configuration.styleConfiguration.supplementalPlayerUiCss = url;
    }

    //TODO: playback configuration
    configuration.playbackConfiguration.autoplayEnabled = true;

    _player = [[BMPBitmovinPlayer alloc] initWithConfiguration:configuration];

    [_player addPlayerListener:self];

    _playerView = [[BMPBitmovinPlayerView alloc] initWithPlayer:_player frame:self.frame];
    _playerView.autoresizingMask = (UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth);
    _playerView.frame = self.bounds;

    [_playerView addUserInterfaceListener:self];

    if ([config[@"style"][@"fullscreenIcon"] boolValue]) {
        _playerView.fullscreenHandler = self;
    }

    [self addSubview:_playerView];
    [self bringSubviewToFront:_playerView];
}

#pragma mark BMPFullscreenHandler protocol
- (BOOL)isFullscreen {
    return _fullscreen;
}

- (void)onFullscreenRequested {
    _fullscreen = YES;
}

- (void)onFullscreenExitRequested {
    _fullscreen = NO;
}

#pragma mark BMPPlayerListener
- (void)onReady:(BMPReadyEvent *)event {
    _onReady(@{});
}

- (void)onPlay:(BMPPlayEvent *)event {
    _onPlay(@{
              @"time": @(event.time),
              });
}

- (void)onPaused:(BMPPausedEvent *)event {
    _onPaused(@{
              @"time": @(event.time),
              });
}

- (void)onTimeChanged:(BMPTimeChangedEvent *)event {
    _onTimeChanged(@{
                @"time": @(event.currentTime),
                });
}

- (void)onStallStarted:(BMPStallStartedEvent *)event {
    _onStallStarted(@{});
}

- (void)onStallEnded:(BMPStallEndedEvent *)event {
    _onStallEnded(@{});
}

- (void)onPlaybackFinished:(BMPPlaybackFinishedEvent *)event {
    _onPlaybackFinished(@{});
}

- (void)onRenderFirstFrame:(BMPRenderFirstFrameEvent *)event {
    _onRenderFirstFrame(@{});
}

- (void)onError:(BMPErrorEvent *)event {
    _onPlayerError(@{
               @"error": @{
                       @"code": @(event.code),
                       @"message": event.message,
                       }
               });
}

- (void)onMuted:(BMPMutedEvent *)event {
    _onMuted(@{});
}

- (void)onUnmuted:(BMPUnmutedEvent *)event {
    _onUnmuted(@{});
}

- (void)onSeek:(BMPSeekEvent *)event {
    _onSeek(@{
              @"seekTarget": @(event.seekTarget),
              @"position": @(event.position),
              });
}

- (void)onSeeked:(BMPSeekedEvent *)event {
    _onSeeked(@{});
}

#pragma mark BMPUserInterfaceListener
- (void)onFullscreenEnter:(BMPFullscreenEnterEvent *)event {
    _onFullscreenEnter(@{});
}

- (void)onFullscreenExit:(BMPFullscreenExitEvent *)event {
    _onFullscreenExit(@{});
}
- (void)onControlsShow:(BMPControlsShowEvent *)event {
    _onControlsShow(@{});
}

- (void)onControlsHide:(BMPControlsHideEvent *)event {
    _onControlsHide(@{});
}

@end
