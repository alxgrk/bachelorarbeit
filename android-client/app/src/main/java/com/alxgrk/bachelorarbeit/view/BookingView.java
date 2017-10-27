package com.alxgrk.bachelorarbeit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.alxgrk.bachelorarbeit.resources.Timeslot;
import com.alxgrk.bachelorarbeit.util.TimeslotUtils;
import com.google.common.collect.Lists;

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

        int smallerPossibleStrokeWidth = Math.min(w / 200, h / 200);
        float strokeWidth = Math.max(5.0f, smallerPossibleStrokeWidth);
        paint.setStrokeWidth(strokeWidth);

        int smallerPossibleTextSize = Math.min(w / 20, h / 20);
        textSize = Math.max(45, smallerPossibleTextSize);
        paint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int withPaddingRight = bounds.right - 10;
        int withPaddingLeft = bounds.left + 10;

        int borderLineHeight = bounds.top - bounds.bottom / 12;

        if (availables.size() > 0 && booked.size() > 0) {

            // FIXME use relative value instead of "... - 150"
            // draw the interval line and the text for availables timeslots
            Timeslot firstAvailable = availables.get(0);
            Timeslot lastAvailable = availables.get(availables.size() - 1);
            canvas.drawText(TimeslotUtils.beginAndEndString(firstAvailable).first,
                    withPaddingLeft, bounds.top + textSize, paint);
            canvas.drawText(TimeslotUtils.beginAndEndString(lastAvailable).second,
                    withPaddingRight - 170, bounds.top + textSize, paint);

            // set color to green
            paint.setColor(Color.argb(120, 71, 193, 19));
            canvas.drawLine(withPaddingLeft, lineHeightAvailables,
                    withPaddingRight, lineHeightAvailables, paint);
            canvas.drawLine(withPaddingLeft, lineHeightAvailables - borderLineHeight,
                    withPaddingLeft, lineHeightAvailables + borderLineHeight, paint);
            canvas.drawLine(withPaddingRight, lineHeightAvailables - borderLineHeight,
                    withPaddingRight, lineHeightAvailables + borderLineHeight, paint);
            paint.setColor(Color.BLACK);

            // draw the interval line for booked timeslots
            Pair<Float, Float> beginAndEndPos = TimeslotUtils.relativeTo(availables, booked);
            float startX = bounds.right * beginAndEndPos.first;
            float stopX = bounds.right * beginAndEndPos.second;

            Timeslot firstBooked = booked.get(0);
            Timeslot lastBooked = booked.get(booked.size() - 1);
            canvas.drawText(TimeslotUtils.beginAndEndString(firstBooked).first,
                    Math.max(withPaddingLeft, startX - 170), bounds.bottom - textSize / 2, paint);
            canvas.drawText(TimeslotUtils.beginAndEndString(lastBooked).first,
                    Math.min(withPaddingRight - 170, stopX), bounds.bottom - textSize / 2, paint);

            // set color to red
            paint.setColor(Color.argb(120, 204, 34, 34));
            canvas.drawLine(startX, lineHeightBooked, stopX, lineHeightBooked, paint);
            canvas.drawLine(startX, lineHeightBooked - borderLineHeight,
                    startX, lineHeightBooked + borderLineHeight, paint);
            canvas.drawLine(stopX, lineHeightBooked - borderLineHeight,
                    stopX, lineHeightBooked + borderLineHeight, paint);
            paint.setColor(Color.BLACK);
        }
    }
}
