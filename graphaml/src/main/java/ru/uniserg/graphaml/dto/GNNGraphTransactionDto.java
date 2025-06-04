package ru.uniserg.graphaml.dto;

import ru.uniserg.graphaml.data.TransactionGraph;

import java.util.List;

public class GNNGraphTransactionDto {
    private TransactionGraph transaction_graph;
    private List<Long> transactions_to_predict;

    public GNNGraphTransactionDto(TransactionGraph transaction_graph, List<Long> transactions_to_predict) {
        this.transaction_graph = transaction_graph;
        this.transactions_to_predict = transactions_to_predict;
    }

    public TransactionGraph getTransaction_graph() {
        return transaction_graph;
    }

    public void setTransaction_graph(TransactionGraph transaction_graph) {
        this.transaction_graph = transaction_graph;
    }

    public List<Long> getTransactions_to_predict() {
        return transactions_to_predict;
    }

    public void setTransactions_to_predict(List<Long> transactions_to_predict) {
        this.transactions_to_predict = transactions_to_predict;
    }
}
