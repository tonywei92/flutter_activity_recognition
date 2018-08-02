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
  /// Requests continuous [Activity] updates.
  ///
  /// The Stream will output the *most probable* [Activity].
  static Stream<Activity> activityUpdates() => _activityChannel.activityUpdates;

  static final _activityChannel = _ActivityChannel();
}
