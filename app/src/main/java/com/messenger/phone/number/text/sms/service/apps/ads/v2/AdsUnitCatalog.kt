package com.messenger.phone.number.text.sms.service.apps.ads.v2

import android.content.Context

object AdsUnitCatalog {

    fun waterfall(context: Context, placement: AdPlacement): List<String> {
        val ids = when (placement) {
            AdPlacement.HOME_LARGE_BANNER -> HOME_BANNER_AD_IDS

            AdPlacement.LANGUAGE_NATIVE -> LANGUAGE_NATIVE_AD_IDS

            AdPlacement.SETTING_NATIVE -> SETTINGS_NATIVE_AD_IDS

            AdPlacement.VIEW_DETAILS_NATIVE -> VIEW_DETAILS_NATIVE_AD_IDS

            AdPlacement.APP_OPEN_RESUME -> APP_OPEN_AD_IDS

            AdPlacement.ARCHIVE_EXIT_INTERSTITIAL -> ARCHIVE_INTER_AD_IDS

            AdPlacement.RECYCLE_EXIT_INTERSTITIAL -> RECYCLE_INTER_AD_IDS

            AdPlacement.BACKUP_REWARDED -> BACKUP_REWARDED_AD_IDS

            AdPlacement.SCHEDULE_REWARDED -> SCHEDULE_REWARDED_AD_IDS

            AdPlacement.TRANSLATION_REWARDED -> TRANSLATION_REWARDED_AD_IDS
        }

        return ids
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    }
}