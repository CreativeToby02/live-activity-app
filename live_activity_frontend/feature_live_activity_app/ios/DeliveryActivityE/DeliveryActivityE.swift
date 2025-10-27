//
//  DeliveryActivityE.swift
//  DeliveryActivityE
//
//  Created by Kudus Rufai on 27/10/2025.
//

import WidgetKit
import SwiftUI
import ActivityKit

struct Provider: TimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(date: Date(), emoji: "😀")
    }

    func getSnapshot(in context: Context, completion: @escaping (SimpleEntry) -> ()) {
        let entry = SimpleEntry(date: Date(), emoji: "😀")
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        var entries: [SimpleEntry] = []

        // Generate a timeline consisting of five entries an hour apart, starting from the current date.
        let currentDate = Date()
        let emojis = ["🚗", "🛋️", "🫶", "😀"]
        for hourOffset in 0 ..< 5 {
            let entryDate = Calendar.current.date(byAdding: .hour, value: hourOffset, to: currentDate)!
            let emoji = emojis[hourOffset % emojis.count]
            let entry = SimpleEntry(date: entryDate, emoji: emoji)
            entries.append(entry)
        }

        let timeline = Timeline(entries: entries, policy: .atEnd)
        completion(timeline)
    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let emoji: String
}

// MARK: - Start Ride Promo Widget View
struct StartRidePromoWidgetView: View {
    var body: some View {
        ZStack {
            LinearGradient(gradient: Gradient(colors: [Color.cyan, Color.blue]), startPoint: .topLeading, endPoint: .bottomTrailing)
                .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
                .shadow(color: .blue.opacity(0.12), radius: 8, y: 4)
            VStack(spacing: 12) {
                Image(systemName: "car.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 36, height: 28)
                    .foregroundColor(.white)
                    .shadow(radius: 2)
                Text("Start a Ride")
                    .font(.title2.bold())
                    .foregroundColor(.white)
                Text("Get to your destination fast & safe.")
                    .font(.subheadline)
                    .foregroundColor(.white.opacity(0.75))
            }
            .padding()
        }
        .padding([.horizontal, .bottom])
    }
}

// MARK: - Comfort Promo Widget View
struct ComfortPromoWidgetView: View {
    var body: some View {
        ZStack {
            LinearGradient(gradient: Gradient(colors: [Color.purple.opacity(0.8), Color.pink.opacity(0.8)]), startPoint: .topLeading, endPoint: .bottomTrailing)
                .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
                .shadow(color: Color.pink.opacity(0.15), radius: 10, y: 5)
            VStack(spacing: 12) {
                Image(systemName: "bed.double.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 36, height: 28)
                    .foregroundColor(.white)
                    .shadow(radius: 2)
                Text("Book a ride and enjoy comfort")
                    .font(.title3.bold())
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
                Text("Relax while we get you there.")
                    .font(.subheadline)
                    .foregroundColor(.white.opacity(0.75))
            }
            .padding()
        }
        .padding([.horizontal, .bottom])
    }
}

// MARK: - Refer Friend Promo Widget View
struct ReferFriendPromoWidgetView: View {
    var body: some View {
        ZStack {
            LinearGradient(gradient: Gradient(colors: [Color.orange, Color.red]), startPoint: .topLeading, endPoint: .bottomTrailing)
                .clipShape(RoundedRectangle(cornerRadius: 22, style: .continuous))
                .shadow(color: Color.red.opacity(0.2), radius: 10, y: 5)
            VStack(spacing: 12) {
                Image(systemName: "heart.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 36, height: 32)
                    .foregroundColor(.white)
                    .shadow(radius: 2)
                Text("Refer a friend & earn rewards!")
                    .font(.title3.bold())
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
                Text("Share the ride, share the love.")
                    .font(.subheadline)
                    .foregroundColor(.white.opacity(0.75))
            }
            .padding()
        }
        .padding([.horizontal, .bottom])
    }
}

// MARK: - Main Widget switching by timeline entry
struct DeliveryLiveActivityE: Widget {
    let kind: String = "DeliveryLiveActivityE"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            switch entry.emoji {
            case "🚗":
                StartRidePromoWidgetView()
            case "🛋️":
                ComfortPromoWidgetView()
            case "🫶":
                ReferFriendPromoWidgetView()
            default:
                StartRidePromoWidgetView()
            }
        }
        .configurationDisplayName("Delivery & Ride Widgets")
        .description("Track orders, book a ride, and more.")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

// MARK: - Separate Promo Widgets
struct StartRidePromoWidget: Widget {
    let kind: String = "StartRidePromoWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { _ in
            StartRidePromoWidgetView()
        }
        .configurationDisplayName("Start a Ride Promo")
        .description("Get to your destination fast & safe.")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

struct ComfortPromoWidget: Widget {
    let kind: String = "ComfortPromoWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { _ in
            ComfortPromoWidgetView()
        }
        .configurationDisplayName("Comfort Promo")
        .description("Relax while we get you there.")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

struct ReferFriendPromoWidget: Widget {
    let kind: String = "ReferFriendPromoWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { _ in
            ReferFriendPromoWidgetView()
        }
        .configurationDisplayName("Refer a Friend Promo")
        .description("Refer to earn")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}
