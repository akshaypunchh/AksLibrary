package com.akslibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.akslibrary.R;


public class TwoHintEditText extends RelativeLayout {

    EditText activeField;
    EditText nonActiveEditText;

    CharSequence hint;

    public TwoHintEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.two_hints_edit_text, this);
        activeField = (EditText) findViewById(R.id.active_edit_text);
        nonActiveEditText = (EditText) findViewById(R.id.non_active_edit_text);
        hint = nonActiveEditText.getHint();
        activeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nonActiveEditText.setHint(s.length() != 0 ? "" : hint);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.TwoHintEditText);
        String leftHint = styledAttrs
                .getString(R.styleable.TwoHintEditText_left_hint);
        String rightHint = styledAttrs
                .getString(R.styleable.TwoHintEditText_right_hint);
        String inputType = styledAttrs.getString(R.styleable.TwoHintEditText_inputtype);
        styledAttrs.recycle();

        if (leftHint != null) {
            activeField.setHint(leftHint);
        }

        if (rightHint != null) {
            nonActiveEditText.setHint(rightHint);
            hint = rightHint;
        }

        if (inputType != null) {
            if (inputType.equalsIgnoreCase("textPassword")) {
                activeField.setTransformationMethod(new PasswordTransformationMethod());
                nonActiveEditText.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                activeField.setTransformationMethod(null);
                nonActiveEditText.setTransformationMethod(null);
            }
        }

    }
}