package vaf.vishal.autodraw.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import vaf.vishal.autodraw.interfaces.OnDrawEventListener

/**
 * Created by vishal on 13/12/17.
 */

class AutoDrawView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    lateinit var listener: OnDrawEventListener
    lateinit var newPath: Path

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val pointX = event.x
        val pointY = event.y

        newPath = Path()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(pointX, pointY)
                listener.onDrawStart()
            }
            MotionEvent.ACTION_MOVE -> {
                listener.onDrawing(pointX.toInt(), pointY.toInt())
                path.lineTo(pointX, pointY)
            }
            MotionEvent.ACTION_UP -> {
                listener.onDrawStop(path)
            }
            else -> {
                return false
            }
        }

        postInvalidate();
        return true

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, drawPaint)
    }

    init {
        setFocusable(true);
        setFocusableInTouchMode(true)
        setupPaint()
    }

    fun setOnDrawEventListener(onDrawEventListener: OnDrawEventListener) {
        listener = onDrawEventListener
    }

    lateinit var drawPaint: Paint
    val path = Path()

    private fun setupPaint() {
        drawPaint = Paint()
        drawPaint.color = Color.BLACK
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = 8f
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
    }
}