package com.example.appmusicmp3.service

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LiveData : ViewModel() {
    // mo dinh V MV M ( view - VIEWMODEL - model )
    // class này có tac vụ lắng nghe sự thay đổi ở service -> rồi gửi thông báo về nhưng thằng đăng kí mình
    val linkMp3  =  MutableLiveData<String>()
}