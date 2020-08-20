package compx576.assignment.httydgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class ChoiceDialog extends DialogFragment {
    public ChoiceDialog() {

    }

    static ChoiceDialog newInstance(Choice choice) {
        ChoiceDialog box = new ChoiceDialog();
        Bundle args = new Bundle();
        args.putParcelable("choice", choice);
        return box;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        return alertDialogBuilder.create();
    }
}
