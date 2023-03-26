package com.example.gps_location

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class Data (val context: Context, val UserList: ArrayList<place>) : BaseAdapter() {

    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.scroll_unit, null)
        val Name = view.findViewById<TextView>(R.id.name)
        val Call = view.findViewById<TextView>(R.id.call)
        val Add = view.findViewById<TextView>(R.id.add)
        val Img=view.findViewById<ImageView>(R.id.imageIcon)

        val user = UserList[position]

        Name.text = user.name
        Call.text = user.call
        Add.text = user.add
        val resourceId=context.resources.getIdentifier(user.img,"drawable",context.packageName)
        Img.setImageResource(resourceId)



        return view
    }

    override fun getItem(position: Int): Any {
        return UserList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return UserList.size
    }
}