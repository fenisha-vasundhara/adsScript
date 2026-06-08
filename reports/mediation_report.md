# Mediation Report Based on Top User Countries

## Executive Summary

Your traffic is concentrated in emerging-market countries, especially:

- Indonesia
- Brazil
- United States
- India
- Thailand
- Tanzania
- Spain

For this country mix, **Meta Audience Network should be one of the highest-priority mediation sources**, especially for Indonesia, Brazil, India, Thailand, and Tanzania.

For the United States and Spain, **AdMob and AppLovin are usually more stable and competitive**, while Meta should remain active but not be your only bet.

## Top Countries Overview

| Rank | Country | Active Users | Traffic Priority |
|---|---|---:|---|
| 1 | Indonesia | 36K | Very High |
| 2 | Brazil | 22K | High |
| 3 | United States | 11K | Medium |
| 4 | India | 8.4K | Medium |
| 5 | Thailand | 3.5K | Low-Medium |
| 6 | Tanzania | 2.1K | Low |
| 7 | Spain | 1.5K | Low |

## Country-Wise Network Recommendation

| Country | Recommended Network Order | Practical Direction |
|---|---|---|
| Indonesia | Meta > AppLovin > AdMob > Unity | Heavy Meta focus |
| Brazil | Meta > AppLovin > AdMob > IronSource | Strong Meta focus |
| India | Meta > AdMob > AppLovin > Unity | Meta should stay strong |
| Thailand | Meta > AppLovin | Meta-first setup |
| Tanzania | Meta > AdMob | Prioritize fill rate |
| United States | AdMob > AppLovin > Meta | Balanced Tier-1 strategy |
| Spain | AdMob > Meta | AdMob-first, Meta secondary |

## Main R&D Conclusion

| Area | Conclusion |
|---|---|
| Best network for your strongest traffic countries | Meta Audience Network |
| Best supporting network | AppLovin |
| Best Tier-1 stabilizer | AdMob |
| Overall recommendation | Run Meta aggressively in emerging markets, but keep AdMob and AppLovin active for balance |

## Recommended Mediation Strategy

### Preferred Setup

Use **bidding first**, not old-style waterfall, for:

- Meta
- AppLovin
- AdMob

If some networks are still on waterfall in your stack, keep waterfall as fallback only, not as your primary design.

### Suggested Mediation Group Split

| Group | Countries | Strategy |
|---|---|---|
| High Priority Emerging Markets | Indonesia, Brazil, India, Thailand, Tanzania | Meta-first, AppLovin second, AdMob active |
| Tier-1 / Higher Value Group | United States, Spain | AdMob-first, AppLovin second, Meta enabled |

## eCPM Floor Guidance

Only apply this if you are still using waterfall for some networks or placements.

| Region Type | Suggested Floor Direction |
|---|---|
| Indonesia / Brazil / India | Low floor to protect fill rate |
| United States / Spain | Higher floor is acceptable |

### Rough Floor Idea

| Region | Suggested Range |
|---|---|
| Indonesia / Brazil / India | INR 8 to INR 15 equivalent |
| United States / Spain | INR 50+ equivalent |

This is not a universal truth. It is only a starting point. Final floors should be based on:

- actual fill rate
- actual match rate
- actual eCPM by country
- daily revenue trend

## Action Plan

| Priority | Action | Why It Matters |
|---|---|---|
| 1 | Strengthen Meta bidding | Meta is likely the strongest revenue driver for your current geo mix |
| 2 | Keep AppLovin strong | AppLovin often performs well in Indonesia and Brazil |
| 3 | Use country-based mediation groups | Your traffic mix is not uniform; one setup for all countries is weak |
| 4 | Verify Meta setup fully | Wrong placement/app mapping kills fill immediately |
| 5 | Monitor mediation performance daily | You need real fill-rate and eCPM evidence, not assumptions |
| 6 | Optimize for fill in lower-value countries | High volume emerging traffic usually wins through fill, not aggressive floors |

## Meta-Specific Checklist

| Check | Expected State |
|---|---|
| Meta app linked correctly | Yes |
| Meta placements mapped correctly in AdMob | Yes |
| Meta bidding enabled in relevant groups | Yes |
| Meta adapter version updated | Yes |
| Meta app approved / active | Yes |
| Real device testing done | Yes |

If any one of these is wrong, Meta can underperform or not serve at all.

## Monitoring Recommendations

Use the following tools and reports regularly:

| Tool / Report | What To Check |
|---|---|
| AdMob Ad Inspector | Adapter responses, request path, mediation chain |
| AdMob Mediation Report | Fill rate, match rate, eCPM, impressions by network |
| Country-wise revenue reporting | Which countries actually monetize via Meta |
| Real device testing in top countries | Whether live demand exists where your traffic is strongest |

## Optimization Direction for Your Traffic Mix

| Traffic Type | Best Optimization Goal |
|---|---|
| Emerging markets | Maximize fill rate with strong Meta presence |
| Tier-1 countries | Balance fill rate and eCPM with AdMob + AppLovin |
| Mixed global traffic | Separate groups by geo instead of using one generic stack |

## Final Recommendation

| Category | Recommendation |
|---|---|
| Primary focus | Meta bidding + AppLovin |
| Best countries for Meta | Indonesia, Brazil, India, Thailand, Tanzania |
| Tier-1 support | AdMob should remain strong for United States and Spain |
| Biggest mistake to avoid | Using one generic mediation setup for all countries |

## Short Summary

- Your traffic profile is **Meta-friendly**.
- Meta should be a **top-priority mediation partner** for your strongest countries.
- AppLovin should be your **secondary strength network**.
- AdMob should remain important for **US and Spain**.
- The best setup is **country-based mediation groups with bidding-first configuration**.

