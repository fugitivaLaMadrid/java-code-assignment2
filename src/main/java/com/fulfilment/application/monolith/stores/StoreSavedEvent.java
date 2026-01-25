package com.fulfilment.application.monolith.stores;

public class StoreSavedEvent {
    public enum Action { CREATE, UPDATE }

    private final Long storeId;
    private final Action action;

    public StoreSavedEvent(Long storeId) {
        this(storeId, Action.CREATE);
    }

    public StoreSavedEvent(Long storeId, Action action) {
        this.storeId = storeId;
        this.action = action;
    }

    public Long getStoreId() {
        return storeId;
    }

    public Action getAction() {
        return action;
    }
}
