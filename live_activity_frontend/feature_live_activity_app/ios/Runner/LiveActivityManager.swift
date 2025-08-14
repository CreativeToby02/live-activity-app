import Foundation
import ActivityKit

@available(iOS 16.2, *)
class LiveActivityManager {
    private var activity: Activity<LiveActivityAttributes>?

    func start(data: [String: Any]) {
        let attributes = LiveActivityAttributes()
        let state = LiveActivityAttributes.ContentState(
            minutesToArrive: data["minutesToDelivery"] as? Int ?? 0,
            progress: data["progress"] as? Int ?? 0
        )
        Task {
            activity = try? Activity.request(attributes: attributes, content: .init(state: state, staleDate: nil), pushType: nil)
        }
    }

    func update(data: [String: Any]) {
        guard let activity = activity else { return }
        let state = LiveActivityAttributes.ContentState(
            minutesToArrive: data["minutesToDelivery"] as? Int ?? 0,
            progress: data["progress"] as? Int ?? 0
        )
        Task { await activity.update(using: state) }
    }

    func end() {
        Task { await self.activity?.end(dismissalPolicy: .immediate) }
    }
}
