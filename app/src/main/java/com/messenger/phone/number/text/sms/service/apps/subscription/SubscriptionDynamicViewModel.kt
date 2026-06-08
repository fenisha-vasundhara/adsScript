package com.messenger.phone.number.text.sms.service.apps.subscription

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.beloo.widget.chipslayoutmanager.util.log.Log
import com.demo.adsmanage.Activity.TermsActivity
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.Constants.BASIC_SKU
import com.demo.adsmanage.Commen.Constants.PREMIUM_LIFTIME
import com.demo.adsmanage.Commen.Constants.PREMIUM_SIX_SKU
import com.demo.adsmanage.Commen.Constants.PREMIUM_SKU
import com.demo.adsmanage.Commen.Constants.isbilingopen
import com.demo.adsmanage.Commen.Constants.mIsRevenuCat
import com.demo.adsmanage.Commen.Constants.packagerenlist
import com.demo.adsmanage.Commen.Constants.testpackagerenlist
import com.demo.adsmanage.SubscriptionBaseClass.manager.SubscriptionManager
import com.demo.adsmanage.basemodule.BaseSharedPreferences
import com.demo.adsmanage.billing.ProductPurchaseHelper.getProductInfo
import com.demo.adsmanage.helper.getSubTrial
import com.demo.adsmanage.helper.isOnline
import com.demo.adsmanage.helper.showToast
import com.messenger.phone.number.text.sms.service.apps.BuildConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.adjustAlpha
import com.messenger.phone.number.text.sms.service.apps.CommanClass.createOptionBackground
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getPaywallUnselectedColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getPaywallUnselectedColor1
import com.messenger.phone.number.text.sms.service.apps.CommanClass.getProperTextColor
import com.messenger.phone.number.text.sms.service.apps.CommanClass.logOnboardingFunnelStep
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.ActivityPaywallDynamicBinding
import com.messenger.phone.number.text.sms.service.apps.subscription.Paywall_dynamic_Activity.Companion.plans
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.models.StoreTransaction
//import com.vision.aftercall.sdk.ads.advanceAds.registerAdsConfigSubscribe
import kotlin.math.roundToInt

