package com.hsu.mapapp.Share_Folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentSearchFriendsBinding
import com.hsu.mapapp.databinding.SearchFriendsListItemBinding

class FriendsSearchFragment : Fragment(R.layout.search_friends_list_item) {
    private var _binding: FragmentSearchFriendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ShareViewModel

    var firestore : FirebaseFirestore? = null

    private lateinit var adapter: FriendsSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firestore = FirebaseFirestore.getInstance()

        _binding = FragmentSearchFriendsBinding.inflate(inflater, container, false)

        return binding.root
    }

    inner class FriendsSearchAdapter(private val context: FriendsSearchFragment) :
        RecyclerView.Adapter<FriendsSearchAdapter.ViewHolder>() {
        var datas_friends_search :ArrayList<FriendsSearchItemList> = arrayListOf()
        var isStartBtnSelected = false

        init{
            firestore?.collection("users")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                datas_friends_search.clear()

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(FriendsSearchItemList::class.java)
                    datas_friends_search.add(item!!)
                }
                notifyDataSetChanged()
            }
        }

        inner class ViewHolder(private val binding: SearchFriendsListItemBinding) :
            RecyclerView.ViewHolder(binding.root){
            fun setFriendsName(item: FriendsSearchItemList){
                binding.friendsSearchName.text = item.userId
            }
            fun Add_Friends_btn_OnClich(){
                binding.addFriendsBtn.isSelected = isStartBtnSelected
                isStartBtnSelected = !isStartBtnSelected
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SearchFriendsListItemBinding.inflate(layoutInflater,parent,false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setFriendsName(datas_friends_search[position])
            holder.Add_Friends_btn_OnClich()
        }

        override fun getItemCount() = datas_friends_search.size
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecycler()
    }

   private fun setRecycler(){


       adapter = FriendsSearchAdapter(this)
       binding.FriendsSearchRecycler.adapter = adapter
       binding.FriendsSearchRecycler.layoutManager = LinearLayoutManager(this.context)
       binding.FriendsSearchRecycler.setHasFixedSize(true)

   }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}