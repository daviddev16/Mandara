package br.com.mandara.core;

public interface QueryListener {

    void onQueryChangedState(QueryState oldState, QueryState newState, IQueryProcessor<?> queryProcessor);
    
}
