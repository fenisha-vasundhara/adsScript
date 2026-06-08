package com.messenger.phone.number.text.sms.service.apps.CustomEditText.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simplemobiletools.commons.helpers.SimpleContactsHelper;

import androidx.core.content.ContextCompat;

import com.messenger.phone.number.text.sms.service.apps.CustomEditText.model.ChipInterface;
import com.messenger.phone.number.text.sms.service.apps.CustomEditText.util.ColorUtil;
import com.messenger.phone.number.text.sms.service.apps.CustomEditText.util.LetterTileProvider;
import com.messenger.phone.number.text.sms.service.apps.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class DetailedChipView extends RelativeLayout {

    private static final String TAG = DetailedChipView.class.toString();
    // context
    Context mContext;
    // xml elements
    RelativeLayout mContentLayout;
    CircleImageView mAvatarIconImageView;
    TextView mNameTextView;
    TextView mInfoTextView;
    ImageButton mDeleteButton;
    // letter tile provider
    private static LetterTileProvider mLetterTileProvider;
    // attributes
    private ColorStateList mBackgroundColor;

    public DetailedChipView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public DetailedChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    /**
     * Inflate the view according to attributes
     *
     * @param attrs the attributes
     */
    private void init(AttributeSet attrs) {
        // inflate layout
        View rootView = inflate(getContext(), R.layout.detailed_chip_view, this);
        // butter knife
//        ButterKnife.bind(this, rootView);
        // letter tile provider
        mLetterTileProvider = new LetterTileProvider(mContext);

        mContentLayout = rootView.findViewById(R.id.content);
        mAvatarIconImageView = rootView.findViewById(R.id.avatar_icon);
        mNameTextView = rootView.findViewById(R.id.name);
        mInfoTextView = rootView.findViewById(R.id.info);
        mDeleteButton = rootView.findViewById(R.id.delete_button);

        // hide on first
        setVisibility(GONE);
        // hide on touch outside
        hideOnTouchOutside();
    }

    /**
     * Hide the view on touch outside of it
     */
    private void hideOnTouchOutside() {
        // set focusable
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
    }

    /**
     * Fade in
     */
    public void fadeIn() {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        startAnimation(anim);
        setVisibility(VISIBLE);
        // focus on the view
        requestFocus();
    }

    /**
     * Fade out
     */
    public void fadeOut() {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        startAnimation(anim);
        setVisibility(GONE);
        // fix onclick issue
        clearFocus();
        setClickable(false);
    }

    public void setAvatarIcon(Drawable icon) {
        mAvatarIconImageView.setImageDrawable(icon);
    }

    public void setAvatarIcon(Bitmap icon) {
        mAvatarIconImageView.setImageBitmap(icon);
    }

    public void setAvatarIcon(Uri icon) {
        mAvatarIconImageView.setImageURI(icon);
    }

    public void setName(String name) {
        mNameTextView.setText(name);
    }

    public void setInfo(String info) {
        if (info != null) {
            mInfoTextView.setVisibility(VISIBLE);
            mInfoTextView.setText(info);
        } else {
            mInfoTextView.setVisibility(GONE);
        }
    }

    public void setTextColor(ColorStateList color) {
        mNameTextView.setTextColor(color);
        mInfoTextView.setTextColor(ColorUtil.alpha(color.getDefaultColor(), 150));
    }

    public void setBackGroundcolor(ColorStateList color) {
        mBackgroundColor = color;
        mContentLayout.getBackground().setColorFilter(color.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
    }

    public int getBackgroundColor() {
        return mBackgroundColor == null ? ContextCompat.getColor(mContext, R.color.colorAccent) : mBackgroundColor.getDefaultColor();
    }

    public void setDeleteIconColor(ColorStateList color) {
        mDeleteButton.getDrawable().mutate().setColorFilter(color.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
    }

    public void setOnDeleteClicked(OnClickListener onClickListener) {
        mDeleteButton.setOnClickListener(onClickListener);
    }

    public void alignLeft() {
        LayoutParams params = (LayoutParams) mContentLayout.getLayoutParams();
        params.leftMargin = 0;
        mContentLayout.setLayoutParams(params);
    }

    public void alignRight() {
        LayoutParams params = (LayoutParams) mContentLayout.getLayoutParams();
        params.rightMargin = 0;
        mContentLayout.setLayoutParams(params);
    }

    public static class Builder {
        private Context context;
        private Uri avatarUri;
        private Drawable avatarDrawable;
        private String name;
        private String info;
        private ColorStateList textColor;
        private ColorStateList backgroundColor;
        private ColorStateList deleteIconColor;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder avatar(Uri avatarUri) {
            this.avatarUri = avatarUri;
            return this;
        }

        public Builder avatar(Drawable avatarDrawable) {
            this.avatarDrawable = avatarDrawable;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder info(String info) {
            this.info = info;
            return this;
        }

        public Builder chip(ChipInterface chip) {
            this.avatarUri = chip.getAvatarUri();
            this.avatarDrawable = chip.getAvatarDrawable();
            this.name = chip.getLabel();
            this.info = chip.getInfo();
            return this;
        }

        public Builder textColor(ColorStateList textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder backgroundColor(ColorStateList backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder deleteIconColor(ColorStateList deleteIconColor) {
            this.deleteIconColor = deleteIconColor;
            return this;
        }

        public DetailedChipView build() {
            return DetailedChipView.newInstance(this);
        }
    }

    private static DetailedChipView newInstance(Builder builder) {
        DetailedChipView detailedChipView = new DetailedChipView(builder.context);

        // avatar
//        if (builder.avatarUri != null)
//            detailedChipView.setAvatarIcon(builder.avatarUri);
//        else if (builder.avatarDrawable != null)
//            detailedChipView.setAvatarIcon(builder.avatarDrawable);
//        else
//            detailedChipView.setAvatarIcon(mLetterTileProvider.getLetterTile(builder.name));


        // background color
//        if (builder.backgroundColor != null)


        // text color
//        if (builder.textColor != null)
//            detailedChipView.setTextColor(builder.textColor);
//        else if (ColorUtil.isColorDark(detailedChipView.getBackgroundColor()))
//            detailedChipView.setTextColor(ColorStateList.valueOf(Color.WHITE));
//        else
//            detailedChipView.setTextColor(ColorStateList.valueOf(Color.BLACK));

        new SimpleContactsHelper(builder.context).loadContactImage(builder.avatarUri.toString(), detailedChipView.mAvatarIconImageView, builder.name, null);

        ColorStateList backgroundColor = builder.backgroundColor != null
                ? builder.backgroundColor
                : ColorStateList.valueOf(builder.context.getColor(R.color.appcolor));
        detailedChipView.setBackGroundcolor(backgroundColor);

        ColorStateList textColor = builder.textColor;
        if (textColor == null) {
            textColor = ColorUtil.isColorDark(backgroundColor.getDefaultColor())
                    ? ColorStateList.valueOf(Color.WHITE)
                    : ColorStateList.valueOf(Color.BLACK);
        }
        detailedChipView.setTextColor(textColor);

        ColorStateList deleteIconColor = builder.deleteIconColor != null ? builder.deleteIconColor : textColor;
        detailedChipView.setDeleteIconColor(deleteIconColor);


        detailedChipView.setName(builder.name);
        detailedChipView.setInfo(builder.info);
        return detailedChipView;
    }
}
