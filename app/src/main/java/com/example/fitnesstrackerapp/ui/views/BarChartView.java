package com.example.fitnesstrackerapp.ui.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BarChartView extends View {

    public enum MetricType {
        STEPS, CALORIES, DURATION
    }

    public static class BarEntry {
        public final String label;      // e.g. "Mon" or "10/01"
        public final String shortDate;  // e.g. "Oct 4"
        public final float value;       // e.g. 6240
        public final String dateIso;

        public BarEntry(String label, String shortDate, float value, String dateIso) {
            this.label = label;
            this.shortDate = shortDate;
            this.value = value;
            this.dateIso = dateIso;
        }
    }

    private final List<BarEntry> entries = new ArrayList<>();
    private MetricType metricType = MetricType.STEPS;
    private float targetGoalValue = 0f;

    private Paint barPaint;
    private Paint gridPaint;
    private Paint goalPaint;
    private Paint textPaint;
    private Paint tooltipBgPaint;
    private Paint tooltipTextPaint;

    private int primaryColor = Color.parseColor("#123B2A");
    private int highlightColor = Color.parseColor("#20A36A");

    private int selectedBarIndex = -1;
    private final RectF barRect = new RectF();
    private final RectF tooltipRect = new RectF();
    private final Path barPath = new Path();
    private final float[] radii = new float[8];

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private boolean isNightMode() {
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private void init() {
        boolean dark = isNightMode();

        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(dark ? Color.parseColor("#2A3830") : Color.parseColor("#E2E8F0"));
        gridPaint.setStrokeWidth(dpToPx(1));

        goalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        goalPaint.setColor(dark ? Color.parseColor("#4B5563") : Color.parseColor("#94A3B8"));
        goalPaint.setStyle(Paint.Style.STROKE);
        goalPaint.setStrokeWidth(dpToPx(1.5f));
        goalPaint.setPathEffect(new DashPathEffect(new float[]{dpToPx(4), dpToPx(4)}, 0));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(dark ? Color.parseColor("#9CA3AF") : Color.parseColor("#718078"));
        textPaint.setTextSize(spToPx(10));
        textPaint.setTextAlign(Paint.Align.CENTER);

        tooltipBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tooltipBgPaint.setColor(dark ? Color.parseColor("#243029") : Color.parseColor("#17201B"));
        tooltipBgPaint.setStyle(Paint.Style.FILL);

        tooltipTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tooltipTextPaint.setColor(Color.WHITE);
        tooltipTextPaint.setTextSize(spToPx(11));
        tooltipTextPaint.setTextAlign(Paint.Align.CENTER);

        updateMetricColors();
    }

    public void setMetricType(MetricType type) {
        this.metricType = type;
        updateMetricColors();
        invalidate();
    }

    public void setTargetGoal(float goal) {
        this.targetGoalValue = goal;
        invalidate();
    }

    public void setEntries(List<BarEntry> newEntries) {
        this.entries.clear();
        if (newEntries != null) {
            this.entries.addAll(newEntries);
        }
        this.selectedBarIndex = -1;
        invalidate();
    }

    private void updateMetricColors() {
        switch (metricType) {
            case CALORIES:
                primaryColor = Color.parseColor("#F59E0B");
                highlightColor = Color.parseColor("#D97706");
                break;
            case DURATION:
                primaryColor = Color.parseColor("#2563EB");
                highlightColor = Color.parseColor("#1D4ED8");
                break;
            case STEPS:
            default:
                primaryColor = Color.parseColor("#20A36A");
                highlightColor = Color.parseColor("#123B2A");
                break;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) return;

        float paddingLeft = dpToPx(16);
        float paddingRight = dpToPx(16);
        float paddingTop = dpToPx(32);
        float paddingBottom = dpToPx(32);

        float chartWidth = width - paddingLeft - paddingRight;
        float chartHeight = height - paddingTop - paddingBottom;

        if (chartWidth <= 0 || chartHeight <= 0) return;

        // Draw baseline
        float baselineY = height - paddingBottom;
        canvas.drawLine(paddingLeft, baselineY, width - paddingRight, baselineY, gridPaint);

        if (entries.isEmpty()) {
            textPaint.setTextSize(spToPx(13));
            canvas.drawText("No activity recorded for this period", width / 2f, height / 2f, textPaint);
            textPaint.setTextSize(spToPx(10));
            return;
        }

        // Calculate max value for Y scaling
        float maxVal = 0f;
        for (BarEntry entry : entries) {
            if (entry.value > maxVal) {
                maxVal = entry.value;
            }
        }

        if (targetGoalValue > maxVal) {
            maxVal = targetGoalValue;
        }

        if (maxVal <= 0f) {
            maxVal = 100f;
        } else {
            maxVal = maxVal * 1.15f;
        }

        // Draw goal line if target set
        if (targetGoalValue > 0) {
            float goalY = baselineY - (targetGoalValue / maxVal) * chartHeight;
            if (goalY >= paddingTop) {
                canvas.drawLine(paddingLeft, goalY, width - paddingRight, goalY, goalPaint);
            }
        }

        int count = entries.size();
        float slotWidth = chartWidth / count;
        float barWidth = Math.max(dpToPx(2), Math.min(slotWidth * 0.55f, dpToPx(28)));
        float cornerRadius = count > 31 ? dpToPx(1.5f) : dpToPx(4);

        radii[0] = cornerRadius; radii[1] = cornerRadius;
        radii[2] = cornerRadius; radii[3] = cornerRadius;
        radii[4] = 0; radii[5] = 0;
        radii[6] = 0; radii[7] = 0;

        // Determine X-axis label display interval to prevent text collisions
        int labelInterval;
        if (count <= 10) {
            labelInterval = 1;
        } else if (count <= 31) {
            labelInterval = 5;
        } else {
            labelInterval = 15;
        }

        float selectedCenterX = -1;
        float selectedBarTop = -1;
        BarEntry selectedEntry = null;

        for (int i = 0; i < count; i++) {
            BarEntry entry = entries.get(i);
            float slotCenterX = paddingLeft + (i + 0.5f) * slotWidth;
            float left = slotCenterX - barWidth / 2f;
            float right = slotCenterX + barWidth / 2f;

            float valHeight = (entry.value / maxVal) * chartHeight;
            if (entry.value > 0 && valHeight < dpToPx(3)) {
                valHeight = dpToPx(3);
            }

            float top = baselineY - valHeight;
            boolean isSelected = (i == selectedBarIndex);

            // Draw Bar
            if (valHeight > 0) {
                barPaint.setColor(isSelected ? highlightColor : primaryColor);

                barRect.set(left, top, right, baselineY);
                barPath.reset();
                barPath.addRoundRect(barRect, radii, Path.Direction.CW);

                canvas.drawPath(barPath, barPaint);
            } else if (count <= 14) {
                // Zero value dot indicator only for short periods
                canvas.drawCircle(slotCenterX, baselineY - dpToPx(3), dpToPx(2), gridPaint);
            }

            // Draw X-axis label only at labelInterval steps or for selected bar
            boolean isLabelVisible = (i == 0 || i == count - 1 || (i % labelInterval == 0) || isSelected);

            if (isLabelVisible) {
                textPaint.setColor(isSelected ? (isNightMode() ? Color.WHITE : Color.parseColor("#17201B")) : (isNightMode() ? Color.parseColor("#9CA3AF") : Color.parseColor("#718078")));
                canvas.drawText(entry.label, slotCenterX, baselineY + dpToPx(18), textPaint);
            }

            if (isSelected) {
                selectedCenterX = slotCenterX;
                selectedBarTop = top;
                selectedEntry = entry;
            }
        }

        // Draw tooltip for selected bar
        if (selectedEntry != null) {
            String tooltipText;
            switch (metricType) {
                case CALORIES:
                    tooltipText = (int) selectedEntry.value + " kcal";
                    break;
                case DURATION:
                    tooltipText = (int) selectedEntry.value + " min";
                    break;
                case STEPS:
                default:
                    tooltipText = String.format(java.util.Locale.getDefault(), "%,d steps", (int) selectedEntry.value);
                    break;
            }

            float textWidth = tooltipTextPaint.measureText(tooltipText);
            float tooltipWidth = textWidth + dpToPx(16);
            float tooltipHeight = dpToPx(26);

            float tooltipX = selectedCenterX;
            if (tooltipX - tooltipWidth / 2f < paddingLeft) {
                tooltipX = paddingLeft + tooltipWidth / 2f;
            } else if (tooltipX + tooltipWidth / 2f > width - paddingRight) {
                tooltipX = width - paddingRight - tooltipWidth / 2f;
            }

            float tooltipY = Math.max(dpToPx(4), selectedBarTop - tooltipHeight - dpToPx(6));

            tooltipRect.set(
                    tooltipX - tooltipWidth / 2f,
                    tooltipY,
                    tooltipX + tooltipWidth / 2f,
                    tooltipY + tooltipHeight
            );

            canvas.drawRoundRect(tooltipRect, dpToPx(6), dpToPx(6), tooltipBgPaint);
            canvas.drawText(tooltipText, tooltipX, tooltipY + dpToPx(17), tooltipTextPaint);
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            int width = getWidth();
            if (width <= 0 || entries.isEmpty()) return super.onTouchEvent(event);

            float paddingLeft = dpToPx(16);
            float paddingRight = dpToPx(16);
            float chartWidth = width - paddingLeft - paddingRight;
            float slotWidth = chartWidth / entries.size();

            float touchX = event.getX() - paddingLeft;
            int index = (int) (touchX / slotWidth);

            if (index >= 0 && index < entries.size()) {
                if (selectedBarIndex != index) {
                    selectedBarIndex = index;
                    invalidate();
                    performClick();
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private float spToPx(float sp) {
        return sp * getResources().getDisplayMetrics().density;
    }
}