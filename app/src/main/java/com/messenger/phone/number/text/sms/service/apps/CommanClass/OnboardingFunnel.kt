package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.content.Context
import android.util.Log
import com.demo.adsmanage.Commen.firebaseFunnel
import com.demo.adsmanage.Commen.funnelOn
import com.demo.adsmanage.Commen.onetimego
import com.demo.adsmanage.Commen.onetimelogOnboardingFunnelStep

private val onboardingStepsByFlow: Map<String, Set<String>> = mapOf(
    "0" to setOf(
        "First_permissionscreen",
        "First_phonestatescreen",
        "First_overlayscreen",
        "First_overlay_allowed",
        "First_language",
        "First_home",
        "First_setasdefaulted",
        "First_paywall",
        "First_home_again"
    ),
    "1" to setOf(
        "First_splash",
        "First_home",
        "First_setasdefaulted",
        "First_paywall",
        "First_language",
        "First_intershow",
        "First_interad",
        "First_home_again",
    ),
    "2" to setOf(
        "First_splash",
        "First_home",
        "First_setasdefaulted",
        "First_language",
        "First_paywall",
        "First_intershow",
        "First_interad",

//        "First_home_again"
    )
)

fun Context.logOnboardingFunnelStep(step: String) {
    val flow = baseConfig.OnBordingFlow_AB
    val allowedSteps = onboardingStepsByFlow[flow] ?: return
    if (step !in allowedSteps) return
    if (baseConfig.tryMarkOnboardingStepLogged(flow, step)) {
        firebaseFunnel(step)
    }
}


/*
fun Context.logOnboardingFunnelStep(step: String) {
    val flow = baseConfig.OnBordingFlow_AB
    val allowedSteps = onboardingStepsByFlow[flow] ?: return

    // Validate step first
    if (!allowedSteps.contains(step)) return



    // Prevent duplicate logging
    val isFirstTime = baseConfig.tryMarkOnboardingStepLogged(flow, step)
    if (!isFirstTime) {
        funnelOn = false
        return
    }

    firebaseFunnel(step)
}*/
