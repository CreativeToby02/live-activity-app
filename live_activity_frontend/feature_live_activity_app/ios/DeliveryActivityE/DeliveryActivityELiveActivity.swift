//
//  DeliveryActivityELiveActivity.swift
//  DeliveryActivityE
//
//  Created by Kudus Rufai on 27/10/2025.
//

import ActivityKit
import WidgetKit
import SwiftUI

// MARK: - Reusable Delivery Progress Card View
@available(iOS 16.1, *)
struct DeliveryProgressCard: View {
    let progress: Int // Now using Int (0-100)
    let minutesToDelivery: Int
    let badgeName: String
    let carName: String

    init(progress: Int, minutesToDelivery: Int, badgeName: String = "moving_car", carName: String = "moving_car") {
        self.progress = max(0, min(100, progress)) // Clamp to 0-100 range
        self.minutesToDelivery = minutesToDelivery
        self.badgeName = badgeName
        self.carName = carName
    }

    var normalizedProgress: Double { Double(progress) / 100.0 } // Convert to 0.0-1.0 for calculations
    var arrived: Bool { progress >= 100 || minutesToDelivery <= 0 }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Header
            HStack(alignment: .center, spacing: 12) {
            
                VStack(alignment: .leading, spacing: 2) {
                    Text(arrived ? "Delivery Arrived! 🎉" : "Delivering in")
                        .font(.headline)
                        .foregroundStyle(.primary)
                    Text(arrived ? "Enjoy your delivery :)" : "Your delivery is on its way")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                Spacer()
            }

            // Progress with moving car
            GeometryReader { geo in
                let width = geo.size.width
                let barHeight: CGFloat = 8
                let carWidth: CGFloat = 22
                let carX = normalizedProgress * max(0, width - carWidth)

                ZStack(alignment: .leading) {
                    // Track
                    Capsule()
                        .fill(Color.gray.opacity(0.25))
                        .frame(height: barHeight)
                    // Fill
                    Capsule()
                        .fill(LinearGradient(gradient: Gradient(colors: [Color.gray.opacity(0.7), Color.gray.opacity(0.55)]), startPoint: .leading, endPoint: .trailing))
                        .frame(width: max(normalizedProgress * width, 6), height: barHeight)
                        .animation(.easeInOut(duration: 0.4), value: normalizedProgress)
                    // Car on top of fill
                    Image(carName)
                        .renderingMode(.template)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: carWidth, height: carWidth)
                        .foregroundColor(.white)
                        .offset(x: carX)
                        .animation(.easeInOut(duration: 0.4), value: normalizedProgress)
                        .shadow(color: Color.black.opacity(0.15), radius: 2, y: 1)
                }
            }
            .frame(height: 22)

            // Footer
            HStack(alignment: .center) {
                Text("\(progress)%")
                    .font(.footnote)
                    .foregroundColor(.white)
                Spacer()
                HStack(spacing: 6) {
                    Image(systemName: "clock")
                        .font(.footnote)
                        .foregroundColor(.white)
                    let mins = max(1, minutesToDelivery)
                    Text(mins == 1 ? "1 minute" : "\(mins) minutes")
                        .font(.footnote)
                        .foregroundColor(.white)
                }
            }
        }
        .padding(12)
        .background(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .fill(Color.black)
                .shadow(color: Color.black.opacity(0.06), radius: 6, y: 3)
        )
    }
}

// MARK: - Activity Attributes
@available(iOS 16.1, *)
struct DeliveryLiveActivityEAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var progress: Int // Changed from Double to Int
        var minutesToDelivery: Int
    }
}

