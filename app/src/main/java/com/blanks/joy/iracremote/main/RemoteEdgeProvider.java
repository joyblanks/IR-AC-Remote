package com.blanks.joy.iracremote.main;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.blanks.joy.iracremote.R;
import com.blanks.joy.iracremote.constants.Constants;
import com.blanks.joy.iracremote.instance.Singleton;
import com.blanks.joy.iracremote.services.Services;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailManager;
import com.samsung.android.sdk.look.cocktailbar.SlookCocktailProvider;


public class RemoteEdgeProvider extends SlookCocktailProvider {

    private static final String TAG = "JoyIR";
    ConsumerIrManager mCIR;
    Singleton m_Inst;// = Singleton.getInstance();

    @Override
    public void onUpdate(Context context, SlookCocktailManager cocktailBarManager, int[] cocktailIds) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.activity_remote_edge);
        setPendingIntent(context, rv);
        m_Inst = Singleton.getInstance();
        rv.setInt(R.id.edge,"setBackgroundResource",m_Inst.power ? R.drawable.remote_on : R.drawable.remote_off);
        rv.setImageViewResource(R.id.swing, (m_Inst.swing ? R.drawable.swingon : R.drawable.swingoff));
        rv.setImageViewResource(R.id.fan, m_Inst.getFan());
        rv.setImageViewResource(R.id.mode, m_Inst.getMode());
        for (int cocktailId : cocktailIds) {
            cocktailBarManager.updateCocktail(cocktailId, rv);
        }

    }

    //onclick on buttons
    private void setPendingIntent(Context context, RemoteViews rv) {
        rv.setOnClickPendingIntent(R.id.edge, setPendingIntent(context, Constants.EDGE_POWER));
        rv.setOnClickPendingIntent(R.id.swing, setPendingIntent(context, Constants.EDGE_SWING));
        //setPendingIntent(context, R.id.fan, new Intent(Intent.ACTION_DIAL), rv);
        //setPendingIntent(context, R.id.mode, new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")), rv);
    }

    //onclick handlers
    private void setPendingIntent(Context context, int rscId, Intent intent, RemoteViews rv) {
        PendingIntent itemClickPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(rscId, itemClickPendingIntent);
    }

    //for onclick on the body
    private PendingIntent setPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);
        String action = intent.getAction();
        //RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.activity_remote_edge);

        mCIR = (ConsumerIrManager) context.getSystemService(android.content.Context.CONSUMER_IR_SERVICE);
        m_Inst = Singleton.getInstance();
        //boolean isPoweredOn = m_Inst.power;
        if (Constants.EDGE_POWER.equals(action)){
            Services.power(m_Inst,mCIR,new RemoteViews(context.getPackageName(), R.layout.activity_remote_edge));
            //rv.setInt(R.id.edge,"setBackgroundResource",R.drawable.remote_on);
        }else if(Constants.EDGE_SWING.equals(action)){
            Services.swing(m_Inst, mCIR, new RemoteViews(context.getPackageName(), R.layout.activity_remote_edge));
        }
        SlookCocktailManager mgr = SlookCocktailManager.getInstance(context);
        int[] cocktailIds = mgr.getCocktailIds(new ComponentName(context,RemoteEdgeProvider.class));
        for (int cocktailId : cocktailIds) {
            mgr.notifyCocktailViewDataChanged(cocktailId, R.id.edge);
        }
    }
    @Override
    public void onVisibilityChanged(Context context, int cocktailId, int visibility){
        super.onVisibilityChanged(context, cocktailId, visibility);
        Log.d(TAG, "visibility" + visibility);
    }

}
