package com.example.flymestatusbarsizer;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

final class SettingsCardFactory {
    private final MainActivity activity;

    SettingsCardFactory(MainActivity activity) {
        this.activity = activity;
    }

    View createIconSizingCard() {
        return buildStatusBarIconScaleCard();
    }

    View createBatterySettingsCard() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        activity.addSwitchRow(content, "Draw Battery Icon in Code",
                "When disabled, the original system battery icon and sizing are restored.",
                SettingsStore.KEY_BATTERY_CODE_DRAW_ENABLED,
                SettingsStore.DEFAULT_BATTERY_CODE_DRAW_ENABLED);
        activity.addDivider(content);
        activity.addChoiceRow(content, "Battery Icon Style",
                "Includes two code-drawn styles: iOS-like and One UI-like.",
                SettingsStore.KEY_BATTERY_ICON_STYLE,
                SettingsStore.DEFAULT_BATTERY_ICON_STYLE,
                new int[]{SettingsStore.BATTERY_STYLE_IOS, SettingsStore.BATTERY_STYLE_ONEUI},
                new String[]{"iOS-like", "One UI-like"});
        activity.addDivider(content);
        activity.addSwitchRow(content, "Show Battery Percentage",
                "When disabled, the battery icon is shown without a percentage number.",
                SettingsStore.KEY_BATTERY_LEVEL_TEXT_ENABLED,
                SettingsStore.DEFAULT_BATTERY_LEVEL_TEXT_ENABLED);
        activity.addDivider(content);
        LinearLayout hollowOptions = buildBatteryHollowOptions();
        hollowOptions.setVisibility(SettingsStore.readBoolean(
                activity.prefs(),
                SettingsStore.KEY_BATTERY_HOLLOW_ENABLED,
                SettingsStore.DEFAULT_BATTERY_HOLLOW_ENABLED) ? View.VISIBLE : View.GONE);
        activity.addSwitchRow(content, "Hollow Battery",
                "Enable the hollow battery style. You can also make the inner fill shrink with the remaining charge.",
                SettingsStore.KEY_BATTERY_HOLLOW_ENABLED,
                SettingsStore.DEFAULT_BATTERY_HOLLOW_ENABLED,
                (buttonView, isChecked) -> hollowOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        LinearLayout.LayoutParams hollowOptionsLp = activity.matchWrapWithTop(10);
        hollowOptionsLp.leftMargin = activity.dp(12);
        content.addView(hollowOptions, hollowOptionsLp);
        activity.addDivider(content);
        int[] batteryTextFontOptions = BatteryTextFontHelper.getAvailableFontOptions(activity);
        activity.addChoiceRow(content, "Battery Number Font",
                "Lists available system fonts and the bundled MiSansLatinVFNumber font.",
                SettingsStore.KEY_BATTERY_TEXT_FONT,
                SettingsStore.DEFAULT_BATTERY_TEXT_FONT,
                batteryTextFontOptions,
                BatteryTextFontHelper.getFontLabels(batteryTextFontOptions));
        return activity.buildSectionCard(
                "Battery Style",
                "Configure the status bar battery, percentage text and hollow-battery details here.",
                content);
    }

    View createNotificationSettingsCard() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        LinearLayout appIconOptions = buildNotificationAppIconOptions();
        appIconOptions.setVisibility(SettingsStore.readBoolean(
                activity.prefs(),
                SettingsStore.KEY_NOTIFICATION_APP_ICON_ENABLED,
                SettingsStore.DEFAULT_NOTIFICATION_APP_ICON_ENABLED) ? View.VISIBLE : View.GONE);
        activity.addSwitchRow(content, "Use App Icons for Notifications",
                "Use each third-party app's own status bar notification icon instead of Flyme's unified icon.",
                SettingsStore.KEY_NOTIFICATION_APP_ICON_ENABLED,
                SettingsStore.DEFAULT_NOTIFICATION_APP_ICON_ENABLED,
                (buttonView, isChecked) -> appIconOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        LinearLayout.LayoutParams optionsLp = activity.matchWrapWithTop(10);
        optionsLp.leftMargin = activity.dp(12);
        content.addView(appIconOptions, optionsLp);
        return activity.buildSectionCard(
                "Notification Icon",
                "Only the source, size and padding of third-party notification icons are changed here.",
                content);
    }

