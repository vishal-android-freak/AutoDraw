package vaf.vishal.autodraw.interfaces

import android.graphics.Path
import android.graphics.Point

/**
 * Created by vishal on 13/12/17.
 */

interface OnDrawEventListener {
    fun onDrawStart()
    fun onDrawing(pointX: Int, pointY: Int)
    fun onDrawStop(path: Path)
}