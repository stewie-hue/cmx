package com.example.flymestatusbarsizer;

import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

final class AboutPageController {
    private AboutPageController() {
    }

    static void bind(MainActivity activity, LinearLayout root) {
        root.addView(buildProjectCard(activity), PageViewUtils.matchWrap());
        root.addView(buildBuildCard(activity), PageViewUtils.matchWrapWithTop(activity, 12));
    }

    private static View buildProjectCard(MainActivity activity) {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(buildLinkRow(activity,
                "GitHub",
                "Repository URL",
                activity.githubUrl(),
                activity.githubUrl()),
                PageViewUtils.matchWrap());
        content.addView(buildDivider(activity), PageViewUtils.matchWrapWithTop(activity, 14));
        content.addView(buildLinkRow(activity,
                "Community Group",
                "QQ Group " + activity.qqGroupNumber(),
                "Join Group Chat",
                activity.qqGroupUrl()),
                PageViewUtils.matchWrapWithTop(activity, 14));
        return activity.buildSectionCard(
                "Project & Community",
                "Keeps repository and community links without adding another top-level navigation item.",
                content);
    }

    private static View buildBuildCard(MainActivity activity) {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(buildInfoRow(activity, "Current Version", BuildConfig.VERSION_NAME), PageViewUtils.matchWrap());
        content.addView(buildDivider(activity), PageViewUtils.matchWrapWithTop(activity, 14));
        content.addView(buildInfoRow(activity, "Build Date", BuildConfig.BUILD_DATE), PageViewUtils.matchWrapWithTop(activity, 14));
        content.addView(buildDivider(activity), PageViewUtils.matchWrapWithTop(activity, 14));
        content.addView(buildInfoRow(activity, "Target Scope", activity.supportedScopesSummary()), PageViewUtils.matchWrapWithTop(activity, 14));
        return activity.buildSectionCard(
                "Version & Scope",
                "Shows static build information and the target scope declared by the module.",
                content);
    }

    private static LinearLayout buildLinkRow(
            MainActivity activity,
            String titleText,
            String summaryText,
            String buttonText,
            String url) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(activity);
        title.setText(titleText);
        title.setTextColor(activity.textColor());
        title.setTextSize(16);
        row.addView(title, PageViewUtils.matchWrap());

        TextView summary = new TextView(activity);
        summary.setText(summaryText);
        summary.setTextColor(activity.subtextColor());
        summary.setTextSize(13);
        summary.setPadding(0, PageViewUtils.dp(activity, 4), 0, 0);
        row.addView(summary, PageViewUtils.matchWrap());

        TextView button = new TextView(activity);
        button.setText(buttonText);
        button.setTextColor(activity.primaryColor());
        button.setTextSize(13);
        button.setGravity(Gravity.CENTER);
        button.setPadding(
                PageViewUtils.dp(activity, 14),
                PageViewUtils.dp(activity, 10),
                PageViewUtils.dp(activity, 14),
                PageViewUtils.dp(activity, 10));
        button.setBackground(buildOutlinedButtonBackground(activity));
        button.setOnClickListener(v -> activity.openExternalLink(url));
        row.addView(button, PageViewUtils.matchWrapWithTop(activity, 12));
        return row;
    }

    private static LinearLayout buildInfoRow(MainActivity activity, String titleText, String valueText) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(activity);
        title.setText(titleText);
        title.setTextColor(activity.textColor());
        title.setTextSize(16);
        row.addView(title, PageViewUtils.matchWrap());

        TextView value = new TextView(activity);
        value.setText(valueText);
        value.setTextColor(activity.subtextColor());
        value.setTextSize(13);
        value.setPadding(0, PageViewUtils.dp(activity, 4), 0, 0);
        row.addView(value, PageViewUtils.matchWrap());
        return row;
    }

    private static TextView buildDivider(MainActivity activity) {
        TextView divider = new TextView(activity);
        divider.setHeight(PageViewUtils.dp(activity, 1));
        divider.setBackgroundColor(activity.strokeColor());
        return divider;
    }

    private static GradientDrawable buildOutlinedButtonBackground(MainActivity activity) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(activity.surfaceColor());
        drawable.setCornerRadius(PageViewUtils.dp(activity, 999));
        drawable.setStroke(PageViewUtils.dp(activity, 1), activity.featureStrokeColor());
        return drawable;
    }
}
