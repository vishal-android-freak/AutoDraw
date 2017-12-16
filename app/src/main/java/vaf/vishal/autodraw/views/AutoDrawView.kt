package vaf.vishal.autodraw.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import vaf.vishal.autodraw.interfaces.OnActionsCountChangeListener
import vaf.vishal.autodraw.interfaces.OnDrawEventListener

/**
 * Created by vishal on 13/12/17.
 */

class AutoDrawView(context: Context, attributeSet: AttributeSet) : ImageView(context, attributeSet) {

    lateinit var drawListener: OnDrawEventListener
    lateinit var actionsListener: OnActionsCountChangeListener

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val pointX = event.x
        val pointY = event.y

        val prevPath = Path()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(pointX, pointY)
                drawListener.onDrawStart()
                prevPath.set(path)
                undoPaths.add(prevPath)
            }
            MotionEvent.ACTION_MOVE -> {
                drawListener.onDrawing(pointX.toInt(), pointY.toInt())
                path.lineTo(pointX, pointY)
            }
            MotionEvent.ACTION_UP -> {
                drawListener.onDrawStop()
                actionsListener.onUndoOrRedoCountChange(undoPaths.size, redoPaths.size)
            }
            else -> {
                return false
            }
        }
        invalidate()
        return true
    }

    fun clear() {
        path.reset()
        undoPaths.clear()
        actionsListener.onUndoOrRedoCountChange(undoPaths.size, redoPaths.size)
    }

    fun undo() {
        val subPath = undoPaths[undoPaths.size - 1]
        redoPaths.add(path)
        path.set(subPath)
        undoPaths.removeAt(undoPaths.size - 1)
        actionsListener.onUndoOrRedoCountChange(undoPaths.size, redoPaths.size)
        invalidate()
    }

    fun redo() {
        val subPath = redoPaths[redoPaths.size - 1]
        undoPaths.add(path)
        path.set(subPath)
        redoPaths.removeAt(redoPaths.size - 1)
        actionsListener.onUndoOrRedoCountChange(undoPaths.size, redoPaths.size)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, drawPaint)
    }

    init {
        setupPaint()
    }

    fun setOnDrawEventListener(onDrawEventListener: OnDrawEventListener) {
        drawListener = onDrawEventListener
    }

    fun setOnActionsCountChangeListener(onActionsCountChangeListener: OnActionsCountChangeListener) {
        actionsListener = onActionsCountChangeListener
    }

    lateinit var drawPaint: Paint
    val path = Path()
    val undoPaths = ArrayList<Path>()
    val redoPaths = ArrayList<Path>()

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