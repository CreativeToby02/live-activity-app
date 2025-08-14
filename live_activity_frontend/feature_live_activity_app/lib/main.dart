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
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: LiveActivityScreen(),
    );
  }
}

class LiveActivityScreen extends StatefulWidget {
  const LiveActivityScreen({super.key});

  @override
  _LiveActivityScreenState createState() => _LiveActivityScreenState();
}

class _LiveActivityScreenState extends State<LiveActivityScreen> {
  final LiveNotificationService _notificationService =
      LiveNotificationService();

  Timer? _timer;
  bool _isDeliveryActive = false;
  int _progress = 0;
  int _minutesToDelivery = 30;

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  void _startDelivery() async {
    setState(() {
      _isDeliveryActive = true;
      _progress = 0;
      _minutesToDelivery = 30;
    });

    // Start the live notification
    await _notificationService.startNotifications(
      data: LiveNotificationModel(
        progress: _progress,
        minutesToDelivery: _minutesToDelivery,
      ),
    );

    // Simulate delivery progress
    _timer = Timer.periodic(Duration(seconds: 2), (timer) async {
      if (_progress >= 100) {
        await _finishDelivery();
        return;
      }

      setState(() {
        _progress += 5;
        _minutesToDelivery = (30 - (_progress * 30 / 100)).round();
        if (_minutesToDelivery < 0) _minutesToDelivery = 0;
      });

      // Update the notification
      await _notificationService.updateNotifications(
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

    // Show delivery finished notification
    await _notificationService.finishDeliveryNotification();

    // Auto dismiss after 5 seconds
    Timer(Duration(seconds: 5), () async {
      await _notificationService.endNotifications();
    });
  }

  void _cancelDelivery() async {
    _timer?.cancel();

    setState(() {
      _isDeliveryActive = false;
      _progress = 0;
      _minutesToDelivery = 30;
    });

    await _notificationService.endNotifications();
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
        title: Text('Live Activity Demo'),
        backgroundColor: Colors.teal,
      ),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Card(
              elevation: 4,
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: [
                    Icon(Icons.local_shipping, size: 60, color: Colors.teal),
                    SizedBox(height: 16),
                    Text(
                      'Delivery Status',
                      style: TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    SizedBox(height: 16),
                    LinearProgressIndicator(
                      value: _progress / 100,
                      backgroundColor: Colors.grey[300],
                      valueColor: AlwaysStoppedAnimation<Color>(Colors.green),
                    ),
                    SizedBox(height: 8),
                    Text(
                      '$_progress% Complete',
                      style: TextStyle(fontSize: 16),
                    ),
                    SizedBox(height: 8),
                    Text(
                      _minutesToDelivery > 0
                          ? 'Arriving in $_minutesToDelivery ${_minutesToDelivery == 1 ? "minute" : "minutes"}'
                          : 'Delivered!',
                      style: TextStyle(
                        fontSize: 18,
                        color: _minutesToDelivery > 0
                            ? Colors.blue
                            : Colors.green,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
              ),
            ),
            SizedBox(height: 30),
            if (!_isDeliveryActive)
              ElevatedButton(
                onPressed: _startDelivery,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.lightBlue,
                  padding: EdgeInsets.symmetric(vertical: 16),
                ),
                child: Text(
                  'Start Delivery Tracking',
                  style: TextStyle(fontSize: 18, color: Colors.white),
                ),
              )
            else
              ElevatedButton(
                onPressed: _cancelDelivery,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.red,
                  padding: EdgeInsets.symmetric(vertical: 16),
                ),
                child: Text('Cancel Delivery', style: TextStyle(fontSize: 18)),
              ),
            SizedBox(height: 20),
            Text(
              'This demo simulates a delivery tracking system with live notifications. When you start tracking, you\'ll see an ongoing notification that updates in real-time.',
              textAlign: TextAlign.center,
              style: TextStyle(fontSize: 14, color: Colors.grey[600]),
            ),
          ],
        ),
      ),
    );
  }
}
