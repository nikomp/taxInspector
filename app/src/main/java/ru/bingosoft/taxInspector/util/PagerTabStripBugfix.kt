package ru.bingosoft.taxInspector.util

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.PagerTabStrip

class PagerTabStripBugfix: PagerTabStrip {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            heightMeasureSpec);
    }
}