//
//  DarwinNotification.swift
//  BroadcastExtension
//
//  Created by Wide Technologies Indonesia, PT on 08/09/22.
//  Copyright © 2022 Wide Technologies Indonesia, PT. All rights reserved.
//

import Foundation

enum DarwinNotification: String {
    case broadcastStarted = "iOS_BroadcastStarted"
    case broadcastStopped = "iOS_BroadcastStopped"
}

class DarwinNotificationCenter {
    
    static var shared = DarwinNotificationCenter()
    
    private var notificationCenter: CFNotificationCenter
    
    init() {
        notificationCenter = CFNotificationCenterGetDarwinNotifyCenter()
    }
    
    func postNotification(_ name: DarwinNotification) {
        CFNotificationCenterPostNotification(notificationCenter, CFNotificationName(rawValue: name.rawValue as CFString), nil, nil, true)
    }
}
