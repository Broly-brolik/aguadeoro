package com.aguadeoro.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.aguadeoro.adapter.ArrayAdapterCustomFonts;

public class EditTextDropdownCustomFont extends EditText {

    public ListPopupWindow lpw = new ListPopupWindow(getContext());
    ArrayAdapterCustomFonts adapter;

    public EditTextDropdownCustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextDropdownCustomFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextDropdownCustomFont(Context context) {
        super(context);
    }

    public ArrayAdapterCustomFonts getAdapter() {
        return adapter;
    }

    public void setAdapter(ArrayAdapterCustomFonts adapter) {
        this.adapter = adapter;
        lpw.setAdapter(this.adapter);
        lpw.setAnchorView(this);
        lpw.setModal(true);
        lpw.setWidth(ListPopupWindow.MATCH_PARENT);
        this.setFocusable(false);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//		        final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
//		            if(event.getX() + 2>= (getWidth() -
//		                getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    lpw.show();
                    return true;
//		            }
                }
                return false;
            }
        });
        lpw.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                View row = EditTextDropdownCustomFont.this.adapter.getCustomView(position, null, null);
                TextView label = row.findViewById(android.R.id.text1);
                setText(label.getText().toString());
                lpw.dismiss();
            }
        });
    }
}
