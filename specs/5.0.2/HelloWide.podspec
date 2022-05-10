Pod::Spec.new do |s|
  s.name         = "HelloWide"
  s.version      = "5.0.2"
  s.summary      = "HelloWide for iOS Testing Purpose"
  s.description  = <<-DESC
  HelloWide is one of products from Wide Technologies Indonesia.
                   DESC
  s.homepage     = "https://github.com/widetechid/HelloWide"
  s.license = { :type => "MIT", :text => "MIT License" }
  s.author       = { "WideTechId" => "github.mobile@primecash.co.id" }
  s.platform     = :ios, "12.0"
  s.ios.deployment_target = "12.0"
  s.source = { :http => 'https://maven.primecash.co.id/repository/maven-releases/id/co/widetechnologies/component/mobile/hellowide-ios/5.0.2/hellowide-ios-5.0.2.zip' }
  s.vendored_frameworks = 'HelloWideSDK.xcframework', 'WebRTC.xcframework'
  s.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
  s.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
end
