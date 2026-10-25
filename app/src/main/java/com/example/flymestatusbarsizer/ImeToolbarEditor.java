package com.example.flymestatusbarsizer;

import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.flymestatusbarsizer.feature.ime.ImeToolbarSpec;

import java.util.ArrayList;

final class ImeToolbarEditor {
    private final MainActivity activity;
    private final ArrayList<String> buttonSlots = new ArrayList<>();

    private LinearLayout buttonContainer;
    private LinearLayout buttonPoolContainer;
    private LinearLayout buttonSlotContainer;
    private TextView applyButton;
    private TextView applyStateView;
    private String appliedSlotsSerialized = SettingsStore.DEFAULT_IME_CONTROL_BAR_BUTTON_SLOTS;

    ImeToolbarEditor(MainActivity activity) {
        this.activity = activity;
    }

    LinearLayout buildSettingsContent() {
        LinearLayout details = new LinearLayout(activity);
        details.setOrientation(LinearLayout.VERTICAL);

        activity.addSwitchRow(details, "Replace Default Keyboard Toolbar",
                "Replaces the current keyboard toolbar, removes its dark-gray background, syncs the keyboard background and merges tool buttons into the toolbar.",
                SettingsStore.KEY_IME_REPLACE_ORIGINAL_CONTROL_BAR,
                SettingsStore.DEFAULT_IME_REPLACE_ORIGINAL_CONTROL_BAR);
        activity.addDivider(details);
        activity.addApplySliderRow(details, "Keyboard Icon Size",
                "Adjusts icon occupancy in the seven bottom slots. Higher values make icons larger and fill more of the toolbar.",
                SettingsStore.KEY_IME_CONTROL_BAR_ICON_SCALE_PERCENT,
                SettingsStore.DEFAULT_IME_CONTROL_BAR_ICON_SCALE_PERCENT,
                60, 180, "%");
        activity.addDivider(details);
        activity.addApplySliderRow(details, "Keyboard Icon Opacity",
                "Adjusts keyboard-toolbar icon opacity. 100% is fully opaque; lower values are more transparent.",
                SettingsStore.KEY_IME_CONTROL_BAR_ICON_ALPHA_PERCENT,
                SettingsStore.DEFAULT_IME_CONTROL_BAR_ICON_ALPHA_PERCENT,
                10, 100, "%");
        activity.addDivider(details);
        activity.addProfileSectionHeader(details, "Button Position & Visibility",
                "Long-press and drag a button into a fixed slot to show it; drag it back to the button pool to hide it.");

        TextView hint = new TextView(activity);
        hint.setText("Slots correspond to actual Keyboard Toolbar positions from left to right. Dragging changes the draft; tap Apply to save and refresh the keyboard interface.");
        hint.setTextColor(activity.subtextColor());
        hint.setTextSize(13);
        hint.setPadding(0, activity.dp(10), 0, 0);
        details.addView(hint, activity.matchWrap());

        buttonContainer = new LinearLayout(activity);
        buttonContainer.setOrientation(LinearLayout.VERTICAL);
        buttonContainer.setPadding(0, activity.dp(12), 0, 0);
        details.addView(buttonContainer, activity.matchWrap());
        loadConfig();
        renderEditor();
        return details;
    }

    private void loadConfig() {
        appliedSlotsSerialized = activity.readStringSetting(
                SettingsStore.KEY_IME_CONTROL_BAR_BUTTON_SLOTS,
                SettingsStore.DEFAULT_IME_CONTROL_BAR_BUTTON_SLOTS);
        buttonSlots.clear();
        buttonSlots.addAll(ImeToolbarSpec.resolveButtonSlots(appliedSlotsSerialized));
    }

