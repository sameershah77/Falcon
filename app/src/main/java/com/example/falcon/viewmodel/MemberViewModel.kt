package com.example.falcon.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.falcon.model.MemberData
import com.example.falcon.model.User
import com.example.falcon.repositories.MemberRepository
import com.example.falcon.repositories.UserRepository

class MemberViewModel(private val repository: MemberRepository) : ViewModel() {

    private val _memberDataList = MutableLiveData<List<MemberData>>(emptyList())
    val memberDataList: LiveData<List<MemberData>> = _memberDataList

    init {
        fetchMembers()
    }

    fun fetchMembers() {
        repository.updateAllMembers()
        repository.fetchMembersList { members ->
            _memberDataList.value = members
        }
    }

    fun addMember(member: MemberData, callback: (Boolean) -> Unit) {
        repository.addMember(member) { success ->
            if (success) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }
}