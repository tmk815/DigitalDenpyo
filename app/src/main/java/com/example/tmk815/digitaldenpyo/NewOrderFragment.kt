package com.example.tmk815.digitaldenpyo

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.order_new.*


class NewOrderFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.order_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setNestedScrollingEnabled(this.menu_list, true)
        val menuName: ArrayList<String> = arrayListOf()
        val menuPrice: ArrayList<Int> = arrayListOf()

        menu_list.setOnItemClickListener { adapterView, view, position, id ->
            val textViewName = view.findViewById<TextView>(android.R.id.text1)
            val textViewPrice = view.findViewById<TextView>(android.R.id.text2)
            val textPrice = textViewPrice.text.substring(0,textViewPrice.length()-1).toInt()

            val textView: TextView = TextView(this.context)
            textView.text = textViewName.text
            order_list.addView(textView)
        }

        val user = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("menu").child(user!!.uid)

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //ArrayList初期化
                menuName.clear()
                menuPrice.clear()
                //RealTimeDBから取り出した内容をArrayListに追加
                for (newMenu in dataSnapshot.children) {
                    menuName.add(newMenu.key!!)
                    menuPrice.add((newMenu.value.toString()).toInt())
                }
                val items = List(menuName.size) { mapOf("name" to menuName[it], "price" to "${menuPrice[it]}円") }
                val adapter = SimpleAdapter(
                    context,
                    items,
                    android.R.layout.simple_list_item_2,
                    arrayOf("name", "price"),
                    intArrayOf(android.R.id.text1, android.R.id.text2)
                )
                menu_list.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
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