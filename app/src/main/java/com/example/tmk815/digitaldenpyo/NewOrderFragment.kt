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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.order_new.*
import java.util.*


class NewOrderFragment : androidx.fragment.app.Fragment() {

    private var seatNumber: String? = null

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


                val upvotesRef = database.getReference("order").child(order!!.uid).child("before").child(seatNumber!!)
                    .child(textViewName.text.toString())
                upvotesRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val currentValue = mutableData.child("number").value
                        if (currentValue == null) {
                            database.getReference("order").child(order.uid).child("before").child(seatNumber!!)
                                .child(textViewName.text.toString()).setValue(newOrder)
                        } else {
                            mutableData.value = Order(false, currentValue.toString().toInt() + number, textPrice)
                        }

                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?, committed: Boolean, dataSnapshot: DataSnapshot?
                    ) {
                        println("Transaction completed")
                    }
                })

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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            // 表示状態になったときの処理
            /*val myEdit = EditText(this.context)
            val dialog = AlertDialog.Builder(this.context!!)
            dialog.setTitle("座席を入力してください")
            dialog.setView(myEdit)
            dialog.setPositiveButton("OK") { _, _ ->
                // OKボタン押したときの処理
                seatNumber = myEdit.text.toString()
                Toast.makeText(this.context, "${seatNumber}の座席の注文を取ります", Toast.LENGTH_SHORT).show()
            }
            dialog.setNegativeButton("キャンセル", null)
            dialog.show()*/
            val user = FirebaseAuth.getInstance().currentUser
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("seat").child(user!!.uid)
            val seatArrayList: ArrayList<String> = arrayListOf()

            // Read from the database
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    //ArrayList初期化
                    seatArrayList.clear()
                    //RealTimeDBから取り出した内容をArrayListに追加
                    for (dataSeat in dataSnapshot.children) {
                        seatArrayList.add(dataSeat.key!!)
                    }

                    var arraySeat = Array(seatArrayList.size) { i -> seatArrayList[i] }

                    var selectedItem = 0 // デフォルトでチェックされているアイテム
                    // 選択肢
                    // ダイアログを作成して表示
                    AlertDialog.Builder(context!!).apply {
                        setTitle("座席名")
                        setCancelable(false)
                        setSingleChoiceItems(arraySeat, 0) { _, i ->
                            // 選択した項目を保持
                            selectedItem = i
                        }
                        setPositiveButton("OK") { _, _ ->
                            seatNumber = seatArrayList[selectedItem]
                        }
                        setNegativeButton("Cancel", null)
                        show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
        } else {
            // 非表示状態になったときの処理
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