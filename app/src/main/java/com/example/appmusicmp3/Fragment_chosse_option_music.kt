package com.example.appmusicmp3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appmusicmp3.databinding.LayoutChosseOptionMusicBinding

class Fragment_chosse_option_music : Fragment() {
    private lateinit var binding : LayoutChosseOptionMusicBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  LayoutChosseOptionMusicBinding.inflate(inflater,container,false)
        binding.btnOnline.setOnClickListener{
            (activity as MainActivity).fragmentmanin()
        }
        binding.btnOffline.setOnClickListener{
            (activity as MainActivity).fragmentMainOffline()
        }
        return binding.root
    }
}