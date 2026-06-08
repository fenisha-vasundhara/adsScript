package com.graphic.design.digital.businessadsmaker.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.messenger.phone.number.text.sms.service.apps.Dialog.BaseDialogFragment
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.DailogLayoutEditBinding

class AttachmentDialog(var mainbackground: ConstraintLayout) : BaseDialogFragment() {

    companion object {
        private const val TAG = "AttachmentDialog"
    }

    private lateinit var binding: DailogLayoutEditBinding


    private var mEditDialogExitCallback: (() -> Unit)? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DailogLayoutEditBinding.inflate(inflater, container, false)
        dialog?.setOnShowListener {
            Log.d(TAG, "onCreateView: ")
            binding.rootViewDialog.transitionToEnd()
            binding.cardRoot.animate()?.alphaBy(1f)?.setDuration(200)?.setStartDelay(400)?.start()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val text = requireArguments().getString("text")
//
//        binding.etText.setText(text)
//        binding.etText.setSelection(binding.etText.length())
        binding.btnNagative.setOnClickListener {
            onDismiss1(true)
        }

    }

    override fun onStart() {
        super.onStart()
        val activity = activity
        if (activity != null && !activity.isFinishing) {
            val dialog: Dialog = requireDialog()
            dialog.setCanceledOnTouchOutside(true)
            val width = (resources.displayMetrics.widthPixels * 0.5).toInt() // 50% of screen width
//            dialog.window?.setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
//            )
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            dialog.window?.setBackgroundDrawable(BitmapDrawable(getScreenShotFromView(mainbackground)))

            val layoutParams = dialog.window?.attributes
            layoutParams?.gravity = Gravity.BOTTOM or Gravity.END
            dialog.window?.attributes = layoutParams as WindowManager.LayoutParams
        } else {
            dismiss() // Or handle appropriately
        }
    }

    override fun dismiss() {
        super.dismiss()
        mEditDialogExitCallback?.invoke()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        mEditDialogExitCallback?.invoke()
    }

    fun onDismiss1(isAnim: Boolean = true) {
        if (isAnim) {
            Handler(Looper.getMainLooper()).postDelayed({
                dismiss()
            }, 200)
            binding.cardRoot.alpha = 1f
            binding.cardRoot.animate().alpha(0f).setDuration(20).start()
            binding.rootViewDialog.transitionToStart()
        } else {
            dismiss()
        }
    }


    interface Callback {
        fun openGallery()

        fun done(text: String)
    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        var screenshot: Bitmap? = null
        try {
            screenshot = Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        return blurBitmap(requireContext(),screenshot!!,25f)
    }

    fun blurBitmap(context: Context, inputBitmap: Bitmap, blurRadius: Float): Bitmap {
        // Create a RenderScript context
        val rs = RenderScript.create(context)

        // Create an allocation from Bitmap
        val input = Allocation.createFromBitmap(rs, inputBitmap)

        // Create an allocation with the same type
        val output = Allocation.createTyped(rs, input.type)

        // Create a blur script using the built-in RenderScript intrinsic
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setInput(input)

        // Set the blur radius
        script.setRadius(blurRadius)

        // Call the script for output allocation
        script.forEach(output)

        // Copy the output allocation to the Bitmap
        output.copyTo(inputBitmap)

        // Destroy everything to free memory
        rs.destroy()

        return inputBitmap
    }
}
