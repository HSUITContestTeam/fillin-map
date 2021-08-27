package com.hsu.mapapp.Share_Folder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment(R.layout.fragment_friends) {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendsAdapter
    private val data_friends = mutableListOf<FriendsItemList>()

    private var firestore: FirebaseFirestore? = null
    private val uid = Firebase.auth.currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setRecycler()
    }

    private fun setRecycler() {
        adapter = FriendsAdapter(this)
        binding.friendsRecycler.adapter = adapter
        binding.friendsRecycler.layoutManager = LinearLayoutManager(this.context)
        binding.friendsRecycler.setHasFixedSize(true)

        val myRef = firestore
            ?.collection("users")?.document("$uid")
        myRef!!.get()
            .addOnSuccessListener { document ->
                if (document.get("friendsList") != null) {
                    val hashMap: Map<String, String> =
                        document.get("friendsList") as Map<String, String>
                    val keySet = hashMap.keys
                    for (key in keySet) {
                        if (hashMap[key].toString() == "friend") {
                            Log.d("friend",key)
                            data_friends.apply {
                                add(FriendsItemList((key)))
                                adapter.datas_friends = data_friends
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}