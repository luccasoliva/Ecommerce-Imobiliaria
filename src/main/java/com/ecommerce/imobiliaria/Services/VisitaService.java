package com.ecommerce.imobiliaria.Services;

import com.ecommerce.imobiliaria.Models.Enums.StatusVisita;
import com.ecommerce.imobiliaria.Models.Imovel;
import com.ecommerce.imobiliaria.Models.User;
import com.ecommerce.imobiliaria.Models.Visita;
import com.ecommerce.imobiliaria.Repositories.ImovelRepository;
import com.ecommerce.imobiliaria.Repositories.UserRepository;
import com.ecommerce.imobiliaria.Repositories.VisitaRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
@Transactional
public class VisitaService {
    private static final String MSG_IMOVEL_NAO_ENCONTRADO = "Imóvel não encontrado";
    private static final String MSG_VISITA_NAO_ENCONTRADA = "Visita não encontrada";

    private VisitaRepository visitaRepository;
    private ImovelRepository imovelRepository;
    private UserRepository userRepository;


    public List<Visita> getAllVisitas() {
        return visitaRepository.findAll();
    }

    public Visita getVisitaById(Integer id) {
        return visitaRepository.findById(id).get();
    }


    public List<Visita> getVisitaByImovelId(Integer id) {
        return visitaRepository.findByImovelId(id);
    }



    public Visita saveVisita(Visita visita, Integer idUserConsumidor,Integer idImovel) {
        Imovel imovel = imovelRepository.findById(idImovel).orElseThrow(() -> new EntityNotFoundException(MSG_IMOVEL_NAO_ENCONTRADO));
        Optional<User> userConsumidor = Optional.ofNullable(userRepository.findById(idUserConsumidor).orElseThrow(
                () -> new EntityNotFoundException("Consumidor não encontrado")));
        Optional<User> userVendedor = Optional.ofNullable(userRepository.findById(imovel.getUserVendedor().getIdUser()).orElseThrow(
                () -> new EntityNotFoundException("Vendedor não encontrado")));

        if (userConsumidor.get().getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("CONSUMIDOR") || r.getAuthority().equals("VENDEDOR") || r.getAuthority().equals("ADMIN"))
                && Objects.equals(userVendedor.get().getIdUser(), imovel.getUserVendedor().getIdUser())) {
            visita.setIdVisita(null);
            visita.setImovel(imovel);
            visita.setStatusVisita(StatusVisita.PENDENTE);
            visita.setDataCriacao(LocalDateTime.now());
            visita.setUserConsumidor(userConsumidor.get());
            visita.setUserVendedor(userVendedor.get());
            return visitaRepository.save(visita);
        } else {
            throw new DataIntegrityViolationException("Usuário não é consumidor ou não é vendedor do imóvel");
        }
    }




    public Visita updateVisita(Visita visita, Integer idVisita ) {
        Visita visitaAtual = visitaRepository.findById(idVisita).orElseThrow(() -> new EntityNotFoundException(MSG_VISITA_NAO_ENCONTRADA));
        visitaAtual.setDataVisita(visita.getDataVisita());
        visitaAtual.setHorarioVisita(visita.getHorarioVisita());

        return visitaRepository.save(visitaAtual);
    }


    public void deleteVisita(Integer id) {
        Visita visita = visitaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(MSG_VISITA_NAO_ENCONTRADA));
        visitaRepository.delete(visita);
    }


    public Visita setStatus(Integer id, String status) {
        Visita visita = visitaRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(MSG_VISITA_NAO_ENCONTRADA));
        switch (status) {
            case "PENDENTE" -> visita.setStatusVisita(StatusVisita.PENDENTE);
            case "CONFIRMADO" -> visita.setStatusVisita(StatusVisita.CONFIRMADO);
            case "CONCLUIDO" -> visita.setStatusVisita(StatusVisita.CONCLUIDO);
            case "CANCELADO" -> visita.setStatusVisita(StatusVisita.CANCELADO);
        }
        return visitaRepository.save(visita);
    }



    public List<Visita> findByidConsumidor(Integer idUserConsumidor) {
        return visitaRepository.findByIdConsumidor(idUserConsumidor);
    }


    public List<Visita> findByidVendedor(Integer idUserVendedor) {
        return visitaRepository.findByIdVendedor(idUserVendedor);
    }


    public List<Visita> findByStatus(String status) {
        try {
            return visitaRepository.findByStatus(status);
        } catch (Exception e) {
            throw new EntityNotFoundException("Status não encontrado");
        }
    }


    public List<Visita> findByStatusAndIdVendedor(String status, Integer idVendedor) {

            return visitaRepository.findByStatusAndIdVendedor(status, idVendedor);

    }


    public List<Visita> findByStatusAndIdConsumidor(String status, Integer idConsumidor) {

        return visitaRepository.findByStatusAndIdConsumidor(status, idConsumidor);

    }


    public List<Visita> findVisitasPorMes() {
        return (List<Visita>) visitaRepository.findVisitasPorMes();
    }


    public Integer findTotalVisitas() {
        return visitaRepository.findTotalVisitas();
    }
}
