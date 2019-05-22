package com.gmail.vanyadubyk.freeride.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmail.vanyadubyk.freeride.R
import com.gmail.vanyadubyk.freeride.model.dto.Poi
import java.util.ArrayList

import kotlinx.android.synthetic.main.search_result_item.view.*

class ResultSearchAdapter(mContext: Context, private val clickListener: ClickListener) : RecyclerView.Adapter<ResultSearchAdapter.ViewHolder>() {


    private var mContext: Context? = null
    private var list: List<Poi> = ArrayList<Poi>()
    private var layoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun setList(list: List<Poi>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    fun clear() {
        this.list = ArrayList<Poi>()
        this.notifyDataSetChanged()
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val title = view.title
        val uppendText = view.uppendText
        val address = view.address

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ResultSearchAdapter.ViewHolder {
        return ViewHolder(layoutInflater!!.inflate(R.layout.search_result_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ResultSearchAdapter.ViewHolder, p1: Int) {

        val item = getSelectedItem(p1)
        if(item!=null) {
            holder.title?.text = item.name
            holder.address?.text = item.address
            holder.title?.setOnClickListener {
                clickListener.onItemClick(p1) }
            holder.uppendText?.setOnClickListener {
                clickListener.onClickUpperText(p1)
            }
        }
    }



    override fun getItemCount(): Int {
        return list!!.size
    }

    fun getSelectedItem(position: Int): Poi? {
        return list!![position]
    }

    interface ClickListener {
        fun onItemClick(position: Int)
        fun onClickUpperText(position: Int)
    }

}
