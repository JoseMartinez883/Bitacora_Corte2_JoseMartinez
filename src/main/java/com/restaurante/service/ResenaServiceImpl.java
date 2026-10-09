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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepositoryMongo resenaRepository;
    private final PedidoRepositoryJPA pedidoRepository;

    @Override
    public ResenaResponseDTO crearResena(ResenaRequestDTO dto) {
        // Regla 1: el pedido debe existir en PostgreSQL y estar ENTREGADO
        PedidoEntity pedido = pedidoRepository.findById(dto.idPedido())
                .orElseThrow(() -> new PedidoNotFoundException(dto.idPedido()));
        if (pedido.getEstado() != EstadoPedido.ENTREGADO) {
            throw new PedidoNoEntregadoException(dto.idPedido());
        }

        // Regla 2: una sola reseña por pedido
        if (resenaRepository.existsByIdPedido(dto.idPedido())) {
            throw new ResenaDuplicadaException(dto.idPedido());
        }

        // Regla 3: el autor se toma del token JWT (no del request) para evitar suplantación
        String email = usuarioAutenticado();

        ResenaDocument doc = ResenaDocument.builder()
                .idPedido(dto.idPedido())
                .emailUsuario(email)
                .nombreCliente(nombrePublico(email))
                .calificacion(dto.calificacion())
                .comentario(dto.comentario())
                .fechaCreacion(LocalDateTime.now(java.time.ZoneId.systemDefault()))
                .build();
        ResenaDocument guardada = resenaRepository.save(doc);
        log.info("Reseña {} guardada en MongoDB para el pedido {} por {}", guardada.getId(), dto.idPedido(), email);
        return toResponse(guardada);
    }

    @Override
    public ResenaResponseDTO obtenerPorId(String id) {
        return resenaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResenaNotFoundException(id));
    }

    @Override
    public List<ResenaResponseDTO> listarTodas() {
        return resenaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<ResenaResponseDTO> listarPorCalificacionMinima(int calificacion) {
        return resenaRepository.findByCalificacionGreaterThanEqual(calificacion)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public void eliminar(String id) {
        ResenaDocument resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResenaNotFoundException(id));
        // Solo el autor o un ADMIN pueden eliminarla
        if (!esAdmin() && !usuarioAutenticado().equals(resena.getEmailUsuario())) {
            throw new AccessDeniedException("Solo puedes eliminar tus propias reseñas.");
        }
        resenaRepository.deleteById(id);
    }

    private String usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("Debes iniciar sesión para realizar esta acción.");
        }
        return auth.getName();
    }

    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /** Muestra solo la parte antes del @ para no exponer el correo completo. */
    private String nombrePublico(String email) {
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }

    private ResenaResponseDTO toResponse(ResenaDocument d) {
        return new ResenaResponseDTO(d.getId(), d.getIdPedido(), d.getNombreCliente(),
                d.getCalificacion(), d.getComentario(), d.getFechaCreacion());
    }
}
