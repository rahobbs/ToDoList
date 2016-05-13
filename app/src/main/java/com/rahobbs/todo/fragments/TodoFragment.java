package com.rahobbs.todo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.rahobbs.todo.R;
import com.rahobbs.todo.helpers.Feedback;
import com.rahobbs.todo.helpers.TodoItem;
import com.rahobbs.todo.helpers.TodoLab;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Fragment that contains a single to-do item detailed view
 */
public class TodoFragment extends Fragment {

    private static final String ARG_TODO_ID = "todoId";
    private static final String ARG_WAS_DELETED = "deleted";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static boolean wasDeleted = false;
    private TodoItem mTodo;
    private EditText mTitle;
    private TextView mDueDateTextField;
    private TextView mDueDateLabel;
    private CheckBox mCompletedCheckbox;
    private LinearLayout mDateComponents;

    public static TodoFragment newInstance(UUID todoId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TODO_ID, todoId);
        args.putSerializable(ARG_WAS_DELETED, wasDeleted);
        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(getContext());
        UUID todoId = (UUID) getArguments().getSerializable(ARG_TODO_ID);
        setHasOptionsMenu(true);

        if (todoId == null) {
            throw new RuntimeException("Todo ID is null.");
        }

        mTodo = TodoLab.get(getActivity()).getItem(todoId);

        if (mTodo == null) {
            throw new RuntimeException("No Todo item with provided ID (" + todoId.toString() + ") was found.");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_todo_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_send_feedback:
                Intent i = new Intent(Intent.ACTION_SEND);
                Feedback fb = new Feedback();
                fb.sendFeedback(i);
                startActivity(Intent.createChooser(i, "Send mail..."));
                return true;
            case R.id.menu_item_delete_todo:
                UUID todoId = mTodo.getID();
                TodoItem tempItem = mTodo;
                TodoLab.get(getActivity()).deleteTodoItem(todoId);
                Intent intent = new Intent();
                intent.putExtra("item", tempItem);
                getActivity().setResult(Activity.RESULT_OK, intent);

                getActivity().finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        saveNewItem();
        super.onPause();
    }

    private void saveNewItem() {
        if (mTodo.getTitle() == null) {
            UUID mTodoID = mTodo.getID();
            TodoLab.get(getActivity()).deleteTodoItem(mTodoID);
        }

        TodoLab.get(getActivity()).updateItem(mTodo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo, container, false);
        mTitle = (EditText) v.findViewById(R.id.todo_title);

        if (mTodo.isCompleted()) {
            mTitle.setTextColor(getResources().getColor(R.color.light_grey));
        }

        mTitle.setFocusable(true);
        mTitle.requestFocus();

        mTitle.setText(mTodo.getTitle());
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodo.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDueDateTextField = (TextView) v.findViewById(R.id.todo_date);
        updateDate();

        mDateComponents = (LinearLayout) v.findViewById(R.id.date_components);
        mDueDateLabel = (TextView) v.findViewById(R.id.due_date_label);
        mDateComponents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDatePicker();
            }
        });

        if (mTodo.getDate().equals(new Date(0))){
            mDueDateLabel.setText("Set Due Date");
            mDueDateTextField.setVisibility(View.GONE);

        }

        mCompletedCheckbox = (CheckBox) v.findViewById(R.id.todo_completed);
        mCompletedCheckbox.setChecked(mTodo.isCompleted());
        mCompletedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTodo.setCompleted(isChecked);
                if (isChecked) {
                    mTitle.setTextColor(getResources().getColor(R.color.light_grey));
                } else {
                    mTitle.setTextColor(getResources().getColor(R.color.darkFont));
                }
            }
        });

        Button saveButton = (Button) v.findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewItem();
                getActivity().finish();
            }
        });
        return v;
    }

    private void launchDatePicker() {
        FragmentManager fragmentManager = getFragmentManager();
        DatePickerFragment dialog;
        if (mTodo.getDate().equals(new Date(0))){
            dialog = DatePickerFragment.newInstance(new Date());
        } else {
            dialog = DatePickerFragment.newInstance(mTodo.getDate());
        }
        dialog.setTargetFragment(TodoFragment.this, REQUEST_DATE);
        dialog.show(fragmentManager, DIALOG_DATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTodo.setDate(date);
            updateDate();
            if (!mTodo.getDate().equals(new Date(0))){
                mDueDateLabel.setText(getResources().getString(R.string.due_date_label ));
                mDueDateTextField.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateDate() {
        SimpleDateFormat format = new SimpleDateFormat("E dd MMM yyyy", Locale.ENGLISH);
        mDueDateTextField.setText(format.format(mTodo.getDate()));
    }
}
