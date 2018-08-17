import 'dart:async';

import 'package:activity_recognition/activity_recognition.dart';
import 'package:flutter/material.dart';

Future main() async {
  print("main: init");
  await ActivityRecognition.initialize();

  print("main: runApp");
  runApp(new MyApp());

  await Future.delayed(Duration(milliseconds: 500));

  print("main: periodicWithStreamOutput");
  await ActivityRecognition.periodicWithStreamOutput(
      Duration(milliseconds: 500));
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Plugin example app'),
        ),
        body: new Center(
          child: StreamBuilder(
            builder: (context, snapshot) {
              print("rebuild ui");
              if (snapshot.hasData) {
                Activity act = snapshot.data;
                return Text("Your phone is to ${act.confidence}% ${act.type}!");
              }

              return Text("No activity detected.");
            },
            stream: ActivityRecognition.activityUpdates,
          ),
        ),
      ),
    );
  }
}
