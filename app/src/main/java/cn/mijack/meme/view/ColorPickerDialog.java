package cn.mijack.meme.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.mijack.meme.R;

/**
 * @author admin
 * @date 2017/6/17
 */

public class ColorPickerDialog implements View.OnClickListener {
    private AlertDialog alertDialog;
    private View dialogContent;
    View colorView;
    Button btn;
    TextView redValue;
    TextView greenValue;
    TextView blueValue;
    SeekBar seekBarRed;
    SeekBar seekBarGreen;
    SeekBar seekBarBlue;
    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == seekBarRed) {
                setUpColorValue(seekBarRed, redValue, progress);
                return;
            }
            if (seekBar == seekBarGreen) {
                setUpColorValue(seekBarGreen, greenValue, progress);
                return;
            }
            if (seekBar == seekBarBlue) {
                setUpColorValue(seekBarBlue, blueValue, progress);
                return;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private ColorPickerListener listener;

    public ColorPickerDialog(Context context, int textColor) {

        dialogContent = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker_content, null);
        colorView = dialogContent.findViewById(R.id.colorView);
        btn = (Button) dialogContent.findViewById(R.id.btn);
        redValue = (TextView) dialogContent.findViewById(R.id.redValue);
        greenValue = (TextView) dialogContent.findViewById(R.id.greenValue);
        blueValue = (TextView) dialogContent.findViewById(R.id.blueValue);
        seekBarRed = (SeekBar) dialogContent.findViewById(R.id.seekBarRed);
        seekBarGreen = (SeekBar) dialogContent.findViewById(R.id.seekBarGreen);
        seekBarBlue = (SeekBar) dialogContent.findViewById(R.id.seekBarBlue);
        seekBarBlue.setMax(255);
        seekBarGreen.setMax(255);
        seekBarRed.setMax(255);
        btn.setOnClickListener(this);
        setUpColorValue(seekBarBlue, blueValue, (textColor & 0x0000ff) >> 0);
        setUpColorValue(seekBarGreen, greenValue, (textColor & 0x00ff00) >> 8);
        setUpColorValue(seekBarRed, redValue, (textColor & 0xff0000) >> 16);
        seekBarBlue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarGreen.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarRed.setOnSeekBarChangeListener(onSeekBarChangeListener);
        alertDialog = new AlertDialog.Builder(context)
                .setView(dialogContent)
                .create();

    }

    private void setUpColorValue(SeekBar seekBar, TextView textView, int value) {
        if (seekBar.getProgress() != value) {
            seekBar.setProgress(value);
        }
        textView.setText(String.valueOf(value));
        setUpColorView();
    }

    private void setUpColorView() {
        int rgb = getRgb();
        colorView.setBackgroundColor(rgb);
    }

    private int getRgb() {
        int blue = seekBarBlue.getProgress();
        int green = seekBarGreen.getProgress();
        int red = seekBarRed.getProgress();
        return Color.rgb(red, green, blue);
    }

    public ColorPickerDialog setListener(ColorPickerListener listener) {
        this.listener = listener;
        return this;
    }

    public void show() {
        if (alertDialog != null) {
            alertDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        listener.onColorPick(alertDialog, getRgb());
    }

    public interface ColorPickerListener {
        void onColorPick(AlertDialog alertDialog, int rgb);
    }
}
