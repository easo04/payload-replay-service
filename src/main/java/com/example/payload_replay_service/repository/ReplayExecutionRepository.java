package com.example.payload_replay_service.repository;

import com.example.payload_replay_service.config.DynamoDbProperties;
import com.example.payload_replay_service.model.ReplayExecutionEntity;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Comparator;
import java.util.Optional;

@Repository
public class ReplayExecutionRepository {

    private final DynamoDbTable<ReplayExecutionEntity> table;

    public ReplayExecutionRepository(
            DynamoDbClient dynamoDbClient,
            DynamoDbProperties properties
    ) {

        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder()
                        .dynamoDbClient(dynamoDbClient)
                        .build();

        this.table =
                enhancedClient.table(
                        properties.tableName(),
                        TableSchema.fromBean(
                                ReplayExecutionEntity.class
                        )
                );
    }

    /**
     * Sauvegarde un résultat de replay
     */
    public void save(ReplayExecutionEntity entity) {

        table.putItem(entity);
    }

    /**
     * Retourne le replay le plus récent.
     *
     * NOTE :
     * Cette implémentation effectue un scan complet de la table.
     * Acceptable pour une POC, mais il faut pas faire ça en prod
     * On peut remplacer par un GSI en production.
     */
    public Optional<ReplayExecutionEntity> findLatest() {

        return table.scan()
                .items()
                .stream()
                .max(
                        Comparator.comparing(
                                ReplayExecutionEntity::getCreatedAtEpoch
                        )
                );
    }
}