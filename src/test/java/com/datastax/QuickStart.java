package com.datastax;

import io.stargate.sdk.json.JsonApiClient;
import io.stargate.sdk.json.JsonCollectionClient;
import io.stargate.sdk.json.JsonNamespaceClient;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.NamespaceDefinition;
import io.stargate.sdk.json.vector.SimilarityMetric;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Unit test for simple App.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuickStart {

    static JsonApiClient jsonApi;

    @BeforeAll
    public static void setup() {
        // If default (localhost:8181) no parameter needed
        jsonApi = new JsonApiClient();
        jsonApi.dropNamespace("demo_namespace");
    }

    @Test
    @Order(1)
    public void workingWithNamespaces() {

        // [NOT PART OF ASTRA CLIENT]

        // List all namespace
        jsonApi.findNamespaces().forEach(System.out::println);

        // Create one with default settings
        jsonApi.createNamespace("demo_namespace");

        // Create one with replication infos
        jsonApi.createNamespace(NamespaceDefinition.builder()
                .name("demo_namespace2").simpleStrategy(1)
                .build());

        Assertions.assertTrue(jsonApi.existNamespace("demo_namespace"));
        Assertions.assertTrue(jsonApi.existNamespace("demo_namespace2"));

        jsonApi.dropNamespace("demo_namespace2");
        Assertions.assertFalse(jsonApi.existNamespace("demo_namespace2"));
    }

    @Test
    @Order(2)
    public void workingWithCollections() {
        JsonNamespaceClient vectorDb = jsonApi.namespace("demo_namespace");

        // List all collections
        vectorDb.findCollections();

        // Create one with default settings
        vectorDb.createCollection("demo_collection");

        // Create one with vector
        vectorDb.createCollection("demo_collection_vector", 14, SimilarityMetric.cosine);

        // Create one with vectorize
        //vectorDb.createCollection("demo_collection_openai", 1536, SimilarityMetric.cosine,
        //        "openai", "text-embeddings-ada-002");

        vectorDb.createCollection("tmp_collection");
        vectorDb.deleteCollection("tmp_collection");
        Assertions.assertFalse(vectorDb.existCollection("tmp_collection"));
    }

    @Test
    @Order(3)
    public void crudOnCollection() {

        JsonCollectionClient collectionClient = jsonApi
                .namespace("demo_namespace")
                .collection("demo_collection");

        // non id, one is generated
        String id1 = collectionClient.insertOne("{\"name\":\"product1\", \"price\": 9.99}");

        // it is not javascript we need object + let us provide an id
        collectionClient.insertOne(new JsonDocument().id("doc1").put("name", "product2").put("price", 19.99));

    }



}
