package com.blanks.joy.iracremote.main;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.blanks.joy.iracremote.R;
import com.blanks.joy.iracremote.constants.Constants;
import com.blanks.joy.iracremote.instance.Singleton;
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
        boolean isPowerOn = m_Inst.power;
        rv.setInt(R.id.edge,"setBackgroundResource",isPowerOn ? R.drawable.remote_on : R.drawable.remote_off);
        rv.setImageViewResource(R.id.swing, (m_Inst.swing ? R.drawable.swingon : R.drawable.swingoff));
        rv.setImageViewResource(R.id.fan, m_Inst.getFan());
        rv.setImageViewResource(R.id.mode, m_Inst.getMode());
        rv.setImageViewBitmap(R.id.sequence, ScaryUtil.buildSequence(context, String.valueOf(m_Inst.sequence)));
        rv.setImageViewBitmap(R.id.temp, ScaryUtil.buildUpdate(context, !isPowerOn ? "--" : String.valueOf(m_Inst.temp)));
        rv.setViewVisibility(R.id.volume, isPowerOn ? View.VISIBLE : View.INVISIBLE);
        for (int cocktailId : cocktailIds) {
            cocktailBarManager.updateCocktail(cocktailId, rv);
        }
        /*try {
            context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "hello");
                }
            }, new IntentFilter("com.blanks.joy.iracremote.SENSE_TEMPERATURE"));
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent("com.blanks.joy.iracremote.SENSE_TEMPERATURE"), 0);
            rv.setOnClickPendingIntent(R.id.volume, pi);
        }catch (Exception w){
            Log.e(TAG,w.toString());
        }*/
    }

    //onclick on buttons
    private void setPendingIntent(Context context, RemoteViews rv) {
        rv.setOnClickPendingIntent(R.id.sequence,   setPendingIntent(context, Constants.EDGE_SEQUENCE));
        rv.setOnClickPendingIntent(R.id.edge,       setPendingIntent(context, Constants.EDGE_POWER));
        rv.setOnClickPendingIntent(R.id.swing,      setPendingIntent(context, Constants.EDGE_SWING));
        rv.setOnClickPendingIntent(R.id.fan,        setPendingIntent(context, Constants.EDGE_FAN));
        rv.setOnClickPendingIntent(R.id.mode,       setPendingIntent(context, Constants.EDGE_MODE));
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
            ScaryUtil.power(context, m_Inst, rv);
            rv.setViewVisibility(R.id.volume, m_Inst.power ? View.VISIBLE : View.INVISIBLE);
        }else if(Constants.EDGE_SWING.equals(action)){
            ScaryUtil.swing(context, m_Inst, rv);
        }else if(Constants.EDGE_FAN.equals(action)){
            ScaryUtil.fan(context, m_Inst, rv);
        }else if(Constants.EDGE_MODE.equals(action)){
            ScaryUtil.mode(context, m_Inst, rv);
        }else if(Constants.EDGE_SEQUENCE.equals(action)){
            m_Inst.sequence = m_Inst.sequence == 3 ? 1 : m_Inst.sequence+1;
            rv.setImageViewBitmap(R.id.sequence, ScaryUtil.buildSequence(context, String.valueOf(m_Inst.sequence)));
        }else if(action!=null && action.indexOf(Constants.COCKTAIL_VISIBILITY_CHANGED) > -1){
            setPendingIntent(context, rv);
            rv.setInt(R.id.edge,"setBackgroundResource",m_Inst.power ? R.drawable.remote_on : R.drawable.remote_off);
            rv.setImageViewResource(R.id.swing, (m_Inst.swing ? R.drawable.swingon : R.drawable.swingoff));
            rv.setImageViewResource(R.id.fan, m_Inst.getFan());
            rv.setImageViewResource(R.id.mode, m_Inst.getMode());
            rv.setImageViewBitmap(R.id.sequence, ScaryUtil.buildSequence(context, String.valueOf(m_Inst.sequence)));
            rv.setImageViewBitmap(R.id.temp, ScaryUtil.buildUpdate(context, !m_Inst.power ? "--" : String.valueOf(m_Inst.temp)));
            rv.setViewVisibility(R.id.volume, m_Inst.power ? View.VISIBLE : View.INVISIBLE);
        }
        intent.setAction(Constants.COCKTAIL_UPDATE);
        SlookCocktailManager mgr = SlookCocktailManager.getInstance(context);
        int[] cocktailIds = mgr.getCocktailIds(new ComponentName(context,getClass()));
        for (int cocktailId : cocktailIds) {
            //mgr.notifyCocktailViewDataChanged(cocktailId, R.id.edge);
            mgr.updateCocktail(cocktailId, rv);
        }
        super.onReceive(context,intent);
    }


    public void senseTemperature(View view)
    {
        Log.d(TAG, ""+view.getId());

    }

}
