# iOS Live Activity Implementation

This directory contains the iOS-specific implementation of Live Activities for the Flutter Live Activity Demo app. Live Activities provide real-time updates on the lock screen and in the Dynamic Island on supported devices.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [File Structure](#file-structure)
- [Key Components](#key-components)
- [Configuration](#configuration)
- [Implementation Details](#implementation-details)
- [Dynamic Island Integration](#dynamic-island-integration)
- [Requirements](#requirements)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

The iOS Live Activity implementation provides:
- **Real-time delivery tracking** with progress updates
- **Dynamic Island integration** for iPhone 14 Pro and newer
- **Lock screen widgets** for all supported devices
- **Seamless Flutter integration** via method channels
- **Rich visual experience** with animated progress indicators

## 🏗️ Architecture

```
├── Runner/                          # Main iOS app target
│   ├── AppDelegate.swift            # Flutter app delegate with Live Activity integration
│   ├── Info.plist                   # App configuration with Live Activity permissions
│   └── Assets.xcassets/             # App icons and assets
├── DeliveryActivityE/               # Widget Extension target for Live Activities
│   ├── DeliveryActivityELiveActivity.swift  # Main Live Activity implementation
│   ├── DeliveryActivityE.swift      # Additional widget implementations
│   ├── DeliveryActivityEBundle.swift        # Widget bundle configuration
│   ├── Info.plist                   # Extension configuration
│   └── Assets.xcassets/             # Shared assets for widgets
└── Runner.xcodeproj/                # Xcode project configuration
```

## 📁 File Structure

### Core Files

| File | Purpose | Key Features |
|------|---------|--------------|
| `AppDelegate.swift` | Flutter-iOS bridge | Method channel handling, Live Activity lifecycle |
| `DeliveryActivityELiveActivity.swift` | Live Activity UI | SwiftUI views, Dynamic Island layouts |
| `DeliveryActivityE.swift` | Additional widgets | Promotional widgets, timeline providers |
| `DeliveryActivityEBundle.swift` | Widget bundle | Widget registration and configuration |

### Configuration Files

| File | Purpose | Key Settings |
|------|---------|--------------|
| `Runner/Info.plist` | App permissions | `NSSupportsLiveActivities`, background modes |
| `DeliveryActivityE/Info.plist` | Extension config | Widget extension point identifier |

## 🔧 Key Components

### 1. AppDelegate.swift

The main Flutter app delegate that bridges Flutter and iOS Live Activities.

**Key Features:**
- **Method Channel Integration**: Handles `startNotifications`, `updateNotifications`, and `endNotifications`
- **Runtime Availability Checks**: Ensures iOS 16.1+ compatibility
- **Activity Lifecycle Management**: Creates, updates, and ends Live Activities
- **Error Handling**: Graceful fallbacks for unsupported devices

**Method Channel Interface:**
```swift
// Channel name for Flutter communication
let channel = FlutterMethodChannel(name: "live_activity_channel_name", binaryMessenger: controller.binaryMessenger)

// Supported methods:
- startNotifications     // Creates new Live Activity
- updateNotifications    // Updates existing Live Activity
- finishDeliveryNotification // Marks delivery as complete
- endNotifications       // Ends Live Activity
```

**Data Structure:**
```swift
// Expected parameters from Flutter
args: [String: Any] = [
    "progress": Int,           // 0-100 progress value
    "minutesToDelivery": Int   // Remaining delivery time
]
```

### 2. DeliveryActivityELiveActivity.swift

The SwiftUI-based Live Activity implementation with Dynamic Island support.

**Key Features:**
- **Reusable UI Components**: `DeliveryProgressCard` for consistent styling
- **Dynamic Island Integration**: Expanded, compact, and minimal presentations
- **Animated Progress**: Smooth transitions with moving car indicator
- **Responsive Design**: Adapts to lock screen and Dynamic Island contexts

**UI Components:**

#### DeliveryProgressCard
A reusable SwiftUI view that displays delivery progress with:
- **Header Section**: Delivery status with icons
- **Progress Bar**: Animated progress with moving car indicator
- **Footer**: Progress percentage and time remaining
- **Adaptive Styling**: Different layouts for lock screen vs. Dynamic Island

#### Activity Attributes
```swift
struct DeliveryLiveActivityEAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var progress: Int        // Progress percentage (0-100)
        var minutesToDelivery: Int // Time remaining in minutes
    }
}
```

### 3. Dynamic Island Integration

The Live Activity provides three Dynamic Island presentations:

#### Expanded View
- **Leading Region**: Car icon with rounded corners
- **Center Region**: Full delivery card with progress and details
- **Trailing Region**: Progress percentage and completion status
- **Bottom Region**: Empty (reserved for future use)

#### Compact View
- **Leading**: Delivery bag icon in cyan
- **Trailing**: Time remaining with color coding (red when <90% complete)

#### Minimal View
- **Single Element**: Time remaining with status-based coloring

## ⚙️ Configuration

### Required Info.plist Entries

#### Runner/Info.plist
```xml
<!-- Enable Live Activities support -->
<key>NSSupportsLiveActivities</key>
<true/>

<!-- Allow frequent updates -->
<key>NSSupportsLiveActivitiesFrequentUpdates</key>
<true/>

<!-- Background processing capabilities -->
<key>UIBackgroundModes</key>
<array>
    <string>remote-notification</string>
    <string>fetch</string>
    <string>processing</string>
</array>
```

#### DeliveryActivityE/Info.plist
```xml
<!-- Widget extension configuration -->
<key>NSExtension</key>
<dict>
    <key>NSExtensionPointIdentifier</key>
    <string>com.apple.widgetkit-extension</string>
</dict>
```

### Xcode Project Configuration

The project includes:
- **Widget Extension Target**: `DeliveryActivityEExtension.appex`
- **Shared Assets**: Images and colors accessible to both app and widget
- **Proper Entitlements**: Live Activity and background processing capabilities

## 🎨 Assets

### Shared Images
Located in `DeliveryActivityE/Assets.xcassets/`:

| Asset | Usage | Description |
|-------|-------|-------------|
| `moving_car` | Progress indicator | Animated car icon on progress bar |
| `rider_logo` | Delivery badge | Company/rider identification |
| `delivery_arrive` | Completion state | Success icon for completed deliveries |

### Color Assets
- `AccentColor`: App-wide accent color
- `WidgetBackground`: Background color for widget content

## 💻 Implementation Details

### Live Activity Lifecycle

1. **Creation** (`startLiveActivity`):
   - Checks `ActivityAuthorizationInfo().areActivitiesEnabled`
   - Creates `Activity<DeliveryLiveActivityEAttributes>` with initial state
   - Handles creation errors gracefully

2. **Updates** (`updateLiveActivity`):
   - Uses `await activity?.update(using: updatedContentState)`
   - Preserves existing activity reference
   - Updates both progress and time remaining

3. **Termination** (`endLiveActivity`):
   - Sets final state (progress: 100, minutesToDelivery: 0)
   - Uses `dismissalPolicy: .immediate` for instant removal
   - Cleans up activity reference

### SwiftUI Best Practices

- **Modifier Ordering**: Ensures `resizable()` comes before `renderingMode()`
- **Animation Integration**: Smooth transitions for progress updates
- **Accessibility**: Proper contrast and readable text sizes
- **Performance**: Efficient view updates and minimal redraws

### Error Handling

- **Device Compatibility**: Runtime checks for iOS 16.1+ availability
- **Permission Handling**: Graceful fallbacks when Live Activities disabled
- **Network Resilience**: Local state management with periodic updates

## 📱 Dynamic Island Integration

### Design Philosophy
- **Glanceable Information**: Essential details at a glance
- **Progressive Disclosure**: More detail in expanded state
- **Visual Hierarchy**: Clear information prioritization
- **Brand Consistency**: Maintains app identity in system UI

### Layout Strategy

#### Compact Layout
```
[🛍️] ────────── [8m]
 Icon              Time
```

#### Expanded Layout
```
[🚗]  ┌─ Delivery Status ─┐  [75%]
      │ Progress Bar      │  [Complete]
      └─ Time Remaining ──┘
```

### Color Coding
- **Blue**: Normal delivery progress
- **Red**: Urgent (progress >90%)
- **Cyan**: Delivery bag icon
- **White**: Text and icons on dark backgrounds

## 📋 Requirements

### System Requirements
- **iOS Version**: 16.1 or later (for Live Activities)
- **Device Support**: iPhone with iOS 16.1+
- **Dynamic Island**: iPhone 14 Pro/Pro Max or later
- **Permissions**: Live Activities must be enabled in Settings

### Development Requirements
- **Xcode**: 14.0 or later
- **Swift**: 5.7 or later
- **Target**: iOS 16.1+ deployment target
- **Frameworks**: ActivityKit, WidgetKit, SwiftUI

## 🚀 Usage

### From Flutter

```dart
// Start Live Activity
await activityService.startNotifications(
  data: LiveNotificationModel(
    progress: 0,
    minutesToDelivery: 10,
  ),
);

// Update progress
await activityService.updateNotifications(
  data: LiveNotificationModel(
    progress: 75,
    minutesToDelivery: 3,
  ),
);

// End activity
await activityService.endNotifications();
```

### Testing in Simulator

1. **Enable Live Activities**: Settings > Face ID & Passcode > Live Activities
2. **Test Lock Screen**: Command+L to lock simulator
3. **Test Dynamic Island**: Use iPhone 14 Pro simulator or later
4. **Debug Output**: Monitor Xcode console for Live Activity logs

## 🔧 Troubleshooting

### Common Issues

#### Live Activity Not Appearing
```
Possible Causes:
- Live Activities disabled in device settings
- iOS version below 16.1
- Invalid activity attributes or content state
- Missing NSSupportsLiveActivities in Info.plist

Solutions:
- Check Settings > Face ID & Passcode > Live Activities
- Verify deployment target is iOS 16.1+
- Validate data passed from Flutter
- Ensure proper Info.plist configuration
```

#### Dynamic Island Not Showing
```
Possible Causes:
- Device doesn't support Dynamic Island (pre-iPhone 14 Pro)
- Running on simulator without Dynamic Island support
- DynamicIsland view configuration issues

Solutions:
- Test on iPhone 14 Pro+ device or simulator
- Verify DynamicIsland view implementation
- Check for SwiftUI compilation errors
```

#### Flutter Channel Errors
```
Possible Causes:
- Method channel name mismatch
- Invalid argument types from Flutter
- iOS method handler not properly registered

Solutions:
- Verify channel name matches Flutter implementation
- Ensure argument types match expected Swift types
- Check AppDelegate method handler registration
```

### Debugging Commands

```bash
# View Live Activity logs
xcrun simctl spawn booted log stream --predicate 'subsystem contains "ActivityKit"'

# Check widget bundle registration
xcrun simctl spawn booted log stream --predicate 'subsystem contains "WidgetKit"'

# Monitor Flutter channel communication
flutter logs --verbose
```

### Performance Optimization

1. **Minimize Updates**: Batch frequent changes to reduce system overhead
2. **Efficient Assets**: Use vector images and optimized assets
3. **Memory Management**: Proper cleanup of activity references
4. **Animation Performance**: Use built-in SwiftUI animations for smooth transitions

## 📚 Additional Resources

- [Apple ActivityKit Documentation](https://developer.apple.com/documentation/activitykit)
- [Live Activities Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/live-activities)
- [WidgetKit Framework](https://developer.apple.com/documentation/widgetkit)
- [SwiftUI Animation Guide](https://developer.apple.com/documentation/swiftui/animation)

---

**Note**: This implementation provides a complete Live Activity solution with Dynamic Island integration. The code is designed to be maintainable, performant, and follows Apple's design guidelines for Live Activities.