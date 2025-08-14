import Foundation
import UserNotifications
import UIKit

class LiveNotificationManager: NSObject, UNUserNotificationCenterDelegate {
    static let shared = LiveNotificationManager()

    private let center = UNUserNotificationCenter.current()
    private let notificationIdentifier = "LIVE_DELIVERY_NOTIFICATION"
    private let threadIdentifier = "live.delivery.progress"

    func configure() {
        center.delegate = self
    }

    func requestAuthorizationIfNeeded(completion: @escaping (Bool) -> Void) {
        center.getNotificationSettings { settings in
            switch settings.authorizationStatus {
            case .authorized, .provisional, .ephemeral:
                completion(true)
            case .denied:
                completion(false)
            case .notDetermined:
                self.center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, _ in
                    completion(granted)
                }
            @unknown default:
                completion(false)
            }
        }
    }

    func showNotification(progress: Int, minutesToDelivery: Int) {
        deliver(progress: progress, minutesToDelivery: minutesToDelivery, delivered: false)
    }

    func updateNotification(progress: Int, minutesToDelivery: Int) {
        deliver(progress: progress, minutesToDelivery: minutesToDelivery, delivered: false)
    }

    func finishDeliveryNotification() {
        let content = UNMutableNotificationContent()
        content.title = "Delivery complete"
        content.body = "Your order has arrived. Enjoy!"
        content.sound = .default
        content.threadIdentifier = threadIdentifier

        // Replace existing notifications with a final one
        center.removePendingNotificationRequests(withIdentifiers: [notificationIdentifier])
        center.removeDeliveredNotifications(withIdentifiers: [notificationIdentifier])

        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 0.5, repeats: false)
        let request = UNNotificationRequest(identifier: notificationIdentifier, content: content, trigger: trigger)
        center.add(request, withCompletionHandler: nil)
    }

    func endNotification() {
        center.removePendingNotificationRequests(withIdentifiers: [notificationIdentifier])
        center.removeDeliveredNotifications(withIdentifiers: [notificationIdentifier])
    }

    private func deliver(progress: Int, minutesToDelivery: Int, delivered: Bool) {
        let content = UNMutableNotificationContent()
        content.title = "Order on the way"
        content.subtitle = "Arriving in ~\(minutesToDelivery)m"
        content.body = "Progress: \(progress)%"
        content.sound = .default
        content.threadIdentifier = threadIdentifier

        // Remove previous notification with same id to mimic update behavior
        center.removePendingNotificationRequests(withIdentifiers: [notificationIdentifier])
        center.removeDeliveredNotifications(withIdentifiers: [notificationIdentifier])

        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 0.5, repeats: false)
        let request = UNNotificationRequest(identifier: notificationIdentifier, content: content, trigger: trigger)
        center.add(request, withCompletionHandler: nil)
    }

    // Show alerts when app is in foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        if #available(iOS 14.0, *) {
            completionHandler([.banner, .list, .sound])
        } else {
            completionHandler([.alert, .sound])
        }
    }
}
