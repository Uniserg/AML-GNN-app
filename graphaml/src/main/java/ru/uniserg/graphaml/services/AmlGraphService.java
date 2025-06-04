package ru.uniserg.graphaml.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.uniserg.graphaml.clients.GnnModelClient;
import ru.uniserg.graphaml.data.Transaction;
import ru.uniserg.graphaml.data.TransactionGraph;
import ru.uniserg.graphaml.dto.GNNGraphTransactionDto;
import ru.uniserg.graphaml.repositories.AmlGraphRepository;

import java.util.List;
import java.util.Map;

@Service
public class AmlGraphService {

    private final AmlGraphRepository amlGraphRepository;
    private final GnnModelClient gnnModelClient;

    @Autowired
    public AmlGraphService(AmlGraphRepository amlGraphRepository, GnnModelClient gnnModelClient) {
        this.amlGraphRepository = amlGraphRepository;
        this.gnnModelClient = gnnModelClient;
    }

    public Map<Long, Double> predictIsLaunderingTransactions(List<Transaction> transactions) {
        TransactionGraph transactionGraph = amlGraphRepository.getSubgraphByTransactions(transactions);

        Map<Long, Double> predictions = gnnModelClient.getTransactionPrediction(new GNNGraphTransactionDto(transactionGraph,
                transactions.stream().map(Transaction::getTransactionId).toList()));

        amlGraphRepository.saveTransactionsPredictions(predictions);

        return predictions;
    }
}
