package be.noselus.dto;

import com.google.common.base.Optional;

public class SearchParameter {

    private static final int DEFAULT_LIMIT = 50;
    private final int limit;
    private final Optional<Object> firstElement;

    public SearchParameter(final Integer limit, final Object firstElement) {
        if (limit == null) {
            this.limit = DEFAULT_LIMIT;
        } else {
            this.limit = limit;
        }
        this.firstElement = Optional.fromNullable(firstElement);
    }

    public SearchParameter(final int limit) {
        this(limit, null);
    }

    public SearchParameter() {
        this(DEFAULT_LIMIT);
    }

    public int getLimit() {
        return limit;
    }

    public Optional<Object> getFirstElement() {
        return firstElement;
    }
}
