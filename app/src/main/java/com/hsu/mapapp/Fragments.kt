package com.hsu.mapapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hsu.mapapp.databinding.*


// onCreateView나 onViewCreated view binding 쓰려면 맨아래
// MapFragment 클래스 참고!


//class ShareFragment : Fragment(R.layout.레이아웃이름)



class SettingFragment : Fragment(R.layout.activity_settings) {
    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        SetBtnClickEvent()
    }


    private fun SetBtnClickEvent() {
        binding.notice.setOnClickListener {
            startActivity(Intent(context, NoticeActivity::class.java))
        }

        binding.noticeImage.setOnClickListener {
            startActivity(Intent(context, NoticeActivity::class.java))
        }

        binding.appInfo.setOnClickListener {
            startActivity(Intent(context, AppinfoActivity::class.java))
        }

        binding.appInfoImage.setOnClickListener {
            startActivity(Intent(context, AppinfoActivity::class.java))
        }

        binding.myProfile.setOnClickListener {
            startActivity(Intent(context, ProfileActivity::class.java))
        }

        binding.myProfileImage.setOnClickListener {
            startActivity(Intent(context, ProfileActivity::class.java))
        }

        binding.themeSet.setOnClickListener {
            startActivity(Intent(context, ThemesetActivity::class.java))
        }

        binding.themeSetImage.setOnClickListener {
            startActivity(Intent(context, ThemesetActivity::class.java))
        }

        binding.notiSet.setOnClickListener {
            startActivity(Intent(context, NotisetActivity::class.java))
        }

        binding.notiSetImage.setOnClickListener {
            startActivity(Intent(context, NotisetActivity::class.java))
        }

        binding.bugReport.setOnClickListener {
            startActivity(Intent(context, BugreportActivity::class.java))
        }

        binding.bugReportImage.setOnClickListener {
            startActivity(Intent(context, BugreportActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

class TestFragment : Fragment(R.layout.activity_test) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}



