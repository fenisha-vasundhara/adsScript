# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化  不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解
-keepattributes *Annotation*

-keep class com.onesignal.** { *; }
-keep interface com.onesignal.** { *; }
-dontwarn com.onesignal.**

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment

#忽略警告
#-ignorewarning
#-ignorewarnings

##记录生成的日志数据,gradle build时在本项目根目录输出##
#apk 包内所有 class 的内部结构


-dump proguard/class_files.txt
#未混淆的类和成员
-printseeds proguard/seeds.txt
#列出从 apk 中删除的代码
-printusage proguard/unused.txt
#混淆前后的映射
-printmapping proguard/mapping.txt
########记录生成的日志数据，gradle build时 在本项目根目录输出-end######

#如果引用了v4或者v7包
-dontwarn android.support.**

####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

# Keep KRDB Realm schema models and generated accessors stable for reflection/native interop.
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.** { *; }
-keep class io.github.xilinjia.krdb.** { *; }
-dontwarn io.github.xilinjia.krdb.**

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#避免混淆泛型 如果混淆报错建议关掉
#-keepattributes Signature

#移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用，另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
#使用这个配置时，一定要注意 -dontoptimize，配置。
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}

#############################################################################################
########################                 以上通用           ##################################
#############################################################################################

#######################     常用第三方模块的混淆选项         ###################################

#1 achartengine
-dontwarn org.achartengine.**
-keep class org.achartengine.** { *;}


#2 高德地图
-dontwarn com.amap.api.**
#3D 地图
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
-keep   class com.amap.api.trace.**{*;}
# 定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
# 搜索
-keep   class com.amap.api.services.**{*;}
#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
# 导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

#3 butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


#4 smack
-dontwarn org.jivesoftware.**
-keep class org.jivesoftware.smack.** { *;}
-keep class org.jivesoftware.smackx.** { *;}


#5 EventBus3.0
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

#6 GreenDao
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**

#7FlycoTabLayout 可以混淆


#8 mqtt 可以混淆

#9 mqtt service 可以混淆

#10 gson
#如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
# 如果使用了Gson之类的工具要使被它解析的JavaBean类即实体类不被混淆。
-keep class com.app.sample.chatting.api.json.** { *; }
-keep class com.app.sample.chatting.bean.** { *; }

#11 MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

#12 logger可以混淆


#13 picasso
-keepattributes SourceFile,LineNumberTable
-keep class com.parse.*{ *; }
-dontwarn com.parse.**
-dontwarn com.squareup.picasso.**
-keepclasseswithmembernames class * {    native <methods>;}

#14 okhttp3
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;}

-keep class org.apache.http.** { *;}
-keep class com.squareup.picasso.OkHttpDownloader
-dontwarn org.apache.http.**
-dontwarn com.squareup.picasso.OkHttpDownloader

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**

#15 retrofit2
#-dontwarn retrofit.**
#-keep class retrofit.** { *; }
#-keepattributes Signature
#-keepattributes Exceptions

#16 zxing
-dontwarn com.google.zxing.**
-keep  class com.google.zxing.**{*;}

#Rxjava Rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

## Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#-ignorewarnings
#-keepattributes Annotation
#-keepattributes Exceptions
#-keepattributes InnerClasses
#-keepattributes Signature
#-keepattributes SourceFile,LineNumberTable
#-keep class com.huawei.hianalytics.*{}
#-keep class com.huawei.updatesdk.*{}
#-keep class com.huawei.hms.*{}


#-ignorewarnings
#-keepattributes Annotation
#-keepattributes Exceptions
#-keepattributes InnerClasses
#-keepattributes Signature
#-keepattributes SourceFile,LineNumberTable
#-keep class com.huawei.hianalytics.*{}
#-keep class com.huawei.updatesdk.*{}
#-keep class com.huawei.hms.*{}

##

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.


# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
-keepattributes Signature

# This is also needed for R8 in compat mode since multiple
# optimizations will remove the generic signature such as class
# merging and argument removal. See:
# https://r8.googlesource.com/r8/+/refs/heads/main/compatibility-faq.md#troubleshooting-gson-gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations

-keep,allowoptimization class com.google.android.libraries.maps.** { *; }

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

# Google Play services Maps
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

# Google Play services Location
-keep class com.google.android.gms.location.** { *; }
-keep interface com.google.android.gms.location.** { *; }

# Google Places
-keep class com.google.android.libraries.places.** { *; }

-keep class com.demo.adsmanage.subeventcheck.** { *; }


-keep interface com.google.android.libraries.places.** { *; }

# Google Maps Services
-keep class com.google.maps.** { *; }
-keep interface com.google.maps.** { *; }

-keep class com.google.android.gms.ads.** { *; }
-dontwarn okio.**
-keep class com.messenger.phone.number.text.sms.service.apps.MyWidget.** { *; }

# ── Realm (krdb / io.github.xilinjia.krdb) ──────────────────────────────────
# RealmObject subclasses are accessed reflectively by the Realm schema engine.
-keep class io.realm.kotlin.** { *; }
-keep class io.github.xilinjia.krdb.** { *; }
-dontwarn io.realm.kotlin.**
-dontwarn io.github.xilinjia.krdb.**

# Keep all RealmObject models in the realmplan package.
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.ConversationRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.ConversationBinRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.MessageAttachmentRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.AttachmentRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.CategoryRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.CategoryNumberRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.ContactRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.RecentSearchRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.ReminderRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.StarNumberRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.FolderRealm { *; }
-keep class com.messenger.phone.number.text.sms.service.apps.realmplan.PhotoRealm { *; }

# Preserve field names so Realm schema lookups survive obfuscation.
-keepclassmembers class com.messenger.phone.number.text.sms.service.apps.realmplan.** {
    <fields>;
    <init>();
}
