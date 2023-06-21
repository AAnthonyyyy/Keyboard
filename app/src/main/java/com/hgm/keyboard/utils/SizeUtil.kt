package com.hgm.keyboard.utils

import com.hgm.keyboard.App


/**
 * @author：  HGM
 * @date：  2023-06-19 20:37
 */
class SizeUtil {
      companion object {
            fun dip2px(dpValue: Float): Int {
                  val scale = App.context.resources.displayMetrics.density
                  return (dpValue * scale + 0.5f).toInt()
            }
      }
}