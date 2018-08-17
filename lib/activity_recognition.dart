/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

library activity_recognition;

import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:isolate';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:http/http.dart' as http;

part 'package:activity_recognition/data/activity.dart';

const String _backgroundName = 'activity_recognition/background_channel';

// This is the entrypoint for the background isolate. Since we can only enter
// an isolate once, we setup a MethodChannel to listen for method invokations
// from the native portion of the plugin. This allows for the plugin to perform
// any necessary processing in Dart (e.g., populating a custom object) before
// invoking the provided callback.
void _alarmManagerCallbackDispatcher() async {
  print("_alarmManagerCallbackDispatcher()");
  const MethodChannel _channel =
      MethodChannel("activity_recognition/method_channel", JSONMethodCodec());
  const MethodChannel _backgroundChannel =
      MethodChannel(_backgroundName, JSONMethodCodec());

  // Setup Flutter state needed for MethodChannels.
  WidgetsFlutterBinding.ensureInitialized();

  print("ensured initialize");

  // This is where the magic happens and we handle background events from the
  // native portion of the plugin.
  _backgroundChannel.setMethodCallHandler((MethodCall call) {
    print("_alarmManagerCallbackDispatcher method handler");
    final dynamic args = call.arguments;
    print("arguments received");
    final CallbackHandle handle = new CallbackHandle.fromRawHandle(args[0]);
    print("handle ok");

    print("activity: ${args[1]}");

    // PluginUtilities.getCallbackFromHandle performs a lookup based on the
    // callback handle and returns a tear-off of the original callback.
    final Function(String) closure =
        PluginUtilities.getCallbackFromHandle(handle);

    if (closure == null) {
      print('Fatal: could not find callback');
      exit(-1);
    }
    closure(args[1]);
  });

  print("set call handler");

  // Once we've finished initializing, let the native portion of the plugin
  // know that it can start scheduling alarms.
  _channel.invokeMethod('AlarmService.initialized');

  print("invoked initialized");
}

/// A Flutter plugin for registering Dart callbacks with the
/// ActivityRecognitionClient on Android, CoreMotion on iOS.
///
/// See the example/ directory in this package for sample usage.
class ActivityRecognition {
  static const String _channelName = 'activity_recognition/method_channel';
  static const String _sendPortName = 'activity_recognition/send_port';
  static const MethodChannel _channel =
      MethodChannel(_channelName, JSONMethodCodec());

  static StreamController<Activity> _activityStreamController =
      StreamController<Activity>();

  static Stream<Activity> get activityUpdates =>
      _activityStreamController.stream;

  /// Starts the [AndroidAlarmManager] service. This must be called before
  /// setting any alarms.
  ///
  /// Returns a [Future] that resolves to `true` on success and `false` on
  /// failure.
  static Future<bool> initialize() async {
    final int isolateId = Isolate.current.hashCode;
    print("initialize() on isolated: $isolateId");

    final CallbackHandle handle =
        PluginUtilities.getCallbackHandle(_alarmManagerCallbackDispatcher);

    print("got handle");
    if (handle == null) {
      print("handle not valid");
      return false;
    }

    print("handle valid, call method channel");
    final dynamic r = await _channel
        .invokeMethod('AlarmService.start', <dynamic>[handle.toRawHandle()]);

    var port = ReceivePort();
    IsolateNameServer.registerPortWithName(port.sendPort, _sendPortName);
    _isolateCommunication(port);

    return r ?? false;
  }

  /// Schedules repeating activity updates and outputs them to `activityUpdates`.
  static Future<bool> periodicWithStreamOutput(
      Duration detectionInterval) async {
    return periodic(detectionInterval, _streamCallback);
  }

  /// Schedules repeating activity updates.
  ///
  /// The `callback` will run whether or not the main application is running or
  /// in the foreground. It will run in the Isolate owned by the
  /// AndroidAlarmManager service.
  ///
  /// `callback` must be either a top-level function or a static method from a
  /// class.
  ///
  /// The repeating timer is uniquely identified by `id`. Calling this function
  /// again with the same `id` will cancel and replace the existing timer.
  ///
  /// If `exact` is passed as `true`, the timer will be created with Android's
  /// `AlarmManager.setRepeating`. When `exact` is `false` (the default), the
  /// timer will be created with `AlarmManager.setInexactRepeating`.
  ///
  /// If `wakeup` is passed as `true`, the device will be woken up when the
  /// alarm fires. If `wakeup` is false (the default), the device will not be
  /// woken up to service the alarm.
  ///
  /// Returns a [Future] that resolves to `true` on success and `false` on
  /// failure.
  static Future<bool> periodic(
      Duration detectionInterval, dynamic Function(String) callback) async {
    final CallbackHandle handle = PluginUtilities.getCallbackHandle(callback);
    if (handle == null) {
      return false;
    }
    final dynamic r = await _channel.invokeMethod('Alarm.periodic',
        <dynamic>[1, detectionInterval.inMilliseconds, handle.toRawHandle()]);
    return (r == null) ? false : r;
  }

  static _isolateCommunication(ReceivePort port) async {
    await for (var msg in port) {
      print("received activity update in _isolateCommunication");
      final int isolateId = Isolate.current.hashCode;
      print("on isolated: $isolateId");
      final Activity activity = Activity.fromJson(json.decode(msg));
      _activityStreamController.add(activity);

      http.post(
        "https://vserver.resi-verbindet.at/backend/activity/new",
        body: json.encode(activity),
        headers: {"Content-Type": "application/json"},
      );
    }
  }

  /// Callback which takes activities and publishes them via `activityUpdates`.
  static _streamCallback(String jsonString) {
    print("received activity update in callback");
    final int isolateId = Isolate.current.hashCode;
    print("on isolated: $isolateId");
    var sendPort = IsolateNameServer.lookupPortByName(_sendPortName);
    sendPort.send(jsonString);
  }
}
