# Messages New Ads Flow Report

Date: 2026-05-08  
Project: Messages

## Scope
This report is based on:
- 28-day Firebase screen/activity data for 2026-04-10 to 2026-05-07
- funnel screenshots and funnel step notes provided in chat
- AdMob export file `admob-report-5.csv`
- project scan of current onboarding, permission, home, app-open, and paywall flows

This step does not change app code.

## Executive Summary
The worst place to push harder is first-session onboarding. The current code and funnel both show pressure around `LanguageActivity`, paywall, and onboarding interstitial flow. That flow may give high eCPM on a small number of impressions, but it is not safe for retention.

The new flow should use:
- `Small Native` as the main monetization format on high-value passive screens
- `Large Anchored Banner` on home and content-style screens, but only after useful content is visible
- `Interstitial` only in low-risk late-session moments
- `Rewarded` only for optional advanced actions
- `App Open` only for returning users, never for onboarding, permission, paywall, or compose/chat flows

## Project Scan Findings
Current flow seen in code:
- launcher: `WelcomeActivity`
- onboarding path: `WelcomeActivity -> LanguageActivity -> HomeABActivity`
- alternate permission path: `WelcomeActivity -> permissionOneActivity -> permissionTwoActivity -> LanguageActivity/HomeABActivity`
- paywall path exists in onboarding variants through `Experiment_Sub_Screen_Activity` and `Paywall_FourPlan_Activity`
- onboarding interstitial can still fire from `LanguageActivity` using `listOfPaywallInter`
- returning flow can hit `SplashScreenActivity -> PermissionAfterNewActivity -> HomeABActivity`
- app open ads are loaded in `MessagerApplication`
- home already has bottom banner logic in `HomeABActivity`
- language screen already has a preloaded native placement

Important current risk:
- app open exclusions exist for paywall/SDK screens, but not for all sensitive flows like `WelcomeActivity`, `LanguageActivity`, `HomeABActivity` first-use state, `SendMessageActivity`, `permissionOneActivity`, `permissionTwoActivity`, and `PermissionAfterNewActivity`

## Highest Traffic Screens From Firebase

| Rank | Screen | Views | Active users | Views / AU | Avg engagement sec / AU | Revenue |
|---|---|---:|---:|---:|---:|---:|
| 1| `HomeABActivity` | 1,742,054 | 90,666 | 19.21 | 168.16 | 42,564.99 |
| 2| `SendMessageActivity` | 1,316,290 | 42,839 | 30.73 | 911.82 | 1,070.99 |
| 3| `WelcomeActivity` | 301,396 | 71,083 | 4.24 | 9.60 | 1,774.47 |
| 4| `AdActivity` | 197,279 | 39,269 | 5.02 | 28.12 | 70,687.65 |
| 5| `SettingActivity` | 165,437 | 10,847 | 15.25 | 74.76 | 5,608.95 |
| 6| `PrivacyChatActivity` | 107,694 | 1,451 | 74.22 | 265.81 | 3.31 |
| 7| `LockScreenActivity` | 105,906 | 1,680 | 63.04 | 279.67 | 2.69 |
| 8 | `ViewDetailsActivity` | 63,573 | 7,435 | 8.55 | 72.26 | 5.11 |
| 9 | `LanguageActivity` | 56,187 | 31,980 | 1.76 | 8.83 | 57,510.84 |
| 10 | `SelectContactActivity` | 44,540 | 12,253 | 3.64 | 46.58 | 19.48 |
| 11 | `permissionTwoActivity` | 42,257 | 19,203 | 2.20 | 24.15 | 7.18 |
| 12 | `ArchivedActivity` | 35,123 | 1,926 | 18.24 | 63.69 | 9.67 |
| 13 | `OverlayPermissionAnimationActivity` | 30,058 | 14,957 | 2.01 | 3.78 | 7.76 |

Traffic interpretation:
- `HomeABActivity` is the best passive scale screen, but user intent is messaging first, not ad consumption.
- `SendMessageActivity` has very high engagement, but this is a protected core screen and should not be monetized aggressively.
- `SettingActivity` and `ViewDetailsActivity` are safe secondary monetization surfaces.
- `LanguageActivity` has huge monetized revenue already, but it is an onboarding-sensitive screen, so more pressure there is risky.

