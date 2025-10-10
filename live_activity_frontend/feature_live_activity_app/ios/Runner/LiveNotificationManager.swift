//
//  LiveNotificationManager.swift
//  Runner
//
//  Created to resolve 'Cannot find LiveNotificationManager in scope' error.
//

import Foundation
import UIKit

class LiveNotificationManager {
    static let shared = LiveNotificationManager()
    private init() {}

    func configure() {
        // Stub: Configure notification settings
    }

    func requestAuthorizationIfNeeded(completion: @escaping (Bool) -> Void) {
        // Stub: Request notification authorization
        completion(true)
    }
    
    func showNotification(progress: Int, minutesToDelivery: Int) {
        // Stub: Show notification
    }
    
    func updateNotification(progress: Int, minutesToDelivery: Int) {
        // Stub: Update notification
    }
    
    func finishDeliveryNotification() {
        // Stub: Finish delivery notification
    }
    
    func endNotification() {
        // Stub: End notification
    }
}
