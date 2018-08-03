# activity_recognition

[![pub package](https://img.shields.io/pub/v/activity_recognition.svg)](https://pub.dartlang.org/packages/activity_recognition)

Activity recognition plugin for Android and iOS. Only working while App is running (= not terminated by the user or OS).

## Getting Started

Check out the `example` directory for a sample app using activity recognition.

### Android Integration

Add permission to your Android Manifest:
```xml
<uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
```

Add the plugin service:
```xml
<service android:name="at.resiverbindet.activityrecognition.activity.ActivityRecognizedService" />
```

### iOS Integration

An iOS app linked on or after iOS 10.0 must include usage description keys in its *Info.plist* file
for the types of data it needs. Failure to include these keys will cause the app to crash.
To access motion and fitness data specifically, it must include `NSMotionUsageDescription`.

### Flutter Integration

```Dart
import 'package:activity_recognition/activity_recognition.dart';

ActivityRecognition.activityUpdates()
```

### Flutter help

For help getting started with Flutter, view our online
[documentation](https://flutter.io/).

For help on editing plugin code, view the [documentation](https://flutter.io/platform-plugins/#edit-code).