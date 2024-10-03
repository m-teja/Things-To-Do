package com.project
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.adapter.HomeAdapter
import com.project.base.BaseFragment


class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private val dataSet = arrayListOf("things", "to", "do")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeAdapter = HomeAdapter(dataSet)

        val recyclerView: RecyclerView = view.findViewById(R.id.home_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = homeAdapter

        view.findViewById<Button>(R.id.home_random_button).setOnClickListener {
            Toast.makeText(context, getRandomSuggestion(), Toast.LENGTH_LONG).show()
        }
    }

    private fun getRandomSuggestion(): String {
        return dataSet[(Math.random() * (dataSet.size)).toInt()]
    }

}