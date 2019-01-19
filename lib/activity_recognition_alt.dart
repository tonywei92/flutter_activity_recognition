/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

library activity_recognition_alt;

import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
part 'package:activity_recognition_alt/channel/activity_channel.dart';
part 'package:activity_recognition_alt/data/activity.dart';

class ActivityRecognitionAlt {
  /// Requests continuous [Activity] updates.
  ///
  /// The Stream will output the *most probable* [Activity].
  static Stream<Activity> activityUpdates() => _activityChannel.activityUpdates;

  static final _activityChannel = _ActivityChannel();
}
