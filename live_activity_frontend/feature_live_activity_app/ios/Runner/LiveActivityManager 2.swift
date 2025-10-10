//
//  LiveActivityManager.swift
//  Runner
//
//  Created to resolve 'Cannot find LiveActivityManager in scope' error.
//

import Foundation
import ActivityKit

@available(iOS 16.2, *)
class LiveActivityManager {
    private var activity: Activity<LiveActivityWidgetAttributes>?

    func showNotification(progress: Int, minutesToDelivery: Int) {
        let attributes = LiveActivityWidgetAttributes()
        let contentState = LiveActivityWidgetAttributes.ContentState(
            driverCode: "",
            carModel: "",
            minutesToArrive: minutesToDelivery,
            carArriveProgress: progress)

        do {
            activity = try Activity.request(attributes: attributes, contentState: contentState)
        } catch {
            print("Failed to start live activity: \(error)")
        }
    }

    func updateNotification(progress: Int, minutesToDelivery: Int) {
        guard let activity = activity else { return }
        let contentState = LiveActivityWidgetAttributes.ContentState(
            driverCode: "",
            carModel: "",
            minutesToArrive: minutesToDelivery,
            carArriveProgress: progress)
        Task {
            await activity.update(using: contentState)
        }
    }

    func finishDeliveryNotification() {
        guard let activity = activity else { return }
        Task {
            await activity.end(dismissalPolicy: .immediate)
        }
    }

    func endNotification() {
        guard let activity = activity else { return }
        Task {
            await activity.end(dismissalPolicy: .immediate)
        }
    }
}
