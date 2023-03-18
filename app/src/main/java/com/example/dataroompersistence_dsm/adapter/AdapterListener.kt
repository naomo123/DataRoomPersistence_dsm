package com.example.dataroompersistence_dsm.adapter

import com.example.dataroompersistence_dsm.model.Activities


interface AdapterListener {
    fun onEditItemClick(activities: Activities)
    fun onDeleteItemClick(activities: Activities)
}