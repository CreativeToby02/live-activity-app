import 'package:feature_live_activity_app/src/model/live_notification_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class LiveNotificationService {
  final MethodChannel _method = const MethodChannel(
    "live_activity_channel_name",
  );

  Future<void> startNotifications({required LiveNotificationModel data}) async {
    try {
      await _method.invokeMethod("startNotifications", data.toJson());
    } on PlatformException catch (e) {
      debugPrint("Error starting notifications: ${e.message}");
      throw PlatformException(code: e.code, message: e.message);
    }
  }

  Future<void> updateNotifications({
    required LiveNotificationModel data,
  }) async {
    try {
      await _method.invokeMethod("updateNotifications", data.toJson());
    } on PlatformException catch (e) {
      debugPrint("Error updating notifications: ${e.message}");
      throw PlatformException(code: e.code, message: e.message);
    }
  }

  Future<void> finishDeliveryNotification() async {
    try {
      await _method.invokeMethod("finishDeliveryNotification");
    } on PlatformException catch (e) {
      debugPrint("Error finishing delivery notification: ${e.message}");
      throw PlatformException(code: e.code, message: e.message);
    }
  }

  Future<void> endNotifications() async {
    try {
      await _method.invokeMethod("endNotifications");
    } on PlatformException catch (e) {
      debugPrint("Error ending notifications: ${e.message}");
      throw PlatformException(code: e.code, message: e.message);
    }
  }
}
