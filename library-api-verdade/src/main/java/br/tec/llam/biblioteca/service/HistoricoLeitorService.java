package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.PersonApiClient;
import br.tec.llam.biblioteca.dto.HistoricoLeitorDTO;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.person.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HistoricoLeitorService {

    private final PersonApiClient personClient;

    public HistoricoLeitorService(PersonApiClient personClient) {
        this.personClient = personClient;
    }

    public HistoricoLeitorDTO buscarHistorico(String leitorId, String tenantId) {
        PersonDTO person = personClient.getPersonById(leitorId);
        if (person == null) {
            throw new ResourceNotFoundException("Leitor não encontrado: " + leitorId);
        }

        return HistoricoLeitorDTO.builder()
            .leitorId(leitorId)
            .nomeLeitor(person.getMotherName())
            .totalEmprestimos(0)
            .emprestimosAtivos(0)
            .totalMultas(0)
            .valorMultasPendentes(0.0)
            .totalReservas(0)
            .ultimoEmprestimo(null)
            .emprestimosRecentes(new ArrayList<>())
            .tenantId(tenantId)
            .build();
    }
}
