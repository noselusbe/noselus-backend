package be.noselus.dto;

import java.util.List;

/**
 * Representation of a partial element of a list.
 *
 * @param <T>
 */
public class PartialResult<T> {
    private List<T> results;
    private Object nextItem;
    private Integer limit;
    private Integer totalNumberOfResult;

    public PartialResult(final List<T> results, final Object nextItem, final Integer limit, final Integer totalNumberOfResult) {
        this.results = results;
        this.nextItem = nextItem;
        this.limit = limit;
        this.totalNumberOfResult = totalNumberOfResult;

    }

    public List<T> getResults() {
        return results;
    }

    public Object getNextItem() {
        return nextItem;
    }

    public boolean moreResultsAvailable() {
        return nextItem != null;
    }

    public Integer getLimit() {
        return limit;
    }

    public Integer getTotalNumberOfResult() {
        return totalNumberOfResult;
    }
}
