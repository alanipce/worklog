package com.builtbyalan.worklog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddProjectDialogFragment extends DialogFragment {
    private AddProjectDialogListener mListener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View customLayout = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_project_content, null);
        final EditText titleEditText = customLayout.findViewById(R.id.edittext_add_project_title);
        final EditText descriptionEditText = customLayout.findViewById(R.id.edittext_add_project_description);
        Button cancelButton = customLayout.findViewById(R.id.button_add_project_cancel);
        Button submitButton = customLayout.findViewById(R.id.button_add_project_submit);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                mListener.onRequestNewProject(AddProjectDialogFragment.this, title, description);
                dismiss();
            }
        });

        return builder.setTitle("Start a new Project")
                .setView(customLayout)
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddProjectDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement AddProjectDialogListener");
        }
    }

    public interface AddProjectDialogListener {
        public void onRequestNewProject(AddProjectDialogFragment fragment, String projectTitle, String projectDescription);
    }
}
