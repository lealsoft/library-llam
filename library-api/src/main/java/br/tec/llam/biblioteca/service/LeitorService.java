package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.PersonApiClient;
import br.tec.llam.biblioteca.dto.LeitorDTO;
import br.tec.llam.biblioteca.exception.BusinessException;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.person.PersonDTO;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;

import br.tec.llam.biblioteca.repository.UsuarioLocalRepository;
import br.tec.llam.biblioteca.entity.UsuarioLocal;
import br.tec.llam.biblioteca.dto.AuthRequestDTO;
import br.tec.llam.biblioteca.dto.AuthResponseDTO;

@Service
public class LeitorService {

    private final PersonApiClient personClient;
    private final UsuarioLocalRepository usuarioLocalRepository;

    public LeitorService(PersonApiClient personClient, UsuarioLocalRepository usuarioLocalRepository) {
        this.personClient = personClient;
        this.usuarioLocalRepository = usuarioLocalRepository;
    }

    public LeitorDTO buscarPorId(String id) {
        PersonDTO person = personClient.getPersonById(id);
        if (person == null) {
            throw new ResourceNotFoundException("Leitor não encontrado: " + id);
        }
        return toLeitorDTO(person);
    }

    public LeitorDTO criar(LeitorDTO dto, String tenantId) {
        // Validar documento único por tenant
        if (dto.getDocumento() != null && !dto.getDocumento().isEmpty()) {
            validarDocumentoUnico(dto.getDocumento(), tenantId);
        }
        
        // Validar email único
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            validarEmailUnico(dto.getEmail(), tenantId);
        }

        String id = UUID.randomUUID().toString();
        String uri = "urn:llam:biblioteca:" + tenantId + ":leitor:" + id;
        
        // Preparar dados com valores padrão para campos obrigatórios
        String nome = dto.getNome() != null ? dto.getNome() : "-";
        java.time.LocalTime birthHour = java.time.LocalTime.MIDNIGHT;
        java.time.LocalDate birthDate = dto.getDataNascimento() != null ? dto.getDataNascimento() : java.time.LocalDate.of(1900, 1, 1);
        String birthCity = "-";
        String birthState = "-";  // Campo pode ter limite de 2 chars
        String birthCountry = "BR";
        
        // Gerar SHA1 para campos obrigatórios
        String birthHourSha1 = sha1(birthHour.toString());
        String birthDateSha1 = sha1(birthDate.toString());
        String birthCitySha1 = sha1(birthCity);
        String birthStateSha1 = sha1(birthState);
        String birthCountrySha1 = sha1(birthCountry);
        String motherNameSha1 = sha1(nome);
        String globalSha1 = sha1(uri + nome + birthDate);
        
        PersonDTO person = new PersonDTO(
            id,                          // personId
            birthHour,                   // birthHour
            birthHourSha1,               // birthHourSHA1
            birthDate,                   // birthDate
            birthDateSha1,               // birthDateSHA1
            birthCity,                   // birthCity
            birthCitySha1,               // birthCitySHA1
            birthState,                  // birthState
            birthStateSha1,              // birthStateSHA1
            birthCountry,                // birthCountry
            birthCountrySha1,            // birthCountrySHA1
            nome,                        // motherName (usando para nome do leitor)
            motherNameSha1,              // motherNameSHA1
            globalSha1,                  // sha1
            uri                          // uri
        );

        PersonDTO created = personClient.createPerson(person);

