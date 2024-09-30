package br.com.easyorm;

import br.com.easyorm.core.IQueryProcessor;
import br.com.easyorm.core.QueryState;

public interface QueryListener {

    void onQueryChangedState(QueryState oldState, QueryState newState, IQueryProcessor<?> queryProcessor);
    
}
