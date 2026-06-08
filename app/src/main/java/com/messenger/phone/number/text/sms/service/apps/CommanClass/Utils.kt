package com.messenger.phone.number.text.sms.service.apps.CommanClass

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.provider.Telephony
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.simplemobiletools.commons.helpers.isQPlus

fun AppCompatActivity.requestDefaultApp(onDefaultAppResult: ActivityResultLauncher<Intent>) {
    val intent = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        val roleManager = getSystemService(RoleManager::class.java)
        roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
    } else {
        val setSmsAppIntent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
        setSmsAppIntent
    }
    onDefaultAppResult.launch(intent)
}
const val PERMISSION_RECEIVE_SMS = 23 // or next available ID
/*******************    💫 Codegeex Suggestion    *******************/
fun Activity.roleDefaultCheck(): Boolean {
    return  if (isQPlus()) {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager?.isRoleAvailable(RoleManager.ROLE_SMS) == true) {
            roleManager.isRoleHeld(RoleManager.ROLE_SMS)
        } else {
            false
        }
    } else {
        false
        Telephony.Sms.getDefaultSmsPackage(this) == packageName
    }

}
/****************  4d74da156cb34dea90188dff837c5051  ****************/

val Context.isFLow1_2: Boolean
    get() = baseConfig.OnBordingFlow_AB == "2" || baseConfig.OnBordingFlow_AB == "1"

val Context.isFLow1: Boolean
    get() = baseConfig.OnBordingFlow_AB == "1"

val Context.isFLow2: Boolean
    get() = baseConfig.OnBordingFlow_AB == "2"

val Context.isFLow0: Boolean
    get() = baseConfig.OnBordingFlow_AB == "0"

// fun createOptionBackground(
//    cornerSize: Float,
//    fillColor: Int,
//    strokeWidth: Float,
//    strokeColor: Int,
//    isTop: Boolean = false,
//    isBottom: Boolean = false,
//    cornerSizeMultiplier: Float = 3f,
//): MaterialShapeDrawable {
//
//
//    val shapeAppearanceModel = when {
//        isTop -> ShapeAppearanceModel.builder()
//            .setTopLeftCornerSize(cornerSize * cornerSizeMultiplier)
//            .setTopRightCornerSize(cornerSize * cornerSizeMultiplier)
//            .setBottomLeftCornerSize(cornerSize)
//            .setBottomRightCornerSize(cornerSize)
//            .build()
//
//        isBottom -> ShapeAppearanceModel.builder()
//            .setTopLeftCornerSize(cornerSize)
//            .setTopRightCornerSize(cornerSize)
//            .setBottomLeftCornerSize(cornerSize * cornerSizeMultiplier)
//            .setBottomRightCornerSize(cornerSize * cornerSizeMultiplier)
//            .build()
//
//        else -> ShapeAppearanceModel.builder()
//            .setAllCornerSizes(cornerSize)
//            .build()
//    }
//
//    return MaterialShapeDrawable(shapeAppearanceModel).apply {
//        this.fillColor = ColorStateList.valueOf(fillColor)
//        setStroke(strokeWidth, strokeColor)
//    }
//}


fun createOptionBackground(
    cornerSize: Float = 0f,
    fillColor: Int = Color.WHITE,
    strokeWidth: Float = 0f,
    strokeColor: Int = Color.BLACK,
    rippleColor: Int = Color.WHITE,
    showRipple: Boolean = false,
    isTop: Boolean = false,
    isBottom: Boolean = false,
    cornerSizeMultiplier: Float = 3f,
    useCustomCorners: Boolean = false,
    applyCornerMultiplierForCustomSizes: Boolean = false,
    topRightCornerSize: Float = 0f,
    topLeftCornerSize: Float = 0f,
    bottomRightCornerSize: Float = 0f,
    bottomLeftCornerSize: Float = 0f,
): Drawable {

    val shapeAppearanceModel = when {

        useCustomCorners -> {
            ShapeAppearanceModel.builder()
                .setTopLeftCornerSize(topLeftCornerSize * if (applyCornerMultiplierForCustomSizes) cornerSizeMultiplier else 1f)
                .setTopRightCornerSize(topRightCornerSize * if (applyCornerMultiplierForCustomSizes) cornerSizeMultiplier else 1f)
                .setBottomLeftCornerSize(bottomLeftCornerSize * if (applyCornerMultiplierForCustomSizes) cornerSizeMultiplier else 1f)
                .setBottomRightCornerSize(bottomRightCornerSize * if (applyCornerMultiplierForCustomSizes) cornerSizeMultiplier else 1f)
                .build()
        }


        isTop -> ShapeAppearanceModel.builder()
            .setTopLeftCornerSize(cornerSize * cornerSizeMultiplier)
            .setTopRightCornerSize(cornerSize * cornerSizeMultiplier)
            .setBottomLeftCornerSize(cornerSize)
            .setBottomRightCornerSize(cornerSize)
            .build()

        isBottom -> ShapeAppearanceModel.builder()
            .setTopLeftCornerSize(cornerSize)
            .setTopRightCornerSize(cornerSize)
            .setBottomLeftCornerSize(cornerSize * cornerSizeMultiplier)
            .setBottomRightCornerSize(cornerSize * cornerSizeMultiplier)
            .build()

        else -> ShapeAppearanceModel.builder()
            .setAllCornerSizes(cornerSize)
            .build()
    }

    val contentDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {

        this.fillColor = ColorStateList.valueOf(fillColor)
        setStroke(strokeWidth, strokeColor)
    }


    if (showRipple.not()) {
        return contentDrawable
    }

    // Mask ensures ripple respects rounded corners
    val maskDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
        setTint(Color.WHITE)
    }

    return RippleDrawable(
        ColorStateList.valueOf(rippleColor),
        contentDrawable,
        maskDrawable
    )
}

fun applyPulseColor(@ColorInt color: Int, vararg views: View) {
    views.forEach { it.background.setTint(color) }
}