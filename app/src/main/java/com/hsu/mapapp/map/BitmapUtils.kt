package com.hsu.mapapp.map

import android.graphics.*

/**
 * Created by tarek on 6/17/17.
 */
// https://github.com/tarek360/Bitmap-Cropping 참고
object BitmapUtils {
    fun getCroppedBitmap(src: Bitmap, path: Path): Bitmap {
        val output = Bitmap.createBitmap(
            src.width,
            src.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setColor(-0x1000000)
        canvas.drawPath(path, paint)

        // Keeps the source pixels that cover the destination pixels,
        // discards the remaining source and destination pixels.
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(src, 0F, 0F, paint)
        return output
    }
}