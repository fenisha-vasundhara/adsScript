# ANRS



## ------------ 1 -----------
main (native):tid=1 systid=3892
#00 pc 0x627140 split_config.arm64_v8a.apk + 5799936 (BuildId: df12f5a975f5f78ccc6c426863aa38ad1ee8135f)
#01 pc 0x513a4 linker64 (__dl_soinfo::call_constructors + 628) (BuildId: e2edf46e65d57754e644a5050e5be593)
#02 pc 0x3bd80 linker64 (__dl_do_dlopen + 2048) (BuildId: e2edf46e65d57754e644a5050e5be593)
#03 pc 0x370e8 linker64 (__loader_android_dlopen_ext + 80) (BuildId: e2edf46e65d57754e644a5050e5be593)
#04 pc 0x10e8 libdl.so (android_dlopen_ext + 16) (BuildId: 04c7aa817fe0002ceba64b0e20566d93)
#05 pc 0x19638 libnativeloader.so (android::NativeLoaderNamespace::Load const + 140) (BuildId: 850fadd9a4aeab796ffd82b02ecc393f)
#06 pc 0x8724 libnativeloader.so (OpenNativeLibrary + 568) (BuildId: 850fadd9a4aeab796ffd82b02ecc393f)
#07 pc 0x422274 libart.so (art::JavaVMExt::LoadNativeLibrary + 792) (BuildId: 7087b2f2160bfbf3335d54ba9779e325)
#08 pc 0x56ac libopenjdkjvm.so (JVM_NativeLoad + 368) (BuildId: f28caae1dfd291e333a7062ac2e6cd95)
#09 pc 0x2c2100 libart.so (art_quick_generic_jni_trampoline + 144) (BuildId: 7087b2f2160bfbf3335d54ba9779e325)
#10 pc 0x2118a88 memfd:jit-zygote-cache (java.lang.Runtime.loadLibrary0 + 296)
#11 pc 0x2118874 memfd:jit-zygote-cache (java.lang.Runtime.loadLibrary0 + 436)
#12 pc 0x2127ee4 memfd:jit-zygote-cache (java.lang.System.loadLibrary + 84)
at java.lang.Runtime.nativeLoad(Native method)
at java.lang.Runtime.loadLibrary0(Runtime.java:1095)
at java.lang.Runtime.loadLibrary0(Runtime.java:1019)
at java.lang.System.loadLibrary(System.java:1765)
at io.github.xilinjia.krdb.internal.AndroidUtilsKt.loadAndroidNativeLibs(AndroidUtils.kt:22)
at io.github.xilinjia.krdb.internal.RealmInitializer.create(RealmInitializer.kt:42)
at io.github.xilinjia.krdb.internal.RealmInitializer.create(RealmInitializer.kt:30)
at androidx.startup.AppInitializer.doInitialize(AppInitializer.java:180)
at androidx.startup.AppInitializer.discoverAndInitialize(AppInitializer.java:239)
at androidx.startup.AppInitializer.discoverAndInitialize(AppInitializer.java:207)
at androidx.startup.InitializationProvider.onCreate(InitializationProvider.java:49)
at android.content.ContentProvider.attachInfo(ContentProvider.java:2443)
at android.content.ContentProvider.attachInfo(ContentProvider.java:2413)
at android.app.ActivityThread.installProvider(ActivityThread.java:7702)
at android.app.ActivityThread.installContentProviders(ActivityThread.java:7214)
at android.app.ActivityThread.handleBindApplication(ActivityThread.java:6971)
at android.app.ActivityThread.access$1600(ActivityThread.java:273)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2173)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loopOnce(Looper.java:241)
at android.os.Looper.loop(Looper.java:342)
at android.app.ActivityThread.main(ActivityThread.java:8124)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:583)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1045)


