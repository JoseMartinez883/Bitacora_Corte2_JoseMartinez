package com.restaurante.service;

import com.restaurante.exception.PedidoNoEntregadoException;
import com.restaurante.exception.PedidoNotFoundException;
import com.restaurante.exception.ResenaDuplicadaException;
import com.restaurante.exception.ResenaNotFoundException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.dto.request.ResenaRequestDTO;
import com.restaurante.model.dto.response.ResenaResponseDTO;
import com.restaurante.persistence.document.ResenaDocument;
import com.restaurante.persistence.entity.PedidoEntity;
import com.restaurante.repository.PedidoRepositoryJPA;
import com.restaurante.repository.ResenaRepositoryMongo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceImplTest {

    @Mock private ResenaRepositoryMongo resenaRepository;
    @Mock private PedidoRepositoryJPA pedidoRepository;
    @InjectMocks private ResenaServiceImpl resenaService;

    private final UUID idPedido = UUID.randomUUID();

    private void autenticarComo(String email, String rol) {
        SecurityContextHolder.setContext(new org.springframework.security.core.context.SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)))));
    }

    @org.junit.jupiter.api.BeforeEach
    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private PedidoEntity pedido(EstadoPedido estado) {
        PedidoEntity p = new PedidoEntity();
        p.setId(idPedido);
        p.setEstado(estado);
        return p;
    }

    private ResenaDocument doc(String autor) {
        return ResenaDocument.builder().id("r1").idPedido(idPedido).emailUsuario(autor).nombreCliente("ana")
                .calificacion(5).comentario("Excelente").fechaCreacion(LocalDateTime.now()).build();
    }

    // ---------- crear ----------

    @Test
    void crearResena_tomaAutorDelTokenYGuardaEnMongo() {
        autenticarComo("ana@test.com", "CLIENTE");
        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido(EstadoPedido.ENTREGADO)));
        when(resenaRepository.existsByIdPedido(idPedido)).thenReturn(false);
        when(resenaRepository.save(any(ResenaDocument.class))).thenAnswer(i -> {
            ResenaDocument d = i.getArgument(0);
            d.setId("r1");
            return d;
        });

        ResenaResponseDTO res = resenaService.crearResena(new ResenaRequestDTO(idPedido, 5, "Excelente"));

        ArgumentCaptor<ResenaDocument> captor = ArgumentCaptor.forClass(ResenaDocument.class);
        verify(resenaRepository).save(captor.capture());
        assertEquals("ana@test.com", captor.getValue().getEmailUsuario());
        assertEquals("ana", res.nombreCliente()); // no expone el correo completo
        assertEquals("r1", res.id());
        assertNotNull(res.fechaCreacion());
    }

    @Test
    void crearResena_pedidoNoExiste_lanzaExcepcion() {
        autenticarComo("ana@test.com", "CLIENTE");
        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.empty());
        ResenaRequestDTO req = new ResenaRequestDTO(idPedido, 5, "x");
        assertThrows(PedidoNotFoundException.class,
                () -> resenaService.crearResena(req));
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_pedidoNoEntregado_lanzaExcepcion() {
        autenticarComo("ana@test.com", "CLIENTE");
        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido(EstadoPedido.EN_PREPARACION)));
        ResenaRequestDTO req = new ResenaRequestDTO(idPedido, 5, "x");
        assertThrows(PedidoNoEntregadoException.class,
                () -> resenaService.crearResena(req));
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_pedidoYaResenado_lanzaExcepcion() {
        autenticarComo("ana@test.com", "CLIENTE");
        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido(EstadoPedido.ENTREGADO)));
        when(resenaRepository.existsByIdPedido(idPedido)).thenReturn(true);
        ResenaRequestDTO req = new ResenaRequestDTO(idPedido, 1, "spam");
        assertThrows(ResenaDuplicadaException.class,
                () -> resenaService.crearResena(req));
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_sinAutenticacion_lanzaAccessDenied() {
        when(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedido(EstadoPedido.ENTREGADO)));
        when(resenaRepository.existsByIdPedido(idPedido)).thenReturn(false);
        ResenaRequestDTO req = new ResenaRequestDTO(idPedido, 5, "x");
        assertThrows(AccessDeniedException.class,
                () -> resenaService.crearResena(req));
    }

    // ---------- consultar ----------

    @Test
    void obtenerPorId_exitoso() {
        when(resenaRepository.findById("r1")).thenReturn(Optional.of(doc("ana@test.com")));
        assertEquals("ana", resenaService.obtenerPorId("r1").nombreCliente());
    }

    @Test
    void obtenerPorId_lanzaExcepcionSiNoExiste() {
        when(resenaRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(ResenaNotFoundException.class, () -> resenaService.obtenerPorId("x"));
    }

    @Test
    void listarTodas_exitoso() {
        when(resenaRepository.findAll()).thenReturn(List.of(doc("ana@test.com")));
        assertEquals(1, resenaService.listarTodas().size());
    }

    @Test
    void listarPorCalificacionMinima_exitoso() {
        when(resenaRepository.findByCalificacionGreaterThanEqual(4)).thenReturn(List.of(doc("ana@test.com")));
        assertEquals(1, resenaService.listarPorCalificacionMinima(4).size());
    }

    // ---------- eliminar ----------

    @Test
    void eliminar_autorPuedeEliminarSuResena() {
        autenticarComo("ana@test.com", "CLIENTE");
        when(resenaRepository.findById("r1")).thenReturn(Optional.of(doc("ana@test.com")));
        resenaService.eliminar("r1");
        verify(resenaRepository).deleteById("r1");
    }

    @Test
    void eliminar_adminPuedeEliminarCualquiera() {
        autenticarComo("admin@test.com", "ADMIN");
        when(resenaRepository.findById("r1")).thenReturn(Optional.of(doc("ana@test.com")));
        resenaService.eliminar("r1");
        verify(resenaRepository).deleteById("r1");
    }

    @Test
    void eliminar_otroClienteNoPuede() {
        autenticarComo("pedro@test.com", "CLIENTE");
        when(resenaRepository.findById("r1")).thenReturn(Optional.of(doc("ana@test.com")));
        assertThrows(AccessDeniedException.class, () -> resenaService.eliminar("r1"));
        verify(resenaRepository, never()).deleteById(any());
    }

    @Test
    void eliminar_lanzaExcepcionSiNoExiste() {
        when(resenaRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(ResenaNotFoundException.class, () -> resenaService.eliminar("x"));
        verify(resenaRepository, never()).deleteById(any());
    }
}
