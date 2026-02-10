package com.fulfilment.application.monolith.test.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ArchiveWarehouseUseCaseUnitTest {

    private WarehouseStore store;
    private ArchiveWarehouseUseCase useCase;

    @BeforeEach
    void setup() {
        store = mock(WarehouseStore.class);
        useCase = new ArchiveWarehouseUseCase(store);
    }

    @Test
    void shouldArchiveWarehouse() {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode("WH-001");

        when(store.findByBusinessUnitCode("WH-001")).thenReturn(w);

        useCase.archiveByBusinessUnitCode("WH-001");

        verify(store).archiveByBusinessUnitCode("WH-001");
    }

    @Test
    void shouldFailWhenWarehouseDoesNotExist() {
        when(store.findByBusinessUnitCode("WH-999")).thenReturn(null);

        Exception ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.archiveByBusinessUnitCode("WH-999")
        );

        assertEquals("Warehouse not found: WH-999", ex.getMessage());
        verify(store, never()).archiveByBusinessUnitCode(any());
    }

    @Test
    void shouldAllowArchiveIfAlreadyArchived() {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode("WH-001");
        w.setArchivedAt(ZonedDateTime.now());

        when(store.findByBusinessUnitCode("WH-001")).thenReturn(w);

        useCase.archiveByBusinessUnitCode("WH-001");

        verify(store).archiveByBusinessUnitCode("WH-001");
    }
}
