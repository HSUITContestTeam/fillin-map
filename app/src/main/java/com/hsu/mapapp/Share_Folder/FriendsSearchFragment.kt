package com.hsu.mapapp.Share_Folder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentSearchFriendsBinding
import com.hsu.mapapp.databinding.SearchFriendsListItemBinding

class FriendsSearchFragment : Fragment(R.layout.search_friends_list_item) {
    private var _binding: FragmentSearchFriendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ShareViewModel

    var firestore : FirebaseFirestore? = null
    private val uid = Firebase.auth.currentUser ?.uid

    private lateinit var adapter: FriendsSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firestore = FirebaseFirestore.getInstance()

        _binding = FragmentSearchFriendsBinding.inflate(inflater, container, false)

        //서치뷰의 검색버튼.setOnclickListener{
        //(binding.FriendsSearchRecycler.adapter as FriendsSearchAdapter).search(searchWord.text.toString())
    //}

        return binding.root
    }
    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int, isUser: Boolean)
    }
    inner class FriendsSearchAdapter(private val context: FriendsSearchFragment) :
        RecyclerView.Adapter<FriendsSearchAdapter.ViewHolder>() {
        var datas_friends_search :ArrayList<FriendsSearchItemList> = arrayListOf()
        var isStartBtnSelected = false
        private val btn: Button? = null
        private val onItemClickListener: OnItemClickListener? = null

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
            fun addFriendsBtnOnclick(item: FriendsSearchItemList){
                binding.addFriendsBtn.isSelected = isStartBtnSelected
                isStartBtnSelected = !isStartBtnSelected
                binding.addFriendsBtn.setOnClickListener {
                    if(item.uid != uid){
                        val myRef = firestore
                            ?.collection("users")?.document("$uid")
                        myRef!!.get()
                            .addOnSuccessListener { document->
                                // 친구 리스트가 있는 경우
                                if(document.get("friendsList") != null) {
                                    val hashMap: Map<String, String> =
                                        document.get("friendsList") as Map<String, String>
                                    // 선택한 아이템의 uid와의 관계가 friend가 아닌 경우
                                    if(hashMap[item.uid] == "request"){
                                        Toast.makeText(activity,"이미 친구요청을 보냈습니다", Toast.LENGTH_LONG).show()
                                    }
                                    else {
                                        Toast.makeText(activity,item.uid+"와 이미 친구입니다.", Toast.LENGTH_LONG).show()
                                    }
                                }
                                else{ // 친구 리스트가 없는 경우
                                    myRef.update("friendsList", hashMapOf(
                                        item.uid to "request"
                                    ))
                                    val friendRef = firestore
                                        ?.collection("users")?.document(item.uid)
                                    friendRef!!.get()
                                        .addOnSuccessListener { document->
                                            // uid에게 친구 요청을 받음
                                            friendRef.update("friendsList", hashMapOf(
                                                uid to "requested"
                                            ))
                                            Toast.makeText(activity,item.uid+"에게 친구요청을 보냈습니다",
                                                Toast.LENGTH_LONG).show()
                                            Log.d("친구요청","성공")
                                        }
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(activity,"${item.uid}에게 친구요청 보내기를 실패하였습니다", Toast.LENGTH_LONG).show()
                                Log.d("친구요청","실패")
                            }
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SearchFriendsListItemBinding.inflate(layoutInflater,parent,false)
            binding.addFriendsBtn.isSelected = false
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setFriendsName(datas_friends_search[position])
            holder.addFriendsBtnOnclick(datas_friends_search[position])
        }

        override fun getItemCount() = datas_friends_search.size



        fun search(searchWord : String, option : String) {
            firestore?.collection("users")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                // ArrayList 비워줌
                datas_friends_search.clear()

                for (snapshot in querySnapshot!!.documents) {
                    if (snapshot.getString(option)!!.contains(searchWord)) {
                        var item = snapshot.toObject(FriendsSearchItemList::class.java)
                        datas_friends_search.add(item!!)
                    }
                }
                notifyDataSetChanged()
            }
        }
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