    private void renderEditor() {
        if (buttonContainer == null) {
            return;
        }
        buttonContainer.removeAllViews();
        TextView poolTitle = new TextView(activity);
        poolTitle.setText("Button Pool");
        poolTitle.setTextColor(activity.primaryColor());
        poolTitle.setTextSize(14);
        buttonContainer.addView(poolTitle, activity.matchWrap());

        TextView poolHint = new TextView(activity);
        poolHint.setText("Long-press and drag a button to a slot below to show it; drag a button back here to hide it.");
        poolHint.setTextColor(activity.subtextColor());
        poolHint.setTextSize(12);
        poolHint.setPadding(0, activity.dp(6), 0, 0);
        buttonContainer.addView(poolHint, activity.matchWrap());

        buttonPoolContainer = new LinearLayout(activity);
        buttonPoolContainer.setOrientation(LinearLayout.VERTICAL);
        buttonPoolContainer.setPadding(activity.dp(12), activity.dp(12), activity.dp(12), activity.dp(12));
        buttonPoolContainer.setBackground(buildPoolBackground(false));
        buttonPoolContainer.setOnDragListener(this::handlePoolDrag);
        buttonContainer.addView(buttonPoolContainer, activity.matchWrapWithTop(10));

        TextView slotTitle = new TextView(activity);
        slotTitle.setText("Seven Fixed Slots");
        slotTitle.setTextColor(activity.primaryColor());
        slotTitle.setTextSize(14);
        slotTitle.setPadding(0, activity.dp(16), 0, 0);
        buttonContainer.addView(slotTitle, activity.matchWrap());

        TextView slotHint = new TextView(activity);
        slotHint.setText("Slots map to toolbar positions from left to right. Empty slots remain reserved.");
        slotHint.setTextColor(activity.subtextColor());
        slotHint.setTextSize(12);
        slotHint.setPadding(0, activity.dp(6), 0, 0);
        buttonContainer.addView(slotHint, activity.matchWrap());

        buttonSlotContainer = new LinearLayout(activity);
        buttonSlotContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonSlotContainer.setPadding(0, activity.dp(10), 0, 0);
        buttonContainer.addView(buttonSlotContainer, activity.matchWrap());

        LinearLayout actionRow = new LinearLayout(activity);
        actionRow.setOrientation(LinearLayout.HORIZONTAL);
        actionRow.setGravity(Gravity.CENTER_VERTICAL);
        actionRow.setPadding(0, activity.dp(14), 0, 0);

        applyStateView = new TextView(activity);
        applyStateView.setTextColor(activity.subtextColor());
        applyStateView.setTextSize(12);
        actionRow.addView(applyStateView, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        applyButton = activity.filledButton("Apply", activity.primaryColor(), android.graphics.Color.WHITE);
        activity.setTapClickListener(applyButton, v -> applyConfig());
        actionRow.addView(applyButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonContainer.addView(actionRow, activity.matchWrap());
        syncEditor();
    }

    private void syncEditor() {
        renderPool();
        renderSlots();
        syncApplyState();
    }

    private void renderPool() {
        if (buttonPoolContainer == null) {
            return;
        }
        buttonPoolContainer.removeAllViews();
        ArrayList<String> poolButtons = ImeToolbarSpec.getAllButtons();
        for (String slotButton : buttonSlots) {
            if (!TextUtils.isEmpty(slotButton)) {
                poolButtons.remove(slotButton);
            }
        }
        if (poolButtons.isEmpty()) {
            TextView empty = new TextView(activity);
            empty.setText("All buttons are already assigned to the slots below.");
            empty.setTextColor(activity.subtextColor());
            empty.setTextSize(13);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, activity.dp(12), 0, activity.dp(12));
            buttonPoolContainer.addView(empty, activity.matchWrap());
            return;
        }
        for (int i = 0; i < poolButtons.size(); i += MainActivity.IME_CONTROL_BAR_POOL_ROW_ITEM_COUNT) {
            LinearLayout row = new LinearLayout(activity);
            row.setOrientation(LinearLayout.HORIZONTAL);
            if (i > 0) {
                row.setPadding(0, activity.dp(8), 0, 0);
            }
            for (int j = 0; j < MainActivity.IME_CONTROL_BAR_POOL_ROW_ITEM_COUNT; j++) {
                int index = i + j;
                if (index >= poolButtons.size()) {
                    View spacer = new View(activity);
                    row.addView(spacer, new LinearLayout.LayoutParams(0, 0, 1f));
                    continue;
                }
                TextView item = buildPoolItem(poolButtons.get(index));
                LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                if (j > 0) {
                    itemLp.leftMargin = activity.dp(8);
                }
                row.addView(item, itemLp);
            }
            buttonPoolContainer.addView(row, activity.matchWrap());
        }
    }

    private TextView buildPoolItem(String button) {
        TextView item = new TextView(activity);
        item.setText(ImeToolbarSpec.getButtonLabel(button));
        item.setTextColor(activity.textColor());
        item.setTextSize(14);
        item.setGravity(Gravity.CENTER);
        item.setMinHeight(activity.dp(48));
        item.setPadding(activity.dp(8), activity.dp(12), activity.dp(8), activity.dp(12));
        item.setBackground(activity.outlinedRect(activity.surfaceColor(), activity.strokeColor(), 1, 18));
        item.setOnLongClickListener(v -> startDrag(v, button, -1));
        return item;
    }

    private void renderSlots() {
        if (buttonSlotContainer == null) {
            return;
        }
        buttonSlotContainer.removeAllViews();
        for (int i = 0; i < ImeToolbarSpec.getButtonSlotCount(); i++) {
            String button = i < buttonSlots.size() ? buttonSlots.get(i) : null;
            View slotView = buildSlotView(i, button);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            if (i > 0) {
                lp.leftMargin = activity.dp(6);
            }
            buttonSlotContainer.addView(slotView, lp);
        }
    }

    private View buildSlotView(int slotIndex, String button) {
        LinearLayout slot = new LinearLayout(activity);
        slot.setOrientation(LinearLayout.VERTICAL);
        slot.setGravity(Gravity.CENTER);
        slot.setPadding(activity.dp(4), activity.dp(10), activity.dp(4), activity.dp(10));
        slot.setMinimumHeight(activity.dp(72));
        slot.setTag(Integer.valueOf(slotIndex));
        slot.setBackground(buildSlotBackground(!TextUtils.isEmpty(button), false));
        slot.setOnDragListener(this::handleSlotDrag);
        if (!TextUtils.isEmpty(button)) {
            slot.setOnLongClickListener(v -> startDrag(v, button, slotIndex));
        }

        TextView label = new TextView(activity);
        label.setText(TextUtils.isEmpty(button) ? "Empty Slot" : ImeToolbarSpec.getButtonLabel(button));
        label.setTextColor(TextUtils.isEmpty(button) ? activity.subtextColor() : activity.textColor());
        label.setTextSize(TextUtils.isEmpty(button) ? 11 : 13);
        label.setGravity(Gravity.CENTER);
        label.setMaxLines(2);
        slot.addView(label, activity.matchWrap());
        return slot;
    }

    private boolean startDrag(View view, String button, int sourceSlotIndex) {
        if (view == null || TextUtils.isEmpty(button)) {
            return false;
        }
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        ClipData data = ClipData.newPlainText(MainActivity.IME_CONTROL_BAR_DRAG_LABEL, button);
        DragState state = new DragState(view, button, sourceSlotIndex);
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.startDragAndDrop(data, shadow, state, 0);
        } else {
            view.startDrag(data, shadow, state, 0);
        }
        view.setAlpha(0.55f);
        return true;
    }

