package com.hsu.mapapp.map

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.AddMapDialogBinding
import com.hsu.mapapp.databinding.FragmentAddMapDialogBinding
import kotlinx.android.synthetic.main.add_map_dialog.*

class AddMapDialog : DialogFragment() {

    private var _binding: AddMapDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapViewModel : MapViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isCancelable = false
        _binding = AddMapDialogBinding.inflate(inflater, container, false)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setDialog()
        setSpinner()

    }

    fun setDialog() {

        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.add_map_dialog, null)

        builder.setView(view)
        binding.yesBtn.setOnClickListener {
            val newMapTitle = binding.mapNameEditTv.text
            var newData = MapItemList(newMapTitle.toString())
            mapViewModel.addMap(newData)
            dismiss()
        }
        binding.noBtn.setOnClickListener {
            dismiss()
        }
        builder.create()
    }

    fun setSpinner() {
        val mapListItems = resources.getStringArray(R.array.map_list_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mapListItems)
        binding.mapSortSpinner.adapter = adapter
        binding.mapSortSpinner.setSelection(0)

        binding.mapSortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(requireContext(), "지도종류 : ${binding.mapSortSpinner.getItemAtPosition(position)}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}