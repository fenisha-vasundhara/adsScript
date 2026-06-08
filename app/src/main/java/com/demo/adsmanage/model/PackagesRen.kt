package com.demo.adsmanage.model

data class PackagesRen(
        val originalPrice: String,
        val freeTrialPeriod: mPeriod? =null,
        val title: String,
        val price: String,
        val description: String,
        val subscriptionPeriod: String,
        val sku: String,
        val presentedOfferingIdentifier: String,
    ) {
        override fun toString(): String {
            return "PackagesRen(originalPrice='$originalPrice', freeTrialPeriod='$freeTrialPeriod', title='$title', price='$price', description='$description', subscriptionPeriod='$subscriptionPeriod', sku='$sku', presentedOfferingIdentifier='$presentedOfferingIdentifier')"
        }
    }
    data class mPeriod(
        val value: Int=0,
        val unit: String="",
        val iso8601: String=""
    ) {
        override fun toString(): String {
            return "mPeriod(value=$value, unit=$unit, iso8601='$iso8601')"
        }
    }