package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RadioButton

class MyView : View {
    private var paint = Paint()
    private var x = 100F
    private var y = 100F
    private var drawMode = 0
    lateinit var mainActivity: MainActivity

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mainActivity = context as MainActivity

        paint.color = Color.BLUE

        if (drawMode == 0) {
            canvas.drawRect(x, y, x + 100, y + 100, paint)
        }
        else if (drawMode == 1) {
            canvas.drawCircle(x, y, 50F, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val radioRect = mainActivity.findViewById<RadioButton>(R.id.radioRect)
        val radioCircle = mainActivity.findViewById<RadioButton>(R.id.radioCircle)

        if (event.action == MotionEvent.ACTION_DOWN) {
            x = event.x
            y = event.y

            if (radioRect.isChecked) {
                drawMode = 0
            }
            if (radioCircle.isChecked) {
                drawMode = 1
            }

            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }
}