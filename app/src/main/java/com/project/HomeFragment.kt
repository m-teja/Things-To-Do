package com.project
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.adapter.HomeAdapter
import com.project.base.BaseFragment


class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataSet = arrayListOf("things", "to", "do")
        val homeAdapter = HomeAdapter(dataSet)

        val recyclerView: RecyclerView = view.findViewById(R.id.home_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = homeAdapter
    }

}