package com.alxgrk.bachelorarbeit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.View;

import com.alxgrk.bachelorarbeit.resources.Timeslot;
import com.google.common.collect.Lists;

import java.util.Calendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class BookingView extends View {

    private Rect bounds;

    private Paint paint;

    @Getter
    @Setter
    private List<Timeslot> availables = Lists.newArrayList();

    @Getter
    @Setter
    private List<Timeslot> booked = Lists.newArrayList();

    private int lineHeightAvailables;

    private int lineHeightBooked;
    private float textSize;

    // visible for usage in editor
    protected BookingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextScaleX(0.95f);
    }

    public BookingView(Context context, @Nullable AttributeSet attrs,
                       List<Timeslot> availables, List<Timeslot> booked) {
        this(context, attrs);

        this.availables = availables;
        this.booked = booked;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bounds = new Rect(0, 0, w, h);

        lineHeightAvailables = h / 3;
        lineHeightBooked = 2 * h / 3;

        float strokeWidth = Math.max(5.0f, Math.max(w / 200, h / 200));
        paint.setStrokeWidth(strokeWidth);

        textSize = Math.max(50, Math.max(w / 20, h / 20));
        paint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int withPaddingRight = bounds.right - 10;
        int withPaddingLeft = bounds.left + 10;

        int borderLineHeight = bounds.top - bounds.bottom / 12;

        if (availables.size() > 0 && booked.size() > 0) {

            // draw the interval line and the text for availables timeslots
            canvas.drawText("Year"+availables.get(0).getBeginning().get(Calendar.DATE),
                    withPaddingLeft, bounds.top + textSize, paint);
            canvas.drawText(availables.get(availables.size()-1).getEnding().toString(),
                    withPaddingRight, bounds.top, paint);

            canvas.drawLine(withPaddingLeft, lineHeightAvailables,
                    withPaddingRight, lineHeightAvailables, paint);
            canvas.drawLine(withPaddingLeft, lineHeightAvailables - borderLineHeight,
                    withPaddingLeft, lineHeightAvailables + borderLineHeight, paint);
            canvas.drawLine(withPaddingRight, lineHeightAvailables - borderLineHeight,
                    withPaddingRight, lineHeightAvailables + borderLineHeight, paint);

            // draw the interval line for booked timeslots
            canvas.drawText(booked.get(0).getBeginning().toString(),
                    withPaddingLeft, bounds.bottom, paint);
            canvas.drawText(booked.get(booked.size()-1).getEnding().toString(),
                    withPaddingRight, bounds.bottom, paint);

            canvas.drawLine(withPaddingLeft, lineHeightBooked,
                    withPaddingRight, lineHeightBooked, paint);
            canvas.drawLine(withPaddingLeft, lineHeightBooked - borderLineHeight,
                    withPaddingLeft, lineHeightBooked + borderLineHeight, paint);
            canvas.drawLine(withPaddingRight, lineHeightBooked - borderLineHeight,
                    withPaddingRight, lineHeightBooked + borderLineHeight, paint);
        }
    }
}
