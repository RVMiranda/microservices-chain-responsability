%%{init: {'theme': 'base', 'themeVariables': { 'primaryColor': '#013750', 'primaryTextColor': '#fef5c8', 'lineColor': '#00988d', 'secondaryColor': '#0b1a24', 'tertiaryColor': '#012a3d'}}}%%
flowchart TD
    %% --- ESTILOS DE CLASE ---
    classDef client fill:#fef5c8,stroke:#f23e02,stroke-width:2px,color:#0b1a24;
    classDef gateway fill:#012a3d,stroke:#00988d,stroke-width:2px,color:#fef5c8;
    classDef service fill:#013750,stroke:#2c6b74,stroke-width:2px,color:#e2e8f0;
    classDef db fill:#0b1a24,stroke:#2c6b74,stroke-width:2px,color:#fef5c8;
    classDef broker fill:#00988d,stroke:#013750,stroke-width:2px,color:#0b1a24;
    classDef chain fill:#012a3d,stroke:#f23e02,stroke-width:2px,color:#fef5c8;
    classDef side fill:#1e293b,stroke:#475569,stroke-width:1px,color:#94a3b8;

    %% --- CAPA 1: PRESENTACIÓN ---
    subgraph Capa1 ["1. CAPA DE CLIENTE"]
        Frontend["Frontend (React App)<br/>Puerto: 5173"]:::client
    end

    %% --- CAPA 2: GATEWAY ---
    subgraph Capa2 ["2. ENRUTAMIENTO Y REGISTRO"]
        Gateway["API Gateway (Spring Cloud)<br/>Puerto: 8089"]:::gateway
        Eureka["Eureka Server (Netflix Discovery)<br/>Puerto: 8761"]:::side
    end

    %% --- CAPA 3: MICROSERVICIOS ---
    subgraph Capa3 ["3. MICROSERVICIOS DE NEGOCIO"]
        ProductService["Product Service<br/>(Gestión de Stock)"]:::service
        OrderService["Order Service<br/>(Gestión de Órdenes)"]:::service
        PagoService["Pago Service<br/>(Procesamiento de Pagos)"]:::service
        KafkaService["Kafka Service<br/>(Envíos y Reintentos)"]:::service
    end

    %% --- PATRÓN: CADENA DE RESPONSABILIDAD (REINTENTOS) ---
    subgraph CapaChain ["PATRÓN: CADENA DE RESPONSABILIDAD (REINTENTOS)"]
        direction LR
        HandlerA["Paso A: ApiCallHandler<br/>(Ejecuta llamada fallida a Microservicio)"]:::chain
        HandlerB["Paso B: EmailNotificationHandler<br/>(Notifica al usuario por Correo)"]:::chain
        HandlerC["Paso C: UpdateDatabaseHandler<br/>(Actualiza estado del Job a SUCCESS/FAIL)"]:::chain
        
        HandlerA -->|Siguiente Handler| HandlerB
        HandlerB -->|Siguiente Handler| HandlerC
    end

    %% --- CAPA 4: INFRAESTRUCTURA DE DATOS ---
    subgraph Capa4 ["4. PERSISTENCIA (BASES DE DATOS)"]
        MongoDB[("MongoDB Local<br/>(productos, ordenes, pagos)<br/>Puerto: 27017")]:::db
        Postgres[("PostgreSQL Local<br/>(shipping, retry_jobs)<br/>Puerto: 5432")]:::db
    end

    %% --- CAPA 5: MENSAJERÍA ---
    subgraph Capa5 ["5. BUS DE EVENTOS (KAFKA)"]
        KafkaBus["Kafka Event Bus (Broker)<br/>• inventory_update_events<br/>• order-events / order-status-changed-events<br/>• payment-events / remaining-balance-events<br/>• product-events"]:::broker
    end

    %% --- FLUJOS DE COMUNICACIÓN ---
    %% Ingress
    Frontend -->|Solicitudes HTTP REST| Gateway

    %% Enrutamiento REST
    Gateway -->|/productos| ProductService
    Gateway -->|/ordenes| OrderService
    Gateway -->|/pagos| PagoService
    Gateway -->|/envios| KafkaService

    %% Persistencia
    ProductService === MongoDB
    OrderService === MongoDB
    PagoService === MongoDB
    KafkaService === Postgres

    %% Publicación de eventos hacia el Bus
    ProductService -->|Publica product-events| KafkaBus
    OrderService -->|Publica order-events & inventory_update| KafkaBus
    PagoService -->|Publica payment-events| KafkaBus

    %% Consumo y suscripción del Bus
    KafkaBus -.->|Consume para stock| ProductService
    KafkaBus -.->|Consume para actualización de saldo| OrderService
    KafkaBus -.->|Consume para Envío y Reintentos| KafkaService

    %% Conexión de KafkaService al motor de reintentos
    KafkaService ==>|Orquesta Reintentos| HandlerA
    HandlerC -.->|Persiste resultado de reintento| Postgres
    HandlerA -.->|Reintenta REST| Gateway

    %% Notas de infraestructura lateral (sin flechas cruzadas)
    ProductService & OrderService & PagoService & KafkaService -.- LogGroup
    subgraph LogGroup ["Central de Logs"]
        Localstack["LocalStack (AWS CloudWatch logs)<br/>Puerto: 4566"]:::side
    end

    %% Estilos de Subgraph
    style Capa1 fill:transparent,stroke:#f23e02,stroke-dasharray: 5 5;
    style Capa2 fill:transparent,stroke:#00988d,stroke-dasharray: 5 5;
    style Capa3 fill:transparent,stroke:#2c6b74,stroke-dasharray: 5 5;
    style CapaChain fill:transparent,stroke:#f23e02,stroke-width:2px;
    style Capa4 fill:transparent,stroke:#2c6b74,stroke-dasharray: 5 5;
    style Capa5 fill:transparent,stroke:#00988d,stroke-dasharray: 5 5;