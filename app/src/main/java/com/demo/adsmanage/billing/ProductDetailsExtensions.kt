package com.demo.adsmanage.billing

import com.android.billingclient.api.ProductDetails

private fun ProductDetails.allPricingPhases(): List<ProductDetails.PricingPhase> {
    val offerDetails = subscriptionOfferDetails ?: return emptyList()
    return offerDetails.flatMap { it.pricingPhases.pricingPhaseList }
}

private fun ProductDetails.paidPricingPhase(): ProductDetails.PricingPhase? {
    val phases = allPricingPhases()
    return phases.firstOrNull { it.priceAmountMicros > 0L } ?: phases.firstOrNull()
}

private fun ProductDetails.trialPricingPhase(): ProductDetails.PricingPhase? {
    return allPricingPhases().firstOrNull { it.priceAmountMicros == 0L }
}

fun ProductDetails.productFormattedPrice(): String? = paidPricingPhase()?.formattedPrice

fun ProductDetails.productPriceAmountMicros(): Long? = paidPricingPhase()?.priceAmountMicros

fun ProductDetails.productCurrencyCode(): String? = paidPricingPhase()?.priceCurrencyCode

fun ProductDetails.productTrialPeriodIso(): String = trialPricingPhase()?.billingPeriod ?: ""
