package com.viewpagerindicator;

import com.example.lib.marterial.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;

public class CirclePageIndicator
  extends View
  implements PageIndicator
{
  private static final int INVALID_POINTER = -1;
  private float mRadius;
  private final Paint mPaintPageFill = new Paint(1);
  private final Paint mPaintStroke = new Paint(1);
  private final Paint mPaintFill = new Paint(1);
  private ViewPager mViewPager;
  private ViewPager.OnPageChangeListener mListener;
  private int mCurrentPage;
  private int mSnapPage;
  private float mPageOffset;
  private int mScrollState;
  private int mOrientation;
  private boolean mCentered;
  private boolean mSnap;
  private int mTouchSlop;
  private float mLastMotionX = -1.0F;
  private int mActivePointerId = -1;
  private boolean mIsDragging;
  
  public CirclePageIndicator(Context context)
  {
    this(context, null);
  }
  
  public CirclePageIndicator(Context context, AttributeSet attrs)
  {
    this(context, attrs, R.attr.vpiCirclePageIndicatorStyle);
  }
  
  public CirclePageIndicator(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    if (isInEditMode()) {
      return;
    }
    Resources res = getResources();
    int defaultPageColor = res.getColor(R.color.default_circle_indicator_page_color);
    int defaultFillColor = res.getColor(R.color.default_circle_indicator_fill_color);
    int defaultOrientation = res.getInteger(R.integer.default_circle_indicator_orientation);
    int defaultStrokeColor = res.getColor(R.color.default_circle_indicator_stroke_color);
    float defaultStrokeWidth = res.getDimension(R.dimen.default_circle_indicator_stroke_width);
    float defaultRadius = res.getDimension(R.dimen.default_circle_indicator_radius);
    boolean defaultCentered = res.getBoolean(R.bool.default_circle_indicator_centered);
    boolean defaultSnap = res.getBoolean(R.bool.default_circle_indicator_snap);
    
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CirclePageIndicator, defStyle, 0);
    
    this.mCentered = a.getBoolean(2, defaultCentered);
    this.mOrientation = a.getInt(0, defaultOrientation);
    this.mPaintPageFill.setStyle(Paint.Style.FILL);
    this.mPaintPageFill.setColor(a.getColor(5, defaultPageColor));
    this.mPaintStroke.setStyle(Paint.Style.STROKE);
    this.mPaintStroke.setColor(a.getColor(8, defaultStrokeColor));
    this.mPaintStroke.setStrokeWidth(a.getDimension(3, defaultStrokeWidth));
    this.mPaintFill.setStyle(Paint.Style.FILL);
    this.mPaintFill.setColor(a.getColor(4, defaultFillColor));
    this.mRadius = a.getDimension(6, defaultRadius);
    this.mSnap = a.getBoolean(7, defaultSnap);
    
    Drawable background = a.getDrawable(1);
    if (background != null) {
      setBackgroundDrawable(background);
    }
    a.recycle();
    
    ViewConfiguration configuration = ViewConfiguration.get(context);
    this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
  }
  
  public void setCentered(boolean centered)
  {
    this.mCentered = centered;
    invalidate();
  }
  
  public boolean isCentered()
  {
    return this.mCentered;
  }
  
  public void setPageColor(int pageColor)
  {
    this.mPaintPageFill.setColor(pageColor);
    invalidate();
  }
  
  public int getPageColor()
  {
    return this.mPaintPageFill.getColor();
  }
  
  public void setFillColor(int fillColor)
  {
    this.mPaintFill.setColor(fillColor);
    invalidate();
  }
  
  public int getFillColor()
  {
    return this.mPaintFill.getColor();
  }
  
  public void setOrientation(int orientation)
  {
    switch (orientation)
    {
    case 0: 
    case 1: 
      this.mOrientation = orientation;
      requestLayout();
      break;
    default: 
      throw new IllegalArgumentException("Orientation must be either HORIZONTAL or VERTICAL.");
    }
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public void setStrokeColor(int strokeColor)
  {
    this.mPaintStroke.setColor(strokeColor);
    invalidate();
  }
  
  public int getStrokeColor()
  {
    return this.mPaintStroke.getColor();
  }
  
  public void setStrokeWidth(float strokeWidth)
  {
    this.mPaintStroke.setStrokeWidth(strokeWidth);
    invalidate();
  }
  
  public float getStrokeWidth()
  {
    return this.mPaintStroke.getStrokeWidth();
  }
  
  public void setRadius(float radius)
  {
    this.mRadius = radius;
    invalidate();
  }
  
  public float getRadius()
  {
    return this.mRadius;
  }
  
  public void setSnap(boolean snap)
  {
    this.mSnap = snap;
    invalidate();
  }
  
  public boolean isSnap()
  {
    return this.mSnap;
  }
  
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    if (this.mViewPager == null) {
      return;
    }
    int count = this.mViewPager.getAdapter().getCount();
    if (count == 0) {
      return;
    }
    if (this.mCurrentPage >= count)
    {
      setCurrentItem(count - 1); return;
    }
    int shortPaddingBefore;
    int longPaddingBefore;
    int longPaddingAfter;
    int longSize;
    if (this.mOrientation == 0)
    {
      longSize = getWidth();
      longPaddingBefore = getPaddingLeft();
      longPaddingAfter = getPaddingRight();
      shortPaddingBefore = getPaddingTop();
    }
    else
    {
      longSize = getHeight();
      longPaddingBefore = getPaddingTop();
      longPaddingAfter = getPaddingBottom();
      shortPaddingBefore = getPaddingLeft();
    }
    float threeRadius = this.mRadius * 3.0F;
    float shortOffset = shortPaddingBefore + this.mRadius;
    float longOffset = longPaddingBefore + this.mRadius;
    if (this.mCentered) {
      longOffset += (longSize - longPaddingBefore - longPaddingAfter) / 2.0F - count * threeRadius / 2.0F;
    }
    float pageFillRadius = this.mRadius;
    if (this.mPaintStroke.getStrokeWidth() > 0.0F) {
      pageFillRadius -= this.mPaintStroke.getStrokeWidth() / 2.0F;
    }
    for (int iLoop = 0; iLoop < count; iLoop++)
    {
      float drawLong = longOffset + iLoop * threeRadius;
      float dY;
      float dX;
      if (this.mOrientation == 0)
      {
        dX = drawLong;
        dY = shortOffset;
      }
      else
      {
        dX = shortOffset;
        dY = drawLong;
      }
      if (this.mPaintPageFill.getAlpha() > 0) {
        canvas.drawCircle(dX, dY, pageFillRadius, this.mPaintPageFill);
      }
      if (pageFillRadius != this.mRadius) {
        canvas.drawCircle(dX, dY, this.mRadius, this.mPaintStroke);
      }
    }
    float cx = (this.mSnap ? this.mSnapPage : this.mCurrentPage) * threeRadius;
    if (!this.mSnap) {
      cx += this.mPageOffset * threeRadius;
    }
    float dY;
    float dX;
  
    if (this.mOrientation == 0)
    {
      dX = longOffset + cx;
      dY = shortOffset;
    }
    else
    {
      dX = shortOffset;
      dY = longOffset + cx;
    }
    canvas.drawCircle(dX, dY, this.mRadius, this.mPaintFill);
  }
  
  public boolean onTouchEvent(MotionEvent ev)
  {
    if (super.onTouchEvent(ev)) {
      return true;
    }
    if ((this.mViewPager == null) || (this.mViewPager.getAdapter().getCount() == 0)) {
      return false;
    }
    int action = ev.getAction() & 0xFF;
    switch (action)
    {
    case 0: 
      this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
      this.mLastMotionX = ev.getX();
      break;
    case 2: 
      int activePointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
      float x = MotionEventCompat.getX(ev, activePointerIndex);
      float deltaX = x - this.mLastMotionX;
      if ((!this.mIsDragging) && 
        (Math.abs(deltaX) > this.mTouchSlop)) {
        this.mIsDragging = true;
      }
      if (this.mIsDragging)
      {
        this.mLastMotionX = x;
        if ((this.mViewPager.isFakeDragging()) || (this.mViewPager.beginFakeDrag())) {
          this.mViewPager.fakeDragBy(deltaX);
        }
      }
      break;
    case 1: 
    case 3: 
      if (!this.mIsDragging)
      {
        int count = this.mViewPager.getAdapter().getCount();
        int width = getWidth();
        float halfWidth = width / 2.0F;
        float sixthWidth = width / 6.0F;
        if ((this.mCurrentPage > 0) && (ev.getX() < halfWidth - sixthWidth))
        {
          if (action != 3) {
            this.mViewPager.setCurrentItem(this.mCurrentPage - 1);
          }
          return true;
        }
        if ((this.mCurrentPage < count - 1) && (ev.getX() > halfWidth + sixthWidth))
        {
          if (action != 3) {
            this.mViewPager.setCurrentItem(this.mCurrentPage + 1);
          }
          return true;
        }
      }
      this.mIsDragging = false;
      this.mActivePointerId = -1;
      if (this.mViewPager.isFakeDragging()) {
        this.mViewPager.endFakeDrag();
      }
      break;
    case 5: 
      int index = MotionEventCompat.getActionIndex(ev);
      this.mLastMotionX = MotionEventCompat.getX(ev, index);
      this.mActivePointerId = MotionEventCompat.getPointerId(ev, index);
      break;
    case 6: 
      int pointerIndex = MotionEventCompat.getActionIndex(ev);
      int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
      if (pointerId == this.mActivePointerId)
      {
        int newPointerIndex = pointerIndex == 0 ? 1 : 0;
        this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
      }
      this.mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, this.mActivePointerId));
    }
    return true;
  }
  
  public void setViewPager(ViewPager view)
  {
    if (this.mViewPager == view) {
      return;
    }
    if (this.mViewPager != null) {
      this.mViewPager.setOnPageChangeListener(null);
    }
    if (view.getAdapter() == null) {
      throw new IllegalStateException("ViewPager does not have adapter instance.");
    }
    this.mViewPager = view;
    this.mViewPager.setOnPageChangeListener(this);
    invalidate();
  }
  
  public void setViewPager(ViewPager view, int initialPosition)
  {
    setViewPager(view);
    setCurrentItem(initialPosition);
  }
  
  public void setCurrentItem(int item)
  {
    if (this.mViewPager == null) {
      throw new IllegalStateException("ViewPager has not been bound.");
    }
    this.mViewPager.setCurrentItem(item);
    this.mCurrentPage = item;
    invalidate();
  }
  
  public void notifyDataSetChanged()
  {
    invalidate();
  }
  
  public void onPageScrollStateChanged(int state)
  {
    this.mScrollState = state;
    if (this.mListener != null) {
      this.mListener.onPageScrollStateChanged(state);
    }
  }
  
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
  {
    this.mCurrentPage = position;
    this.mPageOffset = positionOffset;
    invalidate();
    if (this.mListener != null) {
      this.mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }
  }
  
  public void onPageSelected(int position)
  {
    if ((this.mSnap) || (this.mScrollState == 0))
    {
      this.mCurrentPage = position;
      this.mSnapPage = position;
      invalidate();
    }
    if (this.mListener != null) {
      this.mListener.onPageSelected(position);
    }
  }
  
  public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener)
  {
    this.mListener = listener;
  }
  
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
    if (this.mOrientation == 0) {
      setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
    } else {
      setMeasuredDimension(measureShort(widthMeasureSpec), measureLong(heightMeasureSpec));
    }
  }
  
  private int measureLong(int measureSpec)
  {
    int specMode = View.MeasureSpec.getMode(measureSpec);
    int specSize = View.MeasureSpec.getSize(measureSpec);
    int result;
    if ((specMode == 1073741824) || (this.mViewPager == null))
    {
      result = specSize;
    }
    else
    {
      int count = this.mViewPager.getAdapter().getCount();
      result = (int)(getPaddingLeft() + getPaddingRight() + count * 2 * this.mRadius + (count - 1) * this.mRadius + 1.0F);
      if (specMode == Integer.MIN_VALUE) {
        result = Math.min(result, specSize);
      }
    }
    return result;
  }
  
  private int measureShort(int measureSpec)
  {
    int specMode = View.MeasureSpec.getMode(measureSpec);
    int specSize = View.MeasureSpec.getSize(measureSpec);
    int result;
    if (specMode == 1073741824)
    {
      result = specSize;
    }
    else
    {
      result = (int)(2.0F * this.mRadius + getPaddingTop() + getPaddingBottom() + 1.0F);
      if (specMode == Integer.MIN_VALUE) {
        result = Math.min(result, specSize);
      }
    }
    return result;
  }
  
  public void onRestoreInstanceState(Parcelable state)
  {
    SavedState savedState = (SavedState)state;
    super.onRestoreInstanceState(savedState.getSuperState());
    this.mCurrentPage = savedState.currentPage;
    this.mSnapPage = savedState.currentPage;
    requestLayout();
  }
  
  public Parcelable onSaveInstanceState()
  {
    Parcelable superState = super.onSaveInstanceState();
    SavedState savedState = new SavedState(superState);
    savedState.currentPage = this.mCurrentPage;
    return savedState;
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    int currentPage;
    
    public SavedState(Parcelable superState)
    {
      super(superState);
    }
    
    private SavedState(Parcel in)
    {
      super(in);
      this.currentPage = in.readInt();
    }
    
    public void writeToParcel(Parcel dest, int flags)
    {
      super.writeToParcel(dest, flags);
      dest.writeInt(this.currentPage);
    }
    
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public CirclePageIndicator.SavedState createFromParcel(Parcel in)
      {
        return new CirclePageIndicator.SavedState(in);
      }
      
      public CirclePageIndicator.SavedState[] newArray(int size)
      {
        return new CirclePageIndicator.SavedState[size];
      }
    };
  }
}