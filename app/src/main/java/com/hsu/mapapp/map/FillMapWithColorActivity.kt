package com.hsu.mapapp.map

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.core.content.ContextCompat
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


        for (color in colors) {
            binding.root.rootView.findViewWithTag<Button>("color_${color}")?.setOnClickListener {
                val colorID = resources.getIdentifier(color,"string","com.hsu.mapapp") // string -> id(int)
                selectColor = resources.getString(colorID)
                binding.selectedTv.setBackgroundColor(Color.parseColor(selectColor))
                intent.putExtra("color", selectColor)
            }
        }

        binding.saveBtn.setOnClickListener {
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}