    View createSignalSettingsCard() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);

        activity.addSwitchRow(content, "Draw Signal Icons in Code",
                "When disabled, the original mobile signal and Wi-Fi icons and their sizing are restored.",
                SettingsStore.KEY_SIGNAL_CODE_DRAW_ENABLED,
                SettingsStore.DEFAULT_SIGNAL_CODE_DRAW_ENABLED);
        activity.addDivider(content);
        activity.addSwitchRow(content, "Redraw Wi-Fi Icon",
                "When signal-icon replacement is enabled, choose separately whether to replace the Wi-Fi icon.",
                SettingsStore.KEY_WIFI_CODE_DRAW_ENABLED,
                SettingsStore.DEFAULT_WIFI_CODE_DRAW_ENABLED);
        activity.addDivider(content);
        activity.addProfileSectionHeader(content, "Description",
                "Mobile signal icons are handled by the module. 5G/5GA labels follow the real network state or Telephony debug settings.");
        return activity.buildSectionCard(
                "Signal & Wi-Fi",
                "Mobile network icons are handled by the module, with an independent Wi-Fi toggle.",
                content);
    }

    View createConnectionRateSettingsCard() {
        return activity.buildSectionCard(
                "Live Network Speed",
                "Keeps the system's original sampling; only visibility thresholds and confirmation counts are adjusted here.",
                buildConnectionRateThresholdPage());
    }

    View createTimeExpressionSettingsCard() {
        return activity.buildSectionCard(
                "Time Format",
                "Build a time expression using buttons, then reorder and apply it on this page.",
                activity.clockExpressionEditor().buildPage());
    }

    View createTimeInteractionSettingsCard() {
        return activity.buildSectionCard(
                "Clock Interaction",
                "Controls the detailed popup opened by tapping the top-left clock. It closes automatically after 8 seconds by default.",
                buildTimeInteractionPage());
    }

    View createTimeTypographySettingsCard() {
        return activity.buildSectionCard(
                "Clock Font",
                "Adjust the weight and size of the status-bar clock, appended date and lock-screen carrier text.",
                buildTimeTypographyPage());
    }

    View createMBackActionSettingsCard() {
        return activity.buildSectionCard(
                "MBack Long-Press Action",
                "Handles only the long-press action; normal taps and other system actions remain unchanged.",
                buildMBackActionPage());
    }

    View createMBackNavigationSettingsCard() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(buildMBackImmersivePage(), activity.matchWrap());
        activity.addDivider(content);
        content.addView(buildMBackHeightPage(), activity.matchWrapWithTop(16));
        return activity.buildSectionCard(
                "Immersive Navigation & Height",
                "Configure transparent background, gesture-pill visibility, inset offset and navigation-bar height on one page.",
                content);
    }

    View createImeToolbarSettingsCard() {
        return activity.buildSectionCard(
                "Keyboard Toolbar",
                "Manage the keyboard toolbar, including icon size, opacity, vertical offset and applying button-layout drafts.",
                activity.imeToolbarEditor().buildSettingsContent());
    }

    View createAdvancedToolsCard() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        activity.addActionButtonRow(content, "Layout Tuning",
                "Open a dedicated page to fine-tune the positions of the clock, battery, signal, Wi-Fi and keyboard toolbar.",
                "Open", activity::showPositionTuningPage);
        activity.addDivider(content);
        activity.addActionButtonRow(content, "Telephony Debugging",
                "Open a dedicated debug page to simulate SIM count, default data SIM, network type and signal strength.",
                "Open", activity::showTelephonyDebugPage);
        return activity.buildSectionCard(
                "Advanced Tools",
                "For testing layout or Telephony behavior in more detail.",
                content);
    }

    View createConfigManagementCard() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        activity.addActionButtonRow(content, "Import Configuration",
                "Restore module settings from a JSON file. Existing settings will be overwritten.",
                "Import", activity::startImportConfig);
        activity.addDivider(content);
        activity.addActionButtonRow(content, "Export Configuration",
                "Export supported settings to JSON for backup or transfer to another device.",
                "Export", activity::startExportConfig);
        activity.addDivider(content);
        activity.addActionButtonRow(content, "Restore Defaults",
                "Clear the current SharedPreferences and reinitialize the interface with default values.",
                "Restore", activity::resetAllSettings);
        return activity.buildSectionCard(
                "Configuration Management",
                "Import, export and restore-default actions previously in the floating menu are now on the main page.",
                content);
    }

    View createPerformanceDebugCard() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        activity.addSwitchRow(content, "Enable Wi-Fi Performance Logging",
                "Enables detailed timing logs for Wi-Fi updates so refresh overhead can be analyzed in logcat.",
                SettingsStore.KEY_WIFI_PERF_LOGGING_ENABLED,
                SettingsStore.DEFAULT_WIFI_PERF_LOGGING_ENABLED);
        return activity.buildSectionCard(
                "Performance Debugging",
                "Only the Wi-Fi timing-log switch related to the current module implementation is provided.",
                content);
    }

    View createPositionTuningSettingsCard() {
        activity.positionTuningSliderBindings().clear();

        LinearLayout card = activity.card(activity.surfaceColor(), activity.strokeColor(), 28);
        activity.addProfileSectionHeader(card, "Clock and Notification Area",
                "Adjusts the status bar clock view's right padding and therefore the gap between the time and notification icons.");
        activity.addPositionOffsetSliderRow(card, "Clock Right Margin",
                "Adjusts the system's default 2dp right margin. Positive values increase the gap; negative values reduce it.",
                SettingsStore.KEY_CLOCK_RIGHT_PADDING_OFFSET_DP,
                SettingsStore.DEFAULT_CLOCK_RIGHT_PADDING_OFFSET_DP * 10);

        activity.addDivider(card);
        activity.addProfileSectionHeader(card, "Battery",
                "The following three options work only when the module draws the battery icon.");
        activity.addPositionOffsetSliderRow(card, "Battery Icon",
                "Vertical position of the overall battery outline. Default: 0dp.",
                SettingsStore.KEY_BATTERY_ICON_Y_OFFSET_DP,
                SettingsStore.DEFAULT_BATTERY_ICON_Y_OFFSET_DP * 10);
        activity.addDivider(card);
        activity.addPositionOffsetSliderRow(card, "Battery Percentage Display",
                "Adjusts only the battery percentage baseline. Default: 0dp.",
                SettingsStore.KEY_BATTERY_TEXT_Y_OFFSET_DP,
                SettingsStore.DEFAULT_BATTERY_TEXT_Y_OFFSET_DP * 10);
        activity.addDivider(card);
        activity.addPositionOffsetSliderRow(card, "Charging Bolt Icon",
                "Adjusts only the charging/fast-charging bolt's vertical position. Default: 0dp.",
                SettingsStore.KEY_BATTERY_BOLT_Y_OFFSET_DP,
                SettingsStore.DEFAULT_BATTERY_BOLT_Y_OFFSET_DP * 10);

        activity.addDivider(card);
        activity.addProfileSectionHeader(card, "Mobile Network",
                "These options affect only module-drawn mobile signal icons and 5G/5GA labels.");
        activity.addPositionOffsetSliderRow(card, "Single-Layer Signal Icon",
                "Vertical position of the signal bars when using one SIM. Default: 0dp.",
                SettingsStore.KEY_SIGNAL_SINGLE_Y_OFFSET_DP,
                SettingsStore.DEFAULT_SIGNAL_SINGLE_Y_OFFSET_DP * 10);
        activity.addDivider(card);
        activity.addPositionOffsetSliderRow(card, "5G / 5GA Label",
                "Adjusts only the vertical position of the 5G / 5GA text label. Default: 0dp.",
                SettingsStore.KEY_SIGNAL_BADGE_Y_OFFSET_DP,
                SettingsStore.DEFAULT_SIGNAL_BADGE_Y_OFFSET_DP * 10);
        activity.addDivider(card);
        activity.addPositionOffsetSliderRow(card, "Dual-Layer Signal Icon",
                "Vertical position of the combined dual-SIM signal graphic. Default: 0dp.",
                SettingsStore.KEY_SIGNAL_DUAL_Y_OFFSET_DP,
                SettingsStore.DEFAULT_SIGNAL_DUAL_Y_OFFSET_DP * 10);

        activity.addDivider(card);
        activity.addProfileSectionHeader(card, "Wi-Fi",
                "Adjusts only the module-drawn Wi-Fi icon. Default: 0dp.");
        activity.addPositionOffsetSliderRow(card, "Wi-Fi Icon",
                "Vertical position of the whole Wi-Fi icon. Default: 0dp.",
                SettingsStore.KEY_WIFI_Y_OFFSET_DP,
                SettingsStore.DEFAULT_WIFI_Y_OFFSET_DP * 10);

        activity.addDivider(card);
        activity.addProfileSectionHeader(card, "Keyboard Toolbar",
                "Fine-tune the overall vertical offset of the keyboard toolbar here.");
        activity.addPositionOffsetSliderRow(card, "Raise Keyboard Toolbar",
                "Positive values move it up; negative values move it down.；Use the slider for 1dp steps, or tap the value on the right to enter 0.1dp increments.",
                SettingsStore.KEY_IME_CONTROL_BAR_Y_OFFSET_DP,
                SettingsStore.DEFAULT_IME_CONTROL_BAR_Y_OFFSET_DP * 10);

        activity.addDivider(card);
        activity.addActionButtonRow(card, "Apply Adjustments",
                "Save all pending adjustments on this page and refresh the current status bar.",
                "Apply", activity::applyAllPositionOffsets);
        activity.addDivider(card);
        activity.addActionButtonRow(card, "Reset All to Zero",
                "Set all pending adjustments on this page to 0.0dp, then tap Apply above to update the status bar.",
                "Reset to Zero", activity::resetAllPositionOffsets);
        return card;
    }

    View createTelephonyDebugSettingsCard() {
        LinearLayout card = activity.card(activity.surfaceColor(), activity.strokeColor(), 28);
        activity.addProfileSectionHeader(card, "Debug Switch",
                "When enabled, remote preferences sync to SystemUI immediately. Configure both test SIMs before changing the simulated SIM count or default data SIM.");
        activity.addSwitchRow(card, "Enable Telephony Simulation",
                "When disabled, real Telephony results are restored while saved debug presets are kept.",
                SettingsStore.KEY_TELEPHONY_DEBUG_ENABLED,
                SettingsStore.DEFAULT_TELEPHONY_DEBUG_ENABLED);
        activity.addDivider(card);
        activity.addChoiceRow(card, "Simulated SIM Count",
                "Tests whether custom signal icons switch layout and visibility correctly with zero, one or two SIMs.",
                SettingsStore.KEY_TELEPHONY_DEBUG_SIM_COUNT,
                SettingsStore.DEFAULT_TELEPHONY_DEBUG_SIM_COUNT,
                new int[]{0, 1, 2},
                new String[]{"0 SIMs", "1 SIM", "2 SIMs"});
        activity.addDivider(card);
        activity.addChoiceRow(card, "Default Data SIM",
                "With two SIMs, the mobile network type and 5G label follow the SIM selected here.",
                SettingsStore.KEY_TELEPHONY_DEBUG_DEFAULT_DATA_SLOT,
                SettingsStore.DEFAULT_TELEPHONY_DEBUG_DEFAULT_DATA_SLOT,
                new int[]{
                        SettingsStore.TELEPHONY_DEBUG_DEFAULT_DATA_SLOT_NONE,
                        SettingsStore.TELEPHONY_DEBUG_DEFAULT_DATA_SLOT_CARD1,
                        SettingsStore.TELEPHONY_DEBUG_DEFAULT_DATA_SLOT_CARD2
                },
                new String[]{"None", "SIM 1", "SIM 2"});

        activity.addDivider(card);
        addTelephonyDebugSlotSection(card,
                "SIM 1",
                "First test SIM. Used by default in single-SIM mode; its signal level controls the upper bars in combined dual-SIM mode.",
                SettingsStore.KEY_TELEPHONY_DEBUG_SLOT1_NETWORK_PROFILE,
                SettingsStore.DEFAULT_TELEPHONY_DEBUG_SLOT1_NETWORK_PROFILE,
                SettingsStore.KEY_TELEPHONY_DEBUG_SLOT1_SIGNAL_LEVEL,
                SettingsStore.DEFAULT_TELEPHONY_DEBUG_SLOT1_SIGNAL_LEVEL);

        activity.addDivider(card);
        addTelephonyDebugSlotSection(card,
                "SIM 2",
                "Second test SIM. Used only when two SIMs are simulated; its signal level controls the lower dots in combined mode.",
                SettingsStore.KEY_TELEPHONY_DEBUG_SLOT2_NETWORK_PROFILE,
                SettingsStore.DEFAULT_TELEPHONY_DEBUG_SLOT2_NETWORK_PROFILE,
                SettingsStore.KEY_TELEPHONY_DEBUG_SLOT2_SIGNAL_LEVEL,
                SettingsStore.DEFAULT_TELEPHONY_DEBUG_SLOT2_SIGNAL_LEVEL);

        activity.addDivider(card);
        activity.addActionButtonRow(card, "Restore Real System",
                "Disables Telephony simulation without clearing the settings for the two test SIMs.",
                "Restore", activity::disableTelephonyDebug);
        return card;
    }

    private LinearLayout buildNotificationAppIconOptions() {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(activity);
        title.setText("Notification Icon Dimensions");
        title.setTextColor(activity.primaryColor());
        title.setTextSize(13);
        card.addView(title, activity.matchWrap());

        activity.addDivider(card);
        activity.addApplySliderRow(card, "Notification Icon Size",
                "Changes the width occupied by this notification icon view while keeping the system status-bar height unchanged.",
                SettingsStore.KEY_NOTIFICATION_APP_ICON_SIZE_DP,
                SettingsStore.DEFAULT_NOTIFICATION_APP_ICON_SIZE_DP,
                12, 28, "dp");
        activity.addDivider(card);
        activity.addApplySliderRow(card, "Icon Container Padding",
                "Changes this icon view's padding. Larger values move the icon closer to the center.",
                SettingsStore.KEY_NOTIFICATION_APP_ICON_PADDING_DP,
                SettingsStore.DEFAULT_NOTIFICATION_APP_ICON_PADDING_DP,
                0, 8, "dp");
        return card;
    }

    private View buildStatusBarIconScaleCard() {
        LinearLayout card = activity.card(activity.surfaceColor(), 24);

        TextView title = new TextView(activity);
        title.setText("Status Bar Icon Size");
        title.setTextColor(activity.textColor());
        title.setTextSize(18);
        card.addView(title, activity.matchWrap());

        TextView summary = new TextView(activity);
        summary.setText("Controls system status icons at the top right and module-drawn battery/signal icons. Notification icons, privacy indicators and privacy dots are excluded.");
        summary.setTextColor(activity.subtextColor());
        summary.setTextSize(14);
        summary.setPadding(0, activity.dp(6), 0, 0);
        card.addView(summary, activity.matchWrap());

        activity.addDivider(card);
        activity.addApplySliderRow(card, "All Status Bar Icons",
                "Default: 100%. Adjusts top-right system status icons and code-drawn battery and signal icons.",
                SettingsStore.KEY_STATUS_BAR_ICON_SCALE_PERCENT,
                SettingsStore.DEFAULT_STATUS_BAR_ICON_SCALE_PERCENT, 50, 200, "%");
        activity.addDivider(card);
        activity.addApplySliderRow(card, "Battery Percentage Size",
                "Changes only the percentage text inside the battery icon. Default: 100%.",
                SettingsStore.KEY_BATTERY_INNER_TEXT_SCALE_PERCENT,
                SettingsStore.DEFAULT_BATTERY_INNER_TEXT_SCALE_PERCENT, 50, 200, "%");
        return card;
    }

    private LinearLayout buildMBackActionPage() {
        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);

        activity.addProfileSectionHeader(page, "Long-Press Action",
                "Handles only mBack long-press actions, preserving normal taps and other system actions. Choose between a URL/Intent and the bottom clock popup.");
        activity.addSwitchRow(page, "Handle mBack Long Press",
                "Intercepts the Flyme SystemUI mBack/Home long-press action that normally opens AICY and runs the action configured here.",
                SettingsStore.KEY_MBACK_LONG_TOUCH_URL_ENABLED,
                SettingsStore.DEFAULT_MBACK_LONG_TOUCH_URL_ENABLED);
        activity.addDivider(page);
        activity.addChoiceRow(page, "Long-Press Action",
                "Choose to send a URL/Intent or show the full clock popup above mBack.",
                SettingsStore.KEY_MBACK_LONG_TOUCH_ACTION,
                SettingsStore.DEFAULT_MBACK_LONG_TOUCH_ACTION,
                new int[]{
                        SettingsStore.MBACK_LONG_TOUCH_ACTION_INTENT_URI,
                        SettingsStore.MBACK_LONG_TOUCH_ACTION_CLOCK_POPUP
                },
                new String[]{"URL / Intent", "Bottom Clock Popup"});
        activity.addDivider(page);
        activity.addTextSettingRow(page, "Target URL / Intent URI",
                "Only applies in URL/Intent mode. Supports https://, custom schemes and intent:// URIs. Tap the value on the right to edit; leave it blank to use the original AICY action.",
                SettingsStore.KEY_MBACK_LONG_TOUCH_INTENT_URI,
                SettingsStore.DEFAULT_MBACK_LONG_TOUCH_INTENT_URI,
                "Not Set");
        activity.addDivider(page);
        activity.addActionButtonRow(page, "Test URL / Intent",
                "Tests only the entered URL/Intent URI, not Bottom Clock Popup mode.",
                "Test Now", activity::testLaunchMBackIntent);
        return page;
    }

    private LinearLayout buildMBackImmersivePage() {
        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);

        activity.addProfileSectionHeader(page, "Immersion / Insets",
                "Adjust the transparent background and mBack background offset here. Transparency affects only the background layer; the inset moves the background upward and may affect how apps calculate the bottom area.");
        activity.addSwitchRow(page, "Transparent mBack Navigation Bar",
                "Makes the navigation-bar background behind mBack transparent without changing mBack's own rendering.",
                SettingsStore.KEY_MBACK_NAV_BAR_TRANSPARENT,
                SettingsStore.DEFAULT_MBACK_NAV_BAR_TRANSPARENT);
        activity.addDivider(page);
        activity.addSwitchRow(page, "Hide Gesture Pill",
                "Hides only the pill drawn by mBack. Long-press behavior and insets are unchanged; useful with a transparent background and offset.",
                SettingsStore.KEY_MBACK_HIDE_PILL,
                SettingsStore.DEFAULT_MBACK_HIDE_PILL);
        activity.addDivider(page);
        activity.addApplyInsetSliderRow(page, "mBack Inset Size",
                "The inset moves the mBack background upward. -1 keeps the system default, 0 adds no offset, and other values are in dp. It can also affect how apps calculate the bottom area.",
                SettingsStore.KEY_MBACK_INSET_SIZE,
                SettingsStore.DEFAULT_MBACK_INSET_SIZE, -1, 80);
        return page;
    }

    private LinearLayout buildMBackHeightPage() {
        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);

        activity.addProfileSectionHeader(page, "Navigation Bar Height",
                "This directly reduces the height of the mBack navigation-bar window, which can reduce interference from the transparent touch area with app buttons.");
        activity.addApplyInsetSliderRow(page, "mBack Navigation Bar Height",
                "Controls the height of the mBack navigation-bar window. -1 keeps the system default. Smaller values reduce the transparent touch area and may help prevent it from covering app buttons.",
                SettingsStore.KEY_MBACK_NAV_BAR_HEIGHT,
                SettingsStore.DEFAULT_MBACK_NAV_BAR_HEIGHT, -1, 80);
        return page;
    }

    private LinearLayout buildConnectionRateThresholdPage() {
        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);

        activity.addProfileSectionHeader(page, "Threshold Visibility",
                "Show and hide thresholds are configured separately. Visibility changes after consecutive samples are confirmed, reducing flicker while retaining GONE behavior.");
        activity.addSwitchRow(page, "Enable Threshold Visibility",
                "Show above the display threshold and hide below the hide threshold. This changes visibility only, not sampling.",
                SettingsStore.KEY_CONNECTION_RATE_AUTO_VISIBILITY_ENABLED,
                SettingsStore.DEFAULT_CONNECTION_RATE_AUTO_VISIBILITY_ENABLED);
        activity.addDivider(page);
        activity.addSliderRow(page, "Show Threshold",
                "Show only after this speed is reached for the required number of consecutive samples (KB/s).",
                SettingsStore.KEY_CONNECTION_RATE_SHOW_THRESHOLD_KB,
                SettingsStore.DEFAULT_CONNECTION_RATE_SHOW_THRESHOLD_KB, 0, 1024, "KB/s");
        activity.addDivider(page);
        activity.addSliderRow(page, "Hide Threshold",
                "Hide when the speed stays below this value (KB/s).",
                SettingsStore.KEY_CONNECTION_RATE_HIDE_THRESHOLD_KB,
                SettingsStore.DEFAULT_CONNECTION_RATE_HIDE_THRESHOLD_KB, 0, 1024, "KB/s");
        activity.addDivider(page);
        activity.addSliderRow(page, "Show Confirmation Count",
                "Number of consecutive samples above the display threshold required to show.",
                SettingsStore.KEY_CONNECTION_RATE_SHOW_SAMPLE_COUNT,
                SettingsStore.DEFAULT_CONNECTION_RATE_SHOW_SAMPLE_COUNT, 1, 5, "times");
        activity.addDivider(page);
        activity.addSliderRow(page, "Hide Confirmation Count",
                "Number of consecutive samples below the hide threshold required to hide.",
                SettingsStore.KEY_CONNECTION_RATE_HIDE_SAMPLE_COUNT,
                SettingsStore.DEFAULT_CONNECTION_RATE_HIDE_SAMPLE_COUNT, 1, 5, "times");
        return page;
    }

    private LinearLayout buildTimeTypographyPage() {
        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);

        activity.addProfileSectionHeader(page, "Font Weight / Size",
                "Adjust the weight and size of status-bar time, appended date and lock-screen carrier text here.");
        activity.addSwitchRow(page, "Bold Clock Text",
                "Applies the selected font weight to the status-bar clock and appended weekday/date.",
                SettingsStore.KEY_CLOCK_BOLD_ENABLED, SettingsStore.DEFAULT_CLOCK_BOLD_ENABLED);
        activity.addDivider(page);
        activity.addApplySliderRow(page, "Clock/Date Weight",
                "Affects only status-bar clock text. Range: 100–900.",
                SettingsStore.KEY_CLOCK_FONT_WEIGHT, SettingsStore.DEFAULT_CLOCK_FONT_WEIGHT, 100, 900, "");
        activity.addDivider(page);
        activity.addApplySliderRow(page, "Clock and Lock-Screen Carrier Font Size",
                "Controls the top-left clock, lock-screen carrier text and network-speed text size. Default: 100%.",
                SettingsStore.KEY_CLOCK_AND_CARRIER_TEXT_SIZE_PERCENT,
                SettingsStore.DEFAULT_CLOCK_AND_CARRIER_TEXT_SIZE_PERCENT, 50, 200, "%");
        return page;
    }

    private LinearLayout buildTimeInteractionPage() {
        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);

        activity.addProfileSectionHeader(page, "Main Status Bar Clock",
                "Applies only to the top-left main status-bar clock, not the lock-screen clock or other time widgets.");
        activity.addSwitchRow(page, "Show Details When Clock Is Tapped",
                "Tap the top-left clock to show the time and date. Pull down inside the popup for memory, battery and recent tasks; swipe up to close.",
                SettingsStore.KEY_CLOCK_DETAIL_POPUP_ENABLED,
                SettingsStore.DEFAULT_CLOCK_DETAIL_POPUP_ENABLED);
        activity.addDivider(page);
        LinearLayout actionGridEditorSection = new LinearLayout(activity);
        actionGridEditorSection.setOrientation(LinearLayout.VERTICAL);
        boolean actionGridEnabled = SettingsStore.readBoolean(
                activity.prefs(),
                SettingsStore.KEY_CLOCK_DETAIL_ACTION_GRID_ENABLED,
                SettingsStore.DEFAULT_CLOCK_DETAIL_ACTION_GRID_ENABLED);
        actionGridEditorSection.setVisibility(actionGridEnabled ? View.VISIBLE : View.GONE);
        activity.addSwitchRow(page, "Show Fixed App Shortcuts",
                "Adds up to five app shortcuts to the details panel. Candidates are shortcuts discoverable in Aicy Overview.",
                SettingsStore.KEY_CLOCK_DETAIL_ACTION_GRID_ENABLED,
                SettingsStore.DEFAULT_CLOCK_DETAIL_ACTION_GRID_ENABLED,
                (buttonView, isChecked) -> actionGridEditorSection.setVisibility(
                        isChecked ? View.VISIBLE : View.GONE));
        activity.addDivider(actionGridEditorSection);
        activity.addActionButtonRow(actionGridEditorSection, "Edit Shortcut Order",
                "Uses cached candidates first. Scan or refresh Aicy Overview shortcuts in the editor. Select up to five, long-press to reorder, then tap Apply to save.",
                "Edit", activity::showClockDetailActionGridEditor);
        page.addView(actionGridEditorSection, activity.matchWrap());
        return page;
    }

    private LinearLayout buildBatteryHollowOptions() {
        LinearLayout card = activity.card(activity.surfaceSoftColor(), activity.strokeColor(), 22);
        TextView title = new TextView(activity);
        title.setText("Hollow Battery");
        title.setTextColor(activity.primaryColor());
        title.setTextSize(13);
        card.addView(title, activity.matchWrap());
        activity.addDivider(card);
        activity.addSwitchRow(card, "Battery Fill Follows Charge",
                "When off, the inner fill stays full. When on, it shrinks with the remaining charge and the empty area stays gray.",
                SettingsStore.KEY_BATTERY_HOLLOW_FILL_FOLLOWS_LEVEL,
                SettingsStore.DEFAULT_BATTERY_HOLLOW_FILL_FOLLOWS_LEVEL);
        return card;
    }

    private void addTelephonyDebugSlotSection(LinearLayout root,
            String titleText,
            String subtitleText,
            String networkKey,
            int defaultNetworkValue,
            String signalKey,
            int defaultSignalValue) {
        activity.addProfileSectionHeader(root, titleText, subtitleText);
        activity.addChoiceRow(root, "Network Type",
                "Changes the network type reported by Telephony. 4G hides the 5G label; 5G, 5G CA, 5GA and 5G+ use their respective 5G paths.",
                networkKey,
                defaultNetworkValue,
                new int[]{
                        SettingsStore.TELEPHONY_DEBUG_NETWORK_PROFILE_OFFLINE,
                        SettingsStore.TELEPHONY_DEBUG_NETWORK_PROFILE_2G,
                        SettingsStore.TELEPHONY_DEBUG_NETWORK_PROFILE_3G,
                        SettingsStore.TELEPHONY_DEBUG_NETWORK_PROFILE_4G,
                        SettingsStore.TELEPHONY_DEBUG_NETWORK_PROFILE_5G,
                        SettingsStore.TELEPHONY_DEBUG_NETWORK_PROFILE_5G_CA,
                        SettingsStore.TELEPHONY_DEBUG_NETWORK_PROFILE_5GA,
                        SettingsStore.TELEPHONY_DEBUG_NETWORK_PROFILE_5G_PLUS
                },
                new String[]{"No Service", "2G", "3G", "4G", "5G", "5G CA", "5GA", "5G+"});
        activity.addDivider(root);
        activity.addChoiceRow(root, "Signal Strength",
                "Enter a standard signal level from 0 to 4. Code-drawn signal icons follow this level.",
                signalKey,
                defaultSignalValue,
                new int[]{0, 1, 2, 3, 4},
                new String[]{"0 slots", "1 slot", "2 slots", "3 slots", "4 slots"});
    }
}
