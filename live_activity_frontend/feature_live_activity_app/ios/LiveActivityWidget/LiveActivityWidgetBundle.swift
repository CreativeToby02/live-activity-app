//
//  LiveActivityWidgetBundle.swift
//  LiveActivityWidget
//
//  Created by Kudus Rufai on 09/10/2025.
//

import WidgetKit
import SwiftUI

@main
struct LiveActivityWidgetBundle: WidgetBundle {
    var body: some Widget {
        LiveActivityWidget()
        LiveActivityWidgetControl()
        LiveActivityWidgetLiveActivity()
    }
}
