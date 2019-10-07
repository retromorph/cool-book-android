package ru.izifak.core.tools

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

class Tools {
    companion object {
        fun dpToPx(c: Context, dp: Int): Int {
            val displayMetrics = c.resources.displayMetrics
            return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
        }
    }
}