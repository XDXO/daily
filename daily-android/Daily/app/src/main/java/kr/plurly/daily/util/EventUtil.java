package kr.plurly.daily.util;

import kr.plurly.daily.R;
import kr.plurly.daily.domain.Event;

public class EventUtil {

    public static int emotionLabel(int emotion) {

        switch (emotion) {

            case Event.EMOTION_HAPPY : return R.string.label_dialog_emotion_happy;
            case Event.EMOTION_GOODIE : return R.string.label_dialog_emotion_goodie;
            case Event.EMOTION_NEUTRAL : return R.string.label_dialog_emotion_neutral;
            case Event.EMOTION_SAD : return R.string.label_dialog_emotion_sad;
            case Event.EMOTION_DISAPPOINTED : return R.string.label_dialog_emotion_disappointed;
            default: return R.string.label_dialog_emotion_select_none;
        }
    }
}
