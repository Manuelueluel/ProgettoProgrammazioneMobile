package com.unitn.lpsmt.group13.pommidori;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


/*  Usato in Homepage come wrap del bottone e lista per prossime scadenze e prossime sessioni.
    La sua unica utilità è nell'intercettare gli eventi (touch, click, move ecc) e reindirizzarli
    ad entrambe le child views, il bottone e la lista appunto.
* */
public class InterceptEventLayout extends LinearLayout {

    public InterceptEventLayout(Context context) {
        super(context);
    }

    public InterceptEventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptEventLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //Intercetta l'evento touch
    @Override
    public boolean onInterceptTouchEvent( MotionEvent event){
        //Recupero le due child views
        View btn = getChildAt(0);
        View lista = getChildAt(1);

        //Reindirizzo l'evento ad entrambe le views
        btn.dispatchTouchEvent( event);
        lista.dispatchTouchEvent( event);

        return false;
    }
}
