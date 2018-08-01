/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

part of activity_recognition;

Future<String> _invokeChannelMethod(
    String tag, MethodChannel channel, String method,
    [dynamic arguments]) async {
  print('invoke ${channel.name}->$method [$arguments]');
  String data;
  try {
    data = await channel.invokeMethod(method, arguments);
  } catch (exception, stack) {
    FlutterError.reportError(new FlutterErrorDetails(
      exception: exception,
      stack: stack,
      library: 'geolocation',
      context: 'while invoking ${channel.name}/$method',
    ));
  }

  print(data);
  return data;
}
