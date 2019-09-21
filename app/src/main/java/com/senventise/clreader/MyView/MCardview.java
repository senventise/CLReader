package com.senventise.clreader.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;

import com.senventise.clreader.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MCardview extends CardView {

    TextView cardTitle, cardDesp;
    LinearLayoutCompat linearLayoutCompat;

    public MCardview(@NonNull Context context) {
        super(context);
    }

    public MCardview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MCardview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, AttributeSet attributeSet){
        inflate(context, R.layout.mcardview, this);
        cardTitle = findViewById(R.id.cardTitle);
        cardDesp = findViewById(R.id.cardDesp);
        linearLayoutCompat = findViewById(R.id.cardLayout);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MCardview);
        String title = typedArray.getString(R.styleable.MCardview_cardTitle);
        String desp = typedArray.getString(R.styleable.MCardview_cardDesp);
        if (title != null && desp != null){
            cardTitle.setText(title);
            cardDesp.setText(desp);
        }

        final String onClick = typedArray.getString(R.styleable.MCardview_onClick);
        if (onClick != null) {
            if (context.isRestricted()){
                throw new IllegalStateException("Cannot be used in a restrict context");
            }
            linearLayoutCompat.setOnClickListener(new OnClickListener() {
                private Method method;
                @Override
                public void onClick(View view) {
                    if (method == null){
                        try {
                            method = getContext().getClass().getMethod(onClick, View.class);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                            throw new IllegalStateException("Could not find the method.");
                        }
                    }
                    try {
                        method.invoke(getContext(), MCardview.this);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        typedArray.recycle();
    }

}
