package com.rahobbs.todo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by rachael on 2/1/16.
 * Fragment that contains a single to-do item detailed view
 */
public class TodoFragment extends Fragment {

    private static final String ARG_TODO_ID = "todoId";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;

    private TodoItem mTodo;
    private EditText mTitle;
    private TextView mDueDateTextField;
    private CheckBox mCompletedCheckbox;

    public static TodoFragment newInstance(UUID todoId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TODO_ID, todoId);

        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            case R.id.menu_item_delete_todo:
                UUID crimeId = mTodo.getID();
                TodoLab.get(getActivity()).deleteTodoItem(crimeId);

                String titleToDisplay;

                if(mTodo.getTitle().length() > 25){
                    titleToDisplay = mTodo.getTitle().substring(0, 25);
                } else {
                    titleToDisplay = mTodo.getTitle();
                }

                Toast.makeText(getActivity(),
                        "\"" + titleToDisplay + "\" deleted",
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveNewItem();
    }

    private void saveNewItem() {
        if(mTodo.getTitle() == null){
            UUID crimeId = mTodo.getID();
            TodoLab.get(getActivity()).deleteTodoItem(crimeId);
            getActivity().finish();
        }

        TodoLab.get(getActivity()).updateItem(mTodo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_todo, container, false);
        mTitle = (EditText) v.findViewById(R.id.todo_title);
        mTitle.requestFocus();

        mTitle.setText(mTodo.getTitle());
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTodo.setTitle(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDueDateTextField = (TextView) v.findViewById(R.id.todo_date);
        updateDate();
        mDueDateTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mTodo.getDate());
                dialog.setTargetFragment(TodoFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        mCompletedCheckbox = (CheckBox) v.findViewById(R.id.todo_completed);
        mCompletedCheckbox.setChecked(mTodo.isCompleted());
        mCompletedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTodo.setCompleted(isChecked);
            }
        });

        FloatingActionButton saveFab = (FloatingActionButton) v.findViewById(R.id.save_fab);

        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewItem();
                getActivity().finish();
            }
        });

        return v;
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
        }
    }

    private void updateDate() {
        SimpleDateFormat format = new SimpleDateFormat("E dd MMM yyyy", Locale.ENGLISH);
        mDueDateTextField.setText(format.format(mTodo.getDate()));
    }
}
