/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

library activity_recognition;

import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

part 'package:activity_recognition/channel/activity_channel.dart';
part 'package:activity_recognition/channel/helper.dart';
part 'package:activity_recognition/data/activity_result.dart';

class ActivityRecognition {
  /// Requests the current [Activity].
  static Future<ActivityResult> currentActivity() =>
      _activityChannel.currentActivity();

  /// Requests continuous [Activity] updates.
  static Stream<Activity> activityUpdates() {
    _activityChannel.startActivityUpdates();
    return _activityChannel.activityUpdates;
  }

  static final _activityChannel = _ActivityChannel();
}
