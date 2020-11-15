#import "FlutterPdfBoxPlugin.h"
#if __has_include(<flutter_pdf_box/flutter_pdf_box-Swift.h>)
#import <flutter_pdf_box/flutter_pdf_box-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_pdf_box-Swift.h"
#endif

@implementation FlutterPdfBoxPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterPdfBoxPlugin registerWithRegistrar:registrar];
}
@end
