package com.hsu.mapapp.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.ActivityFillMapWithColorBinding


class FillMapWithColorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFillMapWithColorBinding
    private var selectColor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFillMapWithColorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val colors = resources.getStringArray(R.array.color_palette)

        // Color Palette 클릭 리스너
        for (color in colors) {
            binding.root.rootView.findViewWithTag<Button>("color_${color}")?.setOnClickListener {
                val colorID =
                    resources.getIdentifier(color, "string", "com.hsu.mapapp") // string -> id(int)
                selectColor = resources.getString(colorID)
                Log.d("selectColor", selectColor.toString())
                selectColor()
            }
        }

        // 왜인지 보라 버튼만 작동을 안함.. 그래서 따로 리스너 달아줌..
        binding.colorPURPLE.setOnClickListener {
            selectColor = "#9C27B0"
            selectColor()
        }
        binding.colorDEEPPURPLE.setOnClickListener {
            selectColor = "#673AB7"
            selectColor()
        }

        // Color Picker
        binding.colorPickerBtn.setOnClickListener {
            ColorPickerDialog
                .Builder(this)                        // Pass Activity Instance
                .setTitle("Color Picker")            // Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                .setDefaultColor(R.color.blue_800)     // Pass Default Color
                .setColorListener { color, colorHex ->
                    selectColor = colorHex
                    selectColor()
                }
                .show()
        }

        binding.saveBtn.setOnClickListener {
            if (selectColor != null) {
                setResult(RESULT_OK, intent) // 결과 main으로 보냄
            }
            finish()
        }
    }

    fun selectColor() {
        // 컬러 선택시 selectedTV 색 변경, putExtra수행
        binding.selectedTv.setBackgroundColor(Color.parseColor(selectColor))
        intent.putExtra("color", selectColor)
    }
}