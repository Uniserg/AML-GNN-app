from data.transaction_graph import TransactionGraph

if __name__ == '__main__':
    
    transaction_graph = TransactionGraph.from_json({
        "nodes": [
            {"accountId": 1, "bank": "Sber", "currency": "RUB"},
            {"accountId": 2, "bank": "Sber", "currency": "RUB"},
        ],
        "edges": [
            {"sourceAccount": 1, "targetAccount": 2, "timestamp": 383922, "amount": 321312, "paymentType": "Remote"}
        ]
    })

    print(transaction_graph)