## ------------- 2 ----------------
main (native):tid=1 systid=29789
#00 pc 0x114dd8 libc.so (mprotect + 8) (BuildId: 5030733eb79980f807b39cd5c2fb5748)
#01 pc 0xaa4cc libc.so (__cxa_atexit + 508) (BuildId: 5030733eb79980f807b39cd5c2fb5748)
#02 pc 0x4fe800 split_config.arm64_v8a.apk + 5799936 (BuildId: df12f5a975f5f78ccc6c426863aa38ad1ee8135f)
#03 pc 0xf0d04 linker64 (__dl_soinfo::call_constructors + 628) (BuildId: 9e7ca3e13b7d70ea11a798079205e9f4)
#04 pc 0xd93e0 linker64 (__dl_do_dlopen + 2816) (BuildId: 9e7ca3e13b7d70ea11a798079205e9f4)
#05 pc 0xd3a78 linker64 (__loader_android_dlopen_ext + 72) (BuildId: 9e7ca3e13b7d70ea11a798079205e9f4)
#06 pc 0x4110 libdl.so (android_dlopen_ext + 16) (BuildId: 73c0ccf88fb05a80b4f0cccb1e7498e9)
#07 pc 0x19638 libnativeloader.so (android::NativeLoaderNamespace::Load const + 140) (BuildId: 850fadd9a4aeab796ffd82b02ecc393f)
#08 pc 0x8724 libnativeloader.so (OpenNativeLibrary + 568) (BuildId: 850fadd9a4aeab796ffd82b02ecc393f)
#09 pc 0x422274 libart.so (art::JavaVMExt::LoadNativeLibrary + 792) (BuildId: 7087b2f2160bfbf3335d54ba9779e325)
#10 pc 0x56ac libopenjdkjvm.so (JVM_NativeLoad + 368) (BuildId: f28caae1dfd291e333a7062ac2e6cd95)
at java.lang.Runtime.nativeLoad(Native method)
at java.lang.Runtime.loadLibrary0(Runtime.java:1095)
at java.lang.Runtime.loadLibrary0(Runtime.java:1019)
at java.lang.System.loadLibrary(System.java:1765)
at io.github.xilinjia.krdb.internal.AndroidUtilsKt.loadAndroidNativeLibs(AndroidUtils.kt:22)
at io.github.xilinjia.krdb.internal.RealmInitializer.create(RealmInitializer.kt:42)
at io.github.xilinjia.krdb.internal.RealmInitializer.create(RealmInitializer.kt:30)
at androidx.startup.AppInitializer.doInitialize(AppInitializer.java:180)
at androidx.startup.AppInitializer.discoverAndInitialize(AppInitializer.java:239)
at androidx.startup.AppInitializer.discoverAndInitialize(AppInitializer.java:207)
at androidx.startup.InitializationProvider.onCreate(InitializationProvider.java:49)
at android.content.ContentProvider.attachInfo(ContentProvider.java:2769)
at android.content.ContentProvider.attachInfo(ContentProvider.java:2738)
at android.app.ActivityThread.installProvider(ActivityThread.java:9453)
at android.app.ActivityThread.installContentProviders(ActivityThread.java:8938)
at android.app.ActivityThread.handleBindApplication(ActivityThread.java:8556)
at android.app.ActivityThread.-$$Nest$mhandleBindApplication(unavailable)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2874)
at android.os.Handler.dispatchMessage(Handler.java:109)
at android.os.Looper.loopOnce(Looper.java:250)
at android.os.Looper.loop(Looper.java:340)
at android.app.ActivityThread.main(ActivityThread.java:9911)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:625)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:957)

## ------------- 3 ----------------
main (native):tid=1 systid=11281
#00 pc 0x902fc libc.so (syscall + 28) (BuildId: 84a42637b3a421b801818f5793418fca)
#01 pc 0x201434 libart.so (art::ConditionVariable::WaitHoldingLocks + 136) (BuildId: 7087b2f2160bfbf3335d54ba9779e325)
#02 pc 0x5fec34 libart.so (art::JNI<false>::CallObjectMethodV + 1644) (BuildId: 7087b2f2160bfbf3335d54ba9779e325)
#03 pc 0xe1a68 libandroid_runtime.so (_JNIEnv::CallObjectMethod + 120) (BuildId: 0e1df5bfe11cd44fb6c0d85364d38f07)
#04 pc 0x14e4d0 libandroid_runtime.so (android::NativeDisplayEventReceiver::dispatchVsync + 64) (BuildId: 0e1df5bfe11cd44fb6c0d85364d38f07)
#05 pc 0xca8f4 libgui.so (android::DisplayEventDispatcher::handleEvent + 276) (BuildId: 19ac09b6851127686ce984f2f648fa5d)
#06 pc 0x18e8c libutils.so (android::Looper::pollInner + 1276) (BuildId: 2fbb3340ac160ce2764241a715f6aed4)
#07 pc 0x1892c libutils.so (android::Looper::pollOnce + 124) (BuildId: 2fbb3340ac160ce2764241a715f6aed4)
#08 pc 0x18e86c libandroid_runtime.so (android::android_os_MessageQueue_nativePollOnce + 44) (BuildId: 0e1df5bfe11cd44fb6c0d85364d38f07)
at android.os.MessageQueue.nativePollOnce(Native method)
at android.os.MessageQueue.next(MessageQueue.java:341)
at android.os.Looper.loopOnce(Looper.java:176)
at android.os.Looper.loop(Looper.java:314)
at android.app.ActivityThread.main(ActivityThread.java:8680)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:565)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1081)

