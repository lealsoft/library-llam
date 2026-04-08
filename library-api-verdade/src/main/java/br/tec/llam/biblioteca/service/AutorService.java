package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.PersonApiClient;
import br.tec.llam.biblioteca.dto.AutorDTO;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.person.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AutorService {

    private final PersonApiClient personClient;

    public AutorService(PersonApiClient personClient) {
        this.personClient = personClient;
    }

    public AutorDTO buscarPorId(String id) {
        PersonDTO person = personClient.getPersonById(id);
        if (person == null) {
            throw new ResourceNotFoundException("Autor não encontrado: " + id);
        }
        return toAutorDTO(person);
    }

    public AutorDTO criar(AutorDTO dto, String tenantId) {
        String id = UUID.randomUUID().toString();
        String uri = "urn:llam:biblioteca:" + tenantId + ":autor:" + id;
        
        PersonDTO person = new PersonDTO(
            id,
            null,
            "",
            null,
            "",
            "",
            "",
            "",
            "",
            dto.getNacionalidade() != null ? dto.getNacionalidade() : "",
            "",
            dto.getNome() + " " + (dto.getSobrenome() != null ? dto.getSobrenome() : ""),
            "",
            "",
            uri
        );

        personClient.createPerson(person);

        dto.setId(id);
        dto.setTenantId(tenantId);
        return dto;
    }

    private AutorDTO toAutorDTO(PersonDTO person) {
        AutorDTO dto = new AutorDTO();
        dto.setId(person.getPersonId());
        
        String nomeCompleto = person.getMotherName();
        if (nomeCompleto != null && !nomeCompleto.isEmpty()) {
            String[] partes = nomeCompleto.split(" ", 2);
            dto.setNome(partes[0]);
            if (partes.length > 1) {
                dto.setSobrenome(partes[1]);
            }
        }
        
        dto.setNacionalidade(person.getBirthCountry());
        
        if (person.getUri() != null && person.getUri().contains(":autor:")) {
            String[] parts = person.getUri().split(":");
            if (parts.length >= 4) {
                dto.setTenantId(parts[3]);
            }
        }
        
        return dto;
    }
}
