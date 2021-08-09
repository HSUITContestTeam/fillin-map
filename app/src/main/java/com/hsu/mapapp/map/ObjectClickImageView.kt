package com.hsu.mapapp.map

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView

/**
 * Created by leetid@naver.com on 2018-04-09.
 */

/**
 * Custom ImageView
 * 이미지 중 투명한 부분은 click 되지 않는 ImageView
 */

/**
 * 사용법
 * 1. xml - ImageView를 ObjectClickImageView로 바꾸기
 * <com.hsu.mapapp.map.ObjectClickImageView
android:id="@+id/test_image"
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:adjustViewBounds="true"
android:src="@drawable/test_image" />
 * 2. 클릭 이벤트 코드
 * val test_image:ObjectClickImageView = (binding.test_image)
 * test_image.setOnObjectClickListener {...}
 */

@SuppressLint("ClickableViewAccessibility")
class ObjectClickImageView(context: Context?, @Nullable attrs: AttributeSet?) :
    AppCompatImageView(context!!, attrs) {
    private var onTouchListener: OnTouchListener? = null
    private var onObjectClickListener: OnClickListener? = null
    override fun setOnTouchListener(listener: OnTouchListener) {
        onTouchListener = listener
    }

    fun setOnObjectClickListener(@Nullable listener: OnClickListener?) {
        onObjectClickListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onObjectTouchListener =
        OnTouchListener { v, event ->
            var consumed = false
            if (onTouchListener != null) consumed = onTouchListener!!.onTouch(v, event)
            if (consumed) return@OnTouchListener true
            if (onObjectClickListener != null) {
                val x = event.x.toInt()
                val y = event.y.toInt()
                when (event.action) {
                    MotionEvent.ACTION_UP -> if (isOnObject(x, y)) return@OnTouchListener true
                    MotionEvent.ACTION_DOWN -> if (isOnObject(x, y)) {
                        onObjectClickListener!!.onClick(v)
                        return@OnTouchListener true
                    }
                }
            }
            false
        }

    private fun isOnObject(x: Int, y: Int): Boolean {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        if (bitmap == null) return false
        if (Color.alpha(bitmap.getPixel(x, y)) > 0) // Color.aplha가 0이면 투명
            return true
        return false
    }

    init {
        super.setOnTouchListener(onObjectTouchListener)
    }
}