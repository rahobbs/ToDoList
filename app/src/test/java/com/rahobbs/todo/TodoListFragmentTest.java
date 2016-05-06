package com.rahobbs.todo;

import android.os.Build;

import com.rahobbs.todo.fragments.TodoListFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.KITKAT)


public class TodoListFragmentTest {

    @Test
    public void shouldNotBeNull() throws Exception
    {
/*        TodoListFragment fragment = new TodoListFragment() {
        };
        startFragment( fragment );
        assertNotNull( fragment );*/
    }
}
