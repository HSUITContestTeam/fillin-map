package com.hsu.mapapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsu.mapapp.databinding.FragmentMapListBinding

class MapListFragment : Fragment(R.layout.fragment_map_list) {
    private var _binding: FragmentMapListBinding? = null
    private lateinit var adapter: CustomAdapter
    private val datas = mutableListOf<MapItemList>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecycler()
        setAddMapBtn()
    }

    private fun setRecycler() {
        adapter = CustomAdapter(this)
        binding.recyclerView.adapter = adapter // RecyclerView와 CustomAdapter 연결
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.setHasFixedSize(true)


        datas.apply {
            add(MapItemList("World"))
            add(MapItemList("Korea"))
            add(MapItemList("Hi"))

            adapter.datas = datas
            adapter.notifyDataSetChanged()
        }
    }

    private fun setAddMapBtn() {
        binding.addMapBtn.setOnClickListener {
            val fm = childFragmentManager
            MapNameDialogFragment().show(fm, "dialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}