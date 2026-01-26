package com.fulfilment.application.monolith.test.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ArchiveWarehouseUseCaseUnitTest {

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
        w.businessUnitCode = "1";

        when(store.findByBusinessUnitCode("1")).thenReturn(w);

        useCase.archive(w);

        verify(store).remove(w);
    }

    @Test
    void shouldFailWhenWarehouseDoesNotExist() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "99";

        when(store.findByBusinessUnitCode("99")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> useCase.archive(w));
    }

    @Test
    void shouldRejectIfNotFound() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "B9";
        when(store.findByBusinessUnitCode("B9")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> useCase.archive(w));
    }
}