## ------------- 4 ------------------
Fatal Exception: java.lang.RuntimeException: Unable to get provider androidx.startup.InitializationProvider: androidx.startup.StartupException: java.lang.UnsatisfiedLinkError: dlopen failed: library "librealmc.so" not found
at android.app.ActivityThread.installProvider(ActivityThread.java:7464)
at android.app.ActivityThread.installContentProviders(ActivityThread.java:6976)
at android.app.ActivityThread.handleBindApplication(ActivityThread.java:6747)
at android.app.ActivityThread.access$1500(ActivityThread.java:256)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2091)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loopOnce(Looper.java:201)
at android.os.Looper.loop(Looper.java:288)
at android.app.ActivityThread.main(ActivityThread.java:7870)
at java.lang.reflect.Method.invoke(Method.java)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:548)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1009)


## --------------- 5 ----------------------
main (runnable):tid=1 systid=10642
at java.util.HashMap.resize(HashMap.java:712)
at java.util.HashMap.putVal(HashMap.java:668)
at java.util.HashMap.put(HashMap.java:617)
at java.util.HashSet.add(HashSet.java:229)
at java.util.zip.ZipFile$Source.initCEN(ZipFile.java:1915)
at java.util.zip.ZipFile$Source.<init>(ZipFile.java:1646)
at java.util.zip.ZipFile$Source.get(ZipFile.java:1599)
at java.util.zip.ZipFile$CleanableResource.<init>(ZipFile.java:797)
at java.util.zip.ZipFile.<init>(ZipFile.java:299)
at java.util.zip.ZipFile.<init>(ZipFile.java:261)
at java.util.jar.JarFile.<init>(JarFile.java:183)
at java.util.jar.JarFile.<init>(JarFile.java:176)
at libcore.io.ClassPathURLStreamHandler.<init>(ClassPathURLStreamHandler.java:52)
at dalvik.system.DexPathList$NativeLibraryElement.maybeInit(DexPathList.java:863)
at dalvik.system.DexPathList$NativeLibraryElement.findNativeLibrary(DexPathList.java:885)
at dalvik.system.DexPathList.findLibrary(DexPathList.java:594)
at dalvik.system.BaseDexClassLoader.findLibrary(BaseDexClassLoader.java:371)
at java.lang.Runtime.loadLibrary0(Runtime.java:1071)
at java.lang.Runtime.loadLibrary0(Runtime.java:1019)
at java.lang.System.loadLibrary(System.java:1765)
at io.github.xilinjia.krdb.internal.AndroidUtilsKt.loadAndroidNativeLibs(AndroidUtils.kt:22)
at io.github.xilinjia.krdb.internal.RealmInitializer.create(RealmInitializer.kt:42)
at io.github.xilinjia.krdb.internal.RealmInitializer.create(RealmInitializer.kt:30)
at androidx.startup.AppInitializer.doInitialize(AppInitializer.java:180)
at androidx.startup.AppInitializer.discoverAndInitialize(AppInitializer.java:239)
at androidx.startup.AppInitializer.discoverAndInitialize(AppInitializer.java:207)
at androidx.startup.InitializationProvider.onCreate(InitializationProvider.java:49)
at android.content.ContentProvider.attachInfo(ContentProvider.java:2636)
at android.content.ContentProvider.attachInfo(ContentProvider.java:2602)
at android.app.ActivityThread.installProvider(ActivityThread.java:8304)
at android.app.ActivityThread.installContentProviders(ActivityThread.java:7805)
at android.app.ActivityThread.handleBindApplication(ActivityThread.java:7484)
at android.app.ActivityThread.-$$Nest$mhandleBindApplication(unavailable)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2413)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loopOnce(Looper.java:222)
at android.os.Looper.loop(Looper.java:314)
at android.app.ActivityThread.main(ActivityThread.java:8742)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:565)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1090)

