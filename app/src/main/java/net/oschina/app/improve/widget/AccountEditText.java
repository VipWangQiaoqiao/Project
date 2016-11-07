package net.oschina.app.improve.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import net.oschina.app.R;

import java.util.regex.Pattern;


/**
 * Created by fei
 * on 2016/11/4.
 * desc:
 */

public class AccountEditText extends EditText implements TextWatcher, View.OnFocusChangeListener, View.OnTouchListener {

    private static final String TAG = "AccountEditText";

    public static final int NO_MATCH = 0x00;
    public static final int MATCH_EMAIL = 0x01;
    public static final int MATCH_TELEPHONE = 0x02;
    public static final int MATCH_EMAIL_TELEPHONE = 0x03;

    private OnDelTextCallback mOnDelTextCallback;

    private Paint mBgPaint;
    private BitmapDrawable mLeftDrawable;
    private BitmapDrawable mRightDrawable;


    float left = 0.0f;
    float top = 0.0f;
    float right = 0.0f;
    float startX = 0.0f;
    float startY = 0.0f;
    float endX = 0.0f;
    float endY = 0.0f;


    private int mMatchType = NO_MATCH;
    private boolean mShowDel;
    private int mWidth;
    private int mHeight;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;


    public AccountEditText(Context context) {
        super(context);
        initLabelAndDel();
        initAttribute(context, null);
        initBgPaint();
        initBg();
        initListener();
    }

    public AccountEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLabelAndDel();
        initAttribute(context, attrs);
        initBgPaint();
        initBg();
        initListener();
    }

    public AccountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLabelAndDel();
        initAttribute(context, attrs);
        initBgPaint();
        initBg();
        initListener();
    }

    private void initAttribute(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AccountEditText);

        int matchType = typedArray.getInt(R.styleable.AccountEditText_matchType, NO_MATCH);
        machType2InputType(matchType);
        this.mMatchType = matchType;

        this.mLeftDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.AccountEditText_labelDrawable);
        this.mRightDrawable = (BitmapDrawable) typedArray.getDrawable(R.styleable.AccountEditText_delDrawable);
        typedArray.recycle();

    }

    private void initListener() {
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
        setOnTouchListener(this);
    }

    @SuppressWarnings("deprecation")
    private void initLabelAndDel() {

        Drawable leftDrawable = getResources().getDrawable(R.mipmap.ic_signup_sms);
        this.mLeftDrawable = (BitmapDrawable) leftDrawable;

        Drawable rightDrawable = getResources().getDrawable(R.mipmap.ic_clear);
        this.mRightDrawable = (BitmapDrawable) rightDrawable;
    }

    private void initBg() {
        setBackgroundResource(R.drawable.bg_login_input_ok);
    }

    @SuppressWarnings("deprecation")
    private void initBgPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.white));
        this.mBgPaint = paint;
    }

    public void setOnDelTextCallback(OnDelTextCallback onDelTextCallback) {
        mOnDelTextCallback = onDelTextCallback;
    }

    /**
     * match type
     *
     * @param matchType matchType
     */
    public void setMatchType(int matchType) {
        machType2InputType(matchType);
        this.mMatchType = matchType;
    }

    private void machType2InputType(int matchType) {
        switch (matchType) {
            case MATCH_EMAIL:
                setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case MATCH_TELEPHONE:
                setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            default:
                setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int width = getWidth();
        int height = getHeight();

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        this.mWidth = width;
        this.mHeight = height;
        this.mPaddingRight = paddingRight;
        this.mPaddingTop = paddingTop;
        this.mPaddingBottom = paddingBottom;

        left = paddingLeft / 4;
        top = paddingTop;
        right = width - paddingRight + paddingRight / 4;

        int minimumWidth = mLeftDrawable.getMinimumWidth();
        int minimumHeight = mLeftDrawable.getMinimumHeight();

        startX = paddingLeft * 0.75f + left * 0.5f;
        startY = paddingTop;
        endX = startX;
        endY = paddingTop + minimumHeight;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mLeftDrawable.getBitmap(), left, top, mLeftDrawable.getPaint());
        canvas.drawLine(startX, startY, endX, endY, mBgPaint);
        if (mShowDel)
            canvas.drawBitmap(mRightDrawable.getBitmap(), right, top, mRightDrawable.getPaint());
    }


    private boolean matchTelephone(CharSequence phoneNumber) {
        String regex = "^[1][34578][0-9]\\d{8}$";
        // Pattern pattern = Pattern.compile(regex);
        // pattern.matcher(phoneNumber).matches();

        //第二种就是对一种的一种封装
        return Pattern.matches(regex, phoneNumber);
    }

    private boolean matchEmail(CharSequence email) {
        String regex = "^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$";
        return Pattern.matches(regex, email);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = s.toString();
        if (TextUtils.isEmpty(input)) {
            setBackgroundResource(R.drawable.bg_login_input_ok);
            setActivated(true);
            invalidate();
            mShowDel = false;
        } else {
            switch (mMatchType) {
                case MATCH_EMAIL:
                    //匹配email
                    if (matchEmail(input)) {
                        setBackgroundResource(R.drawable.bg_login_input_ok);
                    } else {
                        setBackgroundResource(R.drawable.bg_login_input_error);
                    }
                    break;
                case MATCH_TELEPHONE:
                    //匹配telephone
                    if (matchTelephone(input)) {
                        setBackgroundResource(R.drawable.bg_login_input_ok);
                    } else {
                        setBackgroundResource(R.drawable.bg_login_input_error);
                    }
                    break;
                case MATCH_EMAIL_TELEPHONE:
                    //匹配email ||  telephone
                    if (matchEmail(input) || matchTelephone(input)) {
                        setBackgroundResource(R.drawable.bg_login_input_ok);
                    } else {
                        setBackgroundResource(R.drawable.bg_login_input_error);
                    }
                    break;
                default:
                    //未匹配规则
                    break;
            }
            setActivated(true);
            invalidate();
            mShowDel = true;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setActivated(true);
        } else {
            setActivated(false);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mShowDel) {
            int actionMasked = event.getActionMasked();
            switch (actionMasked) {
                case MotionEvent.ACTION_UP:
                    float upX = event.getX();
                    float upY = event.getY();
                    if (upX <= mWidth && (upX >= right) && (upY >= top && upY <= mHeight)) {
                        if (mOnDelTextCallback != null) mOnDelTextCallback.delText();
                    }
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    public interface OnDelTextCallback {
        void delText();
    }
}
