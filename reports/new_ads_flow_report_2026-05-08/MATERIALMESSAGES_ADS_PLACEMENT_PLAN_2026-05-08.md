# MaterialMessages Ads Placement Plan

Date: 2026-05-08

## Placement Summary

| Priority | Screen | Ad type | Trigger | High | Med | Low | Note |
|---|---|---|---|---|---|---|---|
| P0 | `DefaultAfterCallActivity` | Small Native | After call summary visible | Yes | Yes | Fallback only | Main revenue engine |
| P1 | `HomeABActivity` | Large Anchored Banner | After first scroll or 20s dwell | Yes | Yes | Yes | Passive scale placement |
| P1 | `LanguageActivity` | Small Native | After list render | Yes | Yes | No | One ad only |
| P1 | `SettingActivity` | Small Native | After lower settings group | Yes | Yes | Yes | Safe secondary placement |
| P2 | `ViewDetailsActivity` | Small Native | After content render | Yes | Yes | Yes | Reading-style placement |
| P2 | Returning foreground | App Open | Returning user only | Yes | Yes | No | Skip sensitive screens |
| P3 | `ArchivedActivity` | Interstitial | Exit only | Yes | Yes | No | Strict caps |
| P3 | `RecycleBinActivity` | Interstitial | Exit only | Yes | Yes | No | Strict caps |
| P3 | `BekupActivity` | Rewarded | User taps advanced action | Yes | Yes | No | User initiated only |
| P3 | `Schedule_Message_Show_Activity` | Rewarded | User taps advanced action | Yes | Yes | No | User initiated only |
| P3 | `Message_Translation_Activity` | Rewarded | User taps advanced action | Yes | Yes | No | User initiated only |

## Screen-Wise Recommendation

| Screen | Final recommendation |
|---|---|
| `DefaultAfterCallActivity` | `Small Native` primary. Use banner only as no-fill fallback. |
| `HomeABActivity` | `Large Anchored Banner` delayed, passive, bottom safe area. |
| `LanguageActivity` | `Small Native` only. No banner. No first-session interstitial in same path. |
| `SettingActivity` | `Small Native` between lower sections. |
| `ViewDetailsActivity` | `Small Native` below detail content, away from action buttons. |
| `ArchivedActivity` | Optional `Interstitial` on exit only. |
| `RecycleBinActivity` | Optional `Interstitial` on exit only. |
| `BekupActivity` | Optional `Rewarded` for advanced backup/restore action. |
| `Schedule_Message_Show_Activity` | Optional `Rewarded` for advanced schedule action. |
| `Message_Translation_Activity` | Optional `Rewarded` for advanced translation action. |
| Returning app foreground | `App Open` for mature returning users only. |

## Avoid List

| Screen | Do not use |
|---|---|
| `WelcomeActivity` | All ad types |
| `permissionOneActivity` | All ad types |
| `permissionTwoActivity` | All ad types |
| `PermissionAfterNewActivity` | All ad types in first rollout |
| `OverlayPermissionAnimationActivity` | All ad types |
| `Experiment_Sub_Screen_Activity` | All ad types |
| `Paywall_FourPlan_Activity` | All ad types |
| `SendMessageActivity` | All ad types |
| `SelectContactActivity` | All ad types |
| `SearchActivity` | All ad types |
| `PrivacyChatActivity` | Banner, Native, Interstitial |
| `LockScreenActivity` | Banner, Native, Interstitial |

## High / Med / Low Rules

| Case | Recommendation |
|---|---|
| After-call | `high + med`, `low` fallback only |
| Home banner | `high + med + low` |
| Language native | `high + med` |
| Settings / details native | `high + med + low` |
| Interstitial | `high + med` |
| Rewarded | `high + med` |
| App Open | `high + med` |

## Trigger Rules
- Never show fullscreen ads on cold start.
- Never show fullscreen ads before first useful app value.
- Never show ads inside paywall or permission flow.
- Never show ads on compose/chat/private/security screens.
- Keep cooldown between fullscreen ads.
- Use one native per screen only.
- Label native ads clearly and keep them visually separate from real app rows.
