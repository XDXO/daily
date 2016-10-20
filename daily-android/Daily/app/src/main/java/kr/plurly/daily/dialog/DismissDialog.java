package kr.plurly.daily.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import kr.plurly.daily.R;

public class DismissDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private DialogInterface.OnClickListener listener;
    public void setOnClickListener(DialogInterface.OnClickListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme_AlertDialogTheme));

        builder.setTitle(R.string.label_dialog_dismiss_title);
        builder.setMessage(R.string.label_dialog_dismiss_description);

        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (listener != null)
            listener.onClick(dialog, which);
    }
}
