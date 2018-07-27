#import "ActivityRecognitionPlugin.h"
#import <activity_recognition/activity_recognition-Swift.h>

@implementation ActivityRecognitionPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftActivityRecognitionPlugin registerWithRegistrar:registrar];
}
@end
