package com.hsu.mapapp.Share_Folder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.MainActivity
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentFriendsBinding
import com.hsu.mapapp.friend_map.FriendMapViewModel
import com.hsu.mapapp.friend_map.ShareMapActivity

class ShareFragment : Fragment(R.layout.fragment_friends) {
    var mainActivity: MainActivity? = null
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    lateinit var text: String
    private lateinit var viewModel: ShareViewModel

    private var firestore: FirebaseFirestore? = null
    private val uid = Firebase.auth.currentUser?.uid

    private lateinit var adapter: FriendsAdapter
    private val data_friends = mutableListOf<FriendsItemList>()

    private lateinit var friendMapViewModel: FriendMapViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true);
        viewModel = ViewModelProvider(this).get(ShareViewModel::class.java)
        // appbar - 뒤로 가기 버튼 없애기
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        firestore = FirebaseFirestore.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()
        setDialog()
    }


    private fun setRecycler() {
        adapter = FriendsAdapter(this)
        binding.friendsRecycler.adapter = adapter
        binding.friendsRecycler.layoutManager = LinearLayoutManager(this.context)
        binding.friendsRecycler.setHasFixedSize(true)

        setFriends()
        adapter.setOnItemClickListener(object : FriendsAdapter.OnItemClickListener{
            override fun onItemClick(v: View, position: Int) {
                val friendUid = data_friends[position].uid
                val intent = Intent(context, ShareMapActivity::class.java)
                intent.putExtra("friendUid",friendUid)
                startActivity(intent)
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFriends() {
        val myRef = firestore?.collection("users")?.document("$uid")
        myRef!!.get()
            .addOnSuccessListener { document ->
                if (document.get("friendsList") != null) {
                    val hashMap: ArrayList<Map<String, String>> =
                        document.get("friendsList") as ArrayList<Map<String, String>>
                    data_friends.clear()
                    for (keys in hashMap) {
                        val key = keys.keys.iterator().next()
                        if (keys[key].toString() == "friend") {
                            val friendRef = firestore?.collection("users")?.document(key)
                            friendRef?.get()?.addOnSuccessListener { document ->
                                data_friends.apply {
                                    val name = document.get("name").toString()
                                    val uid = document.get("uid").toString()
                                    add(FriendsItemList(name,uid))
                                    adapter.setData(data_friends)
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
    }

    fun setDialog() {
        var myRef = firestore?.collection("users")?.document("$uid")
        myRef!!.get()
            .addOnSuccessListener { document ->
                if (document.get("friendsList") != null) {
                    val hashMap: ArrayList<Map<String, String>> =
                        document.get("friendsList") as ArrayList<Map<String, String>>
                    for (keys in hashMap) {
                        val friend: MutableMap<String, Any> = HashMap()
                        val mine: MutableMap<String, Any> = HashMap()
                        val key = keys.keys.iterator().next()
                        if (keys[key] == "requested") {
                            val friendRef = firestore?.collection("users")?.document(key)
                            friendRef!!.get()
                                .addOnSuccessListener { document ->
                                    val builder = AlertDialog.Builder(requireContext())
                                    builder
                                        .setTitle("친구 요청")
                                        .setMessage(
                                            document.get("name")
                                                .toString() + "가 친구 요청을 보냈습니다. 수락하시겠습니까?"
                                        )
                                        .setPositiveButton("예") { dialog, which ->
                                            mine[key] = "requested"
                                            myRef.update(
                                                "friendsList",
                                                FieldValue.arrayRemove(mine)
                                            )
                                            mine[key] = "friend"
                                            myRef.update("friendsList", FieldValue.arrayUnion(mine))

                                            friend[uid.toString()] = "request"
                                            friendRef.update(
                                                "friendsList",
                                                FieldValue.arrayRemove(friend)
                                            )
                                            friend[uid.toString()] = "friend"
                                            friendRef.update(
                                                "friendsList",
                                                FieldValue.arrayUnion(friend)
                                            )
                                        }
                                        .setNegativeButton("아니오") { dialog, which -> dialog.dismiss() }
                                        .show()
                                }
                        }
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_menu, menu);

//        val searchItem = menu.findItem(R.id.item_search)
//        val searchView = searchItem.actionView as SearchView
//        searchView.queryHint = "검색어를 입력하시오"
//
//
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (query != null) {
//                    text = query
//
//                }
//                findNavController().navigate(R.id.action_shareFragment_to_friendsSearchFragment2)
//                // 검색어 완료시
//               //println(query)
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                text = newText;
//
//                //검색어 입력시
//                //println(newText);
//
//                return true
//            }
//        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.item_search -> {
                println("검색버튼 클릭")
                findNavController().navigate(R.id.action_shareFragment_to_friendsSearchFragment2)
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

