package ru.uniserg.graphaml.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.uniserg.graphaml.dto.GNNGraphTransactionDto;
import ru.uniserg.graphaml.utils.MapUtils;

import java.util.List;
import java.util.Map;

@Component
public class GnnModelClient {
    private final String amlModelUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public GnnModelClient(@Value("${aml.model.url}") String amlModelUrl, RestTemplate restTemplate) {
        this.amlModelUrl = amlModelUrl;
        this.restTemplate = restTemplate;
    }

    /**
     * Отправляет транзакцию в AML-модель и получает предсказание (вероятности)
     * @param gnnGraphTransactionDto DTO с данными транзакции для GNN-модели
     * @return Список вероятностей (вероятность отмывания денег)
     */
    public Map<Long, Double> getTransactionPrediction(GNNGraphTransactionDto gnnGraphTransactionDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GNNGraphTransactionDto> request = new HttpEntity<>(gnnGraphTransactionDto, headers);


        ResponseEntity<List> response = restTemplate.postForEntity(
                amlModelUrl,
                request,
                List.class
        );

        return MapUtils.zipToMap(gnnGraphTransactionDto.getTransactions_to_predict(), response.getBody());
    }
}