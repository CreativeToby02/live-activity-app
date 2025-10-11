import ActivityKit
import Foundation

@available(iOS 16.2, *)
struct LiveActivityAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var driverCode: String
        var carModel: String
        var minutesToArrive: Int
        var carArriveProgress: Int
    }
}

@available(iOS 16.2, *)
class LiveNotificationManager {
    private var liveActivity: Activity<LiveActivityAttributes>? = nil

    func startLiveActivity(data: [String: Any]?) {
        let attributes = LiveActivityAttributes()
        if let info = data {
            let state = LiveActivityAttributes.ContentState(
                driverCode: info["driverCode"] as? String ?? "",
                carModel: info["carModel"] as? String ?? "",
                minutesToArrive: info["minutesToArrive"] as? Int ?? 0,
                carArriveProgress: info["carArriveProgress"] as? Int ?? 0
            )
            Task {
                liveActivity = try? Activity<LiveActivityAttributes>.request(
                    attributes: attributes, content: .init(state: state, staleDate: nil))
            }
        }
    }

    func updateLiveActivity(data: [String: Any]?) {
        if let info = data {
            let updatedState = LiveActivityAttributes.ContentState(
                driverCode: info["driverCode"] as? String ?? "",
                carModel: info["carModel"] as? String ?? "",
                minutesToArrive: info["minutesToArrive"] as? Int ?? 0,
                carArriveProgress: info["carArriveProgress"] as? Int ?? 0
            )

            Task {
                await liveActivity?.update(using: updatedState)
            }
        }
    }

    func endLiveActivity() {
        Task {
            await self.liveActivity?.end(dismissalPolicy: .immediate)
        }
    }
}
