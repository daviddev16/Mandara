package br.com.easyorm;

import java.util.ArrayList;
import java.util.List;

import br.com.easyorm.core.IQueryProcessor;
import br.com.easyorm.core.QueryState;

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
        System.out.println();
        System.out.println("QueryID: " + queryProcessor.getQueryProcessId());
        System.out.println("Changing States for query: " + queryProcessor.getSQLQuery());
        System.out.println("Old State: " + oldState + ", newState: " + newState);
        System.out.println("Processor type: " + queryProcessor.getClass().getSimpleName());
        System.out.println();
        
    }
    
}
