# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#VPN
-keep class com.anchorfree.sdk.SessionConfig { *; }
    -keep class com.anchorfree.sdk.fireshield.** { *; }
    -keep class com.anchorfree.sdk.dns.** { *; }
    -keep class com.anchorfree.sdk.HydraSDKConfig { *; }
    -keep class com.anchorfree.partner.api.ClientInfo { *; }
    -keep class com.anchorfree.sdk.NotificationConfig { *; }
    -keep class com.anchorfree.sdk.NotificationConfig$Builder { *; }
    -keep class com.anchorfree.sdk.NotificationConfig$StateNotification { *; }
    -keepclassmembers public class com.anchorfree.ucr.transport.DefaultTrackerTransport {
       public <init>(...);
     }
     -keepclassmembers class com.anchorfree.ucr.SharedPrefsStorageProvider{
        public <init>(...);
     }
     -keepclassmembers class com.anchorfree.sdk.InternalReporting$InternalTrackingTransport{
     public <init>(...);
     }
     -keep class com.anchorfree.sdk.exceptions.* {
        *;
     }


       -keepclassmembers class com.ironsource.sdk.controller.IronSourceWebView$JSInterface {
              public *;
          }
          -keepclassmembers class * implements android.os.Parcelable {
              public static final android.os.Parcelable$Creator *;
          }
          -keep public class com.google.android.gms.ads.** {
             public *;
          }
          -keep class com.ironsource.adapters.** { *;
          }
          -dontwarn com.ironsource.mediationsdk.**
          -dontwarn com.ironsource.adapters.**
          -keepattributes JavascriptInterface
          -keepclassmembers class * {
              @android.webkit.JavascriptInterface <methods>;
          }


          -dontwarn com.facebook.ads.internal.**
          -keeppackagenames com.facebook.*
          -keep public class com.facebook.ads.** {*;}
          -keep public class com.facebook.ads.**
          { public protected *; }