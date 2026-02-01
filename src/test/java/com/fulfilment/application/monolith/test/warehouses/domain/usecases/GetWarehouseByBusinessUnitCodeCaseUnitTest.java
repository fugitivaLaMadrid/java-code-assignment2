package com.fulfilment.application.monolith.test.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.usecases.GetWarehouseByBusinessUnitCodeUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetWarehouseByBusinessUnitCodeCaseUnitTest {

    private WarehouseStore store;
    private GetWarehouseByBusinessUnitCodeUseCase useCase;

    @BeforeEach
    void setup() {
        store = mock(WarehouseStore.class);
        useCase = new GetWarehouseByBusinessUnitCodeUseCase(store);
    }

    @Test
    void getByBusinessUnitCode_success() {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode("WH-001");
        w.setLocation("AMSTERDAM");
        w.setCapacity(100);
        w.setStock(50);

        when(store.findByBusinessUnitCode("WH-001")).thenReturn(w);

        Warehouse result = useCase.getByBusinessUnitCode("WH-001");

        assertNotNull(result);
        assertEquals("WH-001", result.getBusinessUnitCode());
        verify(store).findByBusinessUnitCode("WH-001");
    }

    @Test
    void getByBusinessUnitCode_notFound() {
        when(store.findByBusinessUnitCode("WH-999")).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.getByBusinessUnitCode("WH-999")
        );
    }

    @Test
    void getByBusinessUnitCode_archived() {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode( "WH-002");
        w.setArchivedAt(java.time.ZonedDateTime.now());

        when(store.findByBusinessUnitCode("WH-002")).thenReturn(w);

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.getByBusinessUnitCode("WH-002")
        );
    }
}
