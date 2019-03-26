package com.gmail.vanyadubik.freeride.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.gmail.vanyadubik.freeride.R
import com.gmail.vanyadubik.freeride.common.Consts.TAGLOG

object ActivityUtils {

    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi / 160f)
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi / 160f)
    }

    fun getToolBarHeight(context: Context): Float {
        val attrs = intArrayOf(R.attr.actionBarSize)
        val ta = context.obtainStyledAttributes(attrs)
        val toolBarHeight = ta.getDimension(0, -1f)
        ta.recycle()
        return toolBarHeight
    }

    fun showMessage(mContext: Context?, textTitle: String, drawableIconTitle: Drawable?,
                    textMessage: String?) {
        if (mContext == null) return

        if (textMessage == null || textMessage.isEmpty()) {
            return
        }
        val builder = AlertDialog.Builder(mContext, R.style.WhiteDialogTheme)

        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val titleView = layoutInflater.inflate(R.layout.dialog_title, null)
        val imageTitle = titleView.findViewById<ImageView>(R.id.image_title)
        if (drawableIconTitle != null) {
            imageTitle.setImageDrawable(drawableIconTitle)
        }
        val titleTV = titleView.findViewById<TextView>(R.id.text_title)
        titleTV.text = if (TextUtils.isEmpty(textTitle))
            mContext.getString(R.string.questions_title_error)
        else
            textTitle
        builder.setCustomTitle(titleView)
        builder.setMessage(textMessage)

        builder.setNeutralButton(mContext.getString(R.string.questions_answer_ok)) { dialog, which -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        try {
            dialog.show()
        } catch (e: Exception) {
            Log.e(TAGLOG, e.toString())
        }

        val textView = dialog.findViewById<View>(android.R.id.message) as TextView?
        textView!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        val button1 = dialog.findViewById<View>(android.R.id.button1) as Button?
        button1!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        val button2 = dialog.findViewById<View>(android.R.id.button2) as Button?
        button2!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        val button3 = dialog.findViewById<View>(android.R.id.button3) as Button?
        button3!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))


    }

    fun showMessageWihtCallBack(mContext: Context?, textTitle: String, drawableIconTitle: Drawable?,
                                textMessage: String?, messageCallBack: MessageCallBack) {
        if (mContext == null) return

        if (textMessage == null || textMessage.isEmpty()) {
            return
        }
        val builder = AlertDialog.Builder(mContext, R.style.WhiteDialogTheme)

        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val titleView = layoutInflater.inflate(R.layout.dialog_title, null)
        val imageTitle = titleView.findViewById<ImageView>(R.id.image_title)
        if (drawableIconTitle != null) {
            imageTitle.setImageDrawable(drawableIconTitle)
        }
        val titleTV = titleView.findViewById<TextView>(R.id.text_title)
        titleTV.text = if (TextUtils.isEmpty(textTitle))
            mContext.getString(R.string.questions_title_error)
        else
            textTitle
        builder.setCustomTitle(titleView)
        builder.setMessage(textMessage)

        builder.setNeutralButton(mContext.getString(R.string.questions_answer_ok)) { dialog, which ->
            dialog.dismiss()
            messageCallBack.onPressOk()
        }
        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.show()

        val textView = dialog.findViewById<View>(android.R.id.message) as TextView?
        textView!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        val button1 = dialog.findViewById<View>(android.R.id.button1) as Button?
        button1!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        val button2 = dialog.findViewById<View>(android.R.id.button2) as Button?
        button2!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        val button3 = dialog.findViewById<View>(android.R.id.button3) as Button?
        button3!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))


    }

    fun showShortToast(mContext: Context?, message: String) {
        if (mContext == null) return
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    fun showLongToast(mContext: Context?, message: String) {
        if (mContext == null) return
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
    }

    fun showQuestion(mContext: Context?, textTitle: String?, drawableIconTitle: Drawable?,
                     textMessage: String?,
                     nameButton1: String, nameButton2: String, nameButton3: String,
                     questionAnswer: QuestionAnswer) {
        if (mContext == null) return
        if (textMessage == null || textMessage.isEmpty()) {
            return
        }
        val builder = AlertDialog.Builder(mContext, R.style.WhiteDialogTheme)

        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val titleView = layoutInflater.inflate(R.layout.dialog_title, null)
        val imageTitle = titleView.findViewById<ImageView>(R.id.image_title)
        if (drawableIconTitle == null) {
            imageTitle.visibility = View.GONE
        } else {
            imageTitle.setImageDrawable(drawableIconTitle)
        }
        val titleTV = titleView.findViewById<TextView>(R.id.text_title)
        titleTV.text = if (textTitle != null && !textTitle.isEmpty())
            textTitle
        else
            mContext.getString(R.string.questions_title_question)

        builder.setCustomTitle(titleView)
        builder.setMessage(textMessage)

        builder.setPositiveButton(if (TextUtils.isEmpty(nameButton1))
            mContext.getString(R.string.questions_answer_yes)
        else
            nameButton1) { dialog, which ->
            dialog.dismiss()
            questionAnswer.onPositiveAnsver()
        }

        builder.setNegativeButton(if (TextUtils.isEmpty(nameButton2))
            mContext.getString(R.string.questions_answer_no)
        else
            nameButton2) { dialog, which ->
            dialog.dismiss()
            questionAnswer.onNegativeAnsver()
        }

        if (!TextUtils.isEmpty(nameButton3)) {
            builder.setNeutralButton(nameButton3
            ) { dialog, which ->
                dialog.dismiss()
                questionAnswer.onNeutralAnsver()
            }
        }

        builder.setOnCancelListener { questionAnswer.onNegativeAnsver() }
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        val textView = dialog.findViewById<View>(android.R.id.message) as TextView?
        textView!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        val button1 = dialog.findViewById<View>(android.R.id.button1) as Button?
        button1!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        val button2 = dialog.findViewById<View>(android.R.id.button2) as Button?
        button2!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        val button3 = dialog.findViewById<View>(android.R.id.button3) as Button?
        button3!!.setTextColor(mContext.resources.getColor(R.color.colorAccent))
    }

    fun hideKeyboard(context: Context?) {
        if (context == null) return
        (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

    fun showKeyboard(context: Context?, view: View) {
        if (context == null) return
        (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun showSelectionList(mContext: Context?, textTitle: String?, drawableIconTitle: Drawable?,
                          listString: List<String>?, listItemClick: ListItemClick) {
        if (mContext == null) return
        if (listString == null) {
            return
        }

        val builder = AlertDialog.Builder(mContext, R.style.WhiteDialogTheme)
        builder.setTitle(if (textTitle != null && !textTitle.isEmpty()) textTitle else mContext.getString(R.string.questions_title_info))

        val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val titleView = layoutInflater.inflate(R.layout.dialog_title, null)
        val imageTitle = titleView.findViewById<ImageView>(R.id.image_title)
        if (drawableIconTitle == null) {
            imageTitle.setImageDrawable(mContext.resources.getDrawable(R.drawable.ic_playlist_add_check_white))
        } else {
            imageTitle.setImageDrawable(drawableIconTitle)
        }
        val titleTV = titleView.findViewById<TextView>(R.id.text_title)
        titleTV.text = if (textTitle != null && !textTitle.isEmpty())
            textTitle
        else
            mContext.getString(R.string.questions_select_from_list)
        builder.setCustomTitle(titleView)

        builder.setAdapter(ArrayAdapter(mContext,
                R.layout.row_sevice_item, R.id.textItem, listString)
        ) { dialog, which -> listItemClick.onItemClik(which, listString[which]) }

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    interface ListItemClick {

        fun onItemClik(item: Int, text: String)
    }

    fun showDatePicket(context: Context?, year: Int, monthOfYear: Int, dayOfMonth: Int,
                       datePicketSet: DatePicketSet) {
        if (context == null) return

        val dateDialog = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> datePicketSet.onDateSet(year, monthOfYear, dayOfMonth) }

        DatePickerDialog(context, dateDialog, year, monthOfYear, dayOfMonth).show()


    }

    interface DatePicketSet {

        fun onDateSet(year: Int, monthOfYear: Int, dayOfMonth: Int)
    }


    fun getHeightDisplay(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return metrics.heightPixels
    }

    fun getWidthDisplay(context: Context): Int {
        val metrics = context.resources.displayMetrics
        return metrics.widthPixels
    }

    interface MessageCallBack {

        fun onPressOk()

    }

    fun getOrientationDisplay(context: Context): Int {
        return context.resources.configuration.orientation
    }


    interface QuestionAnswer {

        fun onPositiveAnsver()

        fun onNegativeAnsver()

        fun onNeutralAnsver()

    }


}
