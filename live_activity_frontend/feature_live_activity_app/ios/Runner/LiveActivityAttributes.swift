import Foundation
import ActivityKit

@available(iOS 16.2, *)
struct LiveActivityAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var minutesToArrive: Int
        var progress: Int
    }
}
