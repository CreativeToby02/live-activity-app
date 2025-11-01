// main.dart
import 'dart:async';

import 'package:feature_live_activity_app/src/model/live_notification_model.dart';
import 'package:feature_live_activity_app/src/service/live_notification_service.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Live Activity Demo',
      theme: ThemeData(primarySwatch: Colors.blue, useMaterial3: true),
      home: LiveActivityScreen(),
    );
  }
}

class LiveActivityScreen extends StatefulWidget {
  const LiveActivityScreen({super.key});

  @override
  LiveActivityScreenState createState() => LiveActivityScreenState();
}

class LiveActivityScreenState extends State<LiveActivityScreen> {
  final LiveNotificationService activityService = LiveNotificationService();
  Timer? _timer;
  bool _isDeliveryActive = false;
  int _progress = 0;
  int _minutesToDelivery = 1;
  final int _rideDuration = 1; // Duration in minutes

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  void _startDelivery() async {
    setState(() {
      _isDeliveryActive = true;
      _progress = 0;
      _minutesToDelivery = _rideDuration;
    });

    await activityService.startNotifications(
      data: LiveNotificationModel(
        progress: _progress,
        minutesToDelivery: _minutesToDelivery,
      ),
    );

    // Calculate progress increment based on duration
    // Total updates = (_rideDuration * 60) / 2 seconds = _rideDuration * 30
    double progressIncrement = 100 / (_rideDuration * 30);

    _timer = Timer.periodic(Duration(seconds: 2), (timer) async {
      if (_progress >= 100) {
        await _finishDelivery();
        return;
      }

      setState(() {
        _progress = (_progress + progressIncrement).round();
        if (_progress > 100) _progress = 100;

        // Calculate remaining minutes based on progress
        _minutesToDelivery = (_rideDuration * (1 - _progress / 100)).round();
        if (_minutesToDelivery < 0) _minutesToDelivery = 0;
      });

      await activityService.updateNotifications(
        data: LiveNotificationModel(
          progress: _progress,
          minutesToDelivery: _minutesToDelivery,
        ),
      );
    });
  }

  Future<void> _finishDelivery() async {
    _timer?.cancel();

    setState(() {
      _isDeliveryActive = false;
      _progress = 100;
      _minutesToDelivery = 0;
    });

    await activityService.finishDeliveryNotification();

    Timer(Duration(seconds: 5), () async {
      await activityService.endNotifications();
    });
  }

  void _cancelDelivery() async {
    _timer?.cancel();

    setState(() {
      _isDeliveryActive = false;
      _progress = 0;
      _minutesToDelivery = _rideDuration;
    });

    await activityService.endNotifications();
  }

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((val) {
      _startDelivery();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Delivery Tracker'),
        backgroundColor: Colors.blue,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          children: [
            // Delivery Status Card
            Container(
              width: double.infinity,
              padding: EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black12,
                    blurRadius: 8,
                    offset: Offset(0, 2),
                  ),
                ],
              ),
              child: Column(
                children: [
                  Container(
                    width: 80,
                    height: 80,
                    decoration: BoxDecoration(
                      color: Colors.blue.withOpacity(0.1),
                      shape: BoxShape.circle,
                    ),
                    child: Icon(
                      Icons.local_shipping,
                      size: 40,
                      color: Colors.blue,
                    ),
                  ),
                  SizedBox(height: 24),
                  Text(
                    'Delivery Status',
                    style: TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.w600,
                      color: Colors.grey[800],
                    ),
                  ),
                  SizedBox(height: 24),
                  // Progress Section
                  Column(
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            'Progress',
                            style: TextStyle(
                              fontSize: 14,
                              color: Colors.grey[600],
                            ),
                          ),
                          Text(
                            '$_progress%',
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                              color: Colors.blue,
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: 8),
                      LinearProgressIndicator(
                        value: _progress / 100,
                        backgroundColor: Colors.grey[200],
                        valueColor: AlwaysStoppedAnimation<Color>(Colors.blue),
                        borderRadius: BorderRadius.circular(4),
                        minHeight: 8,
                      ),
                    ],
                  ),
                  SizedBox(height: 20),
                  // Delivery Time
                  Container(
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                    decoration: BoxDecoration(
                      color: _minutesToDelivery > 0
                          ? Colors.orange.withOpacity(0.1)
                          : Colors.green.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          _minutesToDelivery > 0
                              ? Icons.access_time
                              : Icons.check_circle,
                          color: _minutesToDelivery > 0
                              ? Colors.orange
                              : Colors.green,
                          size: 20,
                        ),
                        SizedBox(width: 8),
                        Text(
                          _minutesToDelivery > 0
                              ? 'Arriving in $_minutesToDelivery ${_minutesToDelivery == 1 ? "minute" : "minutes"}'
                              : 'Delivered Successfully!',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w500,
                            color: _minutesToDelivery > 0
                                ? Colors.orange
                                : Colors.green,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            SizedBox(height: 32),
            // Action Button
            SizedBox(
              width: double.infinity,
              height: 56,
              child: ElevatedButton(
                onPressed: _isDeliveryActive ? _cancelDelivery : _startDelivery,
                style: ElevatedButton.styleFrom(
                  backgroundColor: _isDeliveryActive ? Colors.red : Colors.blue,
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  elevation: 0,
                ),
                child: Text(
                  _isDeliveryActive
                      ? 'Cancel Delivery'
                      : 'Start Delivery Tracking',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
                ),
              ),
            ),
            SizedBox(height: 24),
            // Info Text
            Text(
              'Live tracking updates will appear in your notifications. '
              'The delivery simulation updates every 2 seconds.',
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey[600],
                height: 1.4,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