@available(iOS 16.1, *)
// MARK: - Live Activity Widget
struct DeliveryLiveActivityELiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: DeliveryLiveActivityEAttributes.self) { context in
            // Use reusable DeliveryProgressCard for lock screen / banner UI
            DeliveryProgressCard(progress: context.state.progress, minutesToDelivery: context.state.minutesToDelivery)
        } dynamicIsland: { context in
            DynamicIsland {
                DynamicIslandExpandedRegion(.leading) {
               
                }

                DynamicIslandExpandedRegion(.center) {
                    // Compact version for Dynamic Island - less padding and spacing
                    VStack(alignment: .leading, spacing: 4) {
                        // Header
                        HStack(alignment: .center, spacing: 8) {
                     
                            VStack(alignment: .leading, spacing: 1) {
                                let arrived = context.state.progress >= 100 || context.state.minutesToDelivery <= 0
                                Text(arrived ? "Delivery Arrived! 🎉" : "Delivering in")
                                    .font(.subheadline.weight(.medium))
                                    .foregroundStyle(.primary)
                                Text(arrived ? "Enjoy your delivery :)" : "Your delivery is on its way")
                                    .font(.caption)
                                    .foregroundStyle(.secondary)
                            }
                            Spacer()
                        }

                        // Progress with moving car
                        GeometryReader { geo in
                            let width = geo.size.width
                            let barHeight: CGFloat = 6
                            let carWidth: CGFloat = 18
                            let normalizedProgress = Double(context.state.progress) / 100.0
                            let carX = normalizedProgress * max(0, width - carWidth)

                            ZStack(alignment: .leading) {
                                // Track
                                Capsule()
                                    .fill(Color.gray.opacity(0.25))
                                    .frame(height: barHeight)
                                // Fill
                                Capsule()
                                    .fill(LinearGradient(gradient: Gradient(colors: [Color.gray.opacity(0.7), Color.gray.opacity(0.55)]), startPoint: .leading, endPoint: .trailing))
                                    .frame(width: max(normalizedProgress * width, 4), height: barHeight)
                                    .animation(.easeInOut(duration: 0.4), value: normalizedProgress)
                                // Car on top of fill
                                Image("moving_car")
                                    .renderingMode(.template)
                                    .resizable()
                                    .aspectRatio(contentMode: .fit)
                                    .frame(width: carWidth, height: carWidth)
                                    .foregroundColor(.white)
                                    .offset(x: carX)
                                    .animation(.easeInOut(duration: 0.4), value: normalizedProgress)
                                    .shadow(color: Color.black.opacity(0.15), radius: 1, y: 0.5)
                            }
                        }
                        .frame(height: 18)

                        // Footer
                        HStack(alignment: .center) {
                            Text("\(context.state.progress)%")
                                .font(.caption2)
                                .foregroundStyle(.primary)
                            Spacer()
                            HStack(spacing: 4) {
                                Image(systemName: "clock")
                                    .font(.caption2)
                                    .foregroundStyle(.secondary)
                                let mins = max(1, context.state.minutesToDelivery)
                                Text(mins == 1 ? "1 minute" : "\(mins) minutes")
                                    .font(.caption2)
                                    .foregroundStyle(.secondary)
                            }
                        }
                    }
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                }

                DynamicIslandExpandedRegion(.trailing) {
                }

                DynamicIslandExpandedRegion(.bottom) {
               
                }
            } compactLeading: {
                Image(systemName: "bag.fill")
                    .resizable()
                    .frame(width: 18, height: 18)
                    .foregroundColor(.cyan)
            } compactTrailing: {
                let mins = max(1, context.state.minutesToDelivery)
                Text("\(mins)m")
                    .font(.caption.bold())
                    .foregroundColor(context.state.progress > 90 ? Color.red : Color.blue)
                    .animation(.easeInOut, value: context.state.progress)
            } minimal: {
                let mins = max(1, context.state.minutesToDelivery)
                Text("\(mins)m")
                    .font(.caption2.bold())
                    .foregroundColor(context.state.progress > 90 ? Color.red : Color.blue)
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
        DeliveryLiveActivityEAttributes.ContentState(progress: 60, minutesToDelivery: 8) // Changed from 0.6 to 60
    }
    
    fileprivate static var almostThere: DeliveryLiveActivityEAttributes.ContentState {
        DeliveryLiveActivityEAttributes.ContentState(progress: 95, minutesToDelivery: 1) // Changed from 0.95 to 95
    }
}

@available(iOS 17.0, *)
#Preview("Notification", as: .content, using: DeliveryLiveActivityEAttributes.preview) {
    DeliveryLiveActivityELiveActivity()
} contentStates: {
    DeliveryLiveActivityEAttributes.ContentState.typical
    DeliveryLiveActivityEAttributes.ContentState.almostThere
}

