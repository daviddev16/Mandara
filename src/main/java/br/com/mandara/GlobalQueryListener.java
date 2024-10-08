package br.com.mandara;

import java.util.ArrayList;
import java.util.List;

import br.com.mandara.core.IQueryProcessor;
import br.com.mandara.core.QueryListener;
import br.com.mandara.core.QueryState;

public final class GlobalQueryListener implements QueryListener {

    public static GlobalQueryListener INSTANCE;
    
    private final List<QueryListener> queryListeners;
    
    public GlobalQueryListener() {
        this.queryListeners = new ArrayList<QueryListener>();
        this.queryListeners.add(this);
    }
    
    public void fireOnChangedStateEvent(QueryState oldState, 
            QueryState newState, IQueryProcessor<?> queryProcessor) {
        for (QueryListener queryListener : queryListeners)
            queryListener.onQueryChangedState(oldState, newState, queryProcessor);
    }
    
    public static GlobalQueryListener getInstance()
    {
        return (INSTANCE != null) ? INSTANCE : (INSTANCE = new GlobalQueryListener());
    }

    @Override
    public void onQueryChangedState(QueryState oldState, QueryState newState, IQueryProcessor<?> queryProcessor) {
        //System.out.println();
        //System.out.println("QueryID: " + queryProcessor.getQueryProcessId());
        System.out.println("Query State: " + newState + " / SQL: " + queryProcessor.getSQLQuery());
        //System.out.println("Old State: " + oldState + ", newState: " + newState);
        //System.out.println("Processor type: " + queryProcessor.getClass().getSimpleName());
        //System.out.println();
        
    }
    
}
