package ru.uniserg.graphaml.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GraphAmlController {

    private final String amlModelUrl;

    @Autowired
    public GraphAmlController(@Value("${aml.model.url}") String amlModelUrl) {
        this.amlModelUrl = amlModelUrl;
    }

    @PostMapping("/api/v1/graph_aml/hello")
    public String hello() {
        return amlModelUrl;
    }
}
