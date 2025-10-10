import SwiftUI
import shared

//iOS App用 Xcode编译，这里记录每次可编日期2024/6/5
//这里是注解出iOS的入口页面 main main main
//界面都封装在contentView ,它里又封装controller,它里又 封装App() 这是具体的控件ui
@main
struct iOSApp: App {

init(){
		KoinKt.doInitKoin()
//		NapierProxyKt.debugBuild()
//        KoinKt.startUp()
}
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
