package com.example.tmk815.digitaldenpyo

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.order_new.*


class NewOrderFragment : androidx.fragment.app.Fragment() {

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


        //クリックしたメニューの処理
        menu_list.setOnItemClickListener { adapterView, view, position, id ->
            val textViewName = view.findViewById<TextView>(android.R.id.text1)
            val textViewPrice = view.findViewById<TextView>(android.R.id.text2)
            val textPrice = textViewPrice.text.substring(0, textViewPrice.length() - 1).toInt()

            //個数を選択するダイアログ
            val numberEdit = EditText(this.context)
            numberEdit.inputType = TYPE_CLASS_NUMBER
            val dialog = AlertDialog.Builder(this.context!!)
            dialog.setTitle("個数を入力してください")
            dialog.setView(numberEdit)
            dialog.setPositiveButton("注文") { _, _ ->
                // OKボタン押したときの処理
                val number = (numberEdit.text.toString()).toInt()
                val newOrder = Order(false, number, textPrice)

                val order = FirebaseAuth.getInstance().currentUser
                val database = FirebaseDatabase.getInstance()
                database.getReference("order").child(order!!.uid).child("before").child(seatNumber.toString())
                    .child(textViewName.text.toString()).setValue(newOrder)

                Toast.makeText(this.context, "${textViewName.text}を${number}個注文しました", Toast.LENGTH_SHORT).show()
            }
            dialog.setNegativeButton("キャンセル", null)
            dialog.show()
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