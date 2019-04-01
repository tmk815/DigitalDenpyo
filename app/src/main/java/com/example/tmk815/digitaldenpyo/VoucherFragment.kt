package com.example.tmk815.digitaldenpyo

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleExpandableListAdapter
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
        val myRef = database.getReference("order").child(order!!.uid)

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
                        for (count in newOrder.children) {
                            childData["NAME"] = count.key.toString()
                            childData["PRICE"] = count.value.toString() + "円"
                        }
                        // リストに文字を格納
                        childList.add(childData)
                    }
                    // 子要素全体用のリストに各グループごとデータを格納
                    allChildList.add(childList)
                }
                // アダプタを作る
                val adapter = SimpleExpandableListAdapter(
                    context,
                    parentList,
                    android.R.layout.simple_expandable_list_item_1,
                    arrayOf("SEAT"),
                    intArrayOf(android.R.id.text1, android.R.id.text2),
                    allChildList,
                    android.R.layout.simple_expandable_list_item_2,
                    arrayOf("NAME", "PRICE"),
                    intArrayOf(android.R.id.text1, android.R.id.text2)
                )
                //生成した情報をセット
                val lv = voucherList
                lv.setAdapter(adapter)
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