package com.demo.adsmanage.basemodule

import android.content.Intent
import android.util.Log
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.demo.adsmanage.Activity.PayAllSubsciptionExterimentActivity
import com.demo.adsmanage.Activity.SubActivityTwoplanActivity
import com.demo.adsmanage.Commen.Constants
import com.demo.adsmanage.Commen.Constants.IsOutAppPermission
import com.demo.adsmanage.Commen.Constants.isActivitychange
import com.demo.adsmanage.Commen.Constants.isAdsClicking
import com.demo.adsmanage.Commen.Constants.isAdsShowing
import com.demo.adsmanage.helper.logD

abstract class BaseActivity : AppCompatActivity() {
    @UiThread
    abstract fun getActivityContext(): AppCompatActivity
    val mActivity: AppCompatActivity
        get() {
            return getActivityContext()
        }

    @Suppress("PropertyName")
    val TAG: String = javaClass.simpleName
    override fun onStart() {
        super.onStart()
        if (Constants.misSubscription!!) {
            with(BaseSharedPreferences(this)) {
                logD(
                    "AdsManageclassTAG",
                    "mIS_SUBSCRIBED->$mIS_SUBSCRIBED \n isAdsShowing->$isAdsShowing \n IsOutAppPermission->$IsOutAppPermission \n" +
                            "mActivityOpen->$mActivityOpen \n isAdsClicking->$isAdsClicking \n mFirstTimeApp->$mFirstTimeApp \n mOneDay->$mOneDay \n" +
                            "mTwoDay->$mTwoDay"
                )
                if (!mIS_SUBSCRIBED!! && !isAdsShowing && !IsOutAppPermission && mActivityOpen!! && !isAdsClicking!! && !isActivitychange!!) {
                    val subscriptionTarget =
                        if (isCurrentPlan == "Experiment Offering") {
                            SubActivityTwoplanActivity::class.java
                        } else {
                            PayAllSubsciptionExterimentActivity::class.java
                        }
                    val intent = Intent(
                        mActivity,
                        subscriptionTarget
                    )

                    if (mFirstTimeApp == 0) {
                        mFirstTimeApp += 1
                    }

                    if (mOneDay!!) {
                        if (mFirstTimeApp == 1) {
                            mFirstTimeApp += 1
                            intent.putExtra("AppOpen", "BaseActivity")
                            startActivity(intent)
                            Log.d(
                                TAG,
                                "AdsManageclassTAG->:mFirstTimeApp==1  App Open On Background->${
                                    mFirstTimeApp
                                }"
                            )
                        } else if (mFirstTimeApp >= 4 && mFirstTimeApp != 1
                        ) {
                            if (mFirstTimeApp == 4) {
                                mFirstTimeApp += 1
                            } else {
                            }

                        } else {
                            if (mFirstTimeApp == 3 || mFirstTimeApp == 2
                            ) {
                                if (mFirstTimeApp == 3) {
                                    mOpenAdsload = true
                                }
                                mFirstTimeApp += 1
                                intent.putExtra("AppOpen", "BaseActivity")
                                startActivity(intent)
                                Log.d(
                                    TAG,
                                    "AdsManageclassTAG->: else App Open On Background->${mFirstTimeApp}"
                                )

                            } else {
                            }

                        }
                    } else if (mTwoDay!!) {
                        Log.d(
                            TAG,
                            "AdsManageclassTAG->:Second day App Open On Background->${
                                mFirstTimeApp
                            }"
                        )
                        if (mFirstTimeApp == 1) {
                            mFirstTimeApp += 1
                            if (mFirstTimeApp == 2) {
                                mOpenAdsload = true
                            }
                            intent.putExtra("AppOpen", "BaseActivity")
                            startActivity(intent)
                        } else {
                            mFirstTimeApp += 1
                        }
                    } else {
                    }
                } else {
                    isActivitychange = false
                }
            }
        }

    }
}
