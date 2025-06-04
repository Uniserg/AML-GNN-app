def createIndices() {
    println "Creating indices..."
    
    // Получаем API управления графом
    mgmt = graph.openManagement()
    
    // Создаем composite индекс для accountId (быстрый поиск)
    if (!mgmt.containsGraphIndex('byAccountId')) {
        accountId = mgmt.getPropertyKey('accountId')
        mgmt.buildIndex('byAccountId', Vertex.class)
             .addKey(accountId)
             .buildCompositeIndex()
        println "Created composite index for accountId"
    }
    
    mgmt.commit()
    println "Indices created successfully!"
}

// Функция для очистки графа с учетом индексов
def clearGraph() {
    println "Clearing the graph..."
    
    // Отключаем индексы перед удалением данных (для ускорения)
    mgmt = graph.openManagement()
    mgmt.updateIndex(mgmt.getGraphIndex('byAccountId'), SchemaAction.DISABLE_INDEX).get()
    mgmt.commit()
    
    // Удаляем все ребра и вершины
    g.E().drop().iterate()
    g.V().drop().iterate()
    
    // Включаем индексы обратно
    mgmt = graph.openManagement()
    mgmt.updateIndex(mgmt.getGraphIndex('byAccountId'), SchemaAction.ENABLE_INDEX).get()
    mgmt.commit()
    
    println "Graph cleared successfully!"
}

// Оптимизированная функция загрузки вершин
def loadAccountNodes(String filePath) {
    println "Loading account vertices from ${filePath}"
    
    batchSize = 1000
    counter = 0
    
    // Используем батчинг для ускорения загрузки
    new File(filePath).readLines().drop(1).each { line ->
        def parts = line.split(',')
        g.addV('Account')
           .property('accountId', parts[0].trim())
           .property('bank', parts[1].trim())
           .property('currency', parts[2].trim())
           .next()
        
        if (++counter % batchSize == 0) {
            g.tx().commit()
            println "Loaded ${counter} accounts"
        }
    }
    
    g.tx().commit()
    println "Total ${counter} account vertices loaded"
}

// Оптимизированная функция загрузки ребер
def loadTransactionEdges(String filePath) {
    println "Loading transaction edges from ${filePath}"
    
    batchSize = 1000
    counter = 0
    lookupCache = [:] // Кеш для быстрого поиска вершин
    
    new File(filePath).readLines().drop(1).each { line ->
        def parts = line.split(',')
        def fromId = parts[0].trim()
        def toId = parts[1].trim()
        
        // Используем кеш для поиска вершин
        def fromV = lookupCache.computeIfAbsent(fromId) { 
            g.V().has('Account', 'accountId', fromId).next() 
        }
        def toV = lookupCache.computeIfAbsent(toId) { 
            g.V().has('Account', 'accountId', toId).next() 
        }
        
        // Создаем ребро
        g.addE('TRANSACTION')
           .from(fromV)
           .to(toV)
           .property('timestamp', parts[2].trim() as Long)
           .property('amount', parts[3].trim() as Double)
           .property('paymentFormat', parts[4].trim())
           .property('isLaundering', parts[5].trim() as Boolean)
           .next()
        
        if (++counter % batchSize == 0) {
            g.tx().commit()
            lookupCache.clear() // Очищаем кеш периодически
            println "Loaded ${counter} transactions"
        }
    }
    
    g.tx().commit()
    println "Total ${counter} transaction edges loaded"
}

// Основной процесс загрузки
try {
    // Получаем ссылку на граф
    graph = g.getGraph()
    
    // 1. Создаем индексы
    createIndices()
    
    // 2. Очищаем граф
    clearGraph()
    
    // 3. Загружаем данные с оптимизациями
    loadAccountNodes('C:/path/to/account_nodes.csv')
    loadTransactionEdges('C:/path/to/trans_edges.csv')
    
    // 4. Ждем регистрации индексов
    mgmt = graph.openManagement()
    mgmt.awaitGraphIndexStatus(graph, 'byAccountId').call()
    mgmt.commit()
    
    println "Data loading completed successfully!"
} catch (Exception e) {
    g.tx().rollback()
    println "Error during data loading: ${e.message}"
    e.printStackTrace()
} finally {
    if (g.tx().isOpen()) {
        g.tx().rollback()
    }
}