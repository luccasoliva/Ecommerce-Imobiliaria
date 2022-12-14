package com.ecommerce.imobiliaria.Services;


import com.ecommerce.imobiliaria.Models.Caracteristica;
import com.ecommerce.imobiliaria.Models.Imovel;
import com.ecommerce.imobiliaria.Repositories.CaracteristicaRepository;
import com.ecommerce.imobiliaria.Repositories.ImovelRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@Builder
public class CaracteristicaService {

    private CaracteristicaRepository caracteristicaRepository;

    private ImovelRepository imovelRepository;



    public List<Caracteristica> mostrarCaracteristicas() {
        return caracteristicaRepository.findAll();
    }

    public Caracteristica mostrarCaracteristicaPeloId(Integer idCarac){
        Optional<Caracteristica> caracteristica = caracteristicaRepository.findById(idCarac);
        return caracteristica.orElseThrow( ()-> new EntityNotFoundException("Caracteristica não encontrada"));
    }

    public List<Caracteristica> mostrarCaracteristicaPeloIdImovel(Integer idImovel){
        return caracteristicaRepository.MostrarCaracteristicaPeloIdImovel(idImovel);

    }



    public Caracteristica save(Caracteristica caracteristica) {
        caracteristica.setId(null);
        return caracteristicaRepository.save(caracteristica);
    }


    public void delete(Integer id) {
        caracteristicaRepository.deleteById(id);
    }

    public void removerCaracteristicasDoImovel(Integer idImovel){
        Imovel imovel = imovelRepository.findById(idImovel).get();
        imovel.getCaracteristicas().clear();
        imovelRepository.save(imovel);
    }

    public void addCaracteristicaToImovel(Integer idImovel, Integer idCarac) {
        Imovel imovel = imovelRepository.findById(idImovel).get();
        Caracteristica caracteristica = caracteristicaRepository.findById(idCarac).get();
        imovel.getCaracteristicas().add(caracteristica);
        imovelRepository.save(imovel);
    }



}