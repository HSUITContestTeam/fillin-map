package com.hsu.mapapp.Share_Folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.inflate
import android.widget.SearchView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import com.hsu.mapapp.MainActivity
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.ActivityAppinfoBinding.inflate
import com.hsu.mapapp.databinding.ActivityShareBinding
import com.hsu.mapapp.databinding.FragmentSearchFriendsBinding
import java.util.zip.Inflater

class ShareFragment : Fragment(R.layout.activity_share) {

    var mainActivity: MainActivity? = null
    private var _binding: ActivityShareBinding? = null
    private val binding get() = _binding!!
    lateinit var text : String
    private lateinit var viewModel: ShareViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityShareBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true);
        viewModel = ViewModelProvider(this).get(ShareViewModel::class.java)


        return binding.root
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_menu, menu);

        val searchItem = menu.findItem(R.id.item_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "검색어를 입력하시오"



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    text = query

                }
                findNavController().navigate(R.id.action_shareFragment_to_friendsSearchFragment2)
                // 검색어 완료시
               //println(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                text = newText;

                //검색어 입력시
                //println(newText);

                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.item_friends -> {
                println("친구목록 클릭")
                findNavController().navigate(R.id.action_shareFragment_to_friendsFragment)
                //mainActivity!!.openFragementOnFrameLayout(1)

            }
            R.id.item_group -> {
                println("그룹목록 클릭")
                findNavController().navigate(R.id.action_shareFragment_to_groupListFragment)
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

