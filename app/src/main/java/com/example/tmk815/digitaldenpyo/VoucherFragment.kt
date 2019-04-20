package com.example.tmk815.digitaldenpyo

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.voucher.*


class VoucherFragment : androidx.fragment.app.Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.voucher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val order = FirebaseAuth.getInstance().currentUser
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("order").child(order!!.uid).child("before")

        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // グループの親項目用のリスト
                val parentList = ArrayList<Map<String, String>>()
                // 子要素全体用のリスト
                val allChildList = ArrayList<List<Map<String, String>>>()

                //RealTimeDBから取り出した内容をArrayListに追加
                for (seat in dataSnapshot.children) {
                    // 親リストに表示する内容を生成
                    val parentData = HashMap<String, String>()
                    parentData["SEAT"] = seat.key.toString()
                    // グループの親項目用のリストに内容を格納
                    parentList.add(parentData)

                    // 各グループ別のリスト項目用のリスト
                    val childList = ArrayList<Map<String, String>>()

                    for (newOrder in seat.children) {
                        val childData = HashMap<String, String>()
                        if (newOrder.child("already").value == false) {
                            for (count in newOrder.children) {
                                childData["NAME"] = newOrder.key.toString()
                                if (count.key == "number") {
                                    childData["NUMBER"] = count.value.toString() + "個"
                                }
                            }
                            // リストに文字を格納
                            childList.add(childData)
                        }
                    }
                    // 子要素全体用のリストに各グループごとデータを格納
                    allChildList.add(childList)
                }
                // アダプタを作る
                val adapter = SimpleExpandableListAdapter(
                    this@VoucherFragment.context,
                    parentList,
                    android.R.layout.simple_expandable_list_item_1,
                    arrayOf("SEAT"),
                    intArrayOf(android.R.id.text1, android.R.id.text2),
                    allChildList,
                    android.R.layout.simple_expandable_list_item_2,
                    arrayOf("NAME", "NUMBER"),
                    intArrayOf(android.R.id.text1, android.R.id.text2)
                )
                //生成した情報をセット
                //val lv = voucherList
                voucherList.setAdapter(adapter)


                //グループの子項目がクリックされた時
                voucherList.setOnChildClickListener { parent, view, groupPosition, childPosition, id ->
                    val adapter = parent.expandableListAdapter
                    // クリックされた場所の内容情報を取得

                    val item = adapter.getChild(groupPosition, childPosition) as Map<String, String>
                    val orderFlag = mutableMapOf<String, Any>()
                    orderFlag["already"] = true
                    val seat = parentList[groupPosition]
                    myRef.child(seat.getValue("SEAT")).child(item["NAME"].toString()).updateChildren(orderFlag)
                    false
                }

                // グループの親項目がクリックされた時の処理
                voucherList.setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent, view, groupPosition, id ->

                    false
                })


                //項目が長押しされた時の処理
                voucherList.setOnItemLongClickListener { parent, view, position, id ->
                    val listView = parent as ExpandableListView
                    val packed = listView.getExpandableListPosition(position)
                    val groupPosition = ExpandableListView.getPackedPositionGroup(packed)
                    val childPosition = ExpandableListView.getPackedPositionChild(packed)
                    var alertCount = true
                    if (ExpandableListView.getPackedPositionType(packed) == 0) {
                        //親要素が長押しされた時のアクションを記述
                        val adapter = parent.expandableListAdapter
                        val seat = adapter.getGroup(groupPosition) as Map<String, String>
                        val seatCount = database.getReference("order").child(order!!.uid).child("before")
                            .child(seat.getValue("SEAT"))
                        seatCount.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // ダイアログを作成して表示
                                if (alertCount) {
                                    AlertDialog.Builder(context!!).apply {
                                        setTitle("会計")
                                        var totalPrice = 0
                                        for (name in dataSnapshot.children) {
                                            val prices = name.getValue(Order::class.java)
                                            totalPrice += prices!!.price * prices!!.number
                                        }
                                        setMessage("${totalPrice}円")
                                        setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                                            // OKをタップしたときの処理
                                            for (menu in dataSnapshot.children) {
                                                val menuName = menu.key!!
                                                val orderAfter = menu.getValue(Order::class.java)
                                                database.getReference("order").child(order!!.uid).child("after")
                                                    .child(seat.getValue("SEAT")).child(menuName).setValue(orderAfter)
                                                database.getReference("order").child(order!!.uid).child("before")
                                                    .child(seat.getValue("SEAT")).removeValue()
                                            }
                                            Toast.makeText(context, "Dialog OK", Toast.LENGTH_LONG).show()
                                        })
                                        setNegativeButton("Cancel", null)
                                        alertCount = false
                                        show()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read value
                                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                            }
                        })
                    } else {
                        //親要素が長押しされた時のアクションを記述
                    }

                    false
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
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
        fun newInstance(): VoucherFragment {
            return VoucherFragment()
        }
    }
}