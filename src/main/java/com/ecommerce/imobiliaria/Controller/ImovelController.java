package com.ecommerce.imobiliaria.Controller;

import com.ecommerce.imobiliaria.Models.Imovel;
import com.ecommerce.imobiliaria.Models.ImovelTemporario;
import com.ecommerce.imobiliaria.Services.ImovelService;
import com.ecommerce.imobiliaria.Services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@CrossOrigin("http://localhost:4200")
@RequestMapping("/imobil")
@RestController
@AllArgsConstructor
public class ImovelController {

    private ImovelService imovelService;
    private UserService userService;

    @PostMapping("/imoveisFiltrar")
    public List<Imovel> filtrar(@RequestBody Imovel imovel){
        return imovelService.filtrar(imovel);
    }

    @GetMapping("/imoveis")
    public List<Imovel> mostrarTodosImoveis(){
        return imovelService.mostrarTodosImoveis();
    }

    @GetMapping("/imoveis/{idImovel}")
    public ResponseEntity<Imovel> mostrarImovelById(@PathVariable Integer idImovel){
        Imovel imovel = imovelService.mostrarImovelById(idImovel);
        return ResponseEntity.ok().body(imovel);
    }

    @GetMapping("/imoveisVendedor")
    public List<Imovel> mostrarImovelVendedor(@RequestParam("idVendedor") Integer idVendedor){
        return imovelService.mostrarImovelVendedor(idVendedor);
    }

    @GetMapping("/imoveis/ativo/{idVendedor}")
    public List<Imovel> mostrarImovelAtivo(@PathVariable Integer idVendedor){
        return imovelService.mostrarImovelAtivo(idVendedor);
    }

    @GetMapping("/imoveis/inativo/{idVendedor}")
    public List<Imovel> mostrarImovelInativo(@PathVariable Integer idVendedor){
        return imovelService.mostrarImovelInativo(idVendedor);
    }


    @GetMapping("/imoveisContratoAluguel")
    public List<Imovel> mostrarImovelContratoAluguel(@RequestParam("contratoAluguel") Boolean contratoAluguel){
        return imovelService.mostrarImovelContratoAluguel(contratoAluguel);
    }

    @GetMapping("/imoveisContratoVenda")
    public List<Imovel> mostrarImovelContratoVenda(@RequestParam("contratoVenda") Boolean contratoVenda){
        return imovelService.mostrarImovelContratoVenda(contratoVenda);
    }

    @GetMapping("/imoveisValorAluguel")
    public List<Imovel> mostrarImovelValorAluguel(@RequestParam("valorAluguelMax") Double valorAluguel){
        return imovelService.mostrarImovelValorAluguel(valorAluguel);
    }

    @GetMapping("/imoveisValorVenda")
    public List<Imovel> mostrarImovelValorVenda(@RequestParam("valorVendaMax") Double valorVenda){
        return imovelService.mostrarImovelValorVenda(valorVenda);
    }

    @GetMapping("/imoveisPelaArea")
    public List<Imovel> mostrarImovelPelaArea(@RequestParam("area") Double area){
        return imovelService.mostrarImovelPelaArea(area);
    }

    @GetMapping("/imoveisPorQuarto")
    public List<Imovel> mostrarImovelQuarto(@RequestParam("quartos") Integer quartos){
        return imovelService.mostrarImovelQuarto(quartos);
    }

    @GetMapping("/imoveisPorSuite")
    public List<Imovel> mostrarImovelSuite(@RequestParam("suite") Integer suite){
        return imovelService.mostrarImovelSuite(suite);
    }

    @GetMapping("/imoveisPorBanheiro")
    public List<Imovel> mostrarImovelBanheiro(@RequestParam("banheiros") Integer banheiros){
        return imovelService.mostrarImovelBanheiro(banheiros);
    }

    @GetMapping("/imoveisPorVaga")
    public List<Imovel> mostrarImovelVaga(@RequestParam("vagas") Integer vagas){
        return imovelService.mostrarImovelVaga(vagas);
    }

    @GetMapping("/imoveisPorFinalidade")
    public List<Imovel> mostrarImovelFinalidade(@RequestParam("finalidadeImovel") String finalidadeImovel){
        return imovelService.mostrarImovelFinalidade(finalidadeImovel);
    }

    @GetMapping("/imoveisPorTipo")
    public List<Imovel> mostrarImovelTipo(@RequestParam("tipoImovel") String tipoImovel){
        return imovelService.mostrarImovelTipo(tipoImovel);
    }

    @GetMapping("/imoveis/mes")
    public List<Imovel> mostrarImovelMes(){
        return imovelService.findImoveisPerMOnth();
    }


    @GetMapping("/imoveis/total")
    public Integer countImoveis(){
        return imovelService.countImoveis();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','VENDEDOR','CONSUMIDOR')")
    @PostMapping("/imoveis/{idVendedor}")
    public ResponseEntity<Imovel> cadastrarImovel(@PathVariable Integer idVendedor,
                                                  @RequestBody ImovelTemporario imovelTemporario){
        Imovel imovel = new Imovel();
        imovel = imovelService.preencherImovel(imovelTemporario);
        imovel.setUserVendedor(userService.findById(idVendedor));
        imovel = imovelService.cadastrarImovel(imovel, idVendedor);
        URI novaURI = ServletUriComponentsBuilder.fromCurrentRequest().path("{/id}")
                .buildAndExpand(imovel.getIdImovel()).toUri();
        return ResponseEntity.created(novaURI).body(imovel);
    }

    @PostMapping("/imoveis/inicial/{idVendedor}")
    public ResponseEntity<Imovel> cadastrarImovelInicial(@PathVariable Integer idVendedor,
                                                  @RequestBody ImovelTemporario imovelTemporario){
        Imovel imovel = new Imovel();
        imovel = imovelService.preencherImovel(imovelTemporario);
        imovel.setUserVendedor(userService.findById(idVendedor));
        imovel = imovelService.cadastrarImovel(imovel, idVendedor);
        URI novaURI = ServletUriComponentsBuilder.fromCurrentRequest().path("{/id}")
                .buildAndExpand(imovel.getIdImovel()).toUri();
        return ResponseEntity.created(novaURI).body(imovel);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/imoveis/{idImovel}")
    public ResponseEntity<Void> excluirImovel(@PathVariable Integer idImovel){
        imovelService.excluirImovel(idImovel);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VENDEDOR')")
    @PutMapping("/imoveis/{idImovel}")
    public ResponseEntity<Imovel> editarImovel(@PathVariable Integer idImovel,
                                               @RequestBody ImovelTemporario imovelTemporario){
        Imovel imovel = new Imovel();
        imovel = imovelService.preencherImovel(imovelTemporario);
        imovel.setIdImovel(idImovel);
        imovelService.editarImovel(imovel, idImovel);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'VENDEDOR')")
    @PutMapping("/imoveisInativar/{idImovel}")
    public ResponseEntity<Imovel> inativarImovel(@PathVariable Integer idImovel,
                                               @RequestParam("inativo") Boolean inativo){
        imovelService.inativarImovel(idImovel, inativo);
        return ResponseEntity.noContent().build();
    }
}
