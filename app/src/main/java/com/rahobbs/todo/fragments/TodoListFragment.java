package com.rahobbs.todo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rahobbs.todo.R;
import com.rahobbs.todo.activities.TodoPagerActivity;
import com.rahobbs.todo.helpers.TodoItem;
import com.rahobbs.todo.helpers.TodoLab;
import com.rahobbs.todo.interfaces.ItemTouchHelperAdapter;
import com.rahobbs.todo.interfaces.SimpleItemTouchHelperCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment that contains the list of to-do items
 */
public abstract class TodoListFragment extends Fragment {

    public List<TodoItem> selectedItems = new ArrayList<>();
    public Boolean multiSelectMode = false;
    public ItemTouchHelper touchHelper;
    public String fragType = "todo_list_fragment";
    public int context_menu = R.menu.context_menu;
    private RecyclerView mTodoRecyclerView;
    private int listSize;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.multi_delete:
                    final List<TodoItem> copySelected = selectedItems;
                    deleteSelected(selectedItems);
                    updateUI();

                    Snackbar.make(getView(), "Deleting " + selectedItems.size() + " items",
                            Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (TodoItem i : copySelected) {
                                TodoLab.get(getActivity()).addTodoItem(i);
                                i.setPosition(i.getPosition());
                            }
                            selectedItems.clear();
                            updateUI();
                        }
                    }).show();
                    updateUI();
                    mode.finish();
                    return true;
                case R.id.archive:
                    for (TodoItem i : selectedItems) {
                        i.setArchived(true);
                        TodoLab.get(getActivity()).updateItem(i);
                        updateUI();
                    }
                    return true;
                case R.id.unarchive:
                    for (TodoItem j : selectedItems) {
                        j.setArchived(false);
                        TodoLab.get(getActivity()).updateItem(j);
                        updateUI();
                    }
                    return true;
                case R.id.close_menu:
                    updateUI();
                    mode.finish();
                    selectedItems.clear();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelectMode = false;
            updateUI();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
        mTodoRecyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton addFab = (FloatingActionButton) view.findViewById(R.id.add_fab);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoItem todoItem = new TodoItem();
                todoItem.setPosition(listSize + 1);
                TodoLab.get(getActivity()).addTodoItem(todoItem);
                Intent intent = TodoPagerActivity.newIntent(getActivity(), todoItem.getID());
                startActivity(intent);
            }
        });

        updateUI();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                final TodoItem item = (TodoItem) data.getSerializableExtra("item");
                Snackbar.make(getView(), "Item Deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TodoLab.get(getActivity()).addTodoItem(item);
                        item.setPosition(item.getPosition());
                        updateUI();
                    }
                }).show();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.todo_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TodoLab todoLab = TodoLab.get(getActivity());
        List<TodoItem> todoItems = todoLab.getUnarchivedItems();
        listSize = todoItems.size();
        Collections.sort(todoItems, new TodoItem.PositionComparator());
        TodoListAdapter adapter = new TodoListAdapter(todoItems);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(adapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mTodoRecyclerView);
    }

    public void updateUI() {
        TodoLab todoLab = TodoLab.get(getActivity());
        List<TodoItem> todoItems = todoLab.getUnarchivedItems();
        Collections.sort(todoItems, new TodoItem.PositionComparator());

        TodoListAdapter recyclerAdapter = (TodoListAdapter) mTodoRecyclerView.getAdapter();
        if (recyclerAdapter == null) {
            mTodoRecyclerView.setAdapter(new TodoListAdapter(todoItems));
        } else {
            recyclerAdapter.setTodoItems(todoItems);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }


    public void deleteSelected(List<TodoItem> selectedItems) {
        for (TodoItem i : selectedItems) {
            TodoLab.get(getActivity()).deleteTodoItem(i.getID());
        }
    }

    public class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitleTextView;
        public TextView mDateLabel;
        public TextView mDateTextView;
        public CheckBox mCompletedCheckBox;
        public RelativeLayout mListItem;
        public ImageView mHandle;
        public int mPosition;
        public ActionMode mActionMode;
        private TodoItem mTodo;

        public TodoHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_todo_title_text_view);
            mCompletedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_todo_completed_check_box);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_todo_date_text_view);
            mDateLabel = (TextView) itemView.findViewById(R.id.due_date_label);

            mListItem = (RelativeLayout) itemView.findViewById(R.id.list_item_todo);

            mHandle = (ImageView) itemView.findViewById(R.id.reorder_handle);
            mPosition = getAdapterPosition();

            mListItem.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View arg0) {
                    if (!multiSelectMode) {
                        selectedItems.clear();
                    }
                    selectedItems.add(mTodo);
                    multiSelectMode = true;
                    mActionMode = getActivity().startActionMode(mActionModeCallback);
                    mListItem.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_grey));

                    return true;    // <- set to true
                }
            });
        }

        public void bindTodo(TodoItem todo) {
            SimpleDateFormat format = new SimpleDateFormat("E dd MMM yyyy", Locale.ENGLISH);
            mTodo = todo;

            if (mTodo.getTitle() != null) {
                String title = mTodo.getTitle();
                if (title.contains("\n")) {
                    title = mTodo.getTitle().replaceFirst("\n", ": ");
                    title = title.replace("\n", "");
                }
                mTitleTextView.setText(title.trim());
                mListItem.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.default_background_light));
            }

            mDateTextView.setText(format.format(mTodo.getDate()));

            if (mTodo.getDate().equals(new Date(0))){
                mDateTextView.setVisibility(View.GONE);
                mDateLabel.setVisibility(View.GONE);
            }

            mCompletedCheckBox.setChecked(mTodo.isCompleted());
            mCompletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mTodo.setCompleted(isChecked);

                    if (isChecked) {
                        checkItem();
                    } else {
                        unCheckItem();
                    }
                    TodoLab.get(getActivity()).updateItem(mTodo);
                }
            });
        }

        public void updateCompleted() {
            if (mTodo.isCompleted()) {
                checkItem();
            }
        }

        @Override
        public void onClick(View v) {
            if (!multiSelectMode) {
                Intent intent = TodoPagerActivity.newIntent(getActivity(), mTodo.getID());
                startActivityForResult(intent, 1);
            }
            if (multiSelectMode) {
                if (selectedItems.contains(mTodo)) {
                    selectedItems.remove(mTodo);
                    mListItem.setBackgroundColor(ContextCompat.getColor(getContext(), (R.color.default_background_light)));
                    if (selectedItems.isEmpty() && mActionMode != null) {
                        mActionMode.finish();
                    }
                } else {
                    selectedItems.add(mTodo);
                    mListItem.setBackgroundColor(ContextCompat.getColor(getContext(), (R.color.dark_grey)));
                }
            }
        }

        private void checkItem() {
            mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.light_grey));
            mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.light_grey));

            if (!mTodo.isCompleted()) {
                mTodo.setCompleted(true);
            }
        }

        private void unCheckItem() {
            mTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.darkFont));
            mDateTextView.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            mDateLabel.setPaintFlags(mDateTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            if (mTodo.isCompleted()) {
                mTodo.setCompleted(false);
            }
        }
    }

    public class TodoListAdapter extends RecyclerView.Adapter<TodoHolder> implements ItemTouchHelperAdapter {
        private List<TodoItem> mTodoItems;

        public TodoListAdapter(List<TodoItem> todoItems) {
            mTodoItems = todoItems;
        }

        @Override
        public TodoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_todo, parent, false);

            return new TodoHolder(view);
        }

        @Override
        public void onBindViewHolder(TodoHolder holder, int position) {
            final TodoHolder mHolder = holder;
            TodoItem todoItem = mTodoItems.get(position);
            mHolder.bindTodo(todoItem);
            mHolder.updateCompleted();

            mHolder.mHandle.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(mHolder);
                    }
                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return mTodoItems.size();
        }

        public void setTodoItems(List<TodoItem> todoItems) {
            mTodoItems = todoItems;
            notifyDataSetChanged();
        }

        @Override
        public void onItemDismiss(int position) {

            final TodoItem item = mTodoItems.get(position);
            final TodoItem copyItem = item;

            if (String.valueOf(fragType).equals("archived_list_fragment")) {
                TodoLab.get(getActivity()).deleteTodoItem(item.getID());
                Snackbar.make(getView(), "Task Deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TodoLab.get(getActivity()).addTodoItem(copyItem);
                        updateUI();
                    }
                }).show();
                updateUI();
            } else {
                item.setArchived(true);
                TodoLab.get(getActivity()).updateItem(item);
                updateUI();

                Snackbar.make(getView(), "Task Archived", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.setArchived(false);
                        TodoLab.get(getActivity()).updateItem(item);
                        updateUI();
                    }
                }).show();
            }
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mTodoItems, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mTodoItems, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);

            for (TodoItem i : mTodoItems) {
                i.setPosition(mTodoItems.indexOf(i));
                TodoLab.get(getActivity()).updateItem(i);
            }
            return true;
        }
    }
}