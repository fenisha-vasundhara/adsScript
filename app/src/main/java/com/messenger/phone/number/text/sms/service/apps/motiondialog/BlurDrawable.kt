import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

class BlurDrawable(private val context: Context, private val radius: Float = 25f) : Drawable() {

    private val paint: Paint = Paint(Paint.FILTER_BITMAP_FLAG)

    override fun draw(canvas: Canvas) {
        val blurredBitmap = createBlurBitmap()
        canvas.drawBitmap(blurredBitmap, bounds.left.toFloat(), bounds.top.toFloat(), paint)
    }

    override fun setAlpha(alpha: Int) {
        // Not implemented
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // Not implemented
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getIntrinsicWidth(): Int {
        return bounds.width()
    }

    override fun getIntrinsicHeight(): Int {
        return bounds.height()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        invalidateSelf()
    }

    override fun invalidateSelf() {
        super.invalidateSelf()
        createBlurBitmap()
    }

    private fun createBlurBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)

        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        blurScript.setRadius(radius)
        blurScript.setInput(input)
        blurScript.forEach(output)

        output.copyTo(bitmap)

        rs.destroy()
        blurScript.destroy()

        return bitmap
    }
}
