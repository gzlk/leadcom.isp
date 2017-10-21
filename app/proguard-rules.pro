# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\android-sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 网易云
-dontwarn com.netease.**
-dontwarn io.netty.**
-keep class com.netease.** {*;}
# 如果 netty 使用的官方版本，它中间用到了反射，因此需要 keep。如果使用的是我们提供的版本，则不需要 keep
-keep class io.netty.** {*;}

# 如果你使用全文检索插件，需要加入
-dontwarn org.apache.lucene.**
-keep class org.apache.lucene.** {*;}

# 小米推
-dontwarn com.xiaomi.push.**
-keep class com.xiaomi.** {*;}

# 图库
-dontwarn com.yanzhenjie.album.**
-keep class com.yanzhenjie.album.**{*;}
# 图片剪切类
-dontwarn com.yanzhenjie.curban.**
-keep class com.yanzhenjie.curban.**{*;}
-dontwarn com.yanzhenjie.loading.**
-keep class com.yanzhenjie.loading.**{*;}

# 高德地图
#3D 地图 V5.0.0之前：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
-keep   class com.amap.api.trace.**{*;}

#3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#搜索
-keep   class com.amap.api.services.**{*;}

#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

#微博
-keep class com.sina.weibo.sdk.** { *; }

#微信
-keep class com.tencent.mm.opensdk.** { *;}
-keep class com.tencent.wxop.** { *;}
-keep class com.tencent.mm.sdk.** { *;}