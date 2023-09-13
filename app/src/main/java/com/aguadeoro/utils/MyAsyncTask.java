package com.aguadeoro.utils;

import android.os.AsyncTask;

public abstract class MyAsyncTask<E, E1, E2> extends AsyncTask<E, E1, E2> {
    protected int limit = Integer.parseInt(Utils.getStringSetting("max_results"));
}
