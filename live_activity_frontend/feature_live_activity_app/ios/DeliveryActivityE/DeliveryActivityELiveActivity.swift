//
//  DeliveryActivityELiveActivity.swift
//  DeliveryActivityE
//
//  Created by Kudus Rufai on 27/10/2025.
//

import ActivityKit
import WidgetKit
import SwiftUI

// MARK: - Activity Attributes
@available(iOS 16.1, *)
struct DeliveryLiveActivityEAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var progress: Double
        var minutesToDelivery: Int
    }
}

@available(iOS 16.1, *)
// MARK: - Live Activity Widget
struct DeliveryLiveActivityELiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: DeliveryLiveActivityEAttributes.self) { context in
            // Lock screen / banner UI
            ZStack {
                RoundedRectangle(cornerRadius: 24, style: .continuous)
                    .fill(Color.cyan.opacity(0.18))
                    .background(.ultraThinMaterial)
                    .shadow(color: Color.black.opacity(0.1), radius: 8, y: 4)

                VStack(alignment: .leading, spacing: 12) {
                    HStack(alignment: .top) {
                        // Left: Delivery icon and items
                        Label {
                            Text("3 items")
                                .font(.headline)
                                .frame(maxWidth: .infinity, alignment: .leading)
                        } icon: {
                            Image(systemName: "bag.fill")
                                .resizable()
                                .frame(width: 28, height: 28)
                                .foregroundColor(.white)
                                .background(Circle().fill(Color.cyan).shadow(radius: 2))
                        }
                        Spacer()
                        // Right: Customer name + stars
                        VStack(alignment: .trailing, spacing: 2) {
                            Text("Juan C.")
                                .font(.headline)
                                .foregroundStyle(.white)
                                .frame(maxWidth: .infinity, alignment: .trailing)
                            HStack(spacing: 2) {
                                ForEach(0..<5) { i in
                                    Image(systemName: "star.fill")
                                        .resizable()
                                        .frame(width: 10, height: 10)
                                        .foregroundColor(i < 4 ? Color.blue : Color.gray.opacity(0.5))
                                }
                            }
                            .frame(maxWidth: .infinity, alignment: .trailing)
                        }
                    }

                    HStack {
                        Text("Arriving in ")
                            .font(.subheadline)
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        Text("\(context.state.minutesToDelivery) minutes")
                            .font(.subheadline.bold())
                            .foregroundColor(context.state.progress > 0.9 ? Color.red : Color.blue)
                            .animation(.easeInOut, value: context.state.progress)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }

                    // Animated progress bar without GeometryReader
                    HStack(spacing: 0) {
                        Capsule()
                            .fill(Color.white.opacity(0.25))
                            .frame(height: 7)
                            .overlay(
                                Capsule()
                                    .fill(LinearGradient(gradient: Gradient(colors: [Color.blue, Color.cyan]), startPoint: .leading, endPoint: .trailing))
                                    .frame(width: max(120 * context.state.progress, 16), height: 7)
                                    .animation(.spring(response: 0.7, dampingFraction: 0.7), value: context.state.progress), alignment: .leading
                            )
                    }
                    .frame(height: 10)
                    .frame(maxWidth: .infinity)
                }
                .padding(.vertical, 12)
                .padding(.horizontal, 14)
            }
            .clipped()
            .transition(.opacity.combined(with: .scale))

        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
                    HStack(spacing: 6) {
                        Image(systemName: "bag.fill")
                            .resizable()
                            .frame(width: 22, height: 22)
                            .foregroundColor(.white)
                            .background(Circle().fill(Color.cyan).shadow(radius: 1))
                        Text("3 items")
                            .font(.subheadline)
                            .foregroundStyle(.white)
                    }
                    .padding(.vertical, 3)
                }

                DynamicIslandExpandedRegion(.center) {
                    HStack(spacing: 0) {
                        Text("Arriving in ")
                            .font(.subheadline)
                            .foregroundStyle(.white)
                        Text("\(context.state.minutesToDelivery) minutes")
                            .font(.subheadline.bold())
                            .foregroundColor(context.state.progress > 0.9 ? Color.red : Color.blue)
                            .animation(.easeInOut, value: context.state.progress)
                    }
                    .padding(.vertical, 3)
                }

                DynamicIslandExpandedRegion(.trailing) {
                    VStack(alignment: .trailing, spacing: 2) {
                        Text("Juan C.")
                            .font(.subheadline)
                            .foregroundStyle(.white)
                        HStack(spacing: 1) {
                            ForEach(0..<5) { i in
                                Image(systemName: "star.fill")
                                    .resizable()
                                    .frame(width: 8, height: 8)
                                    .foregroundColor(i < 4 ? Color.blue : Color.gray.opacity(0.5))
                            }
                        }
                    }
                    .padding(.vertical, 3)
                }

                DynamicIslandExpandedRegion(.bottom) { }
            } compactLeading: {
                Image(systemName: "bag.fill")
                    .resizable()
                    .frame(width: 18, height: 18)
                    .foregroundColor(.cyan)
            } compactTrailing: {
                Text("\(context.state.minutesToDelivery)m")
                    .font(.caption.bold())
                    .foregroundColor(context.state.progress > 0.9 ? Color.red : Color.blue)
                    .animation(.easeInOut, value: context.state.progress)
            } minimal: {
                Text("\(context.state.minutesToDelivery)m")
                    .font(.caption2.bold())
                    .foregroundColor(context.state.progress > 0.9 ? Color.red : Color.blue)
                    .animation(.easeInOut, value: context.state.progress)
            }
            .widgetURL(URL(string: "http://www.apple.com"))
            .keylineTint(Color.red)
        }
    }
}

@available(iOS 16.1, *)
extension DeliveryLiveActivityEAttributes {
    fileprivate static var preview: DeliveryLiveActivityEAttributes {
        DeliveryLiveActivityEAttributes()
    }
}

@available(iOS 16.1, *)
extension DeliveryLiveActivityEAttributes.ContentState {
    fileprivate static var typical: DeliveryLiveActivityEAttributes.ContentState {
        DeliveryLiveActivityEAttributes.ContentState(progress: 0.6, minutesToDelivery: 8)
    }
    
    fileprivate static var almostThere: DeliveryLiveActivityEAttributes.ContentState {
        DeliveryLiveActivityEAttributes.ContentState(progress: 0.95, minutesToDelivery: 1)
    }
}

;@available(iOS 17.0, *)
#Preview("Notification", as: .content, using: DeliveryLiveActivityEAttributes.preview) {
    DeliveryLiveActivityELiveActivity()
} contentStates: {
    DeliveryLiveActivityEAttributes.ContentState.typical
    DeliveryLiveActivityEAttributes.ContentState.almostThere
}
