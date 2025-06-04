package ru.uniserg.graphaml.data;

import java.util.List;

public class TransactionGraph {
    private List<Account> nodes;
    private List<Transaction> edges;

    public List<Account> getNodes() {
        return nodes;
    }

    public void setNodes(List<Account> nodes) {
        this.nodes = nodes;
    }

    public List<Transaction> getEdges() {
        return edges;
    }

    public void setEdges(List<Transaction> edges) {
        this.edges = edges;
    }

    @Override
    public String toString() {
        return "TransactionGraph{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                '}';
    }
}