        // Usar o ID gerado pelo person-backend (não o ID local)
        dto.setId(created.getPersonId());
        dto.setTenantId(tenantId);
        dto.setStatusLeitor("LEITOR_ATIVO");
        return dto;
    }
    
    private String sha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString().replace("-", "").substring(0, 40);
        }
    }

    public LeitorDTO atualizar(String id, LeitorDTO dto) {
        PersonDTO existing = personClient.getPersonById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Leitor não encontrado: " + id);
        }
        
        existing.setBirthDate(dto.getDataNascimento());
        if (dto.getNome() != null) {
            existing.setMotherName(dto.getNome());
        }
        personClient.updatePerson(existing);
        
        dto.setId(id);
        return dto;
    }

    public LeitorDTO excluir(String id) {
        PersonDTO person = personClient.getPersonById(id);
        if (person == null) {
            throw new ResourceNotFoundException("Leitor não encontrado: " + id);
        }
        
        // Exclusão LÓGICA: marcar URI com :excluido: (não deleta fisicamente)
        String uri = person.getUri();
        if (uri != null && !uri.contains(":excluido:")) {
            // Remover outros status antes de marcar como excluído
            uri = uri.replace(":suspenso:", ":");
            uri = uri.replace(":inativo:", ":");
            uri = uri.replace(":leitor:", ":leitor:excluido:");
            person.setUri(uri);
            personClient.updatePerson(person);
        }
        
        LeitorDTO dto = toLeitorDTO(person);
        dto.setStatusLeitor("LEITOR_EXCLUIDO");
        return dto;
    }

    private LeitorDTO toLeitorDTO(PersonDTO person) {
        LeitorDTO dto = new LeitorDTO();
        dto.setId(person.getPersonId());
        dto.setDataNascimento(person.getBirthDate());
        dto.setNome(person.getMotherName());
        
        if (person.getUri() != null && person.getUri().contains(":leitor:")) {
            String[] parts = person.getUri().split(":");
            if (parts.length >= 4) {
                dto.setTenantId(parts[3]);
            }
            // Verificar status no URI (ordem importa: excluido > suspenso > inativo)
            if (person.getUri().contains(":excluido:")) {
                dto.setStatusLeitor("LEITOR_EXCLUIDO");
            } else if (person.getUri().contains(":suspenso:")) {
                dto.setStatusLeitor("LEITOR_SUSPENSO");
            } else if (person.getUri().contains(":inativo:")) {
                dto.setStatusLeitor("LEITOR_INATIVO");
            } else {
                dto.setStatusLeitor("LEITOR_ATIVO");
            }
        } else {
            dto.setStatusLeitor("LEITOR_ATIVO");
        }
        
        return dto;
    }

    private void validarDocumentoUnico(String documento, String tenantId) {
        // TODO: Buscar leitores do tenant e verificar se documento já existe
        // Por enquanto, não bloqueia - implementar quando houver endpoint de busca
    }

    private void validarEmailUnico(String email, String tenantId) {
        // TODO: Buscar leitores do tenant e verificar se email já existe
        // Por enquanto, não bloqueia - implementar quando houver endpoint de busca
    }

    public LeitorDTO suspender(String id, String motivo) {
        PersonDTO person = personClient.getPersonById(id);
        if (person == null) {
            throw new ResourceNotFoundException("Leitor não encontrado: " + id);
        }
        
        // Atualizar URI para indicar suspensão
        String uri = person.getUri();
        if (uri != null && !uri.contains(":suspenso:")) {
            uri = uri.replace(":leitor:", ":leitor:suspenso:");
            person.setUri(uri);
            personClient.updatePerson(person);
        }
        
        LeitorDTO dto = toLeitorDTO(person);
        dto.setStatusLeitor("LEITOR_SUSPENSO");
        return dto;
    }

    public LeitorDTO reativar(String id) {
        PersonDTO person = personClient.getPersonById(id);
        if (person == null) {
            throw new ResourceNotFoundException("Leitor não encontrado: " + id);
        }
        
        // Remover indicação de suspensão/inatividade do URI
        String uri = person.getUri();
        if (uri != null) {
            uri = uri.replace(":suspenso:", ":");
            uri = uri.replace(":inativo:", ":");
            uri = uri.replace(":excluido:", ":");
            person.setUri(uri);
            personClient.updatePerson(person);
        }
        
        LeitorDTO dto = toLeitorDTO(person);
        dto.setStatusLeitor("LEITOR_ATIVO");
        return dto;
    }

    public List<LeitorDTO> listarExcluidos(String tenantId) {
        List<PersonDTO> allPersons = personClient.getAllPersons();
        
        return allPersons.stream()
                .filter(p -> p.getUri() != null && p.getUri().contains(":excluido:"))
                .filter(p -> tenantId == null || p.getUri().contains(":" + tenantId + ":"))
                .map(this::toLeitorDTO)
                .collect(Collectors.toList());
    }

    public List<LeitorDTO> listarAtivos(String tenantId) {
        List<PersonDTO> allPersons = personClient.getAllPersons();
        
        return allPersons.stream()
                .filter(p -> p.getUri() != null && p.getUri().contains(":leitor:"))
                .filter(p -> !p.getUri().contains(":excluido:"))
                .filter(p -> tenantId == null || p.getUri().contains(":" + tenantId + ":"))
                .map(this::toLeitorDTO)
                .collect(Collectors.toList());
    }

    public AuthResponseDTO autenticar(AuthRequestDTO auth) {
        UsuarioLocal usuarioLocal = usuarioLocalRepository.findByDocumento(auth.getDocumento())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com este documento na base local."));
                
        // Verificando a criptografia forte BCrypt (Senha plana vs Hash)
        if (!BCrypt.checkpw(auth.getSenha(), usuarioLocal.getSenhaHash())) {
            throw new BusinessException("Credenciais inválidas. Falha na verificação criptográfica.");
        }
        
        PersonDTO person = personClient.getPersonById(usuarioLocal.getPersonId());
        
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(UUID.randomUUID().toString() + "-" + System.currentTimeMillis());
        response.setExpiresAt(System.currentTimeMillis() + 3600000L); // 1 hora
        response.setPersonId(usuarioLocal.getPersonId());
        response.setNome(person != null ? person.getMotherName() : "Leitor");
        
        return response;
    }

    public AuthResponseDTO registrarAcessoLocal(String personId, AuthRequestDTO dto) {
        PersonDTO person = personClient.getPersonById(personId);
        if(person == null) throw new ResourceNotFoundException("Leitor não encontrado no ecossistema.");
        
        Optional<UsuarioLocal> existente = usuarioLocalRepository.findByDocumento(dto.getDocumento());
        if(existente.isPresent()) throw new BusinessException("Acesso local já registrado para este documento.");
        
        UsuarioLocal local = new UsuarioLocal();
        local.setDocumento(dto.getDocumento());
        // Aplicando Criptografia salt-based (BCrypt) exigida
        local.setSenhaHash(BCrypt.hashpw(dto.getSenha(), BCrypt.gensalt(12)));
        local.setPersonId(person.getPersonId());
        usuarioLocalRepository.save(local);
        
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken("REGISTERED_SUCCESS");
        response.setPersonId(person.getPersonId());
        response.setNome(person.getMotherName());
        return response;
    }
}
