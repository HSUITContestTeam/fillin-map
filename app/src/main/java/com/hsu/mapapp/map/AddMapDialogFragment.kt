package com.hsu.mapapp.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.hsu.mapapp.databinding.FragmentAddMapDialogBinding

class AddMapDialogFragment : DialogFragment() {
    private var _binding: FragmentAddMapDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapViewModel : MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = FragmentAddMapDialogBinding.inflate(inflater, container, false)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
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
            val newMapTitle = binding.editMapNameText.text
            var newData = MapItemList(newMapTitle.toString())
            mapViewModel.addMap(newData)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}