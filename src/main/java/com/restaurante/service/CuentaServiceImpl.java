package com.restaurante.service;

import com.restaurante.exception.CuentaNotFoundException;
import com.restaurante.exception.MesaNotFoundException;
import com.restaurante.mapper.CuentaEntityMapper;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.mapper.CuentaMapperOut;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.dto.request.PagoCuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.repository.CuentaRepositoryJPA;
import com.restaurante.repository.MesaRepositoryJPA;
import com.restaurante.repository.PedidoRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {
    private final CuentaRepositoryJPA cuentaRepository;
    private final MesaRepositoryJPA mesaRepository;
    private final PedidoRepositoryJPA pedidoRepository;
    private final CuentaEntityMapper cuentaEntityMapper;
    private final MesaEntityMapper mesaEntityMapper;
    private final CuentaMapperOut cuentaMapperOut;

    @Override
    public CuentaResponseDTO obtenerCuentaActivaPorMesa(Long idMesa) {
        log.info("Consultando cuenta activa para la mesa ID: {}", idMesa);
        Cuenta cuenta = cuentaRepository.findByIdMesaAndEstado(idMesa, EstadoCuenta.ABIERTA).map(cuentaEntityMapper::toDomain).orElseThrow(() -> {
            log.warn("No se encontró cuenta activa para la mesa ID: {}", idMesa);
            return new CuentaNotFoundException(idMesa);
        });
        return cuentaMapperOut.toResponse(cuenta);
    }

    @Override
    @Transactional
    public CuentaResponseDTO registrarPago(Long idMesa, PagoCuentaRequestDTO dto) {
        log.info("Iniciando registro de pago para mesa ID: {} con método {}", idMesa, dto.metodoPago());
        Cuenta cuenta = cuentaRepository.findByIdMesaAndEstado(idMesa, EstadoCuenta.ABIERTA).map(cuentaEntityMapper::toDomain).orElseThrow(() -> {
            log.warn("Fallo al registrar pago: no hay cuenta abierta para la mesa ID: {}", idMesa);
            return new CuentaNotFoundException(idMesa);
        });
        
        if (pedidoRepository.existsByIdMesaAndEstadoNot(idMesa, com.restaurante.model.domain.EstadoPedido.ENTREGADO)) {
            log.warn("Pago rechazado en mesa ID {}: existen pedidos pendientes por entregar", idMesa);
            throw new IllegalStateException("No se puede pagar la cuenta porque hay pedidos pendientes por entregar en la mesa.");
        }

        double totalConsumido = calcularTotalConsumido(cuenta, idMesa);

        if (totalConsumido > 0 && dto.montoRecibido() != null && dto.montoRecibido() < totalConsumido) {
            log.warn("Pago insuficiente en mesa ID {}: total=${}, recibido=${}", idMesa, totalConsumido, dto.montoRecibido());
            throw new IllegalArgumentException(String.format(
                    "El monto recibido ($%.2f) es insuficiente para cubrir el total de la cuenta ($%.2f).",
                    dto.montoRecibido(), totalConsumido));
        }

        cuenta.setTotal(totalConsumido);
        cuenta.cerrarCuenta(dto.metodoPago());
        Mesa mesa = mesaRepository.findById(idMesa).map(mesaEntityMapper::toDomain).orElseThrow(() -> {
            log.warn("Mesa ID {} no encontrada al intentar liberar la mesa", idMesa);
            return new MesaNotFoundException(idMesa);
        });
        mesa.cerrarCuenta();
        mesaRepository.save(mesaEntityMapper.toEntity(mesa));
        var guardada = cuentaEntityMapper.toDomain(cuentaRepository.save(cuentaEntityMapper.toEntity(cuenta)));
        log.info("Pago registrado exitosamente para mesa ID: {}. Total: ${}. Mesa liberada.", idMesa, totalConsumido);
        return cuentaMapperOut.toResponse(guardada);
    }

    private double calcularTotalConsumido(Cuenta cuenta, Long idMesa) {
        if (cuenta.getTotal() != null && cuenta.getTotal() > 0) {
            return cuenta.getTotal();
        }
        var pedidosMesa = pedidoRepository.findByIdMesa(idMesa);
        if (pedidosMesa == null) {
            return 0.0;
        }
        return pedidosMesa.stream()
                .filter(p -> p.getItems() != null)
                .flatMap(p -> p.getItems().stream())
                .mapToDouble(i -> (i.getPrecioCongelado() != null ? i.getPrecioCongelado() : 0.0)
                        * (i.getCantidad() != null ? i.getCantidad() : 1))
                .sum();
    }
}
