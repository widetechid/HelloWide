# WebRTC
-keep class org.webrtc.** { *; }

# HelloWide & Jitsi SDK
-keep class com.wide.mobile.hellowide.** { *; }
-keep class org.jitsi.meet.sdk.** { *; }

# Rule to avoid build errors related to SVGs.
-keep public class com.horcrux.svg.** { *; }
