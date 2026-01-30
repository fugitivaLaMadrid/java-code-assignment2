package com.fulfilment.application.monolith.test.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ListWarehousesUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListWarehousesUseCaseUnitTest {

    private WarehouseStore store;
    private ListWarehousesUseCase useCase;

    @BeforeEach
    void setup() {
        store = mock(WarehouseStore.class);
        useCase = new ListWarehousesUseCase(store);
    }

    @Test
    void list_noLocation_noLimit() {
        Warehouse w1 = new Warehouse();
        w1.businessUnitCode = "WH-001";

        Warehouse w2 = new Warehouse();
        w2.businessUnitCode = "WH-002";

        when(store.findByLocation(null)).thenReturn(List.of(w1, w2));

        List<Warehouse> result = useCase.list(null, null);

        assertEquals(2, result.size());
        assertEquals("WH-001", result.get(0).businessUnitCode);
        assertEquals("WH-002", result.get(1).businessUnitCode);
    }

    @Test
    void list_withLocationAndLimit() {
        Warehouse w1 = new Warehouse();
        w1.businessUnitCode = "WH-001";
        w1.location = "AMSTERDAM";

        Warehouse w2 = new Warehouse();
        w2.businessUnitCode = "WH-002";
        w2.location = "AMSTERDAM";

        when(store.findByLocation("AMSTERDAM")).thenReturn(List.of(w1, w2));

        List<Warehouse> result = useCase.list("AMSTERDAM", 1);

        assertEquals(1, result.size());
        assertEquals("WH-001", result.get(0).businessUnitCode);
    }

    @Test
    void list_withLocation_noResults() {
        when(store.findByLocation("UNKNOWN")).thenReturn(List.of());

        List<Warehouse> result = useCase.list("UNKNOWN", 5);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
