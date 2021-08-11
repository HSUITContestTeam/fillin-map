package com.hsu.mapapp.map

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import androidx.core.graphics.PathParser

class CustomView(context: Context?, private val path: String) :
    View(context) {
    // test용 paint
    var paint1 = Paint()
    // 진짜 paint
    var paint = Paint()
    override fun onDraw(canvas: Canvas) {

    }
    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        // 주석 시작
        // path는 갤러리에서 선택한 사진의 uri.toString()
        // https://medium.com/@Tarek360/crop-a-shape-from-an-android-bitmap-9690b7432774 참고했음
        val srcBitmap = BitmapFactory.decodeFile(path)
        Log.d("getPath",path)

        val outputBitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        Log.d("canvas",canvas.toString())

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(Color.BLACK)

        val goseongPathData = "M340.083,66.939l13.334,-7l-8.334,-17.5l-1.333,-4.333l-7.167,-10l1.334,-1.833l-8,-14.167V8.939l-6.5,-8.333l-5.334,2.167L317.75,16.94l-1.167,7.833l-10.166,12.833l2.833,2l2.5,0.833l0.833,0.167l-1,1.833l0.667,4.333l5.5,-1.5l1,3.667l0.833,5.5l4.167,0.333l1,3.833l3.667,1l3.833,-1.833l2.167,2.5v5.5L340.083,66.939z"
        // pathData를 이용해 path 생성
        val goseongPath = PathParser.createPathFromPathData(goseongPathData)
        Log.d("goseongPath",goseongPath.toString())

        canvas.drawPath(goseongPath,paint)
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))

        canvas.drawBitmap(srcBitmap,0F,0F,paint)
        // 주석 끝

        // test용 canvas
        // 빨간 동그라미 그려짐
        // 실행 시키려면 주석 시작~주석 끝을 주석처리시키고 실행해야 함
        paint1.color = Color.RED
        canvas.drawCircle(600f, 500f, 60f, paint1)
    }
}