package kr.plurly.daily.dialog;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import kr.plurly.daily.R;
import kr.plurly.daily.databinding.LayoutDialogEmotionBinding;

public class EmotionDialog extends BottomSheetDialogFragment {

    public interface OnEmotionClickListener {

        void onEmotionClick(int emotion);
    }

    private BottomSheetBehavior.BottomSheetCallback callback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {

            if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View sheet, float offset) {

        }
    };

    private OnEmotionClickListener listener;
    public void setOnEmotionClickListener(OnEmotionClickListener listener) { this.listener = listener; }

    private LayoutDialogEmotionBinding binding;

    @Override
    public void setupDialog(Dialog dialog, int style) {

        super.setupDialog(dialog, style);

        binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), R.layout.layout_dialog_emotion, null, false);
        binding.setDialog(this);

        View view = binding.getRoot();
        dialog.setContentView(view);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {

            ((BottomSheetBehavior) behavior).setBottomSheetCallback(callback);
        }
    }

    public void setEmotion(int emotion) {

        binding.setEmotion(emotion);

        if (listener != null)
            listener.onEmotionClick(emotion);

        dismiss();
    }
}
