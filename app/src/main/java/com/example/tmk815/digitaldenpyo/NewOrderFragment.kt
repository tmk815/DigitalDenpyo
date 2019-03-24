package com.example.tmk815.digitaldenpyo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.neworder.*


class NewOrderFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.neworder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setNestedScrollingEnabled(this.menu_list, true)
        val menuName = resources.getStringArray(R.array.menu_name)
        val menuPrice = resources.getStringArray(R.array.menu_price);
        val items = List(menuName.size) { mapOf("name" to menuName[it], "price" to menuPrice[it]+"円") }
        val adapter = SimpleAdapter(
            this.context,
            items,
            android.R.layout.simple_list_item_2,
            arrayOf("name", "price"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        menu_list.adapter = adapter

        menu_list.setOnItemClickListener { adapterView, view, position, id ->
            val textView = view.findViewById<TextView>(android.R.id.text1)
            Toast.makeText(this.context, "Clicked: ${textView.text}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        fun newInstance(): NewOrderFragment {
            return NewOrderFragment()
        }
    }
}