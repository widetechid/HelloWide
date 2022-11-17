//
//  AppDelegate.swift
//  HelloWideExample
//
//  Created by Wijaya, Ardy on 01/09/22.
//

import UIKit
import PushKit
import UserNotifications
import HelloWide

@main
class AppDelegate: UIResponder, UIApplicationDelegate, PKPushRegistryDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        HWPermissionManager.sharedInstance()?.requestPermission(completionHandler: { response  in
            if (!response.isEmpty) {
                if (response["microphoneStatus"] as? Int == 1 && response["cameraStatus"] as? Int == 1) {
                    self.window?.showToast(message: "Permission Granted")
                }else {
                    self.window?.showToast(message: "Permission Denied")
                }
            }else {
                self.window?.showToast(message: "Permission Denied")
            }
            self.registerForPushNotification()
            self.registerForVoIPNotification()
        })
        
        HWCallCompact.sharedInstance().notifyAppOnBackground()
        
        return true
    }
    
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        return UIInterfaceOrientationMask.portrait
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        HWCallCompact.sharedInstance().notifyAppOnForeground()
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        HWCallCompact.sharedInstance().notifyAppOnBackground()
    }
    
    func registerForVoIPNotification() {
        let mainQueue = DispatchQueue.main
        let voipRegistry: PKPushRegistry = PKPushRegistry(queue: mainQueue)
        voipRegistry.delegate = self
        voipRegistry.desiredPushTypes = [PKPushType.voIP]
    }
    
    func registerForPushNotification() {
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { granted, error in
            if (error != nil) {
                return
            }else {
                if (granted) {
                    UNUserNotificationCenter.current().delegate = self
                    UNUserNotificationCenter.current().getNotificationSettings { settings in
                        UNUserNotificationCenter.current().delegate = self
                        if (settings.authorizationStatus == .authorized) {
                            DispatchQueue.main.async {
                                UIApplication.shared.registerForRemoteNotifications()
                            }
                        }
                    }
                }
            }
        }
    }
    
    //MARK: - PKPushRegistryDelegate

    func pushRegistry(_ registry: PKPushRegistry, didUpdate credentials: PKPushCredentials, for type: PKPushType) {
        HWCallCompact.sharedInstance().credentialsUpdated(credentials)
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "receivePushKitToken"), object: nil, userInfo: nil)
    }
        
    func pushRegistry(_ registry: PKPushRegistry, didInvalidatePushTokenFor type: PKPushType) {
        HWCallCompact.sharedInstance().credentialsInvalidated()
    }
    
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        HWCallCompact.sharedInstance().incomingPushReceived(payload, withCompletionHandler: completion)
    }
    
    // MARK:- UNUserNotificationCenterDelegate
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([[.alert, .sound]])
    }

    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        completionHandler()
    }
}