## ---------- 6 -------------
main (native):tid=1 systid=19950
#00 pc 0x4b50c libc.so (syscall + 28)
#01 pc 0x1b17a4 libart.so (art::ConditionVariable::WaitHoldingLocks(art::Thread*) + 148)
#02 pc 0x676130 libart.so (art::GoToRunnable(art::Thread*) + 480)
#03 pc 0x675f0c libart.so (art::JniMethodEnd(unsigned int, art::Thread*) + 28)
at libcore.io.Linux.access(Native method)
at libcore.io.ForwardingOs.access(ForwardingOs.java:72)
at libcore.io.BlockGuardOs.access(BlockGuardOs.java:73)
at libcore.io.ForwardingOs.access(ForwardingOs.java:72)
at android.app.ActivityThread$AndroidOs.access(ActivityThread.java:7891)
at java.io.UnixFileSystem.checkAccess(UnixFileSystem.java:281)
at java.io.File.exists(File.java:815)
at com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication.onCreate(MessagerApplication.kt:308)
at android.app.Instrumentation.callApplicationOnCreate(Instrumentation.java:1198)
at android.app.ActivityThread.handleBindApplication(ActivityThread.java:7050)
at android.app.ActivityThread.access$1500(ActivityThread.java:263)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2032)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loop(Looper.java:268)
at android.app.ActivityThread.main(ActivityThread.java:8016)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:627)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:997)


## ------------ 7 -------------
main (native):tid=1 systid=3614
#00 pc 0xac1a4 libc.so (__ioctl + 4) (BuildId: 0b760306a5009e324d0e77f8fb6f8b05)
#01 pc 0x5dc9c libc.so (ioctl + 156) (BuildId: 0b760306a5009e324d0e77f8fb6f8b05)
#02 pc 0x5b3ac libbinder.so (android::IPCThreadState::talkWithDriver + 284) (BuildId: 2e11f3c8f02355f692b7a2a58b9ac59f)
#03 pc 0x5cc88 libbinder.so (android::IPCThreadState::waitForResponse + 72) (BuildId: 2e11f3c8f02355f692b7a2a58b9ac59f)
#04 pc 0x5c9b8 libbinder.so (android::IPCThreadState::transact + 216) (BuildId: 2e11f3c8f02355f692b7a2a58b9ac59f)
#05 pc 0x53a3c libbinder.so (android::BpBinder::transact + 188) (BuildId: 2e11f3c8f02355f692b7a2a58b9ac59f)
#06 pc 0x177f68 libandroid_runtime.so (android_os_BinderProxy_transact + 152) (BuildId: 5840e47e6a499e288ab0c36161d2a90f)
at android.os.BinderProxy.transactNative(Native method)
at android.os.BinderProxy.transact(BinderProxy.java:602)
at android.content.pm.IShortcutService$Stub$Proxy.setDynamicShortcuts(IShortcutService.java:597)
at android.content.pm.ShortcutManager.setDynamicShortcuts(ShortcutManager.java:148)
at com.messenger.phone.number.text.sms.service.apps.HomeABActivity.checkShortcut(HomeABActivity.kt:4012)
at com.messenger.phone.number.text.sms.service.apps.HomeABActivity.onResume(HomeABActivity.kt:3757)
at android.app.Instrumentation.callActivityOnResume(Instrumentation.java:1535)
at android.app.Activity.performResume(Activity.java:8487)
at android.app.ActivityThread.performResumeActivity(ActivityThread.java:4954)
at android.app.ActivityThread.handleResumeActivity(ActivityThread.java:5003)
at android.app.servertransaction.ResumeActivityItem.execute(ResumeActivityItem.java:54)
at android.app.servertransaction.ActivityTransactionItem.execute(ActivityTransactionItem.java:45)
at android.app.servertransaction.TransactionExecutor.executeLifecycleState(TransactionExecutor.java:176)
at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:97)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2393)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loopOnce(Looper.java:204)
at android.os.Looper.loop(Looper.java:291)
at android.app.ActivityThread.main(ActivityThread.java:8134)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:588)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1019)


## --------------- 8 --------------
main (native):tid=1 systid=19950
#00 pc 0x4b50c libc.so (syscall + 28)
#01 pc 0x1b17a4 libart.so (art::ConditionVariable::WaitHoldingLocks(art::Thread*) + 148)
#02 pc 0x676130 libart.so (art::GoToRunnable(art::Thread*) + 480)
#03 pc 0x675f0c libart.so (art::JniMethodEnd(unsigned int, art::Thread*) + 28)
at libcore.io.Linux.access(Native method)
at libcore.io.ForwardingOs.access(ForwardingOs.java:72)
at libcore.io.BlockGuardOs.access(BlockGuardOs.java:73)
at libcore.io.ForwardingOs.access(ForwardingOs.java:72)
at android.app.ActivityThread$AndroidOs.access(ActivityThread.java:7891)
at java.io.UnixFileSystem.checkAccess(UnixFileSystem.java:281)
at java.io.File.exists(File.java:815)
at com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication.onCreate(MessagerApplication.kt:308)
at android.app.Instrumentation.callApplicationOnCreate(Instrumentation.java:1198)
at android.app.ActivityThread.handleBindApplication(ActivityThread.java:7050)
at android.app.ActivityThread.access$1500(ActivityThread.java:263)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2032)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loop(Looper.java:268)
at android.app.ActivityThread.main(ActivityThread.java:8016)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:627)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:997)

