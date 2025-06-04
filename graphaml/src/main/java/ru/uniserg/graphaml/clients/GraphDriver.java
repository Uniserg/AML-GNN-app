package ru.uniserg.graphaml.clients;


import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.driver.ser.GraphBinaryMessageSerializerV1;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Component
public class GraphDriver {

    private Cluster cluster;

    private GraphTraversalSource g;

    private final String clusterHosts;
    private final String port;

    public GraphDriver(@Value("${janusgraph.clusterHosts}") String clusterHosts, @Value("${janusgraph.port}") String port) {
        this.clusterHosts = clusterHosts;
        this.port = port;
    }

    private Configuration getConfiguration() {
        return new MapConfiguration(
                Map.of(
                        "hosts", clusterHosts,
                        "port", port,
                        "serializer", Map.of(
                                "className", GraphBinaryMessageSerializerV1.class.getName(),
                                "config", Map.of("ioRegistries", List.of(JanusGraphIoRegistry.class.getName()))
                        )
                )
        );
    }

    private Cluster getCluster() {
        if (cluster.isClosed()) {
            this.cluster = Cluster.open(getConfiguration());
        }
        return this.cluster;
    }

    public GraphTraversalSource g() {
        if (g == null) {
            this.g = traversal().withRemote(
                    DriverRemoteConnection.using(getCluster(), "g")
            );
        }
        return g;
    }
}