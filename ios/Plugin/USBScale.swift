import Foundation

@objc public class USBScale: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