## ------------ 9 ------------
main (native):tid=1 systid=19950
#00 pc 0x4b50c libc.so (syscall + 28)
#01 pc 0x1b17a4 libart.so (art::ConditionVariable::WaitHoldingLocks(art::Thread*) + 148)
#02 pc 0x676130 libart.so (art::GoToRunnable(art::Thread*) + 480)
#03 pc 0x675f0c libart.so (art::JniMethodEnd(unsigned int, art::Thread*) + 28)
at libcore.io.Linux.access(Native method)
at libcore.io.ForwardingOs.access(ForwardingOs.java:72)
at libcore.io.BlockGuardOs.access(BlockGuardOs.java:73)
at libcore.io.ForwardingOs.access(ForwardingOs.java:72)
at android.app.ActivityThread$AndroidOs.access(ActivityThread.java:7891)
at java.io.UnixFileSystem.checkAccess(UnixFileSystem.java:281)
at java.io.File.exists(File.java:815)
at com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication.onCreate(MessagerApplication.kt:308)
at android.app.Instrumentation.callApplicationOnCreate(Instrumentation.java:1198)
at android.app.ActivityThread.handleBindApplication(ActivityThread.java:7050)
at android.app.ActivityThread.access$1500(ActivityThread.java:263)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2032)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loop(Looper.java:268)
at android.app.ActivityThread.main(ActivityThread.java:8016)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:627)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:997)

## ---------------- 10 ------------------
main (runnable):tid=1 systid=17284
at com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication.onCreate(MessagerApplication.kt:382)
at android.app.Instrumentation.callApplicationOnCreate(Instrumentation.java:1386)
at android.app.ActivityThread.handleBindApplication(ActivityThread.java:7685)
at android.app.ActivityThread.-$$Nest$mhandleBindApplication(unavailable)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2505)
at android.os.Handler.dispatchMessage(Handler.java:107)
at android.os.Looper.loopOnce(Looper.java:232)
at android.os.Looper.loop(Looper.java:317)
at android.app.ActivityThread.main(ActivityThread.java:8927)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:681)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:915)
88

## ------------ 11 -------------
main (runnable):tid=1 systid=17478
at kotlin.jvm.JvmClassMappingKt.c(JvmClassMapping.kt:1)
at kotlin.reflect.jvm.internal.KClassImpl.equals(KClassImpl.kt:303)
at kotlin.jvm.internal.Intrinsics.areEqual(Intrinsics.java:169)
at io.github.xilinjia.krdb.internal.ConfigurationImpl$3.createInstanceOf(ConfigurationImpl.kt:234)
at io.github.xilinjia.krdb.internal.RealmObjectUtilKt.toRealmObject(RealmObjectUtil.kt:73)
at io.github.xilinjia.krdb.internal.ConvertersKt.realmValueToRealmObject-YHBdMnE(Converters.kt:518)
at io.github.xilinjia.krdb.internal.RealmResultsImpl.get(RealmResultsImpl.kt:70)
at io.github.xilinjia.krdb.internal.RealmResultsImpl.contains(RealmResultsImpl.kt:50)
at io.github.xilinjia.krdb.internal.RealmResultsImpl.get(RealmResultsImpl.kt:50)
at kotlin.collections.AbstractList$IteratorImpl.next(AbstractList.kt:84)
at kotlin.collections.CollectionsKt___CollectionsKt.filterTo(CollectionsKt___Collections.kt:865)
at com.messenger.phone.number.text.sms.service.apps.realmplan.RealmConversationDataSource$observeMessages$$inlined$map$1$2.emit(Emitters.kt:56)
at kotlinx.coroutines.flow.FlowKt__ChannelsKt.emitAllImpl$FlowKt__ChannelsKt(Channels.kt:33)
at kotlinx.coroutines.flow.FlowKt__ChannelsKt.access$emitAllImpl$FlowKt__ChannelsKt(Channels.kt:1)
at kotlinx.coroutines.flow.FlowKt__ChannelsKt$emitAllImpl$1.invokeSuspend(Channels.kt:11)
at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:100)
at android.os.Handler.handleCallback(Handler.java:942)
at android.os.Handler.dispatchMessage(Handler.java:99)
at android.os.Looper.loopOnce(Looper.java:211)
at android.os.Looper.loop(Looper.java:300)
at android.app.ActivityThread.main(ActivityThread.java:8503)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:561)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:954)



