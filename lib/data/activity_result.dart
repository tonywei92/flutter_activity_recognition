/*
 * Copyright (c) 2018. Daniel Morawetz
 * Licensed under Apache License v2.0
 */

part of activity_recognition;

class Activity {
  String type;
  int confidence;

  Activity(this.type, this.confidence);

  factory Activity.fromJson(List<dynamic> json) {
    var act = json[0];
    return Activity(act['type'], act['confidence']);
  }
}

class ActivityResult {}
