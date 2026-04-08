package br.tec.llam.biblioteca.client;

import br.tec.llam.biblioteca.config.LlamApiProperties;
import org.mediavitae.dto.transaction.TransactionDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public TransactionApiClient(RestTemplate restTemplate, LlamApiProperties properties) {
        this.restTemplate = restTemplate;
        this.baseUrl = properties.getTransactionUrl();
    }

    public TransactionDTO getTransactionById(String id) {
        return restTemplate.getForObject(
                baseUrl + "/transaction/{id}",
                TransactionDTO.class,
                id
        );
    }

    public TransactionDTO createTransaction(TransactionDTO dto) {
        return restTemplate.postForObject(
                baseUrl + "/transaction",
                dto,
                TransactionDTO.class
        );
    }

    public void updateTransaction(TransactionDTO dto) {
        restTemplate.put(baseUrl + "/transaction", dto);
    }

    public void deleteTransaction(String id) {
        restTemplate.delete(baseUrl + "/transaction/{id}", id);
    }

    public Double getBalanceByExemplar(String exemplarId) {
        try {
            return restTemplate.getForObject(
                    baseUrl + "/transaction/balance?exemplarId={exemplarId}",
                    Double.class,
                    exemplarId
            );
        } catch (Exception e) {
            return 0.0;
        }
    }
}
