// import Flutter
// import UIKit

// @main
// @objc class AppDelegate: FlutterAppDelegate {
//   override func application(
//     _ application: UIApplication,
//     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
//   ) -> Bool {
//     GeneratedPluginRegistrant.register(with: self)
//     return super.application(application, didFinishLaunchingWithOptions: launchOptions)
//   }
// }


import Flutter
import UIKit
import ActivityKit

@available(iOS 16.1, *)
@main
@objc class AppDelegate: FlutterAppDelegate {

    var deliveryActivity: Activity<DeliveryLiveActivityEAttributes>? = nil

    override func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        let controller = window?.rootViewController as! FlutterViewController
        let channel = FlutterMethodChannel(name: "com.example.ios_dybamic_island_flutter", binaryMessenger: controller.binaryMessenger)

        channel.setMethodCallHandler { [weak self] call, result in
            switch call.method {
            case "startNotifications":
                if let args = call.arguments as? [String: Any] {
                    self?.startLiveActivity(args: args)
                    result(nil)
                } else {
                    result(FlutterError(code: "INVALID_ARGUMENTS", message: "Arguments missing", details: nil))
                }
            case "updateNotifications":
                if let args = call.arguments as? [String: Any] {
                    self?.updateLiveActivity(args: args)
                    result(nil)
                } else {
                    result(FlutterError(code: "INVALID_ARGUMENTS", message: "Arguments missing", details: nil))
                }
            case "finishDeliveryNotification", "endNotifications":
                self?.endLiveActivity()
                result(nil)
            default:
                result(FlutterMethodNotImplemented)
            }
        }

        GeneratedPluginRegistrant.register(with: self)
        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }

    func startLiveActivity(args: [String: Any]) {
        if ActivityAuthorizationInfo().areActivitiesEnabled {
            let attributes = DeliveryLiveActivityEAttributes()
            let initialContentState = DeliveryLiveActivityEAttributes.ContentState(
                progress: args["progress"] as? Double ?? 0,
                minutesToDelivery: args["minutesToDelivery"] as? Int ?? 1
            )
            do {
                deliveryActivity = try Activity<DeliveryLiveActivityEAttributes>.request(
                    attributes: attributes,
                    contentState: initialContentState,
                    pushType: nil
                )
            } catch {
                print("Error starting live activity : \(error.localizedDescription)")
            }
        }
    }

    func updateLiveActivity(args: [String: Any]) {
        let updatedContentState = DeliveryLiveActivityEAttributes.ContentState(
            progress: args["progress"] as? Double ?? 0,
            minutesToDelivery: args["minutesToDelivery"] as? Int ?? 1
        )
        Task {
            await deliveryActivity?.update(using: updatedContentState)
        }
    }

    func endLiveActivity() {
        guard let activity = deliveryActivity else { return }
        let finalContentState = DeliveryLiveActivityEAttributes.ContentState(progress: 1, minutesToDelivery: 0)
        Task {
            await activity.end(using: finalContentState, dismissalPolicy: .immediate)
            deliveryActivity = nil
        }
    }
}