class SubscriptionDynamicViewModel(
    private val binding: ActivityPaywallDynamicBinding,
    private val mActivity: AppCompatActivity,
    private val liveDataPeriod: MutableLiveData<HashMap<String, String>>,
    private val liveDataPrice: MutableLiveData<HashMap<String, String>>,
    private val liveDataPriceMicro: MutableLiveData<HashMap<String, Long>>,
    private val subscriptionManager: SubscriptionManager,
    private val isSelecterdPlan: IsSelecterdPlan,
) : ViewModel() {

    interface IsSelecterdPlan {
        fun monMonthPlan()
        fun monYearPlan()
        fun monWeekPlan()
        fun monBackPress()
    }

    private enum class PlanType {
        MONTHLY,
        WEEKLY,
        YEARLY,
        LIFETIME,
    }

    private data class PlanData(
        val type: PlanType,
        val revenueCatUi: com.demo.adsmanage.model.PackagesRen? = null,
        val revenueCatPackage: Package? = null,
        val fallbackSku: String? = null,
        val fallbackPrice: String? = null,
        val fallbackPriceMicros: Long? = null,
        val fallbackTrialPeriod: String? = null,
    ) {
        val sku: String?
            get() = revenueCatUi?.sku ?: revenueCatPackage?.product?.sku ?: fallbackSku

        val displayPrice: String?
            get() = revenueCatUi?.originalPrice ?: fallbackPrice

        val priceAmount: Double?
            get() = revenueCatUi?.price?.toDoubleOrNull()
                ?: revenueCatPackage?.product?.price?.amountMicros?.div(1_000_000.0)
                ?: fallbackPriceMicros?.div(1_000_000.0)

        val trialIso: String?
            get() = revenueCatUi?.freeTrialPeriod?.iso8601
                ?: revenueCatPackage?.product?.defaultOption?.freePhase?.billingPeriod?.iso8601
                ?: fallbackTrialPeriod?.takeUnless { it.equals("Not Found", true) }

        fun hasTrial(): Boolean = !trialIso.isNullOrBlank()
    }

    private data class PlanViews(
        val rootId: Int,
        val cardId: Int,
        val titleId: Int,
        val mainPriceId: Int,
        val mainSuffixId: Int? = null,
        val subPriceId: Int? = null,
        val subSuffixId: Int? = null,
        val discountContainerId: Int? = null,
        val discountTextId: Int? = null,
        val extraLabelId: Int? = null,
    )

    private val planOrder = listOf(
        PlanType.MONTHLY,
        PlanType.WEEKLY,
        PlanType.YEARLY,
        PlanType.LIFETIME,
    )

    private val bottomPlanOrder = listOf(
        PlanType.MONTHLY,
        PlanType.WEEKLY,
        PlanType.LIFETIME,
    )

    private val topBarSectionIds = intArrayOf(
        R.id.img_close,
        R.id.textTitle,
        R.id.textTitleDis,
    )

    private val carouselSectionIds = intArrayOf(
        R.id.myviewpager,
        R.id.worm_dots_indicator,
    )

    private val plansSectionIds = intArrayOf(
        R.id.mCLYearLayout,
        R.id.mCLMonthLayout,
        R.id.mCLWeekLayout,
        R.id.mCLLIFELayout,
        R.id.year_tag,
    )

    private val footerSectionIds = intArrayOf(
        R.id.textsub,
        R.id.mCLUnlockLayout,
        R.id.txtBottom,
        R.id.mCLUnlockLayout2,
    )

    private val planViews = mapOf(
        PlanType.MONTHLY to PlanViews(
            rootId = R.id.mCLMonthLayout,
            cardId = R.id.monthconstraintid,
            titleId = R.id.txt_auto_renew_month,
            mainPriceId = R.id.txtMonthlyPrice,
            subPriceId = R.id.monthstaticprice,
            subSuffixId = R.id.monthstatic,
        ),
        PlanType.WEEKLY to PlanViews(
            rootId = R.id.mCLWeekLayout,
            cardId = R.id.weekconstraintid,
            titleId = R.id.txt_auto_renew_week,
            mainPriceId = R.id.txtweekPrice,
            subPriceId = R.id.weekstaticprice,
            subSuffixId = R.id.weekstatic,
        ),
        PlanType.YEARLY to PlanViews(
            rootId = R.id.mCLYearLayout,
            cardId = R.id.yearconstraintid,
            titleId = R.id.txt_auto_renew_year,
            mainPriceId = R.id.txtYearlyPrice,
            mainSuffixId = R.id.yeartxtcustom,
            subPriceId = R.id.yearstaticprice,
            subSuffixId = R.id.yesrstatic,
            discountContainerId = R.id.year_discount_show,
            discountTextId = R.id.txt_year_discount,
        ),
        PlanType.LIFETIME to PlanViews(
            rootId = R.id.mCLLIFELayout,
            cardId = R.id.lifeconstraintid,
            titleId = R.id.txt_auto_renew_life,
            mainPriceId = R.id.txtlifePrice,
            extraLabelId = R.id.lifetime,
        ),
    )

    @SuppressLint("AnnotateVersionCheck")
    fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    private lateinit var selectedBackground: Drawable
    private lateinit var unselectedBackground: Drawable
    private var selectedPlan: PlanData? = null
    private var availablePlans: List<PlanData> = emptyList()

    init {
        onMain()
    }

    private fun onMain() {
        val fillColor = binding.root.context.getPaywallUnselectedColor()
        val strokeColor = binding.root.context.getPaywallUnselectedColor1()
        val primaryColor =
            ContextCompat.getColor(binding.root.context, R.color.paywall_primry_color)

        selectedBackground = createOptionBackground(
            cornerSize = binding.root.context.resources.getDimension(com.intuit.sdp.R.dimen._12sdp),
            fillColor = fillColor,
            strokeColor = primaryColor,
            strokeWidth = binding.root.context.resources.getDimension(com.intuit.sdp.R.dimen._1sdp),
            showRipple = true,
            rippleColor = fillColor.adjustAlpha(0.5f),
        )
        unselectedBackground = createOptionBackground(
            cornerSize = binding.root.context.resources.getDimension(com.intuit.sdp.R.dimen._12sdp),
            fillColor = fillColor,
            strokeColor = strokeColor,
            strokeWidth = binding.root.context.resources.getDimension(com.intuit.ssp.R.dimen._1ssp),
            showRipple = true,
            rippleColor = fillColor.adjustAlpha(0.5f),
        )

        setSubScriptionUI()
        initListener()
    }

    @SuppressLint("SetTextI18n")
    fun setSubScriptionUI() {

        Log.e("SUB_PLAN", "❌ mIsRevenuCat ! $mIsRevenuCat")

        availablePlans = if (mIsRevenuCat == true) {
            buildRevenueCatPlans()
        } else {
            buildFallbackPlans()
        }
        availablePlans = applyDebugPlanFilter(availablePlans)
        if (availablePlans.isEmpty()) {
            binding.imgClose111.performClick()
//            Toast.makeText(mActivity, "network error", Toast.LENGTH_SHORT).show()
            Log.e("SUB_PLAN", "❌ No plans available! UI will be empty.")
        }else{
            mActivity.logOnboardingFunnelStep("First_paywall")
        }
        bindPlanCards()
        applyPlanLayout()
//
//        val defaultPlan = availablePlans.firstOrNull {
//            it.type == PlanType.MONTHLY
//        } ?: availablePlans.firstOrNull {
//            it.type == PlanType.WEEKLY
//        } ?: availablePlans.firstOrNull {
//            it.type == PlanType.YEARLY
//        } ?: availablePlans.firstOrNull {
//            it.type == PlanType.LIFETIME
//        }
        val defaultPlan = availablePlans.firstOrNull {
            it.type == PlanType.YEARLY
        } ?: availablePlans.firstOrNull {
            it.type == PlanType.MONTHLY
        } ?: availablePlans.firstOrNull {
            it.type == PlanType.WEEKLY
        } ?: availablePlans.firstOrNull {
            it.type == PlanType.LIFETIME
        }

        defaultPlan?.let { selectPlan(it) }
    }

    private fun initListener() {
        planViews.forEach { (type, views) ->
            binding.root.findViewById<View>(views.rootId).setOnClickListener {
                val heroPlan = singleBottomPlanHeroPlan()
                val targetPlan = if (type == PlanType.YEARLY && heroPlan != null) {
                    heroPlan
                } else {
                    availablePlans.firstOrNull { plan -> plan.type == type }
                }
                targetPlan?.let { plan ->
                    selectPlan(plan)
                }
            }
        }

        binding.txtPrivacy.setOnClickListener {
            val url = Constants.mPrivacyPolicyURL
            val customIntent = CustomTabsIntent.Builder()
            customIntent.setToolbarColor(ContextCompat.getColor(mActivity, R.color.white))
            openCustomTab(mActivity, customIntent.build(), Uri.parse(url))
        }

//        binding.txtTerms.setOnClickListener {
//            Constants.isActivitychange = true
//            mActivity.startActivity(Intent(mActivity, TermsActivity::class.java))
//        }

        binding.mCLUnlockLayout.setOnClickListener {
            val selected = selectedPlan ?: return@setOnClickListener
            if (!mActivity.isOnline) {
                mActivity.showToast(
                    mActivity.getString(R.string.please_check_connection),
                    Toast.LENGTH_SHORT,
                )
                return@setOnClickListener
            }

            if (isbilingopen) {
                return@setOnClickListener
            }
            isbilingopen = true

            if (mIsRevenuCat == true) {
                purchaseRevenueCatPlan(selected)
            } else {
                purchaseFallbackPlan(selected)
            }
        }
    }

    private fun bindPlanCards() {
        val heroPlan = singleBottomPlanHeroPlan()
        val weeklyPlan = availablePlans.firstOrNull { it.type == PlanType.WEEKLY }

        if (heroPlan != null) {
            planViews.forEach { (type, views) ->
                binding.root.findViewById<View>(views.rootId).visibility =
                    if (type == PlanType.YEARLY) View.VISIBLE else View.GONE
            }
            bindSinglePlanAsYearCard(heroPlan)
            binding.yearTag.visibility = View.GONE
            return
        }

        planViews.forEach { (type, views) ->
            val plan = availablePlans.firstOrNull { it.type == type }
            val root = binding.root.findViewById<View>(views.rootId)
            root.visibility = if (plan == null) View.GONE else View.VISIBLE

            if (plan == null) {
                views.discountContainerId?.let {
                    binding.root.findViewById<View>(it).visibility = View.GONE
                }
                if (type == PlanType.YEARLY) {
                    binding.yearTag.visibility = View.GONE
                }
                return@forEach
            }

            bindSinglePlan(plan, views, weeklyPlan)
        }

        binding.yearTag.visibility = if (availablePlans.any { it.type == PlanType.YEARLY }) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun calculateDiscount(weeklyPrice: Double, yearlyPrice: Double): Double {
        val totalWeeklyCost = weeklyPrice * 52
        val discountAmount = totalWeeklyCost - yearlyPrice
        val discountPercentage = (discountAmount / totalWeeklyCost) * 100
        return discountPercentage
    }

    fun calculateMonthlyDiscountFixedWeeks(
        weeklyPrice: Double,
        monthlyPrice: Double
    ): Pair<Double, Double> {
        val weeksPerMonth = 4.0
        val weeklyCostPerMonth = weeklyPrice * weeksPerMonth
        val discountAmount = weeklyCostPerMonth - monthlyPrice
        val discountPercentage = (discountAmount / weeklyCostPerMonth) * 100
        return Pair(discountAmount, discountPercentage)
    }

    private fun bindSinglePlanAsYearCard(plan: PlanData) {
        binding.txtAutoRenewYear.text = when (plan.type) {
            PlanType.MONTHLY -> mActivity.getString(R.string.Monthly_new_plan)
            PlanType.WEEKLY -> mActivity.getString(R.string.Weekly_new_plan)
            PlanType.LIFETIME -> mActivity.getString(R.string.lifetime_static)
            PlanType.YEARLY -> mActivity.getString(R.string.yearly_plan)
        }
        binding.yearDiscountShow.visibility = View.GONE
        binding.txtYearDiscount.text = ""
        binding.txtlifediscountid.visibility = View.GONE
        binding.yearconstraintid.background = createPlanCardBackground(true)

        when (plan.type) {
            PlanType.MONTHLY -> {
                binding.yearstaticprice.text = plan.weeklyEquivalentText(divisor = 4)
                binding.yesrstatic.visibility = View.VISIBLE
                binding.yesrstatic.text = mActivity.getString(R.string.week)
                binding.txtYearlyPrice.text = sanitizePrice(plan.displayPrice)
                binding.yeartxtcustom.text = mActivity.getString(R.string.month_static)

            }

            PlanType.WEEKLY -> {
                val weeklyPrice = sanitizePrice(plan.displayPrice)
                binding.yearstaticprice.text = weeklyPrice
                binding.yesrstatic.visibility = View.VISIBLE
                binding.yesrstatic.text = mActivity.getString(R.string.week_static)
                binding.txtYearlyPrice.text = weeklyPrice
                binding.yeartxtcustom.text = mActivity.getString(R.string.week_static)
            }

            PlanType.LIFETIME -> {
                binding.yearstaticprice.text = sanitizePrice(plan.displayPrice)
                binding.yesrstatic.visibility = View.GONE
                binding.txtYearlyPrice.text = sanitizePrice(plan.displayPrice)
                binding.yeartxtcustom.text = "/" + mActivity.getString(R.string.One_Time_Pay)
            }

            PlanType.YEARLY -> Unit
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindSinglePlan(plan: PlanData, views: PlanViews, weeklyPlan: PlanData?) {
        when (plan.type) {
            PlanType.MONTHLY -> {
                binding.txtMonthlyPrice.text = sanitizePrice(plan.displayPrice)
                binding.monthstaticprice.text = plan.weeklyEquivalentText(divisor = 4)
                binding.monthstatic.visibility = View.VISIBLE
            }

            PlanType.WEEKLY -> {
                val weeklyPrice = sanitizePrice(plan.displayPrice)
                binding.txtweekPrice.text = weeklyPrice
                binding.weekstaticprice.text = weeklyPrice
                binding.weekstatic.visibility = View.VISIBLE
            }

            PlanType.YEARLY -> {
                binding.txtYearlyPrice.text = sanitizePrice(plan.displayPrice)
                binding.yearstaticprice.text = plan.weeklyEquivalentText(divisor = 52)
                binding.yesrstatic.visibility = View.VISIBLE

                val discountText = getDiscountText(weeklyPlan, plan)
                binding.yearDiscountShow.visibility =
                    if (discountText == null) View.GONE else View.VISIBLE
                binding.txtYearDiscount.text = discountText ?: ""
            }

            PlanType.LIFETIME -> {
                binding.txtlifePrice.text = sanitizePrice(plan.displayPrice)
                binding.lifetime.visibility = View.VISIBLE
            }
        }
    }

    private fun singleBottomPlanHeroPlan(): PlanData? {
        val hasYearly = availablePlans.any { it.type == PlanType.YEARLY }
        if (hasYearly) {
            return null
        }
        val bottomPlans = availablePlans.filter { it.type in bottomPlanOrder }
        return bottomPlans.singleOrNull()
    }


    private fun clearVerticalConstraints(constraintSet: ConstraintSet, viewIds: IntArray) {
        viewIds.forEach { id ->
            constraintSet.clear(id, ConstraintSet.TOP)
            constraintSet.clear(id, ConstraintSet.BOTTOM)
        }
    }

    private fun alignBottomPlanInnerContentWithoutYear() {

    }

    private fun updateYearPlusSingleBottomPlanInsets(
        bottomVisible: List<PlanType>,
        visibleYear: Boolean,
        horizontalInset: Int,
    ) {
        val singleBottomType = bottomVisible.singleOrNull()

        bottomPlanOrder.forEach { type ->
            val views = planViews.getValue(type)
            val rootView = binding.root.findViewById<View>(views.rootId)
            val cardView = binding.root.findViewById<View>(views.cardId)
            val rootParams =
                rootView.layoutParams as? ViewGroup.MarginLayoutParams ?: return@forEach
            val cardParams =
                cardView.layoutParams as? ViewGroup.MarginLayoutParams ?: return@forEach

            if (visibleYear && singleBottomType == type) {
                rootParams.marginStart = 0
                rootParams.marginEnd = 0
                cardParams.marginStart = horizontalInset
                cardParams.marginEnd = horizontalInset
            } else {
                rootParams.marginStart = horizontalInset
                rootParams.marginEnd = horizontalInset
                cardParams.marginStart = 0
                cardParams.marginEnd = 0
            }

            rootView.layoutParams = rootParams
            cardView.layoutParams = cardParams
        }
    }

    private fun applyPlanLayout() {

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.main)
        val density = binding.root.resources.displayMetrics.density
        val screenHeightDp = binding.root.resources.displayMetrics.heightPixels / density
        val isCompactScreen = screenHeightDp <= 760f
        val spacingSmall =
            binding.root.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._4sdp)
        val spacingMedium =
            binding.root.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
        val spacingLarge =
            binding.root.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._25sdp)
        val pagerBottomSpacing =
            binding.root.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._16sdp)
        val dotsTopSpacing =
            binding.root.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp)
        val titleTopBaseMargin =
            binding.root.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
        val pagerHeightPercent = if (isCompactScreen) 0.30f else 0.34f

        val bottomPlanCardHeight = if (isCompactScreen) {
            binding.root.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._80sdp)
        } else {
            binding.root.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._80sdp)
        }
        val planToSummaryGap = spacingMedium

        clearVerticalConstraints(constraintSet, carouselSectionIds)