## Funnel And Drop-Off Analysis
Observed funnel signals from the provided screenshots:

Baseline style funnel:
- `First_Open`: 5,187
- `First_splash`: 4,757
- `First_home`: 4,549
- `First_paywall`: 2,733
- `First_interad`: 2,603
- `First_home_again`: 1,809

Variant B style funnel:
- `First_Open`: 5,187
- `First_splash`: 4,757
- `First_home`: 4,549
- `First_language`: 1,555
- `First_paywall`: 1,243
- `First_interad`: 1,191
- `First_home_again`: 774

Main drop points:
- `First_home -> First_language` in Variant B loses about `65.82%`
- `First_home -> First_paywall` in baseline still loses about `39.9%`
- `First_interad -> First_home_again` loses about `30% to 35%`
- retention chart shows only about `4.4%` active users by day 33

Conclusion:
- first-session fullscreen pressure is too expensive
- language, paywall, and onboarding interstitial should not all compete in the same first-session path
- first-time monetization should move from fullscreen-first to passive-first

## Old AdMob Performance Analysis

Best performers from the export:

| Ad Unit | Earnings USD | eCPM | Requests | Match rate | Show rate | Impressions | Read |
|---|---:|---:|---:|---:|---:|---:|---|
| `Inter_splash` | 146.40 | 35.24 | 7,828 | 99.80% | 53.17% | 4,154 | High eCPM, risky onboarding format |
| `Native_language` | 144.05 | 26.45 | 12,361 | 99.70% | 44.19% | 5,446 | Strong eCPM, but onboarding-sensitive |
| `MS_Banner_Home` | 58.04 | 1.60 | 57,619 | 88.74% | 70.76% | 36,180 | Good fill/scale, weak yield |
| `MS_APP_OPEN_AD` | 54.90 | 2.53 | 35,071 | 89.87% | 68.90% | 21,717 | Usable only with strict gating |

Weak or risky areas:
- home banners have volume but much weaker yield than native language or interstitial
- splash/banner variants have very small scale and should not drive strategy
- generic app open units with very low show rate or tiny viewer count are not strong enough for broad exposure
- `inter_chat_back` and chat-side monetization are poor fits for a core messaging app

AdMob conclusion:
- keep one compact native on language, but do not add more pressure there
- improve home monetization with better timing and a stronger passive format, not more aggressive fullscreen ads
- treat app open as a mature-user monetization layer, not a default global layer

## New Ad Flow Principles
- no ads on first cold start screen
- no interstitial before first useful app value
- no fullscreen ads in permission flow
- no ads inside paywall or billing flow
- no aggressive ads on compose/chat/private/lock screens
- use `high -> med -> low` only where passive placements can tolerate fallback delay
- use `high -> med` only on sensitive screens

## Recommended New Ad Placements

| Priority | Activity / Screen | Placement Structure | Ad Type | Trigger Point | Notes |
|---|---|---|---|---|---|
| P1 | `HomeABActivity` | `home_large_banner_1_1` (high), `home_large_banner_1_2` (medium), `home_large_banner_1_3` (low), `home_large_banner_low1` | Large Anchored Banner | After first scroll or 20s dwell | Never instantly on first paint. |
| P1 | `LanguageActivity` | `language_native_1_1` (high), `language_native_1_2` (medium) | Small Native | After language list visible | Avoid low priority for onboarding speed. |
| P1 | `SettingActivity` | `setting_native_1_1` (high), `setting_native_1_2` (medium), `setting_native_1_3` (low), `setting_native_low1` | Small Native | After user scrolls lower settings groups | Keep separated from premium/settings rows. |
| P2 | `ViewDetailsActivity` | `view_details_native_1_1` (high), `view_details_native_1_2` (medium), `view_details_native_1_3` (low), `view_details_native_low1` | Small Native | After details render below actions | Safe reading-style inventory. |
| P2 | Returning app foreground | `resume_app_open_1_1` (high), `resume_app_open_1_2` (medium) | App Open | After meaningful background gap | Skip onboarding/paywall/deep-link returns. |
| P3 | `ArchivedActivity` | `archive_interstitial_1_1` (high), `archive_interstitial_1_2` (medium) | Interstitial | On exit after task complete | Strict cap required. |
| P3 | `RecycleBinActivity` | `recycle_interstitial_1_1` (high), `recycle_interstitial_1_2` (medium) | Interstitial | On exit after restore/delete | Avoid destructive-flow interruption. |
| P3 | `BekupActivity` | `backup_rewarded_1_1` (high), `backup_rewarded_1_2` (medium) | Rewarded | Optional advanced backup/restore action | Explicit value exchange only. |
| P3 | `Schedule_Message_Show_Activity` | `schedule_rewarded_1_1` (high), `schedule_rewarded_1_2` (medium) | Rewarded | Optional advanced schedule action | Must remain optional. |
| P3 | `Message_Translation_Activity` | `translation_rewarded_1_1` (high), `translation_rewarded_1_2` (medium) | Rewarded | Optional advanced translation action | Never gate basic flow. |
| Fallback only | `PermissionAfterNewActivity` | `permission_banner_1_2` (medium) | Banner | Only after onboarding-stage testing | Sensitive screen, avoid first-session usage. |

