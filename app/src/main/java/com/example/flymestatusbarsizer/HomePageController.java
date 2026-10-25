package com.example.flymestatusbarsizer;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

final class HomePageController {
    private HomePageController() {
    }

    static void bind(MainActivity activity, LinearLayout root) {
        root.setPadding(
                root.getPaddingLeft(),
                root.getPaddingTop() + activity.statusBarInset(),
                root.getPaddingRight(),
                root.getPaddingBottom());
        root.addView(buildHeroCard(activity), PageViewUtils.matchWrap());
        root.addView(buildEntryCard(activity,
                "Icons & Battery",
                "Icon scaling, battery style, notification icons and signal controls",
                activity.primaryColor(),
                0x1A005CAE,
                v -> activity.openPage(MainActivity.Page.ICONS_BATTERY)),
                PageViewUtils.matchWrapWithTop(activity, 12));
        root.addView(buildEntryCard(activity,
                "Time & Network",
                "Network speed thresholds, time formats, clock actions and font",
                activity.secondaryColor(),
                0x1A006688,
                v -> activity.openPage(MainActivity.Page.TIME_NETWORK)),
                PageViewUtils.matchWrapWithTop(activity, 12));
        root.addView(buildEntryCard(activity,
                "System Interaction",
                "MBack Long-press actions, immersive navigation and keyboard toolbar",
                activity.tertiaryColor(),
                0x1A964500,
                v -> activity.openPage(MainActivity.Page.SYSTEM_INTERACTION)),
                PageViewUtils.matchWrapWithTop(activity, 12));
        root.addView(buildEntryCard(activity,
                "Advanced & Debugging",
                "Configuration, SystemUI actions, layout tuning and Telephony debugging",
                activity.errorColor(),
                0x1ABA1A1A,
                v -> activity.openPage(MainActivity.Page.ADVANCED_DEBUG)),
                PageViewUtils.matchWrapWithTop(activity, 12));
        root.addView(buildEntryCard(activity,
                "About & Support",
                "Project links, community, build information and module scope",
                activity.primaryDeepColor(),
                0x1A003B73,
                v -> activity.openPage(MainActivity.Page.ABOUT)),
                PageViewUtils.matchWrapWithTop(activity, 12));
    }

    private static View buildHeroCard(MainActivity activity) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(
                PageViewUtils.dp(activity, 20),
                PageViewUtils.dp(activity, 20),
                PageViewUtils.dp(activity, 20),
                PageViewUtils.dp(activity, 20));
        card.setBackground(buildHeroBackground(activity));

        TextView title = new TextView(activity);
        title.setText("FlymeBarSizer");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        card.addView(title, PageViewUtils.matchWrap());

        TextView restartButton = buildChip(activity, "Restart SystemUI", Color.WHITE, 0x26FFFFFF);
        restartButton.setPadding(
                PageViewUtils.dp(activity, 14),
                PageViewUtils.dp(activity, 10),
                PageViewUtils.dp(activity, 14),
                PageViewUtils.dp(activity, 10));
        activity.setTapClickListener(restartButton, v -> activity.restartSystemUi());
        card.addView(restartButton, PageViewUtils.wrapWrapWithTop(activity, 18));
        return card;
    }

    private static View buildEntryCard(
            MainActivity activity,
            String titleText,
            String summaryText,
            int accentColor,
            int accentBackground,
            View.OnClickListener listener) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(
                PageViewUtils.dp(activity, 16),
                PageViewUtils.dp(activity, 16),
                PageViewUtils.dp(activity, 16),
                PageViewUtils.dp(activity, 16));
        card.setMinimumHeight(PageViewUtils.dp(activity, 92));
        card.setBackground(buildOutlinedBackground(
                tintSurfaceColor(accentBackground),
                accentColor,
                18,
                activity));
        activity.setTapClickListener(card, listener);

        LinearLayout textGroup = new LinearLayout(activity);
        textGroup.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f);

        TextView title = new TextView(activity);
        title.setText(titleText);
        title.setTextColor(activity.textColor());
        title.setTextSize(17);
        textGroup.addView(title, PageViewUtils.matchWrap());

        TextView summary = new TextView(activity);
        summary.setText(summaryText);
        summary.setTextColor(activity.subtextColor());
        summary.setTextSize(13);
        summary.setPadding(0, PageViewUtils.dp(activity, 4), 0, 0);
        textGroup.addView(summary, PageViewUtils.matchWrap());
        card.addView(textGroup, textLp);

        TextView enterChip = buildChip(activity, "Open", accentColor, 0x14FFFFFF);
        enterChip.setPadding(
                PageViewUtils.dp(activity, 10),
                PageViewUtils.dp(activity, 6),
                PageViewUtils.dp(activity, 10),
                PageViewUtils.dp(activity, 6));
        LinearLayout.LayoutParams enterLp = PageViewUtils.wrapWrap();
        enterLp.rightMargin = PageViewUtils.dp(activity, 10);
        card.addView(enterChip, enterLp);

        TextView arrow = new TextView(activity);
        arrow.setText("›");
        arrow.setTextColor(accentColor);
        arrow.setTextSize(24);
        card.addView(arrow, PageViewUtils.wrapWrap());
        return card;
    }

    private static TextView buildChip(MainActivity activity, String text, int textColor, int backgroundColor) {
        TextView chip = new TextView(activity);
        chip.setText(text);
        chip.setTextColor(textColor);
        chip.setTextSize(12);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(
                PageViewUtils.dp(activity, 12),
                PageViewUtils.dp(activity, 6),
                PageViewUtils.dp(activity, 12),
                PageViewUtils.dp(activity, 6));
        chip.setBackground(buildSolidBackground(backgroundColor, 999, activity));
        return chip;
    }

    private static GradientDrawable buildHeroBackground(MainActivity activity) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{activity.primaryContainerColor(), activity.primaryColor(), activity.primaryDeepColor()});
        drawable.setCornerRadius(PageViewUtils.dp(activity, 24));
        return drawable;
    }

    private static GradientDrawable buildSolidBackground(int color, int radiusDp, MainActivity activity) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(PageViewUtils.dp(activity, radiusDp));
        return drawable;
    }

    private static GradientDrawable buildOutlinedBackground(
            int background,
            int stroke,
            int radiusDp,
            MainActivity activity) {
        GradientDrawable drawable = buildSolidBackground(background, radiusDp, activity);
        drawable.setStroke(PageViewUtils.dp(activity, 1), stroke);
        return drawable;
    }

    private static int tintSurfaceColor(int accentBackground) {
        int red = (255 * 9 + Color.red(accentBackground)) / 10;
        int green = (255 * 9 + Color.green(accentBackground)) / 10;
        int blue = (255 * 9 + Color.blue(accentBackground)) / 10;
        return Color.rgb(red, green, blue);
    }
}