## ---------------- 12 -----------
main (runnable):tid=1 systid=17478
at kotlin.jvm.JvmClassMappingKt.c(JvmClassMapping.kt:1)
at kotlin.reflect.jvm.internal.KClassImpl.equals(KClassImpl.kt:303)
at kotlin.jvm.internal.Intrinsics.areEqual(Intrinsics.java:169)
at io.github.xilinjia.krdb.internal.ConfigurationImpl$3.createInstanceOf(ConfigurationImpl.kt:234)
at io.github.xilinjia.krdb.internal.RealmObjectUtilKt.toRealmObject(RealmObjectUtil.kt:73)
at io.github.xilinjia.krdb.internal.ConvertersKt.realmValueToRealmObject-YHBdMnE(Converters.kt:518)
at io.github.xilinjia.krdb.internal.RealmResultsImpl.get(RealmResultsImpl.kt:70)
at io.github.xilinjia.krdb.internal.RealmResultsImpl.contains(RealmResultsImpl.kt:50)
at io.github.xilinjia.krdb.internal.RealmResultsImpl.get(RealmResultsImpl.kt:50)
at kotlin.collections.AbstractList$IteratorImpl.next(AbstractList.kt:84)
at kotlin.collections.CollectionsKt___CollectionsKt.filterTo(CollectionsKt___Collections.kt:865)
at com.messenger.phone.number.text.sms.service.apps.realmplan.RealmConversationDataSource$observeMessages$$inlined$map$1$2.emit(Emitters.kt:56)
at kotlinx.coroutines.flow.FlowKt__ChannelsKt.emitAllImpl$FlowKt__ChannelsKt(Channels.kt:33)
at kotlinx.coroutines.flow.FlowKt__ChannelsKt.access$emitAllImpl$FlowKt__ChannelsKt(Channels.kt:1)
at kotlinx.coroutines.flow.FlowKt__ChannelsKt$emitAllImpl$1.invokeSuspend(Channels.kt:11)
at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:100)
at android.os.Handler.handleCallback(Handler.java:942)
at android.os.Handler.dispatchMessage(Handler.java:99)
at android.os.Looper.loopOnce(Looper.java:211)
at android.os.Looper.loop(Looper.java:300)
at android.app.ActivityThread.main(ActivityThread.java:8503)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:561)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:954)


## ------------- 13 -----------
main (runnable):tid=1 systid=31216
at com.messenger.phone.number.text.sms.service.apps.ads.v2.AdsOrchestrator.<clinit>(AdsOrchestrator.kt:961)
at com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication.onCreate(MessagerApplication.kt:273)
at android.app.Instrumentation.callApplicationOnCreate(Instrumentation.java:1316)
at android.app.ActivityThread.handleBindApplication(ActivityThread.java:7711)
at android.app.ActivityThread.-$$Nest$mhandleBindApplication(unavailable)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2478)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loopOnce(Looper.java:230)
at android.os.Looper.loop(Looper.java:319)
at android.app.ActivityThread.main(ActivityThread.java:8919)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:578)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1103)


