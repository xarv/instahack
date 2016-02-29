package io.iwa.instamojo;


import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

/**
 * //TODO Add Class Description
 * Author: harshit  on 28/2/16.
 */
public class CustomViewGroup extends ViewGroup {
    private Context context;
    private int deviceWidth;


    public CustomViewGroup(Context context) {
        super(context);
        detectDeviceWidth(context);
    }

    public CustomViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        detectDeviceWidth(context);
    }

    public CustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        detectDeviceWidth(context);
    }

    private void detectDeviceWidth(Context context) {
        this.context = context;
        DisplayMetrics displayMetrics =  context.getResources().getDisplayMetrics();
        deviceWidth = displayMetrics.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        int mLeftWidth = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            maxWidth += Math.max(maxWidth, child.getMeasuredWidth());
            mLeftWidth += child.getMeasuredWidth();

            if ((mLeftWidth / deviceWidth) >= 1) {
                maxHeight += child.getMeasuredHeight();
                mLeftWidth = child.getMeasuredWidth();
            } else {
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        /*
            Method to position all children in the view group.
        */

        final int count = getChildCount();
        int childWidth, childHeight, curLeft, curTop, maxHeight;

        //get the available size of where child view can be contained.
        final int containerLeft = this.getPaddingLeft();
        final int containerTop = this.getPaddingTop();
        final int containerRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int containerBottom = this.getMeasuredHeight() - this.getPaddingBottom();

        final int containerWidth = containerRight - containerLeft; //width in which child views can appear.
        final int containerHeight = containerBottom - containerTop;//height in which child views can appear.

        maxHeight = 0;
        curLeft = containerLeft;
        curTop = containerTop;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                return;

            //Get the maximum size of the child using MesureSpec.AT_MOST flag.
            child.measure(MeasureSpec.makeMeasureSpec(containerWidth, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(containerHeight, MeasureSpec.AT_MOST));
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();

            //store the max height of the child.
            if (maxHeight < childHeight)
                maxHeight = childHeight;

            //when the child view is width combined with the current left layout position,
            //it is time to wrap the view group with new child and extend its height.

            if (curLeft + childWidth >= containerRight) {
                curLeft = containerLeft;
                curTop += maxHeight;
            }

            //call layout for the child. iterative call to child to layout itself.
            child.layout(curLeft, curTop, curLeft + childWidth, curTop + childHeight);
            //update position of curLeft.
            curLeft += childWidth;
        }
    }


}
