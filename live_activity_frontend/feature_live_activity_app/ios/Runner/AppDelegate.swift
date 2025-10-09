import ActivityKit
import Flutter
import UIKit

@main
@objc class AppDelegate: FlutterAppDelegate {
  private let channelName = "androidInteractiveNotifications"  // reuse same channel name
  private var liveManager: Any?

  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)

    // Configure notifications (fallback mode)
    LiveNotificationManager.shared.configure()
    LiveNotificationManager.shared.requestAuthorizationIfNeeded { _ in }

    // Setup MethodChannel bridge
    if let controller = window?.rootViewController as? FlutterViewController {
      let channel = FlutterMethodChannel(
        name: channelName, binaryMessenger: controller.binaryMessenger)
      channel.setMethodCallHandler { call, result in
        switch call.method {
        case "startNotifications":
          if let args = call.arguments as? [String: Any] {
            if #available(iOS 16.2, *) {
              if self.liveManager == nil { self.liveManager = LiveActivityManager() }
              (self.liveManager as! LiveActivityManager).start(data: args)
            } else {
              let progress = args["progress"] as? Int ?? 0
              let minutes = args["minutesToDelivery"] as? Int ?? 0
              LiveNotificationManager.shared.showNotification(
                progress: progress, minutesToDelivery: minutes)
            }
            result("Notification displayed")
          } else {
            result(
              FlutterError(
                code: "INVALID_ARGUMENTS", message: "Missing progress/minutesToDelivery",
                details: nil))
          }
        case "updateNotifications":
          if let args = call.arguments as? [String: Any] {
            if #available(iOS 16.2, *) {
              (self.liveManager as? LiveActivityManager)?.update(data: args)
            } else {
              let progress = args["progress"] as? Int ?? 0
              let minutes = args["minutesToDelivery"] as? Int ?? 0
              LiveNotificationManager.shared.updateNotification(
                progress: progress, minutesToDelivery: minutes)
            }
            result("Notification updated")
          } else {
            result(
              FlutterError(
                code: "INVALID_ARGUMENTS", message: "Missing progress/minutesToDelivery",
                details: nil))
          }
        case "finishDeliveryNotification":
          if #available(iOS 16.2, *) { (self.liveManager as? LiveActivityManager)?.end() }
          LiveNotificationManager.shared.finishDeliveryNotification()
          result("Notification delivered")
        case "endNotifications":
          if #available(iOS 16.2, *) { (self.liveManager as? LiveActivityManager)?.end() }
          LiveNotificationManager.shared.endNotification()
          result("Notification cancelled")
        default:
          result(FlutterMethodNotImplemented)
        }
      }
    }

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
