#import "ActivityRecognitionPlugin.h"
#import <activity_recognition_alt/activity_recognition-Swift.h>

@implementation ActivityRecognitionPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    if (@available(iOS 9.0, *)) {
        [SwiftActivityRecognitionPlugin registerWithRegistrar:registrar];
    } else {
        // Fallback on earlier versions
    }
}
@end
