package com.hgm.keyboard.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import com.hgm.keyboard.utils.SizeUtil
import com.hgm.keyboard.R

/**
 * @author：  HGM
 * @date：  2023-06-20 19:45
 * 使用自定义ViewGroup实现数字键盘
 */
class Keyboard : ViewGroup {

      companion object {
            const val TAG = "Keyboard"
            const val DEFAULT_ROW = 4
            const val DEFAULT_COLUMN = 3
            var DEFAULT_PADDING = SizeUtil.dip2px(5f)
      }

      //属性
      private var itemPadding: Int = DEFAULT_PADDING
      private var numberColor: Int = 0
      private var numberSize: Int = 0
      private var itemPressBg: Int = 0
      private var itemNormalBg: Int = 0

      //变量
      private val row = DEFAULT_ROW
      private val column = DEFAULT_COLUMN


      constructor(context: Context) : this(context, null)

      constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

      constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
      ) {
            initAttrs(context, attrs)
            setUpItem()
      }

      /**
       * 初始化属性
       */
      private fun initAttrs(
            context: Context,
            attrs: AttributeSet?
      ) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.Keyboard)
            with(a) {
                  //item边距
                  itemPadding =
                        getDimensionPixelOffset(R.styleable.Keyboard_itemPadding, DEFAULT_PADDING)
                  //颜色
                  numberColor = getColor(
                        R.styleable.Keyboard_numberColor,
                        resources.getColor(R.color.key_item_text_color)
                  )
                  //尺寸大小
                  numberSize = getDimensionPixelSize(R.styleable.Keyboard_numberSize, -1)
                  //触摸背景颜色
                  itemPressBg = getColor(
                        R.styleable.Keyboard_itemPressBg,
                        resources.getColor(R.color.key_item_press_color_bg)
                  )
                  //正常背景颜色
                  itemNormalBg = getColor(
                        R.styleable.Keyboard_itemNormalBg,
                        resources.getColor(R.color.key_item_color_bg)
                  )
            }
            a.recycle()
      }


      /**
       * 设置Item
       */
      private fun setUpItem() {
            removeAllViews()
            for (i in 0 until 11) {
                  val item = TextView(context).apply {
                        //配置item属性（最后一个为删除键）
                        if (i == 10) {
                              tag = true
                              text = "删除"
                        } else {
                              tag = false
                              text = i.toString()
                        }
                        gravity = Gravity.CENTER
                        setTextColor(numberColor)
                        setTextSize(TypedValue.COMPLEX_UNIT_PX, numberSize.toFloat())
                        background = providerItemBg()
                        //点击事件
                        setOnClickListener {
                              if (tag == true) {
                                    onKeyClickListener?.onDeleteKeyClick()
                              } else {
                                    onKeyClickListener?.onNumKeyClick(text.toString().toInt())
                              }
                        }
                  }.let {
                        addView(it)
                  }
            }
      }

      /**
       * 用代码的形式配置item点击和默认的效果
       * 不同xml的方式来配置selector
       */
      private fun providerItemBg(): Drawable {
            val bg = StateListDrawable()
            //按下去的shape
            val pressShape = GradientDrawable().apply {
                  setColor(itemPressBg)
                  cornerRadius = SizeUtil.dip2px(5f).toFloat()
            }
            bg.addState(IntArray(1) { android.R.attr.state_pressed }, pressShape)
            //普通的shape
            val normalShape = GradientDrawable().apply {
                  setColor(itemNormalBg)
                  cornerRadius = SizeUtil.dip2px(5f).toFloat()
            }
            bg.addState(IntArray(1), normalShape)
            return bg
      }


      /**
       * 测量
       */
      override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            //动态获取用户设置的padding
            val verticalPadding = paddingTop + paddingBottom
            val horizontalPadding = paddingLeft + paddingRight
            //获取宽高
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)
            //一行有3个、一共有4列（去除边距，剩下的分给item）
            val perItemWidth = (widthSize - (column + 1) * itemPadding - horizontalPadding) / column
            val perItemHeight = (heightSize - (row + 1) * itemPadding - verticalPadding) / row
            //设置测量孩子的标准
            val normalWidthSpec = MeasureSpec.makeMeasureSpec(
                  perItemWidth,
                  MeasureSpec.EXACTLY
            )
            val deleteWidthSpec = MeasureSpec.makeMeasureSpec(
                  perItemWidth * 2 + itemPadding,
                  MeasureSpec.EXACTLY
            )
            val normalHeightSpec = MeasureSpec.makeMeasureSpec(
                  perItemHeight,
                  MeasureSpec.EXACTLY
            )
            //《测量孩子》
            for (i in 0 until childCount) {
                  val item = getChildAt(i)
                  val isDeleteKey = item.tag as Boolean
                  if (isDeleteKey) {
                        item.measure(deleteWidthSpec, normalHeightSpec)
                  } else {
                        item.measure(normalWidthSpec, normalHeightSpec)
                  }
            }
            //《测量自己》
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
      }


      /**
       * 布局
       */
      override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            //只需要关注left和top，因为布局从这2个开始
            val paddingLeft = paddingLeft
            val paddingTop = paddingTop

            val childCount = childCount
            var left = itemPadding + paddingLeft
            var (right, top, bottom) = 0



            for (i in 0 until childCount) {
                  //计算出当前item的第几行、第几列
                  val rowIndex = i / column
                  val columnIndex = i % column
                  //判断是否到下一行，重置left
                  if (columnIndex == 0) {
                        left = itemPadding + paddingLeft
                  }
                  val item = getChildAt(i)
                  top = rowIndex * item.measuredHeight + itemPadding * (rowIndex + 1) + paddingTop
                  right = left + item.measuredWidth
                  bottom = top + item.measuredHeight
                  item.layout(left, top, right, bottom)
                  left += item.measuredWidth + itemPadding
            }
      }


      /**
       * 定义接口
       */
      interface OnKeyClickListener {
            fun onNumKeyClick(number: Int)
            fun onDeleteKeyClick()
      }

      private var onKeyClickListener: OnKeyClickListener? = null
      fun setOnKeyClickListener(listener: OnKeyClickListener) {
            onKeyClickListener = listener
      }
}