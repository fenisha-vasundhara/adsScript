package com.messenger.phone.number.text.sms.service.apps.ads.v2

enum class AdFormat {
    BANNER,
    NATIVE_SMALL,
    APP_OPEN,
    INTERSTITIAL,
    REWARDED
}

enum class AdPlacement(
    val format: AdFormat,
    val cooldownMs: Long,
    val allowInFirstSession: Boolean
) {
    HOME_LARGE_BANNER(
        format = AdFormat.BANNER,
        cooldownMs = 45_000L,
        allowInFirstSession = true
    ),
    LANGUAGE_NATIVE(
        format = AdFormat.NATIVE_SMALL,
        cooldownMs = 30_000L,
        allowInFirstSession = true
    ),
    SETTING_NATIVE(
        format = AdFormat.NATIVE_SMALL,
        cooldownMs = 30_000L,
        allowInFirstSession = true
    ),
    VIEW_DETAILS_NATIVE(
        format = AdFormat.NATIVE_SMALL,
        cooldownMs = 30_000L,
        allowInFirstSession = true
    ),
    APP_OPEN_RESUME(
        format = AdFormat.APP_OPEN,
        cooldownMs = 5 * 60_000L,
        allowInFirstSession = false
    ),
    ARCHIVE_EXIT_INTERSTITIAL(
        format = AdFormat.INTERSTITIAL,
        cooldownMs = 2 * 60_000L,
        allowInFirstSession = false
    ),
    RECYCLE_EXIT_INTERSTITIAL(
        format = AdFormat.INTERSTITIAL,
        cooldownMs = 2 * 60_000L,
        allowInFirstSession = false
    ),
    BACKUP_REWARDED(
        format = AdFormat.REWARDED,
        cooldownMs = 45_000L,
        allowInFirstSession = true
    ),
    SCHEDULE_REWARDED(
        format = AdFormat.REWARDED,
        cooldownMs = 45_000L,
        allowInFirstSession = true
    ),
    TRANSLATION_REWARDED(
        format = AdFormat.REWARDED,
        cooldownMs = 45_000L,
        allowInFirstSession = true
    )
}
