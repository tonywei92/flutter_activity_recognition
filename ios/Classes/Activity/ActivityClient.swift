//
//  ActivityClient.swift
//  activity_recognition
//
//  Created by RESI Relate People on 02.08.18.
//

import Foundation
import CoreMotion

class ActivityClient {
    
    private let activityManager = CMMotionActivityManager()
    private var activityUpdatesCallback: ActivityUpdatesCallback? = nil
    
    private var isPaused = true
    
    public func resume() {
        print("has access: \(CMMotionActivityManager.isActivityAvailable())")
        
        guard isPaused else {
            return
        }
        
        isPaused = false
        activityManager.startActivityUpdates(to: OperationQueue.init()) { (activity) in
            if (activity != nil) {
                self.activityUpdatesCallback?(Result<Activity>.success(with: Activity(from:activity!)))
            }
        }
    }
    
    public func pause() {
        guard !isPaused else {
            return
        }
        
        isPaused = true
        activityManager.stopActivityUpdates()
    }
    
    
    public func registerActivityUpdates(callback: @escaping ActivityUpdatesCallback) {
        //precondition(locationUpdatesCallback == nil, "trying to register a 2nd location updates callback")
        activityUpdatesCallback = callback
    }
    
    public func deregisterActivityUpdatesCallback() {
        //precondition(locationUpdatesCallback != nil, "trying to deregister a non-existent location updates callback")
        activityUpdatesCallback = nil
    }
    
    typealias ActivityUpdatesCallback = (Result<Activity>) -> Void
}
