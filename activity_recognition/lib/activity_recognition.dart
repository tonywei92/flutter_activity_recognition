/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

import 'dart:async';

import 'package:flutter/services.dart';

class ActivityRecognition {
  static const MethodChannel _channel =
      const MethodChannel('activity_recognition');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
