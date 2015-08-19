package marterial.handlers;

import android.content.Context;

import marterial.views.Toolbar;

public abstract class ActionBarHandler {

    protected final Context mContext;

    public ActionBarHandler(Context context) {
        mContext = context;
    }

    public abstract Toolbar build();

}
