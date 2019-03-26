package com.gmail.vanyadubik.freeride.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.vanyadubik.freeride.R;

import java.util.List;

import static com.gmail.vanyadubik.freeride.common.Consts.TAGLOG;

public class ActivityUtils {

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static float getToolBarHeight(Context context) {
        int[] attrs = new int[] {R.attr.actionBarSize};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        float toolBarHeight = ta.getDimension(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    public static void showMessage(Context mContext, String textTitle, Drawable drawableIconTitle,
                                   String textMessage) {
        if(mContext == null) return;

        if (textMessage == null || textMessage.isEmpty()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.WhiteDialogTheme);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = layoutInflater.inflate(R.layout.dialog_title, null);
        ImageView imageTitle = titleView.findViewById(R.id.image_title);
        if(drawableIconTitle!=null){
            imageTitle.setImageDrawable(drawableIconTitle);
        }
        TextView titleTV = titleView.findViewById(R.id.text_title);
        titleTV.setText(TextUtils.isEmpty(textTitle) ?
                mContext.getString(R.string.questions_title_error) :
                textTitle);
        builder.setCustomTitle(titleView);
        builder.setMessage(textMessage);

        builder.setNeutralButton(mContext.getString(R.string.questions_answer_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            dialog.show();
        }catch (Exception e){
            Log.e(TAGLOG, e.toString());
        }

        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        Button button1 = (Button) dialog.findViewById(android.R.id.button1);
        button1.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        Button button2 = (Button) dialog.findViewById(android.R.id.button2);
        button2.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        Button button3 = (Button) dialog.findViewById(android.R.id.button3);
        button3.setTextColor(mContext.getResources().getColor(R.color.colorAccent));


    }

    public static void showMessageWihtCallBack(Context mContext, String textTitle, Drawable drawableIconTitle,
                                               String textMessage, MessageCallBack messageCallBack) {
        if(mContext == null) return;

        if (textMessage == null || textMessage.isEmpty()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.WhiteDialogTheme);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = layoutInflater.inflate(R.layout.dialog_title, null);
        ImageView imageTitle = titleView.findViewById(R.id.image_title);
        if(drawableIconTitle!=null){
            imageTitle.setImageDrawable(drawableIconTitle);
        }
        TextView titleTV = titleView.findViewById(R.id.text_title);
        titleTV.setText(TextUtils.isEmpty(textTitle) ?
                mContext.getString(R.string.questions_title_error) :
                textTitle);
        builder.setCustomTitle(titleView);
        builder.setMessage(textMessage);

        builder.setNeutralButton(mContext.getString(R.string.questions_answer_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                messageCallBack.onPressOk();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        Button button1 = (Button) dialog.findViewById(android.R.id.button1);
        button1.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        Button button2 = (Button) dialog.findViewById(android.R.id.button2);
        button2.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        Button button3 = (Button) dialog.findViewById(android.R.id.button3);
        button3.setTextColor(mContext.getResources().getColor(R.color.colorAccent));


    }

    public static void showShortToast(Context mContext, String message){
        if(mContext == null) return;
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context mContext, String message){
        if(mContext == null) return;
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    public static void showQuestion(Context mContext, String textTitle, Drawable drawableIconTitle,
                                    String textMessage,
                                    String nameButton1, String nameButton2, String nameButton3,
                                    final QuestionAnswer questionAnswer) {
        if(mContext == null) return;
        if (textMessage == null || textMessage.isEmpty()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.WhiteDialogTheme);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = layoutInflater.inflate(R.layout.dialog_title, null);
        ImageView imageTitle = titleView.findViewById(R.id.image_title);
        if(drawableIconTitle==null){
            imageTitle.setVisibility(View.GONE);
        }else {
            imageTitle.setImageDrawable(drawableIconTitle);
        }
        TextView titleTV = titleView.findViewById(R.id.text_title);
        titleTV.setText(textTitle != null && !textTitle.isEmpty() ? textTitle :
                mContext.getString(R.string.questions_title_question));

        builder.setCustomTitle(titleView);
        builder.setMessage(textMessage);

        builder.setPositiveButton(TextUtils.isEmpty(nameButton1) ?
                mContext.getString(R.string.questions_answer_yes) : nameButton1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                questionAnswer.onPositiveAnsver();
            }
        });

        builder.setNegativeButton(TextUtils.isEmpty(nameButton2) ?
                mContext.getString(R.string.questions_answer_no) : nameButton2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                questionAnswer.onNegativeAnsver();
            }
        });

        if(!TextUtils.isEmpty(nameButton3)) {
            builder.setNeutralButton(nameButton3,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            questionAnswer.onNeutralAnsver();
                        }
                    });
        }

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                questionAnswer.onNegativeAnsver();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        Button button1 = (Button) dialog.findViewById(android.R.id.button1);
        button1.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        Button button2 = (Button) dialog.findViewById(android.R.id.button2);
        button2.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        Button button3 = (Button) dialog.findViewById(android.R.id.button3);
        button3.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
    }

    public static void hideKeyboard(Context context){
        if(context == null) return;
        ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public static void showKeyboard(Context context, View view){
        if(context == null) return;
        ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE))
                .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showSelectionList(Context mContext, String textTitle, Drawable drawableIconTitle,
                                         final List<String> listString, final ListItemClick listItemClick) {
        if(mContext == null) return;
        if (listString == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.WhiteDialogTheme);
        builder.setTitle(textTitle != null && !textTitle.isEmpty() ? textTitle : mContext.getString(R.string.questions_title_info));

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View titleView = layoutInflater.inflate(R.layout.dialog_title, null);
        ImageView imageTitle = titleView.findViewById(R.id.image_title);
        if(drawableIconTitle==null){
            imageTitle.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_playlist_add_check_white));
        }else {
            imageTitle.setImageDrawable(drawableIconTitle);
        }
        TextView titleTV = titleView.findViewById(R.id.text_title);
        titleTV.setText(textTitle != null && !textTitle.isEmpty() ? textTitle :
                mContext.getString(R.string.questions_select_from_list));
        builder.setCustomTitle(titleView);

        builder.setAdapter(new ArrayAdapter<String>(mContext,
                        R.layout.row_sevice_item, R.id.textItem, listString),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listItemClick.onItemClik(which, listString.get(which));
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public interface ListItemClick {

        void onItemClik(int item, String text);
    }

    public static void showDatePicket(Context context, int year, int monthOfYear, int dayOfMonth,
                               final DatePicketSet datePicketSet){
        if(context == null) return;

        DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datePicketSet.onDateSet(year, monthOfYear, dayOfMonth);
            }
        };

        new DatePickerDialog(context, dateDialog, year, monthOfYear, dayOfMonth).show();


    }

    public interface DatePicketSet {

        void onDateSet(int year, int monthOfYear, int dayOfMonth);
    }




    public static int getHeightDisplay(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//        int orientation = context.getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return metrics.heightPixels;
//        } else {
//            return metrics.widthPixels;
//        }
    }

    public static int getWidthDisplay(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//        int orientation = context.getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//            return metrics.heightPixels;
//        } else {
            return metrics.widthPixels;
//        }
    }

    public interface MessageCallBack {

        void onPressOk();

    }

    public static int getOrientationDisplay(Context context){
        return context.getResources().getConfiguration().orientation;
    }


    public interface QuestionAnswer {

        void onPositiveAnsver();

        void onNegativeAnsver();

        void onNeutralAnsver();

    }


}
