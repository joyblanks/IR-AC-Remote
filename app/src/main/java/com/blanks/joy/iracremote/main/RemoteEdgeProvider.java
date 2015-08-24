package com.blanks.joy.iracremote.main;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.ConsumerIrManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.blanks.joy.iracremote.R;
import com.blanks.joy.iracremote.constants.Constants;
import com.blanks.joy.iracremote.instance.Singleton;
import com.blanks.joy.iracremote.services.Services;
import com.blanks.joy.iracremote.utils.ScaryUtil;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;


public class RemoteEdgeProvider extends SlookCocktailProvider {

    private static final String TAG = "JoyIR";
    private Singleton m_Inst;// = Singleton.getInstance();

    @Override
    public void onUpdate(Context context, SlookCocktailManager cocktailBarManager, int[] cocktailIds) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.activity_remote_edge);
        setPendingIntent(context, rv);
        m_Inst = Singleton.getInstance();
        rv.setInt(R.id.edge,"setBackgroundResource",m_Inst.power ? R.drawable.remote_on : R.drawable.remote_off);
        rv.setImageViewResource(R.id.swing, (m_Inst.swing ? R.drawable.swingon : R.drawable.swingoff));
        rv.setImageViewResource(R.id.fan, m_Inst.getFan());
        rv.setImageViewResource(R.id.mode, m_Inst.getMode());
        rv.setImageViewBitmap(R.id.temp, ScaryUtil.buildUpdate(context, !m_Inst.power ? "--" : String.valueOf(m_Inst.temp)));
        for (int cocktailId : cocktailIds) {
            cocktailBarManager.updateCocktail(cocktailId, rv);
        }
    }

    //onclick on buttons
    private void setPendingIntent(Context context, RemoteViews rv) {
        rv.setOnClickPendingIntent(R.id.edge, setPendingIntent(context, Constants.EDGE_POWER));
        rv.setOnClickPendingIntent(R.id.swing, setPendingIntent(context, Constants.EDGE_SWING));
        rv.setOnClickPendingIntent(R.id.fan, setPendingIntent(context, Constants.EDGE_FAN));
        rv.setOnClickPendingIntent(R.id.mode, setPendingIntent(context, Constants.EDGE_MODE));
    }

    //for onclick on the generalized
    private PendingIntent setPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.activity_remote_edge);
        m_Inst = Singleton.getInstance();
        Log.d(TAG,"onReceive() : action - "+action);

        if (Constants.EDGE_POWER.equals(action)){
            Services.power(context, m_Inst, rv);
        }else if(Constants.EDGE_SWING.equals(action)){
            Services.swing(context, m_Inst, rv);
        }else if(Constants.EDGE_FAN.equals(action)){
            Services.fan(context, m_Inst, rv);
        }else if(Constants.EDGE_MODE.equals(action)){
            Services.mode(context, m_Inst, rv);
        }
        intent.setAction("com.samsung.android.cocktail.action.COCKTAIL_UPDATE");
        SlookCocktailManager mgr = SlookCocktailManager.getInstance(context);
        int[] cocktailIds = mgr.getCocktailIds(new ComponentName(context,getClass()));
        for (int cocktailId : cocktailIds) {
            //mgr.notifyCocktailViewDataChanged(cocktailId, R.id.edge);
            mgr.updateCocktail(cocktailId, rv);
        }
        super.onReceive(context,intent);
    }

}
