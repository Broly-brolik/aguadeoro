package com.aguadeoro.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;

public class EditTextDropdown extends EditText {
    String[] list;
    ListPopupWindow lpw = new ListPopupWindow(getContext());

    public EditTextDropdown(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextDropdown(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EditTextDropdown(Context context) {
        super(context);
    }

    public void setAdapter(ArrayAdapter<String> adapter) {
        lpw.setAdapter(adapter);
    }

    public String[] getList() {
        return list;
    }

    public void setList(String[] dropdownList) {
        this.list = dropdownList;
        lpw.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, list));
        lpw.setAnchorView(this);
        lpw.setModal(true);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getX() + 2 >= (getWidth() -
                            getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        lpw.show();
                        return true;
                    }
                }
                return false;
            }
        });
        lpw.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String item = list[position];
                setText(item);
                lpw.dismiss();
            }
        });
    }
}
