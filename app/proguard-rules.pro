# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/yanghonghe/Downloads/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class net.oschina.app.improve.bean.** { *; }

##---------------End: proguard configuration for Gson  ----------

-keep class net.oschina.app.** { *; }


-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-dontwarn com.thoughtworks.xstream.**
-keep class com.thoughtworks.xstream.** { *; }

-dontwarn com.makeramen.roundedimageview.**
-keep class com.makeramen.roundedimageview.RoundedTransformationBuilder

-dontwarn com.tencent.weibo.sdk.android.**
-keep class com.tencent.weibo.sdk.android.** { *; }

-dontwarn com.squareup.leakcanary.DisplayLeakService
-keep class com.squareup.leakcanary.DisplayLeakService

-dontwarn com.umeng.socialize.sso.**
-keep class com.umeng.socialize.sso.** { *; }




#-libraryjars libs/library-2.4.0.jar
#-libraryjars libs/locSDK_3.1.jar
#-libraryjars libs/pinyin4j-2.5.0.jar
#-libraryjars libs/xstream-1.4.7.jar