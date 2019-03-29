package com.gmail.vanyadubik.freeride.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.common.Consts
import com.gmail.vanyadubik.freeride.model.dto.PoiReview
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_review_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReviewListAdapter(private val mContext: Context, private val clickListener: ClickListener) : RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {


    private var list: MutableList<PoiReview> = ArrayList<PoiReview>()
    private var layoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun setList(list: MutableList<PoiReview>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    fun addist(newList: MutableList<PoiReview>) {
        this.list.addAll(newList)
        this.notifyDataSetChanged()
    }

    fun clear() {
        this.list.clear()
        this.notifyDataSetChanged()
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        val guestImage = view.guestImage
        val guestStatusPoi = view.guestStatusPoi
        val guestDatePoi = view.guestDatePoi
        val guestCommentPoi = view.guestCommentPoi
        val guestNamePoi = view.guestNamePoi

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ReviewListAdapter.ViewHolder {
        return ViewHolder(layoutInflater!!.inflate(R.layout.layout_review_item, viewGroup, false))
    }

    override fun onBindViewHolder(holder: ReviewListAdapter.ViewHolder, p1: Int) {

        val item = getSelectedItem(p1)
        if(item!=null) {
            Picasso.get()
                    .load(item.user?.image)
                    .resizeDimen(R.dimen.guest_logo_mini_size, R.dimen.guest_logo_mini_size)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.guestImage)

            when (item.accessible) {
                Consts.EVALUATION_AVAILABLE -> {
                    mContext.resources?.getColor(R.color.weirdGreen)?.let { holder.guestStatusPoi.setTextColor(it) }
                    holder.guestStatusPoi.text = mContext.getString(R.string.available)
                }
                Consts.EVALUATION_TROUBLESOME -> {
                    mContext.resources?.getColor(R.color.orangeYellow)?.let { holder.guestStatusPoi.setTextColor(it) }
                    holder.guestStatusPoi.text = mContext.getString(R.string.troublesome)
                }
                Consts.EVALUATION_INACCESSIBLE -> {
                    mContext.resources?.getColor(R.color.coral)?.let { holder.guestStatusPoi.setTextColor(it) }
                    holder.guestStatusPoi.text = mContext.getString(R.string.inaccessible)
                }
            }
            holder.guestDatePoi?.text = SimpleDateFormat("dd.MM.yy").format(Date(item.date))
            holder.guestCommentPoi?.text = item.review
            holder.guestNamePoi?.text = if (item.user?.name.isNullOrEmpty())
                mContext.getString(R.string.guest) else item.user?.name
        }
    }



    override fun getItemCount(): Int {
        return list!!.size
    }

    fun getSelectedItem(position: Int): PoiReview? {
        return list!![position]
    }

    interface ClickListener {
        fun onItemClick(position: Int)
        fun onClickUpperText(position: Int)
    }

}