    private boolean handlePoolDrag(View target, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return event.getLocalState() instanceof DragState;
            case DragEvent.ACTION_DRAG_ENTERED:
                target.setBackground(buildPoolBackground(true));
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                target.setBackground(buildPoolBackground(false));
                return true;
            case DragEvent.ACTION_DROP:
                target.setBackground(buildPoolBackground(false));
                DragState state = event.getLocalState() instanceof DragState
                        ? (DragState) event.getLocalState() : null;
                if (state == null || state.sourceSlotIndex < 0) {
                    return false;
                }
                removeButtonFromSlots(state.button);
                syncEditor();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                target.setBackground(buildPoolBackground(false));
                restoreDraggedView(event.getLocalState());
                return true;
            default:
                return true;
        }
    }

    private boolean handleSlotDrag(View target, DragEvent event) {
        if (!(target.getTag() instanceof Integer)) {
            return false;
        }
        int slotIndex = (Integer) target.getTag();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return event.getLocalState() instanceof DragState;
            case DragEvent.ACTION_DRAG_ENTERED:
                target.setBackground(buildSlotBackground(isOccupied(slotIndex), true));
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                target.setBackground(buildSlotBackground(isOccupied(slotIndex), false));
                return true;
            case DragEvent.ACTION_DROP:
                target.setBackground(buildSlotBackground(isOccupied(slotIndex), false));
                DragState state = event.getLocalState() instanceof DragState
                        ? (DragState) event.getLocalState() : null;
                if (state == null) {
                    return false;
                }
                moveButtonToSlot(state.button, slotIndex);
                syncEditor();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                target.setBackground(buildSlotBackground(isOccupied(slotIndex), false));
                restoreDraggedView(event.getLocalState());
                return true;
            default:
                return true;
        }
    }

    private void moveButtonToSlot(String button, int targetSlotIndex) {
        if (TextUtils.isEmpty(button) || targetSlotIndex < 0 || targetSlotIndex >= buttonSlots.size()) {
            return;
        }
        int sourceSlotIndex = findButtonSlotIndex(button);
        if (sourceSlotIndex == targetSlotIndex) {
            return;
        }
        String replacedButton = buttonSlots.get(targetSlotIndex);
        if (sourceSlotIndex >= 0) {
            buttonSlots.set(sourceSlotIndex, replacedButton);
        }
        buttonSlots.set(targetSlotIndex, button);
    }

    private void removeButtonFromSlots(String button) {
        int slotIndex = findButtonSlotIndex(button);
        if (slotIndex >= 0) {
            buttonSlots.set(slotIndex, null);
        }
    }

    private int findButtonSlotIndex(String button) {
        if (TextUtils.isEmpty(button)) {
            return -1;
        }
        for (int i = 0; i < buttonSlots.size(); i++) {
            if (button.equals(buttonSlots.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean isOccupied(int slotIndex) {
        return slotIndex < buttonSlots.size() && !TextUtils.isEmpty(buttonSlots.get(slotIndex));
    }

    private android.graphics.drawable.GradientDrawable buildPoolBackground(boolean active) {
        int background = active ? activity.surfaceStrongColor() : activity.surfaceSoftColor();
        int stroke = active ? activity.featureStrokeColor() : activity.strokeColor();
        return activity.outlinedRect(background, stroke, 1, 22);
    }

    private android.graphics.drawable.GradientDrawable buildSlotBackground(boolean occupied, boolean active) {
        int background = active
                ? activity.surfaceStrongColor()
                : (occupied ? activity.featureSurfaceColor() : activity.surfaceColor());
        int stroke = active
                ? activity.featureStrokeColor()
                : (occupied ? activity.primaryContainerColor() : activity.strokeColor());
        return activity.outlinedRect(background, stroke, 1, 18);
    }

    private void restoreDraggedView(Object localState) {
        if (!(localState instanceof DragState)) {
            return;
        }
        View sourceView = ((DragState) localState).sourceView;
        if (sourceView != null) {
            sourceView.setAlpha(1f);
        }
    }

    private boolean hasPendingChanges() {
        return !TextUtils.equals(ImeToolbarSpec.serializeButtonSlots(buttonSlots), appliedSlotsSerialized);
    }

    private void syncApplyState() {
        boolean changed = hasPendingChanges();
        if (applyStateView != null) {
            applyStateView.setText(changed ? "There are unapplied changes" : "Current draft applied");
            applyStateView.setTextColor(changed ? activity.primaryColor() : activity.subtextColor());
        }
        if (applyButton != null) {
            applyButton.setEnabled(changed);
            applyButton.setAlpha(changed ? 1f : 0.55f);
        }
    }

    private void applyConfig() {
        if (!hasPendingChanges()) {
            activity.showToast("No new keyboard button changes");
            return;
        }
        saveConfig();
        syncApplyState();
        activity.showToast("Keyboard button layout applied");
    }

    private void saveConfig() {
        appliedSlotsSerialized = ImeToolbarSpec.serializeButtonSlots(buttonSlots);
        SharedPreferences.Editor editor = activity.prefs().edit();
        editor.putString(SettingsStore.KEY_IME_CONTROL_BAR_BUTTON_SLOTS, appliedSlotsSerialized);
        editor.remove(SettingsStore.KEY_IME_CONTROL_BAR_BUTTON_ORDER);
        editor.remove(SettingsStore.KEY_IME_CONTROL_BAR_HIDDEN_BUTTONS);
        editor.remove(SettingsStore.KEY_IME_CONTROL_BAR_ALIGNMENT);
        editor.apply();
        SettingsStore.notifyChanged(activity);
        activity.invalidatePreview();
    }

    private static final class DragState {
        private final View sourceView;
        private final String button;
        private final int sourceSlotIndex;

        private DragState(View sourceView, String button, int sourceSlotIndex) {
            this.sourceView = sourceView;
            this.button = button;
            this.sourceSlotIndex = sourceSlotIndex;
        }
    }
}