## -------------- 14 ----------
main (runnable):tid=1 systid=8779
at kotlin.jvm.internal.Intrinsics.i(Intrinsics.java:1)
at kotlin.reflect.jvm.internal.ConcurrentHashMapCache.a(CacheByClass.kt:3)
at kotlin.reflect.jvm.internal.CachesKt.getOrCreateKotlinClass(caches.kt:21)
at kotlin.reflect.jvm.internal.ReflectionFactoryImpl.getOrCreateKotlinClass(ReflectionFactoryImpl.java:41)
at kotlin.jvm.internal.Reflection.getOrCreateKotlinClass(Reflection.java:60)
at kotlin.jvm.JvmClassMappingKt.getKotlinClass(JvmClassMapping.kt:81)
at kotlin.jvm.JvmClassMappingKt.getAnnotationClass(JvmClassMapping.kt:108)
at kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectClassStructure.processAnnotation(ReflectKotlinClass.kt:164)
at kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectClassStructure.loadClassAnnotations(ReflectKotlinClass.kt:85)
at kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectKotlinClass$Factory.create(ReflectKotlinClass.kt:56)
at kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectKotlinClassFinder.findKotlinClass(ReflectKotlinClassFinder.kt:34)
at kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectKotlinClassFinder.findKotlinClassOrContent(ReflectKotlinClassFinder.kt:38)
at kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaPackageScope.classes$lambda$1(LazyJavaPackageScope.kt:67)
at kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaPackageScope.accessor$LazyJavaPackageScope$lambda1(LazyJavaPackageScope.kt:1)
at kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaPackageScope$$Lambda$1.invoke(LazyJavaPackageScope.kt:7)
at kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager$MapBasedMemoizedFunction.invoke(LockBasedStorageManager.java:578)
at kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaPackageScope.findClassifier(LazyJavaPackageScope.kt:146)
at kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaPackageScope.getContributedClassifier(LazyJavaPackageScope.kt:136)
at kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.JvmPackageScope.getContributedClassifier(JvmPackageScope.kt:55)
at kotlin.reflect.jvm.internal.impl.resolve.scopes.ChainedMemberScope.getContributedClassifier(ChainedMemberScope.kt:35)
at kotlin.reflect.jvm.internal.impl.resolve.scopes.AbstractScopeAdapter.getContributedClassifier(AbstractScopeAdapter.kt:44)
at kotlin.reflect.jvm.internal.impl.descriptors.FindClassInModuleKt.findClassifierAcrossModuleDependencies(findClassInModule.kt:26)
at kotlin.reflect.jvm.internal.impl.descriptors.FindClassInModuleKt.findClassAcrossModuleDependencies(findClassInModule.kt:48)
at kotlin.reflect.jvm.internal.KClassImpl$Data.descriptor_delegate$lambda$0(KClassImpl.kt:64)
at kotlin.reflect.jvm.internal.KClassImpl$Data.accessor$KClassImpl$Data$lambda0(KClassImpl.kt:1)
at kotlin.reflect.jvm.internal.KClassImpl$Data$$Lambda$0.invoke(KClassImpl.kt:3)
at kotlin.reflect.jvm.internal.ReflectProperties$LazySoftVal.invoke(ReflectProperties.java:70)
at kotlin.reflect.jvm.internal.ReflectProperties$Val.getValue(ReflectProperties.java:32)
at kotlin.reflect.jvm.internal.KClassImpl$Data.<clinit>(KClassImpl.kt:53)
at kotlin.reflect.jvm.internal.KClassImpl$Data.getDescriptor(KClassImpl.kt:53)
at kotlin.reflect.jvm.internal.KClassImpl$Data.nestedClasses_delegate$lambda$10(KClassImpl.kt:111)
at kotlin.reflect.jvm.internal.KClassImpl$Data.accessor$KClassImpl$Data$lambda5(KClassImpl.kt:1)
at kotlin.reflect.jvm.internal.KClassImpl$Data$$Lambda$5.invoke(KClassImpl.kt:3)
at kotlin.reflect.jvm.internal.ReflectProperties$LazySoftVal.invoke(ReflectProperties.java:70)
at kotlin.reflect.jvm.internal.ReflectProperties$Val.getValue(ReflectProperties.java:32)
at kotlin.reflect.jvm.internal.KClassImpl$Data.<clinit>(KClassImpl.kt:110)
at kotlin.reflect.jvm.internal.KClassImpl$Data.getNestedClasses(KClassImpl.kt:110)
at kotlin.reflect.jvm.internal.KClassImpl.getNestedClasses(KClassImpl.kt:251)
at kotlin.reflect.full.KClasses.getCompanionObject(KClasses.kt:47)
at kotlin.reflect.full.KClasses.getCompanionObjectInstance(KClasses.kt:57)
at io.github.xilinjia.krdb.internal.platform.RealmObjectKt.realmObjectCompanionOrNull(RealmObject.kt:28)
at io.github.xilinjia.krdb.internal.RealmObjectUtilKt.realmObjectCompanionOrNull(RealmObjectUtil.kt:112)
at io.github.xilinjia.krdb.Configuration$SharedBuilder.<init>(Configuration.kt:191)
at io.github.xilinjia.krdb.RealmConfiguration$Builder.<init>(RealmConfiguration.kt:51)
at com.messenger.phone.number.text.sms.service.apps.realmplan.AppRealmConfiguration.build(RealmConfigurationSkeleton.kt:16)
at com.messenger.phone.number.text.sms.service.apps.DI.RealmModule.provideRealm(RealmModule.kt:25)
at com.messenger.phone.number.text.sms.service.apps.DI.RealmModule_ProvideRealmFactory.provideRealm(RealmModule_ProvideRealmFactory.java:38)
at com.messenger.phone.number.text.sms.service.apps.ApplicationClass.DaggerMessagerApplication_HiltComponents_SingletonC$SingletonCImpl$SwitchingProvider.get(DaggerMessagerApplication_HiltComponents_SingletonC.java:1971)
at dagger.internal.DoubleCheck.getSynchronized(DoubleCheck.java:54)
at dagger.internal.DoubleCheck.get(DoubleCheck.java:45)
at com.messenger.phone.number.text.sms.service.apps.SendMessageActivity.getsetIntent(SendMessageActivity.kt:2115)
at com.messenger.phone.number.text.sms.service.apps.SendMessageActivity.onCreate(SendMessageActivity.kt:612)
at android.app.Activity.performCreate(Activity.java:8110)
at android.app.Activity.performCreate(Activity.java:8090)
at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1329)
at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:3764)
at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:4010)
at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:109)
at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:140)
at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:95)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2337)
at android.os.Handler.dispatchMessage(Handler.java:106)
at android.os.Looper.loopOnce(Looper.java:241)
at android.os.Looper.loop(Looper.java:342)
at android.app.ActivityThread.main(ActivityThread.java:8143)
at java.lang.reflect.Method.invoke(Native method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:583)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1045)


