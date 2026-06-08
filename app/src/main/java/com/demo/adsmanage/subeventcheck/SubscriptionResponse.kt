package com.demo.adsmanage.subeventcheck

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


data class SubscriptionResponse(
    @SerializedName("activeSubscriptions")
    val activeSubscriptions: ActiveSubscriptionsDelegateDeserializer,
    @SerializedName("allExpirationDatesByProduct")
    val allExpirationDatesByProduct: Map<String, String>,
    @SerializedName("allPurchaseDatesByProduct")
    val allPurchaseDatesByProduct: Map<String, String>,
    @SerializedName("entitlements")
    val entitlements: Entitlements
)

data class ActiveSubscriptionsDelegate(
    @SerializedName("_value")
    val value: List<String>
)

data class Entitlements(
    val active: Map<String, SubscriptionInfo>,
    val all: Map<String, SubscriptionInfo>
)

data class SubscriptionInfo(
    val expirationDate: String,
    val identifier: String,
    val isActive: Boolean,
    val latestPurchaseDate: String,
    val originalPurchaseDate: String,
    val willRenew: Boolean,
    val unsubscribeDetectedAt: String?,
    val isTrial: Boolean
)