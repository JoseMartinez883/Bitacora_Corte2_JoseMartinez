package com.restaurante.model.domain;

import com.restaurante.exception.TransicionEstadoInvalidaException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainTest {

    @Test
    void testItemPedido() {
        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setIdPlato(10L);
        item.setNombrePlato("Pizza");
        item.setPrecioCongelado(100.0);
        item.setCantidad(2);

        assertEquals(1L, item.getId());
        assertEquals(10L, item.getIdPlato());
        assertEquals("Pizza", item.getNombrePlato());
        assertEquals(100.0, item.getPrecioCongelado());
        assertEquals(2, item.getCantidad());
        assertEquals(200.0, item.calcularSubtotal());
        assertTrue(item.esValido());

        item.actualizarCantidad(3);
        assertEquals(3, item.getCantidad());

        assertThrows(IllegalArgumentException.class, () -> item.actualizarCantidad(0));

        item.setPrecioCongelado(null);
        assertEquals(0.0, item.calcularSubtotal());
        assertFalse(item.esValido());

        assertNotNull(item.toString());
        assertNotEquals(0, item.hashCode());
        assertNotEquals(item, new ItemPedido());
    }

    @Test
    void testPedido() {
        Pedido pedido = new Pedido();
        UUID id = UUID.randomUUID();
        pedido.setId(id);
        pedido.setIdMesa(1L);
        pedido.setEstado(EstadoPedido.RECIBIDO);
        LocalDateTime now = LocalDateTime.now();
        pedido.setTimestamp(now);

        assertEquals(id, pedido.getId());
        assertEquals(1L, pedido.getIdMesa());
        assertEquals(EstadoPedido.RECIBIDO, pedido.getEstado());
        assertEquals(now, pedido.getTimestamp());

        assertTrue(pedido.puedeModificarse());

        ItemPedido item = new ItemPedido();
        item.setPrecioCongelado(100.0);
        item.setCantidad(2);
        pedido.agregarItem(item);
        assertEquals(1, pedido.getItems().size());
        assertEquals(200.0, pedido.calcularTotal());

        pedido.cambiarEstado(EstadoPedido.EN_PREPARACION);
        assertFalse(pedido.puedeModificarse());
        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstado());

        ItemPedido emptyItem = new ItemPedido();
        assertThrows(IllegalStateException.class, () -> pedido.agregarItem(emptyItem));
        assertThrows(TransicionEstadoInvalidaException.class, () -> pedido.cambiarEstado(EstadoPedido.RECIBIDO));
        
        pedido.cambiarEstado(EstadoPedido.LISTO);
        pedido.cambiarEstado(EstadoPedido.ENTREGADO);
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
        
        // RECIBIDO -> CANCELADO validacion
        Pedido p2 = new Pedido();
        p2.setEstado(EstadoPedido.RECIBIDO);
        p2.cambiarEstado(EstadoPedido.CANCELADO);
        assertEquals(EstadoPedido.CANCELADO, p2.getEstado());

        assertNotNull(pedido.toString());
        assertNotEquals(0, pedido.hashCode());
        assertNotEquals(pedido, p2);
    }

    @Test
    void testPlato() {
        Plato plato = new Plato();
        plato.setId(1L);
        plato.setNombre("Pasta");
        plato.setPrecio(150.0);
        plato.setCategoria("PASTA");
        plato.setMasa("Normal");
        plato.setSalsa("Roja");
        plato.setToppings(List.of("Queso"));
        plato.setProteinas(List.of("Pollo"));
        plato.setSalsasExtras(List.of("Ajo"));
        plato.setActivo(true);
        plato.setDisponible(true);

        assertEquals(1L, plato.getId());
        assertEquals("Pasta", plato.getNombre());
        assertEquals(150.0, plato.getPrecio());
        assertEquals("PASTA", plato.getCategoria());
        assertEquals("Normal", plato.getMasa());
        assertEquals("Roja", plato.getSalsa());
        assertEquals(1, plato.getToppings().size());
        assertEquals(1, plato.getProteinas().size());
        assertEquals(1, plato.getSalsasExtras().size());
        assertTrue(plato.isActivo());
        assertTrue(plato.isDisponible());

        assertNotNull(plato.toString());
        assertNotEquals(0, plato.hashCode());
        assertNotEquals(plato, new Plato());
    }

    @Test
    void testReserva() {
        Reserva r = new Reserva();
        UUID id = UUID.randomUUID();
        r.setId(id);
        r.setIdMesa(1L);
        r.setCliente("Juan");
        LocalDateTime now = LocalDateTime.now().plusDays(1);
        r.setFechaHora(now);
        r.setComensales(4);
        r.setActiva(true);
        r.setUsuarioId(123L);

        assertEquals(id, r.getId());
        assertEquals(1L, r.getIdMesa());
        assertEquals("Juan", r.getCliente());
        assertEquals(now, r.getFechaHora());
        assertEquals(4, r.getComensales());
        assertTrue(r.isActiva());
        assertEquals(123L, r.getUsuarioId());
        assertTrue(r.estaVigente());

        r.reprogramar(now.plusDays(1));
        assertEquals(now.plusDays(1), r.getFechaHora());

        r.cancelar();
        assertFalse(r.isActiva());

        assertNotNull(r.toString());
        assertNotEquals(0, r.hashCode());
        assertNotEquals(r, new Reserva());
    }

    @Test
    void testRegistroVehiculo() {
        RegistroVehiculo rv = new RegistroVehiculo();
        rv.setId(1L);
        rv.setPlaca("ABC");
        LocalDateTime now = LocalDateTime.now();
        rv.setEntrada(now);
        rv.setSalida(now.plusHours(1));
        rv.setCobro(100.0);
        rv.setEstado("ACTIVO");

        assertEquals(1L, rv.getId());
        assertEquals("ABC", rv.getPlaca());
        assertEquals(now, rv.getEntrada());
        assertEquals(now.plusHours(1), rv.getSalida());
        assertEquals(100.0, rv.getCobro());
        assertEquals("ACTIVO", rv.getEstado());

        rv.setSalida(null);
        assertTrue(rv.estaActivo());
        
        rv.registrarSalida();
        assertEquals("FINALIZADO", rv.getEstado());
        assertNotNull(rv.getCobro());

        assertNotNull(rv.toString());
        assertNotEquals(0, rv.hashCode());
        assertNotEquals(rv, new RegistroVehiculo());
    }

    @Test
    void testCuenta() {
        Cuenta c = new Cuenta();
        c.setId(1L);
        c.setIdMesa(10L);
        c.setTotal(119.0);
        c.setEstado(EstadoCuenta.ABIERTA);
        LocalDateTime now = LocalDateTime.now();
        c.setFechaApertura(now);
        c.setFechaCierre(now.plusHours(1));
        c.setMetodoPago("EFECTIVO");

        assertEquals(1L, c.getId());
        assertEquals(10L, c.getIdMesa());
        assertEquals(119.0, c.getTotal());
        assertEquals(EstadoCuenta.ABIERTA, c.getEstado());
        assertEquals(now, c.getFechaApertura());
        assertEquals(now.plusHours(1), c.getFechaCierre());
        assertEquals("EFECTIVO", c.getMetodoPago());
        
        c.cerrarCuenta("TARJETA");
        assertEquals(EstadoCuenta.CERRADA, c.getEstado());
        assertEquals("TARJETA", c.getMetodoPago());
        
        ItemPedido item = new ItemPedido();
        item.setCantidad(2);
        item.setPrecioCongelado(50.0);
        c.agregarCargo(item);
        assertEquals(100.0, c.calcularTotal());

        assertNotNull(c.toString());
        assertNotEquals(0, c.hashCode());
        assertNotEquals(c, new Cuenta());
    }

    @Test
    void testMesa() {
        Mesa m = new Mesa();
        m.setId(1L);
        m.setNumero(10);
        m.setCapacidad(4);
        m.setEstado(EstadoMesa.DISPONIBLE);
        m.setCuentaAbierta(false);

        assertEquals(1L, m.getId());
        assertEquals(10, m.getNumero());
        assertEquals(4, m.getCapacidad());
        assertEquals(EstadoMesa.DISPONIBLE, m.getEstado());
        assertFalse(m.isCuentaAbierta());
        assertTrue(m.estaDisponible());

        m.abrirCuenta();
        assertEquals(EstadoMesa.OCUPADA, m.getEstado());
        assertTrue(m.isCuentaAbierta());

        m.cerrarCuenta();
        assertEquals(EstadoMesa.DISPONIBLE, m.getEstado());
        assertFalse(m.isCuentaAbierta());

        assertNotNull(m.toString());
        assertNotEquals(0, m.hashCode());
        assertNotEquals(m, new Mesa());
    }
}
