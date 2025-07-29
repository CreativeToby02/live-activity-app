class LiveNotificationModel {
  final int progress;
  final int minutesToDelivery;

  LiveNotificationModel({
    required this.progress,
    required this.minutesToDelivery,
  });

  Map<String, dynamic> toJson() {
    return {'progress': progress, 'minutesToDelivery': minutesToDelivery};
  }

  factory LiveNotificationModel.fromJson(Map<String, dynamic> json) {
    return LiveNotificationModel(
      progress: json['progress'] ?? 0,
      minutesToDelivery: json['minutesToDelivery'] ?? 0,
    );
  }
}
