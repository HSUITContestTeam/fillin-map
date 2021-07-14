package com.hsu.mapapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.hsu.mapapp.databinding.FragmentMapNameDialogBinding

class MapNameDialogFragment : DialogFragment() {
    private var _binding: FragmentMapNameDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = FragmentMapNameDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.dialogTitle.text = "지도 추가하기"
        binding.dialogCancelBtn.text = "취소"
        binding.dialogSaveBtn.text = "저장"

        
        binding.dialogCancelBtn.setOnClickListener {
            dismiss()
        }
        binding.dialogSaveBtn.setOnClickListener {
            Toast.makeText(this.context, "저장버튼 클릭!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}