## Screens To Avoid

| Screen | Avoid | Reason |
|---|---|---|
| `WelcomeActivity` | All ads | First impression and very short dwell screen |
| `permissionOneActivity` | All ads | Sensitive permission grant step |
| `permissionTwoActivity` | All ads | Sensitive permission + overlay step |
| `OverlayPermissionAnimationActivity` | All ads | Helper screen, not monetization inventory |
| `Experiment_Sub_Screen_Activity` | Ads beyond paywall content | Paywall conversion focus |
| `Paywall_FourPlan_Activity` | All ads | Billing/paywall should stay clean |
| `SendMessageActivity` | All ads | Core messaging screen with long engagement; ad pressure hurts retention |
| `SelectContactActivity` | All ads | Action-heavy selection screen |
| `SearchActivity` | All ads | Intent-heavy utility screen with low pause tolerance |
| `PrivacyChatActivity` | Banner/Native/Interstitial | Sensitive premium/privacy space |
| `LockScreenActivity` | Banner/Native/Interstitial | Security-sensitive screen |

## Recommended First-Time Flow
Recommended new first-time flow:
- `WelcomeActivity`
- `LanguageActivity`
- paywall only if business requires it, but keep it as the single monetization pressure point
- `HomeABActivity`

Recommended first-time ad behavior:
- no ad on `WelcomeActivity`
- one `Small Native` on `LanguageActivity`
- no onboarding interstitial before first home use
- no app open during first-session flow
- first fullscreen ad only after user has already used the app and moved into a secondary screen or later session

## High / Med / Low Strategy

| Placement type | Strategy |
|---|---|
| Sensitive onboarding placements | `high + med` only |
| Home passive placements | `high + med + low` |
| Secondary reading screens | `high + med + low` |
| Interstitial | `high + med` only |
| Rewarded | `high + med` only |
| App Open | `high + med` only |

Rules:
- `high`: best eCPM unit
- `med`: fill protection if `high` no-fills or times out
- `low`: use only on passive banner/native inventory where empty space is worse than lower yield

## Final Plan By Business Goal

For show rate:
- scale passive placements on `HomeABActivity`, `SettingActivity`, and `ViewDetailsActivity`

For match rate:
- use `high + med + low` only on passive placements
- reduce fragile experimental slots on splash and permission surfaces

For eCPM:
- keep language native
- move interstitial usage to later-session moments instead of first-session pressure

For retention:
- remove onboarding interstitial from the main first-session path
- keep chat/compose/search/private flows ad-light

For policy safety:
- do not imitate rows or buttons with native ads
- do not interrupt permission or paywall flows
- do not place ads near destructive or security-sensitive actions

## Recommended Rollout Order
1. `HomeABActivity` large anchored banner with delayed trigger
2. `LanguageActivity` keep single compact native, remove extra fullscreen pressure
3. `SettingActivity` small native
4. `ViewDetailsActivity` small native
5. returning-user app open gating cleanup
6. late-session interstitial test on `ArchivedActivity` and `RecycleBinActivity`
7. rewarded experiments on `BekupActivity`, `Schedule_Message_Show_Activity`, and `Message_Translation_Activity`