#### make sure . -----------
Act as a Senior Android Performance Engineer.

Analyze the entire Android project for ANRs, freezes, startup delays, memory issues, coroutine issues, threading issues, database performance issues, RecyclerView performance issues, and any operation that may block the main thread.

IMPORTANT REQUIREMENTS:

1. Preserve Existing Behavior

* Do NOT change business logic.
* Do NOT change app flow.
* Do NOT change user-visible behavior.
* Do NOT change feature functionality.
* Do NOT remove any existing logic.
* Do NOT simplify or bypass existing logic.
* Do NOT change filtering, sorting, loading, pagination, update, refresh, sync, or data-processing behavior.
* Do NOT introduce behavior differences between old and new users.

2. Preserve User Data

* Existing users must continue to see exactly the same data.
* Existing preferences, SharedPreferences, Room/Realm data, local database data, onboarding states, purchase states, and cached data must remain compatible.
* Do NOT rename storage keys unless migration is provided.
* Do NOT reset user data.
* Do NOT break backward compatibility.
* If a migration is required, provide a safe migration strategy.

3. No UI Changes

* Do NOT redesign screens.
* Do NOT modify layouts.
* Do NOT change colors.
* Do NOT change dimensions.
* Do NOT change navigation.
* Do NOT change user interactions.
* Do NOT move UI components.
* Keep the exact same user experience.

4. Performance Optimization Rules
   You may:

* Move heavy work off the main thread.
* Improve coroutine usage.
* Improve dispatcher selection.
* Optimize database queries.
* Optimize RecyclerView updates.
* Optimize startup initialization.
* Optimize memory usage.
* Reduce object allocations.
* Optimize SMS loading.
* Optimize contact loading.
* Optimize ad loading.
* Optimize filtering and searching.
* Improve background processing.

But:

* Output data must remain identical.
* Timing-sensitive business logic must remain identical.
* Final user results must remain identical.

5. ANR Investigation
   Find:

* Main thread blocking operations.
* Disk I/O on main thread.
* SharedPreferences abuse.
* Database queries on main thread.
* ContentResolver queries on main thread.
* SMS queries on main thread.
* Contact queries on main thread.
* Network calls on main thread.
* Excessive synchronization.
* Deadlocks.
* Coroutine misuse.
* Excessive object creation.
* RecyclerView update bottlenecks.
* Startup bottlenecks.
* BroadcastReceiver issues.
* Service issues.
* WorkManager issues.
* Ad SDK initialization delays.
* RevenueCat delays.
* OneSignal delays.
* Remote Config delays.

6. Memory Investigation
   Find:

* Memory leaks.
* Activity leaks.
* Fragment leaks.
* Context leaks.
* Handler leaks.
* ViewBinding leaks.
* Observer leaks.
* Coroutine scope leaks.
* Bitmap issues.
* Large collections.
* OOM risks.

7. Output Format
   For every finding provide:

* Severity (Critical / High / Medium / Low)
* File name
* Function name
* Root cause
* Impact
* Exact fix
* Code example
* Why the fix is safe
* Proof that old and new users will behave identically

8. Safety Verification
   Before suggesting any change verify:

* No user data loss.
* No preference reset.
* No migration issues.
* No UI changes.
* No business logic changes.
* No feature changes.
* No slowdown in update, refresh, filtering, sorting, loading, syncing, or list rendering logic.
* No impact on ads, purchases, subscriptions, analytics, onboarding, or notifications.

Analyze only first. Do not modify files automatically. Produce a prioritized action plan from highest impact to lowest impact.
