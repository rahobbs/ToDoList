package com.rahobbs.todo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.UUID;

/**
 * Created by rachael on 2/1/16.
 * Fragment that contains a single to-do item detailed view
 */
public class TodoFragment extends Fragment {

    private static final String ARG_TODO_ID = "todoId";

    private TodoItem mTodo;
    private EditText mTitle;
    private Button mDueDateButton;
    private CheckBox mCompletedCheckbox;

    public static TodoFragment newInstance(UUID todoId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TODO_ID, todoId);

        TodoFragment fragment = new TodoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID todoId = (UUID) getArguments().getSerializable(ARG_TODO_ID);

        if (todoId == null) {
            throw new RuntimeException("Todo ID is null.");
        }

        mTodo = TodoLab.get(getActivity()).getItem(todoId);

        if (mTodo == null) {
            throw new RuntimeException("No Todo item with provided ID (" + todoId.toString() + ") was found.");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_todo, container, false);
        mTitle = (EditText)v.findViewById(R.id.todo_title);
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

        mDueDateButton = (Button)v.findViewById(R.id.todo_date);
        mDueDateButton.setText(mTodo.getDate().toString());
        mDueDateButton.setEnabled(false);

        mCompletedCheckbox =(CheckBox)v.findViewById(R.id.todo_completed);
        mCompletedCheckbox.setChecked(mTodo.isCompleted());
        mCompletedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTodo.setCompleted(isChecked);
            }
        });

        return v;
    }
}