//        clearVerticalConstraints(constraintSet1, plansSectionIds)


        constraintSet.connect(
            R.id.myviewpager,
            ConstraintSet.TOP,
            R.id.textTitleDis,
            ConstraintSet.BOTTOM,
        )
        constraintSet.connect(
            R.id.myviewpager,
            ConstraintSet.BOTTOM,
            R.id.worm_dots_indicator,
            ConstraintSet.BOTTOM,
        )
        constraintSet.connect(
            R.id.worm_dots_indicator,
            ConstraintSet.TOP,
            R.id.myviewpager,
            ConstraintSet.BOTTOM,
        )
        constraintSet.constrainPercentHeight(R.id.myviewpager, pagerHeightPercent)
        constraintSet.setVerticalBias(R.id.myviewpager, 0.5f)
        constraintSet.connect(
            R.id.worm_dots_indicator,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
        )
        constraintSet.connect(
            R.id.worm_dots_indicator,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END,
        )
        constraintSet.setMargin(R.id.worm_dots_indicator, ConstraintSet.TOP, dotsTopSpacing)

        planViews.values.forEach { views ->
            constraintSet.clear(views.rootId, ConstraintSet.START)
            constraintSet.clear(views.rootId, ConstraintSet.END)
            constraintSet.clear(views.rootId, ConstraintSet.TOP)
            constraintSet.clear(views.rootId, ConstraintSet.BOTTOM)
            constraintSet.constrainWidth(views.rootId, 0)
            constraintSet.setMargin(views.rootId, ConstraintSet.START, 0)
            constraintSet.setMargin(views.rootId, ConstraintSet.END, 0)
        }

        val heroPlan = singleBottomPlanHeroPlan()
        val visibleYear = availablePlans.any { it.type == PlanType.YEARLY } || heroPlan != null
        val bottomVisible = if (heroPlan != null) {
            emptyList()
        } else {
            bottomPlanOrder.filter { type ->
                availablePlans.any { it.type == type }
            }
        }
        updateYearPlusSingleBottomPlanInsets(bottomVisible, visibleYear, spacingMedium)
        val hasSinglePlanRow = visibleYear.xor(bottomVisible.isNotEmpty())
        val topBarExtraMargin = if (hasSinglePlanRow) spacingLarge else 0
        constraintSet.setMargin(R.id.img_close, ConstraintSet.TOP, topBarExtraMargin)
        constraintSet.setMargin(
            R.id.textTitle,
            ConstraintSet.TOP,
            titleTopBaseMargin + topBarExtraMargin + topBarExtraMargin
        )
        val firstBottomId = bottomVisible.firstOrNull()?.let { planViews.getValue(it).rootId }
        val plansTopTargetId = when {
            visibleYear -> R.id.mCLYearLayout
            firstBottomId != null -> firstBottomId
            else -> R.id.textsub
        }



        if (visibleYear) {
            constraintSet.connect(
                R.id.mCLYearLayout,
                ConstraintSet.TOP,
                R.id.worm_dots_indicator,
                ConstraintSet.BOTTOM,
            )
            constraintSet.setMargin(R.id.mCLYearLayout, ConstraintSet.TOP, spacingSmall)
            constraintSet.connect(
                R.id.mCLYearLayout,
                ConstraintSet.BOTTOM,
                firstBottomId ?: R.id.textsub,
                ConstraintSet.TOP,
            )
            constraintSet.setMargin(
                R.id.mCLYearLayout,
                ConstraintSet.BOTTOM,
                if (firstBottomId != null) spacingSmall else planToSummaryGap,
            )
            constraintSet.connect(
                R.id.mCLYearLayout,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
            )
            constraintSet.connect(
                R.id.mCLYearLayout,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
            )
            constraintSet.connect(
                R.id.year_tag,
                ConstraintSet.TOP,
                R.id.mCLYearLayout,
                ConstraintSet.TOP,
            )
            constraintSet.connect(
                R.id.year_tag,
                ConstraintSet.END,
                R.id.mCLYearLayout,
                ConstraintSet.END,
            )
        }

        constraintSet.connect(
            R.id.worm_dots_indicator,
            ConstraintSet.BOTTOM,
            plansTopTargetId,
            ConstraintSet.TOP,
        )
        constraintSet.setMargin(R.id.worm_dots_indicator, ConstraintSet.BOTTOM, pagerBottomSpacing)

        if (bottomVisible.isNotEmpty()) {
            val topAnchorId = if (visibleYear) R.id.mCLYearLayout else R.id.worm_dots_indicator
            val chainIds = bottomVisible.mapNotNull { planViews[it]?.rootId }.toIntArray()

            bottomVisible.forEach { type ->
                val rootId = planViews.getValue(type).rootId
//                constraintSet.connect(rootId, ConstraintSet.TOP, topAnchorId, ConstraintSet.BOTTOM)
                constraintSet.connect(rootId, ConstraintSet.BOTTOM, R.id.textsub, ConstraintSet.TOP)
//                constraintSet.setMargin(rootId, ConstraintSet.TOP, spacingSmall)
//                constraintSet.setMargin(rootId, ConstraintSet.BOTTOM, planToSummaryGap)
                constraintSet.constrainHeight(rootId, bottomPlanCardHeight)
                constraintSet.setVerticalBias(rootId, 1f)
            }

            if (chainIds.size == 1) {
                constraintSet.connect(
                    chainIds.first(),
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START,
                )
                constraintSet.connect(
                    chainIds.first(),
                    ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.END,
                )
                constraintSet.setHorizontalBias(chainIds.first(), 0.5f)
            } else {
                chainIds.forEachIndexed { index, id ->
                    if (index == 0) {
                        constraintSet.connect(
                            id,
                            ConstraintSet.START,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.START
                        )
                        constraintSet.setMargin(id, ConstraintSet.START, spacingMedium)
                    } else {
                        constraintSet.connect(
                            id,
                            ConstraintSet.START,
                            chainIds[index - 1],
                            ConstraintSet.END
                        )
                        constraintSet.setMargin(id, ConstraintSet.START, spacingSmall)
                    }

                    if (index == chainIds.lastIndex) {
                        constraintSet.connect(
                            id,
                            ConstraintSet.END,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.END
                        )
                        constraintSet.setMargin(id, ConstraintSet.END, spacingMedium)
                    } else {
                        constraintSet.connect(
                            id,
                            ConstraintSet.END,
                            chainIds[index + 1],
                            ConstraintSet.START
                        )
                        constraintSet.setMargin(id, ConstraintSet.END, spacingSmall)
                    }
                }
                constraintSet.setHorizontalChainStyle(chainIds.first(), ConstraintSet.CHAIN_SPREAD)
            }

            val widthPercent = when (chainIds.size) {
                1 -> 0.82f
                2 -> 0.44f
                else -> 0.28f
            }

            chainIds.forEach { id ->
                if (visibleYear && chainIds.size == 1) {
                    constraintSet.constrainWidth(id, 0)
                } else {
                    constraintSet.constrainPercentWidth(id, widthPercent)
                }
            }
        }

        if (heroPlan == null && !availablePlans.any { it.type == PlanType.YEARLY }) {
            alignBottomPlanInnerContentWithoutYear()
        }

        if (heroPlan == null && !availablePlans.any { it.type == PlanType.YEARLY }) {
            binding.yearTag.visibility = View.GONE
        }

        constraintSet.applyTo(binding.main)
    }

    @SuppressLint("SetTextI18n")
    private fun selectPlan(plan: PlanData) {
        selectedPlan = plan
        plans = plan.sku.orEmpty()

        val textColor = binding.root.context.getProperTextColor()
        val textColorlightPro = textColor.adjustAlpha(0.25f)

        val primaryColor =
            ContextCompat.getColor(binding.root.context, R.color.paywall_primry_color)

        planViews.forEach { (type, views) ->
            val card = binding.root.findViewById<View>(views.cardId)
            val isSelected = plan.type == type
            card.background = createPlanCardBackground(isSelected)

            val mainPriceView =
                binding.root.findViewById<android.widget.TextView>(views.mainPriceId)
            val mainSuffixView =
                views.mainSuffixId?.let { binding.root.findViewById<android.widget.TextView>(it) }
            val subSuffixView =
                views.subSuffixId?.let { binding.root.findViewById<android.widget.TextView>(it) }
            val subPriceView =
                views.subPriceId?.let { binding.root.findViewById<android.widget.TextView>(it) }
            val extraLabelView =
                views.extraLabelId?.let { binding.root.findViewById<android.widget.TextView>(it) }

            val mainTextColor = when {
                type == PlanType.YEARLY -> textColor
//                type == PlanType.MONTHLY -> textColorlightPro
//                type == PlanType.WEEKLY -> textColorlightPro
                type == PlanType.LIFETIME && isSelected -> primaryColor
                else -> textColor
            }
            if (type == PlanType.MONTHLY){
//                mainPriceView.setTextColor(textColorlightPro)

            }else{
                mainPriceView.setTextColor(mainTextColor)

            }

            mainSuffixView?.setTextColor(mainTextColor)
            subPriceView?.setTextColor(if (type != PlanType.LIFETIME && isSelected) primaryColor else textColor)
            extraLabelView?.setTextColor(textColorlightPro)


        }

        singleBottomPlanHeroPlan()?.let { heroPlan ->
            if (heroPlan.type == plan.type) {
                binding.yearconstraintid.background = createPlanCardBackground(true)
            }
        }

        updateSelectedPlanTexts(plan)
    }

    private fun createPlanCardBackground(isSelected: Boolean): Drawable {
        val source = if (isSelected) selectedBackground else unselectedBackground
        return source.constantState?.newDrawable()?.mutate() ?: source.mutate()
    }

    @SuppressLint("SetTextI18n")
    private fun updateSelectedPlanTexts(plan: PlanData) {
        binding.txtUnlockKriadl.text = getContinueText(plan)
        binding.txtBottom.text = if (plan.type == PlanType.LIFETIME) {
            mActivity.getString(R.string.One_Time_Pay)
        } else {
            mActivity.getString(R.string.subscription_will_auto_renew_cancel_anytime)
        }

        binding.textsub.text =
            mActivity.getString(R.string.full_access_for) + " " + if (plan.hasTrial()) {
                val trialText = mActivity.getSubTrial(plan.trialIso.orEmpty()).orEmpty()
                "${mActivity.getString(R.string._7_days)} ${mActivity.getString(R.string.FREE_trial_new_new)} ${
                    mActivity.getString(R.string.than_price_ex)
                } ${sanitizePrice(plan.displayPrice)}${getSummarySuffix(plan.type)}"
                    .replace(mActivity.getString(R.string._7_days), trialText)
            } else {
                when (plan.type) {
                    PlanType.MONTHLY -> mActivity.getString(
                        R.string.monthly_price,
                        sanitizePrice(plan.displayPrice),
                    )

                    PlanType.WEEKLY -> mActivity.getString(
                        R.string.week_price,
                        sanitizePrice(plan.displayPrice),
                    )

                    PlanType.YEARLY -> mActivity.getString(
                        R.string.year_price,
                        sanitizePrice(plan.displayPrice),
                    )

                    PlanType.LIFETIME -> mActivity.getString(
                        R.string.lifetime_price,
                        sanitizePrice(plan.displayPrice),
                    )
                }
            }
    }

    private fun buildRevenueCatPlans(): List<PlanData> {
        val uiByType = packagerenlist.mapNotNull { item ->
            resolvePlanType(item.sku, null, item.subscriptionPeriod)?.let { type -> type to item }
        }.toMap()

        val packageByType = testpackagerenlist.mapNotNull { item ->
            resolvePlanType(
                item.product.sku,
                item.packageType,
                item.product.period?.iso8601
            )?.let { type ->
                type to item
            }
        }.toMap()

        BASIC_SKU = uiByType[PlanType.MONTHLY]?.sku ?: packageByType[PlanType.MONTHLY]?.product?.sku
                ?: BASIC_SKU
        PREMIUM_SIX_SKU =
            uiByType[PlanType.WEEKLY]?.sku ?: packageByType[PlanType.WEEKLY]?.product?.sku
                    ?: PREMIUM_SIX_SKU
        PREMIUM_SKU = uiByType[PlanType.YEARLY]?.sku ?: packageByType[PlanType.YEARLY]?.product?.sku
                ?: PREMIUM_SKU
        PREMIUM_LIFTIME =
            uiByType[PlanType.LIFETIME]?.sku ?: packageByType[PlanType.LIFETIME]?.product?.sku
                    ?: PREMIUM_LIFTIME

        return planOrder.mapNotNull { type ->
            val uiItem = uiByType[type]
            val rcPackage = packageByType[type]
            if (uiItem == null && rcPackage == null) {
                null
            } else {
                PlanData(
                    type = type,
                    revenueCatUi = uiItem,
                    revenueCatPackage = rcPackage,
                )
            }
        }
    }

    private fun applyDebugPlanFilter(plans: List<PlanData>): List<PlanData> {
//        if (!BuildConfig.DEBUG) {
            return plans
//        }


        /*

//        "monthly"
//        "weekly"
//        "yearly"
//        "lifetime"
//        "monthly,weekly"
//        "monthly,yearly"
//        "yearly,lifetime"
//        "monthly,weekly,yearly"
//        "monthly,weekly,lifetime"
//        "monthly,weekly,yearly,lifetime"



        val rawFilter = ("lifetime,weekly").trim().orEmpty()
//        val rawFilter = mActivity.intent?.getStringExtra(DEBUG_PLAN_FILTER)?.trim().orEmpty()
        if (rawFilter.isBlank()) {
            return plans
        }

        val allowedTypes = rawFilter.split(",")
            .mapNotNull { token -> parseDebugPlanType(token.trim()) }
            .toSet()

        if (allowedTypes.isEmpty()) {
            return plans
        }

        return plans.filter { it.type in allowedTypes }


         */
    }

    private fun buildFallbackPlans(): List<PlanData> {
        val plans = mutableListOf<PlanData>()

        BASIC_SKU.getProductInfo?.let { product ->
            plans += PlanData(
                type = PlanType.MONTHLY,
                fallbackSku = BASIC_SKU,
                fallbackPrice = liveDataPrice.value?.get(BASIC_SKU),
                fallbackPriceMicros = liveDataPriceMicro.value?.get(BASIC_SKU),
                fallbackTrialPeriod = product.freeTrialPeriod,
            )
        }

        PREMIUM_SIX_SKU.getProductInfo?.let { product ->
            plans += PlanData(
                type = PlanType.WEEKLY,
                fallbackSku = PREMIUM_SIX_SKU,
                fallbackPrice = liveDataPrice.value?.get(PREMIUM_SIX_SKU),
                fallbackPriceMicros = liveDataPriceMicro.value?.get(PREMIUM_SIX_SKU),
                fallbackTrialPeriod = product.freeTrialPeriod,
            )
        }

        PREMIUM_SKU.getProductInfo?.let { product ->
            plans += PlanData(
                type = PlanType.YEARLY,
                fallbackSku = PREMIUM_SKU,
                fallbackPrice = liveDataPrice.value?.get(PREMIUM_SKU),
                fallbackPriceMicros = liveDataPriceMicro.value?.get(PREMIUM_SKU),
                fallbackTrialPeriod = product.freeTrialPeriod,
            )
        }

        return plans
    }

    private fun purchaseRevenueCatPlan(plan: PlanData) {
        val purchasePackage = plan.revenueCatPackage
        if (purchasePackage == null) {
            isbilingopen = false
            Toast.makeText(
                mActivity,
                mActivity.getString(R.string.some_time_after_try_again),
                Toast.LENGTH_SHORT,
            ).show()
            return
        }

        Purchases.sharedInstance.purchasePackage(
            mActivity,
            purchasePackage,
            object : PurchaseCallback {
                override fun onCompleted(
                    storeTransaction: StoreTransaction,
                    customerInfo: CustomerInfo,
                ) {
                    BaseSharedPreferences(mActivity).mIS_SUBSCRIBED = true
//                    registerAdsConfigSubscribe(true)
                    isbilingopen = false
                    isSelecterdPlan.monBackPress()
                }

                override fun onError(error: PurchasesError, userCancelled: Boolean) {
                    isbilingopen = false
                }
            },
        )
    }

    private fun purchaseFallbackPlan(plan: PlanData) {
        when (plan.type) {
            PlanType.MONTHLY -> isSelecterdPlan.monMonthPlan()
            PlanType.WEEKLY -> isSelecterdPlan.monWeekPlan()
            PlanType.YEARLY -> isSelecterdPlan.monYearPlan()
            PlanType.LIFETIME -> isSelecterdPlan.monWeekPlan()
        }
        isbilingopen = false
    }

    private fun resolvePlanType(
        sku: String?,
        packageType: PackageType?,
        periodIso: String?,
    ): PlanType? {
        when (packageType) {
            PackageType.MONTHLY -> return PlanType.MONTHLY
            PackageType.WEEKLY -> return PlanType.WEEKLY
            PackageType.ANNUAL -> return PlanType.YEARLY
            PackageType.LIFETIME -> return PlanType.LIFETIME
            else -> Unit
        }

        val normalized = listOfNotNull(sku, periodIso).joinToString(" ").lowercase()
        return when {
            "lifetime" in normalized || "life" in normalized -> PlanType.LIFETIME
            "year" in normalized || "annual" in normalized || "p1y" in normalized -> PlanType.YEARLY
            "week" in normalized || "p1w" in normalized -> PlanType.WEEKLY
            "month" in normalized || "p1m" in normalized -> PlanType.MONTHLY
            else -> null
        }
    }

    private fun PlanData.weeklyEquivalentText(divisor: Int): String {
        val amount = priceAmount ?: return sanitizePrice(displayPrice)
        val weeklyValue = (amount / divisor).roundToInt()
        val prefix = sanitizePrice(displayPrice).firstOrNull()?.toString().orEmpty()
        return "$prefix$weeklyValue"
    }

    private fun getDiscountText(weeklyPlan: PlanData?, targetPlan: PlanData): String? {
        val weeklyAmount = weeklyPlan?.priceAmount ?: return null
        val targetAmount = targetPlan.priceAmount ?: return null
        val multiplier = when (targetPlan.type) {
            PlanType.MONTHLY -> 4.0
            PlanType.YEARLY -> 52.0
            else -> return null
        }

        val regularCost = weeklyAmount * multiplier
        if (regularCost <= 0.0 || targetAmount >= regularCost) {
            return null
        }

        val discount = ((regularCost - targetAmount) / regularCost * 100).roundToInt()
        return if (discount > 0) "Save $discount%" else null
    }

    private fun getContinueText(plan: PlanData): String {
        if (!plan.hasTrial()) {
            return mActivity.getString(R.string.continue_txt)
        }

        val trialText = mActivity.getSubTrial(plan.trialIso.orEmpty())
        return "${mActivity.getString(R.string.continue_txt_22)}$trialText${mActivity.getString(R.string.continue_txt_222)}"
    }

    private fun getSummarySuffix(type: PlanType): String {
        return when (type) {
            PlanType.MONTHLY -> mActivity.getString(R.string.month_static)
            PlanType.WEEKLY -> mActivity.getString(R.string.week_static)
            PlanType.YEARLY -> mActivity.getString(R.string.year_static)
            PlanType.LIFETIME -> mActivity.getString(R.string.One_Time_Pay)
        }
    }

    private fun sanitizePrice(price: String?): String {
        return price?.replace(".00", "") ?: ""
    }

    private fun parseDebugPlanType(value: String): PlanType? {
        return when (value.lowercase()) {
            "month", "monthly" -> PlanType.MONTHLY
            "week", "weekly" -> PlanType.WEEKLY
            "year", "yearly", "annual" -> PlanType.YEARLY
            "life", "lifetime" -> PlanType.LIFETIME
            else -> null
        }
    }

    fun openCustomTab(activity: Activity, customTabsIntent: CustomTabsIntent, uri: Uri?) {
        val packageName = "com.android.chrome"
        customTabsIntent.intent.setPackage(packageName)
        if (isChromeCustomTabsSupported(activity, customTabsIntent.intent)) {
            customTabsIntent.launchUrl(activity, uri!!)
        } else {
            activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    private fun isChromeCustomTabsSupported(context: Context, intent: Intent): Boolean {
        val activities =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return activities.isNotEmpty()
    }

    fun calculateDiscountPercentage(originalPrice: Double, discountedPrice: Double): Int {
        val discountAmount = originalPrice - discountedPrice
        val discountPercentage = (discountAmount / originalPrice) * 100
        return discountPercentage.roundToInt()
    }

    fun getDiscount(weekPlan: Package, product: Package): Triple<Int, Int, String> {
        val price = weekPlan.product.price.amountMicros.div(1_000_000)
        return when (product.packageType) {
            PackageType.MONTHLY -> getPair(4.3f, price, product, "P1M")
            PackageType.SIX_MONTH -> getPair(25.8f, price, product, "P6M")
            PackageType.ANNUAL -> getPair(52f, price, product, "P1Y")
            else -> Triple(0, 0, "")
        }
    }

    private fun getPair(
        time: Float,
        price: Long,
        product: Package,
        dur: String,
    ): Triple<Int, Int, String> {
        val highPrice = price * time.roundToInt()
        val productPrice = product.product.price.amountMicros.div(1_000_000).toInt()
        val less = highPrice - productPrice
        val discount = if (highPrice != 0L) ((less / highPrice.toFloat()) * 100).roundToInt() else 0
        return Triple(highPrice.toInt(), discount, dur)
    }

    companion object {
        const val DEBUG_PLAN_FILTER = "debug_plan_filter"
    }
}
