package com.fulfilment.application.monolith.location;

import org.junit.jupiter.api.Test;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationGatewayTest {


  @Test
  void testWhenResolveExistingLocationShouldReturn() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    // then
    assertEquals("ZWOLLE-001", location.getIdentification());
  }

  @Test
  void testWhenResolveAnotherExistingLocationShouldReturn() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    Location location = locationGateway.resolveByIdentifier("AMSTERDAM-002");

    // then
    assertEquals("AMSTERDAM-002", location.getIdentification());
  }

  @Test
  void testWhenResolveNonExistingLocationShouldThrow() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when / then
    UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
            () -> locationGateway.resolveByIdentifier("UNKNOWN-001"));

    assertTrue(ex.getMessage().contains("UNKNOWN-001"));
  }

  @Test
  void testWhenResolveNullIdentifierShouldThrow() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when / then
    assertThrows(UnsupportedOperationException.class,
            () -> locationGateway.resolveByIdentifier(null));
  }
}
