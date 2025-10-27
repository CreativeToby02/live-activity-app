//
//  DeliveryActivityELiveActivity.swift
//  DeliveryActivityE
//
//  Created by Kudus Rufai on 27/10/2025.
//

import ActivityKit
import WidgetKit
import SwiftUI

struct DeliveryActivityEAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        // Dynamic stateful properties about your activity go here!
        var emoji: String
    }

    // Fixed non-changing properties about your activity go here!
    var name: String
}

struct DeliveryActivityELiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: DeliveryActivityEAttributes.self) { context in
            // Lock screen/banner UI goes here
            VStack {
                Text("Hello \(context.state.emoji)")
            }
            .activityBackgroundTint(Color.cyan)
            .activitySystemActionForegroundColor(Color.black)

        } dynamicIsland: { context in
            DynamicIsland {
                // Expanded UI goes here.  Compose the expanded UI through
                // various regions, like leading/trailing/center/bottom
                DynamicIslandExpandedRegion(.leading) {
                    Text("Leading")
                }
                DynamicIslandExpandedRegion(.trailing) {
                    Text("Trailing")
                }
                DynamicIslandExpandedRegion(.bottom) {
                    Text("Bottom \(context.state.emoji)")
                    // more content
                }
            } compactLeading: {
                Text("L")
            } compactTrailing: {
                Text("T \(context.state.emoji)")
            } minimal: {
                Text(context.state.emoji)
            }
            .widgetURL(URL(string: "http://www.apple.com"))
            .keylineTint(Color.red)
        }
    }
}

extension DeliveryActivityEAttributes {
    fileprivate static var preview: DeliveryActivityEAttributes {
        DeliveryActivityEAttributes(name: "World")
    }
}

extension DeliveryActivityEAttributes.ContentState {
    fileprivate static var smiley: DeliveryActivityEAttributes.ContentState {
        DeliveryActivityEAttributes.ContentState(emoji: "😀")
     }
     
     fileprivate static var starEyes: DeliveryActivityEAttributes.ContentState {
         DeliveryActivityEAttributes.ContentState(emoji: "🤩")
     }
}

#Preview("Notification", as: .content, using: DeliveryActivityEAttributes.preview) {
   DeliveryActivityELiveActivity()
} contentStates: {
    DeliveryActivityEAttributes.ContentState.smiley
    DeliveryActivityEAttributes.ContentState.starEyes
}
