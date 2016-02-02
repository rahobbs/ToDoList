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

/**
 * Created by rachael on 2/1/16.
 */
public class TodoFragment extends Fragment {

    private TodoItem mTodo;
    private EditText mTitle;
    private Button mDueDateButton;
    private CheckBox mCompletedCheckbox;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mTodo = new TodoItem();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_todo, container, false);
        mTitle = (EditText)v.findViewById(R.id.todo_title);
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

        mDueDateButton =(Button)v.findViewById(R.id.todo_date);
        mDueDateButton.setText(mTodo.getDate().toString());
        mDueDateButton.setEnabled(false);

        mCompletedCheckbox =(CheckBox)v.findViewById(R.id.todo_completed);
        mCompletedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTodo.setCompleted(isChecked);
            }
        });

        return v;
    }
}
