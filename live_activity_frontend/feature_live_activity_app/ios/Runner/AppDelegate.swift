import ActivityKit
import Flutter
import UIKit

@main
@objc class AppDelegate: FlutterAppDelegate {
  private let channelName = "androidInteractiveNotifications"

  private func getLiveNotificationManager() -> LiveNotificationManager? {
    if #available(iOS 16.2, *) {
      return LiveNotificationManager()
    }
    return nil
  }

  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)

    // Setup MethodChannel bridge
    if let controller = window?.rootViewController as? FlutterViewController {
      let channel = FlutterMethodChannel(
        name: channelName, binaryMessenger: controller.binaryMessenger)
      channel.setMethodCallHandler { call, result in
        switch call.method {
        case "startNotifications":
          if let args = call.arguments as? [String: Any] {
            if #available(iOS 16.2, *) {
              print("Starting Live Activity with args: \(args)")
                self.registerDevice(application: application,result: result )
                self.requestNotificationPermissions(result: result)
              self.getLiveNotificationManager()?.startLiveActivity(data: args)
              result("Live Activity started")
            } else {
              result(
                FlutterError(
                  code: "UNSUPPORTED_VERSION", message: "Live Activities require iOS 16.2+",
                  details: nil))
            }
          } else {
            result(
              FlutterError(
                code: "INVALID_ARGUMENTS", message: "Missing required arguments",
                details: nil))
          }
        case "updateNotifications":
          if let args = call.arguments as? [String: Any] {
            if #available(iOS 16.2, *) {
              print("Updating Live Activity with args: \(args)")
              self.getLiveNotificationManager()?.updateLiveActivity(data: args)
              result("Live Activity updated")
            } else {
              result(
                FlutterError(
                  code: "UNSUPPORTED_VERSION", message: "Live Activities require iOS 16.2+",
                  details: nil))
            }
          } else {
            result(
              FlutterError(
                code: "INVALID_ARGUMENTS", message: "Missing required arguments",
                details: nil))
          }
        case "finishDeliveryNotification":
          if #available(iOS 16.2, *) {
            print("Finishing Live Activity")
            self.getLiveNotificationManager()?.endLiveActivity()
            result("Live Activity ended")
          } else {
            result(
              FlutterError(
                code: "UNSUPPORTED_VERSION", message: "Live Activities require iOS 16.2+",
                details: nil))
          }
        case "endNotifications":
          if #available(iOS 16.2, *) {
            print("Ending Live Activity")
            self.getLiveNotificationManager()?.endLiveActivity()
            result("Live Activity cancelled")
          } else {
            result(
              FlutterError(
                code: "UNSUPPORTED_VERSION", message: "Live Activities require iOS 16.2+",
                details: nil))
          }
        default:
          result(FlutterMethodNotImplemented)
        }
      }
    }

    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

  private func registerDevice(application: UIApplication, result: @escaping FlutterResult) {
    application.registerForRemoteNotifications()
    result("Device Token registration initiated")
  }

  private func requestNotificationPermissions(result: @escaping FlutterResult) {
    UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) {
      granted, error in
      if let error = error {
        result(
          FlutterError(
            code: "PERMISSION_ERROR", message: "Failed to request permissions",
            details: error.localizedDescription))
        return
      }
      result(granted)
    }
  }

}
