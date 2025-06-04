package ru.uniserg.graphaml.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.uniserg.graphaml.clients.GraphDriver;
import ru.uniserg.graphaml.data.Account;
import ru.uniserg.graphaml.data.Transaction;
import ru.uniserg.graphaml.data.TransactionGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class AmlGraphRepository {

    private final GraphDriver gd;

    private final ObjectMapper objectMapper;

    @Autowired
    public AmlGraphRepository(GraphDriver gd, ObjectMapper objectMapper) {
        this.gd = gd;
        this.objectMapper = objectMapper;
    }

    public TransactionGraph getSubgraphByTransactions(List<Transaction> transactions) {

        int maxDepth = 8;
        // 1. Собираем все уникальные accountId из входных транзакций
        Set<Long> accountIds = transactions.stream()
                .flatMap(t -> Stream.of(t.getSourceAccount(), t.getTargetAccount()))
                .collect(Collectors.toSet());

        // 2. Получаем начальные вершины аккаунтов
        List<Vertex> startVertices = gd.g().V()
                .hasLabel("account")
                .has("accountId", P.within(accountIds))
                .toList();

        // 3. Выполняем обход графа на указанную глубину
        List<Path> paths = gd.g().V(startVertices)
                .repeat(
                        __.bothE("transaction")
                                .otherV()
                                .simplePath()
                )
                .times(maxDepth)
                .path()
                .by(__.valueMap(true))
                .toList();

        // 4. Извлекаем уникальные вершины и ребра из путей
        Set<Vertex> allVertices = new HashSet<>();
        Set<Edge> allEdges = new HashSet<>();

        for (Path path : paths) {
            for (Object element : path) {
                if (element instanceof Vertex) {
                    allVertices.add((Vertex) element);
                } else if (element instanceof Edge) {
                    allEdges.add((Edge) element);
                }
            }
        }

        // 5. Преобразуем в объект TransactionGraph
        TransactionGraph graph = new TransactionGraph();

        // Конвертируем вершины в Account
        List<Account> accounts = allVertices.stream()
                .map(v -> new Account(
                        v.value("accountId"),
                        v.value("currency"),
                        v.value("bank")))
                .collect(Collectors.toList());

        // Конвертируем ребра в Transaction
        List<Transaction> transactionsList = allEdges.stream()
                .map(e -> new Transaction(
                        e.value("transactionId"),
                        e.outVertex().value("accountId"),
                        e.inVertex().value("accountId"),
                        e.value("amount"),
                        e.value("timestamp"),
                        e.value("paymentType")))
                .collect(Collectors.toList());

        graph.setNodes(accounts);
        graph.setEdges(transactionsList);

        return graph;
    }

    public void saveTransactionsPredictions(Map<Long, Double> predictions) {
        predictions.forEach((transactionId, prediction) -> {
            if (prediction != null) {
                gd.g().E()
                        .hasLabel("TRANSFER")
                        .has("transactionId", transactionId)
                        .property("isLaundering", prediction)
                        .next();
            }
        });
        gd.g().tx().commit();
    }
}
