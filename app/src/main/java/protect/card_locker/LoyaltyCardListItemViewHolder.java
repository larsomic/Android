package protect.card_locker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;

import protect.card_locker.databinding.LoyaltyCardLayoutBinding;

public class LoyaltyCardListItemViewHolder extends RecyclerView.ViewHolder {

    public TextView mStoreField, mNoteField, mBalanceField, mExpiryField;
    public ImageView mCardIcon, mStarBackground, mStarBorder, mTickIcon, mArchivedBackground;
    public MaterialCardView mRow, mIconLayout;
    public ConstraintLayout mStar, mArchived;
    public View mDivider;

    private int mIconBackgroundColor;
    private LoyaltyCardCursorAdapter lcCursorAdaptor;

    protected LoyaltyCardListItemViewHolder(LoyaltyCardLayoutBinding loyaltyCardLayoutBinding,
                                            LoyaltyCardCursorAdapter.CardAdapterListener inputListener,
                                            LoyaltyCardCursorAdapter cursorAdaptor) {
        super(loyaltyCardLayoutBinding.getRoot());
        View inputView = loyaltyCardLayoutBinding.getRoot();
        mRow = loyaltyCardLayoutBinding.row;
        mDivider = loyaltyCardLayoutBinding.infoDivider;
        mStoreField = loyaltyCardLayoutBinding.store;
        mNoteField = loyaltyCardLayoutBinding.note;
        mBalanceField = loyaltyCardLayoutBinding.balance;
        mExpiryField = loyaltyCardLayoutBinding.expiry;
        mIconLayout = loyaltyCardLayoutBinding.iconLayout;
        mCardIcon = loyaltyCardLayoutBinding.thumbnail;
        mStar = loyaltyCardLayoutBinding.star;
        mStarBackground = loyaltyCardLayoutBinding.starBackground;
        mStarBorder = loyaltyCardLayoutBinding.starBorder;
        mArchived = loyaltyCardLayoutBinding.archivedIcon;
        mArchivedBackground = loyaltyCardLayoutBinding.archiveBackground;
        mTickIcon = loyaltyCardLayoutBinding.selectedThumbnail;
        inputView.setOnLongClickListener(view -> {
            inputListener.onRowClicked(getAdapterPosition());
            inputView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        });
        lcCursorAdaptor = cursorAdaptor;
    }

    public void setExtraField(TextView field, String text, Integer color) {
        // If text is null, hide the field
        // If iconColor is null, use the default text and icon color based on theme
        if (text == null) {
            field.setVisibility(View.GONE);
            field.requestLayout();
            return;
        }

        int size = lcCursorAdaptor.mSettings.getFontSizeMax(lcCursorAdaptor.mSettings.getSmallFont());

        field.setVisibility(View.VISIBLE);
        field.setText(text);
        field.setTextSize(size);
        field.setTextColor(color != null ? color : MaterialColors.getColor(lcCursorAdaptor.mContext,
                R.attr.colorSecondary, ContextCompat.getColor(lcCursorAdaptor.mContext, lcCursorAdaptor.mDarkModeEnabled ? R.color.md_theme_dark_secondary : R.color.md_theme_light_secondary)));

        int drawableSize = dpToPx((size * 24) / 14, lcCursorAdaptor.mContext);
        mDivider.setVisibility(View.VISIBLE);
        field.setVisibility(View.VISIBLE);
        Drawable icon = field.getCompoundDrawables()[0];
        if (icon != null) {
            icon.mutate();
            icon.setBounds(0, 0, drawableSize, drawableSize);
            field.setCompoundDrawablesRelative(icon, null, null, null);

            if (color != null) {
                icon.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP));
            } else {
                icon.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(lcCursorAdaptor.mDarkModeEnabled ? Color.WHITE : Color.BLACK, BlendModeCompat.SRC_ATOP));
            }
        }

        field.requestLayout();
    }

    public void setStoreField(String text) {
        mStoreField.setText(text);
        mStoreField.setTextSize(lcCursorAdaptor.mSettings.getFontSizeMax(lcCursorAdaptor.mSettings.getMediumFont()));
        mStoreField.requestLayout();
    }

    public void setNoteField(String text) {
        if (text == null) {
            mNoteField.setVisibility(View.GONE);
        } else {
            mNoteField.setVisibility(View.VISIBLE);
            mNoteField.setText(text);
            mNoteField.setTextSize(lcCursorAdaptor.mSettings.getFontSizeMax(lcCursorAdaptor.mSettings.getSmallFont()));
        }
        mNoteField.requestLayout();
    }

    public void toggleCardStateIcon(boolean enableStar, boolean enableArchive, boolean colorByTheme) {
            /* the below code does not work in android 5! hence the change of drawable instead
            boolean needDarkForeground = Utils.needsDarkForeground(mIconBackgroundColor);
            Drawable borderDrawable = mStarBorder.getDrawable().mutate();
            Drawable backgroundDrawable = mStarBackground.getDrawable().mutate();
            DrawableCompat.setTint(borderDrawable, needsDarkForeground ? Color.BLACK : Color.WHITE);
            DrawableCompat.setTint(backgroundDrawable, needsDarkForeground ? Color.BLACK : Color.WHITE);
            mStarBorder.setImageDrawable(borderDrawable);
            mStarBackground.setImageDrawable(backgroundDrawable);
            */
        boolean dark = Utils.needsDarkForeground(mIconBackgroundColor);
        if (colorByTheme) {
            dark = !lcCursorAdaptor.mDarkModeEnabled;
        }

        if (dark) {
            mStarBorder.setImageResource(R.drawable.ic_unstarred_white);
            mStarBackground.setImageResource(R.drawable.ic_starred_black);
            mArchivedBackground.setImageResource(R.drawable.ic_baseline_archive_24_black);
        } else {
            mStarBorder.setImageResource(R.drawable.ic_unstarred_black);
            mStarBackground.setImageResource(R.drawable.ic_starred_white);
            mArchivedBackground.setImageResource(R.drawable.ic_baseline_archive_24);
        }

        if (enableStar) {
            mStar.setVisibility(View.VISIBLE);
        } else{
            mStar.setVisibility(View.GONE);
        }

        if (enableArchive) {
            mArchived.setVisibility(View.VISIBLE);
        } else{
            mArchived.setVisibility(View.GONE);
        }

        mStarBorder.invalidate();
        mStarBackground.invalidate();
        mArchivedBackground.invalidate();

    }

    public void setIconBackgroundColor(int color) {
        mIconBackgroundColor = color;
        mCardIcon.setBackgroundColor(color);
    }

    public int dpToPx(int dp, Context mContext) {
        Resources r = mContext.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }
}
