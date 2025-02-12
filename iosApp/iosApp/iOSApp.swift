import SwiftUI

@main
struct iOSApp: App {

init(){
		KoinKt.startKoin()
		NapierProxyKt.debugBuild()
}
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}