import Flutter
import UIKit

@available(iOS 9.0, *)
public class SwiftActivityRecognitionPlugin: NSObject, FlutterPlugin {
    internal let registrar: FlutterPluginRegistrar
    private let activityClient = ActivityClient()
    private let activityChannel: ActivityChannel
    
    init(registrar: FlutterPluginRegistrar) {
        self.registrar = registrar
        self.activityChannel = ActivityChannel(activityClient: activityClient)
        super.init()
        
        //registrar.addApplicationDelegate(self)
        activityChannel.register(on: self)
    }
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        _ = SwiftActivityRecognitionPlugin(registrar: registrar)
    }
    
    // UIApplicationDelegate
    /*
    public func applicationDidBecomeActive(_ application: UIApplication) {
        activityClient.resume()
    }
    
    public func applicationWillResignActive(_ application: UIApplication) {
        activityClient.pause()
    }*/
}
