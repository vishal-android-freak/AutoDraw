package vaf.vishal.autodraw.interfaces

/**
 * Created by vishal on 16/12/17.
 */

interface OnActionsCountChangeListener {
    fun onUndoOrRedoCountChange(undoCount: Int, redoCount: Int)
}