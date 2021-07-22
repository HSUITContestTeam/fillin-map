package com.hsu.mapapp.Share_Folder

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.hsu.mapapp.MainActivity
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.ActivityShareBinding

class ShareFragment : Fragment(R.layout.activity_share) {


    private var _binding: ActivityShareBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityShareBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true);
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_menu,menu);

        //var searchView : SearchView = menu.findItem(R.menu.share_menu).actionView as SearchView
       //searchView.maxWidth;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_friends -> {
                println("친구목록 클릭")
            }
            R.id.item_group -> {

            }
            R.id.item_search -> {

            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

