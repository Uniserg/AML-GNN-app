package ru.uniserg.graphaml.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.uniserg.graphaml.data.Transaction;
import ru.uniserg.graphaml.services.AmlGraphService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class GraphAmlController {

    private final AmlGraphService amlGraphService;

    @Autowired
    public GraphAmlController(AmlGraphService amlGraphService) {
        this.amlGraphService = amlGraphService;
    }

    @PostMapping("/api/v1/graph_aml/predict_is_laundering_transations")
    public Map<Long, Double> predictIsLaunderingTransactions(List<Transaction> transactions) throws IOException {
        return amlGraphService.predictIsLaunderingTransactions(transactions);
    }
}
