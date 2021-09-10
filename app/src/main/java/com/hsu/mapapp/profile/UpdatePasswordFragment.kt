package com.hsu.mapapp.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hsu.mapapp.R
import com.hsu.mapapp.databinding.ActivityUpdatePasswordBinding

class UpdatePasswordFragment : Fragment(R.layout.activity_update_password) {
    private lateinit var _binding: ActivityUpdatePasswordBinding
    private val updatePasswordBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  ActivityUpdatePasswordBinding.inflate(inflater, container, false)
        updatePasswordBtnEvent()
        // appbar - 뒤로 가기 버튼 없애기
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        return updatePasswordBinding.root
    }
    private fun updatePasswordBtnEvent() {
        updatePasswordBinding.updatePasswordBtn.setOnClickListener {
            val user = Firebase.auth.currentUser!!
            val email = user.email.toString()
            val currentPassword = updatePasswordBinding.currentPasswordEt.text.toString()
            val newPassword = updatePasswordBinding.newPasswordEt.text.toString()

            if(currentPassword.length>=6 && newPassword.length >= 6 ){
                // 사용자 재인증
                val credential = EmailAuthProvider
                    .getCredential(email, currentPassword)
                user.reauthenticate(credential)
                    .addOnCompleteListener {  }

                // 비밀번호 업데이트
                user.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Snackbar.make(updatePasswordBinding.root,"비밀번호가 변경되었습니다.", Snackbar.LENGTH_LONG).show()
                            parentFragmentManager.beginTransaction().apply {
                                replace(R.id.fragmentContainerView,ProfileFragment())
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                commitNow()
                            }
                        } else{
                            Snackbar.make(updatePasswordBinding.root,"비밀번호 변경을 실패했습니다.", Snackbar.LENGTH_LONG).show()
                        }
                    }
            }
            else{
                Snackbar.make(updatePasswordBinding.root,"비밀번호를 6글자 이상 입력하세요.", Snackbar.LENGTH_LONG).show()
            }

        }
    }

}