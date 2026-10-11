# AquaPort — Nivel Empoleon (Enterprise)

60 drones, 4 zonas hídricas interconectadas (Embalse de Investigación, Red de Canales, Laguna de Reserva y Laboratorio Hídrico Central) y rutas multi-etapa con waypoints.

> **Nivel activo en el `pom.xml`:** `Empoleon`. Para compilar y probar todo el nivel:
> ```
> mvn clean test
> ```

**Estructura del nivel** (una carpeta por ejercicio con código; los ejercicios Enterprise van por capas):
```
Empoleon/src/
├── main/java/com/eci/aquaport/
│   ├── ejercicio1/   → Reto 01: AnalizadorRedHidrica + Collector propio (Streams)
│   ├── ejercicio3/   → Reto 03: patrones Enterprise por capas
│   │   ├── dominio/          (entidades, reglas, puertos — solo Java puro)
│   │   ├── aplicacion/       (casos de uso)
│   │   ├── infraestructura/  (adaptadores de API, decoradores técnicos)
│   │   └── Main.java         (raíz de composición)
│   └── ejercicio12/  → Reto 12: rutas multi-etapa con TDD (mismas 3 capas)
└── test/java/com/eci/aquaport/
    ├── ArquitecturaCapasTest → Reto 14: auditoría de capas con ArchUnit
    ├── ejercicio1/   → AnalizadorRedHidricaTest
    ├── ejercicio3/   → CadenaValidacionTest, DroneConMonitoreoTest, AdaptadorAPIHidricaTest (Mockito)
    └── ejercicio12/  → PlanificadorRutaTest, EjecutorRutaTest, ReglasTramoTest, FlujoMultiEtapaIntegracionTest
```

Reto 1 — Streams para rutas multi-etapa entre 60 drones
- [x] Eficiencia por zona, mejor historial, ruta multi-etapa, Collector propio y justificación del stream paralelo
- Evidencia:

  **Ubicación del código:** `Empoleon/src/main/java/com/eci/aquaport/ejercicio1/`
  **Pruebas:** `Empoleon/src/test/java/com/eci/aquaport/ejercicio1/AnalizadorRedHidricaTest.java` (7 pruebas)

  | # | Consulta | Implementación |
  |---|---|---|
  | 1 | Eficiencia de cada zona (completadas / totales) | `eficienciaPorZona`: `groupingBy(zonaDestino, averagingDouble(entregada ? 1 : 0))`. El promedio de 1 y 0 es exactamente la proporción de misiones entregadas. |
  | — | Carga de trabajo por zona (ejemplo del enunciado) | `cargaPorZona`: `filter(EN_TRANSITO)` + `flatMap(waypoints)` + `groupingBy(counting())`. |
  | 2 | Drone con mejor historial de entregas | `droneConMejorHistorial`: `max(comparingInt(entregasExitosas))`. |
  | 3 | Ruta multi-etapa con el drone más cercano por tramo | `planificarRuta`: `IntStream.range` sobre los pares de paradas consecutivas; para cada tramo elige el drone disponible con batería ≥ 35% que ya esté en la zona de origen y, entre ellos, el de más batería. Si ningún drone sirve, lanza una excepción que indica el tramo. |
  | 4 | Collector personalizado: batería promedio por zona en una sola pasada | `ColectoresFlota.promedioBateriaPorZona()` con `Collector.of`: el acumulador guarda `[suma, cantidad]` por zona en un `EnumMap`, el combiner suma los parciales y el finisher divide. |

  **¿Cuándo usar stream paralelo?**
  - **Sí aplica** al Collector propio sobre volúmenes grandes (por ejemplo, el histórico de telemetría de miles de lecturas): la operación es asociativa, no tiene estado compartido y el combiner suma parciales. La prueba `collectorEnParalelo` verifica con 10 000 drones que `parallelStream()` da exactamente el mismo resultado que `stream()`.
  - **No aplica** a las consultas sobre la flota de 60 drones: con tan pocos elementos, el costo de dividir el trabajo entre hilos (fork/join) es mayor que el de recorrer la lista.
  - **No aplica** a `planificarRuta`: los tramos son una secuencia ordenada (origen → waypoints → destino) y el orden importa.

  ```
  mvn exec:java "-Dexec.mainClass=com.eci.aquaport.ejercicio1.Main"
  ```
  **Salida obtenida (60 drones, 15 por zona):**
  ```
  1) Eficiencia por zona destino: {LAB_HIDRICO_CENTRAL=0.4, RED_CANALES=1.0}
     Carga actual por zona (waypoints EN_TRANSITO): {EMBALSE_INVESTIGACION=1, LAB_HIDRICO_CENTRAL=2, LAGUNA_RESERVA=1, RED_CANALES=1}
  2) Drone con mejor historial: DroneAcuatico[id=AR-23, tipo=SUPERFICIAL, bateria=71, disponible=true, zona=RED_CANALES, entregasExitosas=49]
  3) Ruta multi-etapa EMBALSE -> CANALES -> LAB:
     EMBALSE_INVESTIGACION -> RED_CANALES con AR-15
     RED_CANALES -> LAB_HIDRICO_CENTRAL con AR-30
  4) Bateria promedio por zona (Collector propio): {EMBALSE_INVESTIGACION=60.0, LAB_HIDRICO_CENTRAL=61.0, LAGUNA_RESERVA=60.67, RED_CANALES=60.33}
  ```
<img width="967" height="673" alt="imagen" src="https://github.com/user-attachments/assets/26574188-7c8f-450c-85cc-a5c2b24e5f25" />

Reto 2 — Historial de git limpio y trazable
- [x] Hook de Conventional Commits, protección de ramas, CHANGELOG automático y 10+ commits del Enterprise
- Evidencia:

  **Protección de ramas (`main` y `Develop`):** configurada con la API de GitHub. Exige Pull Request para integrar, aplica también a los administradores, prohíbe force push y prohíbe borrar la rama. Prueba de que el push directo queda bloqueado:
  ```
  $ git push origin HEAD:Develop
  remote: error: GH006: Protected branch update failed for refs/heads/Develop.
   ! [remote rejected] HEAD -> Develop (protected branch hook declined)
  ```

  **Log de git del Enterprise** (`git log --oneline v2.0.0..HEAD`, sin merges): 15 commits, todos con Conventional Commits:
  ```
  504a136 refactor: dividir los Main de Empoleon en metodos de maximo 15 lineas (estandar Enterprise)
  9368527 test: auditoria de arquitectura por capas con ArchUnit (direccion de dependencias, dominio puro, inyeccion por constructor) (Empoleon reto 14)
  fb8ba75 build: quality gate Enterprise de JaCoCo (85% lineas y 75% ramas del proyecto, 80% por clase) con pruebas de las ramas pendientes (Empoleon reto 13)
  c00da80 feat: PlanificadorRuta y EjecutorRuta con reasignacion, cadena de custodia y notificacion de waypoints (Green)
  c514420 test: pruebas TDD del planificador y ejecutor de rutas multi-etapa con los 5 flujos alternos e integracion de capas (Red)
  6eb4a6b chore: copiar el modelo por capas de ejercicio3 a ejercicio12 (TDD de rutas multi-etapa)
  54396f6 docs: README de Empoleon con estructura por capas y evidencia de los retos 01, 02 y 03
  acc9029 test: Mockito verifica que la cadena se detiene en el primer fallo, que el decorator no altera el drone y que el adapter convierte valores limite
  79cc02b feat: cadena de validacion, decorator de telemetria y adapter de la API hidrica por capas (Empoleon reto 03)
  1648a92 docs: CHANGELOG generado desde los commits
  0207eec build: hook commit-msg que valida Conventional Commits y script que genera el CHANGELOG (Empoleon reto 02)
  ecbd89c test: pruebas del AnalizadorRedHidrica y del Collector en stream paralelo
  948dde9 feat: streams de la red multi-embalse con eficiencia por zona, ruta multi-etapa y Collector propio (Empoleon reto 01)
  855f3c3 build: activar el nivel Empoleon en el pom (version 3.0.0-SNAPSHOT)
  aa5455f build: excluir copias de ejercicio12 del calculo de duplicacion y los Main de la cobertura en Sonar
  ```

  **Hook de pre-commit (`.githooks/commit-msg`):** rechaza cualquier commit cuya primera línea no siga `<tipo>(alcance opcional): descripción`, con los tipos `feat`, `fix`, `refactor`, `test`, `docs`, `build`, `chore`, `style`, `perf` y `ci`. Los merges y reverts se permiten. Se activa una vez por clon:
  ```
  git config core.hooksPath .githooks
  ```
  Prueba del hook:
  ```
  $ git commit -m "cambios"
  ERROR: el mensaje no sigue Conventional Commits:
    "cambios"
  Formato: <tipo>(alcance opcional): descripcion
  ```

  **CHANGELOG automático (`scripts/generar-changelog.sh`):** lee `git log`, arma una sección por versión (tags `v1.0.0`, `v2.0.0` y "Sin publicar") y dentro agrupa por tipo (Funcionalidades, Correcciones, Refactorizaciones, Pruebas, Documentación, Build). Los commits que no siguen el formato, como los "Update README" anteriores al hook, quedan fuera.
  ```
  sh scripts/generar-changelog.sh
  ```
  Resultado: [`CHANGELOG.md`](CHANGELOG.md).
<img width="806" height="541" alt="imagen" src="https://github.com/user-attachments/assets/39ca023c-8c9e-48d4-9c75-8a27ba3ebb3b" />

Reto 3 — Chain of Responsibility + Decorator + Adapter
- [x] Los 3 patrones integrados en el flujo Enterprise, con pruebas Mockito
- Evidencia:

  **Ubicación del código:** `Empoleon/src/main/java/com/eci/aquaport/ejercicio3/` (por capas)
  **Pruebas:** `CadenaValidacionTest` (8), `DroneConMonitoreoTest` (3), `AdaptadorAPIHidricaTest` (10)

  | Patrón | Capa | Clases | Rol |
  |---|---|---|---|
  | Chain of Responsibility | dominio | `ValidadorMision` (abstracta), `ValidadorBateria` → `ValidadorCapacidadCarga` → `ValidadorZonaActiva` → `ValidadorCondicionesHidricas` | Cada eslabón valida una sola regla; el primero que falla detiene la cadena y devuelve el motivo. La cadena estándar se arma en `ServicioAsignacion.cadenaEstandar`. |
  | Decorator | infraestructura | `Drone` (interfaz del dominio), `DroneConMonitoreo` | Envuelve cualquier `Drone` y registra la telemetría de inicio y fin de cada tramo en el puerto `RegistroTelemetria`, sin modificar `DroneAcuatico`. Se puede apilar. |
  | Adapter | infraestructura | `AdaptadorAPIHidrica` implementa el puerto `ServicioCondicionesHidricas` | Traduce la respuesta externa en inglés (`waterLevel`, `turbidity`) a los value objects del dominio (`NivelAgua`, `Turbidez`). El dominio nunca sabe que existe una API. |

  **Pruebas obligatorias con Mockito:**
  | Prueba pedida | Test | Cómo se verifica |
  |---|---|---|
  | La cadena se detiene en el primer validador que falla | `cadenaSeDetieneEnPrimerFallo` | El siguiente eslabón es un mock: `verify(siguienteMock, never()).validar(...)`. También `capacidadExcedida` verifica que la API hídrica (mock) nunca se consulta si falla antes la capacidad. |
  | El decorator registra sin alterar el comportamiento del drone | `registraTelemetria`, `noAlteraComportamiento` | `InOrder` sobre el mock de `RegistroTelemetria` (inicio y luego fin); un drone decorado y uno simple terminan con el mismo estado. |
  | El adapter convierte campos con valores límite | `valoresLimiteValidos`, `valoresLimiteInvalidos` | Con el cliente mock: 0.0 y valores extremos se convierten exactos; negativos y `NaN` se rechazan en la frontera con `IllegalArgumentException`. |

  ```
  mvn exec:java "-Dexec.mainClass=com.eci.aquaport.ejercicio3.Main"
  ```
  **Salida obtenida:**
  ```
  M-601 asignada a AR-01
     [TELEMETRIA AR-01] Inicio tramo EMBALSE_INVESTIGACION -> LAB_HIDRICO_CENTRAL con 90%
     [TELEMETRIA AR-01] Fin tramo en LAB_HIDRICO_CENTRAL con 80%
  M-602 rechazada; motivo por drone:
     AR-01: Carga de 400 g supera la capacidad de 300 g del BUCEADOR
     AR-16: Condiciones adversas en LAGUNA_RESERVA: turbidez 64.0 NTU
     AR-31: Bateria insuficiente: 30% (minimo 35%)
  M-603 rechazada; motivo por drone:
     AR-01: La zona destino RED_CANALES esta inactiva
     AR-16: La zona destino RED_CANALES esta inactiva
     AR-31: Bateria insuficiente: 30% (minimo 35%)
  ```
<img width="594" height="536" alt="imagen" src="https://github.com/user-attachments/assets/ad65ccde-bb44-41d5-8dba-e8989a939aed" />

Reto 4 — Auditoría SOLID
- [x] Capas, constructor injection, dominio sin dependencias externas
- Evidencia:

  **Revisión manual de imports** (`grep` de los `import` de cada capa, agrupados):
  | Capa | ejercicio3 importa | ejercicio12 importa |
  |---|---|---|
  | dominio | solo `java.util` | solo `java.util` |
  | aplicacion | `dominio`, `java.util` | `dominio`, `java.util`, `java.util.stream` |
  | infraestructura | `dominio`, `java.util` | `dominio`, `java.util` |

  **Verificaciones pedidas:**
  | Regla | Resultado | Cómo se verificó |
  |---|---|---|
  | Ninguna clase del dominio depende de infraestructura | ✅ | Tabla de imports + regla ArchUnit `dominioNoCreaInfraestructura` (reto 14) |
  | El dominio no importa librerías externas (Mockito, Jackson, HttpClient…) | ✅ | Solo `java.util`; regla ArchUnit `dominioSoloJavaPuro` |
  | No hay `new ClaseInfraestructura()` en el dominio | ✅ | `grep "new .*Adaptador\|Cliente\|DroneConMonitoreo"` fuera de `infraestructura/` y `Main`: ninguno |
  | Los servicios reciben dependencias por constructor (no por campo ni setter) | ✅ | Todos los campos de `aplicacion/` son `final` y se asignan en el constructor; regla ArchUnit `serviciosConInyeccionPorConstructor` |

  **Principios SOLID en la arquitectura Enterprise:**
  | Principio | Dónde | Por qué importa en AquaPort |
  |---|---|---|
  | **S** | Cada `Validador*` revisa una sola regla; `PlanificadorRuta` solo planifica; `EjecutorRuta` solo ejecuta y coordina traspasos; `AdaptadorAPIHidrica` solo traduce. | Cambiar el umbral de turbidez no toca la planificación de rutas. |
  | **O** | Para agregar la regla "drone operativo" en el reto 12 se creó `ValidadorEstadoOperativo` y se enlazó en la cadena; ningún validador existente cambió. | Las 60 unidades y las 4 zonas van a pedir reglas nuevas; se agregan sin reabrir las probadas. |
  | **L** | `DroneConMonitoreo` es intercambiable con `DroneAcuatico` en cualquier lugar donde se espera un `Drone` (prueba `noAlteraComportamiento`). | La telemetría se puede activar o desactivar sin que la lógica de rutas lo note. |
  | **I** | Puertos pequeños: `ServicioCondicionesHidricas` (1 método), `RegistroTelemetria` (1), `NotificadorWaypoints` (2). | Cada adaptador implementa solo lo que necesita. |
  | **D** | El dominio define los puertos y la infraestructura los implementa; los servicios reciben todo por constructor y los concretos solo se crean en `Main` (raíz de composición). | Las pruebas inyectan mocks y la API hídrica real se puede cambiar sin tocar el dominio. |

  **Mapa de dependencias entre capas:**
  ```
  Main (raíz de composición) ──► infraestructura ──► aplicacion ──► dominio
                                       │                              ▲
                                       └──────────────────────────────┘
  dominio: no depende de ninguna capa ni librería (solo java.*)
  ```
<img width="699" height="430" alt="imagen" src="https://github.com/user-attachments/assets/0bdb5a8b-7f45-4a86-9fd2-06399cbcf238" />

Reto 5 — C4 nivel 2: contenedores
- [x] Diagrama de contenedores en docs/c4-contenedores-empoleon.png
- Evidencia:
<img width="1024" height="639" alt="imagen" src="https://github.com/user-attachments/assets/652e83d1-82ad-4c73-8180-362952a30a8f" />
<img width="652" height="471" alt="imagen" src="https://github.com/user-attachments/assets/6644022a-fa57-4a90-8476-ea5695e5f1c0" />


  **Qué cambió respecto al nivel 1 (Prinplup):** el nivel 1 mostraba AquaPort como una caja negra. El nivel 2 la abre en 4 contenedores, y cada línea indica el **protocolo** y el **dato** que fluye. El adaptador y la telemetría coinciden con las clases de infraestructura del reto 03, y la aplicación principal con las capas de dominio y aplicación del reto 12.

Reto 6 — RF y RNF Enterprise
- [x] 5 RF + 5 RNF con métricas de producción + MoSCoW + tensiones
- Evidencia:

  **Requisitos Funcionales:**
  - **AP-15 Rutas multi-etapa:** cuando el Solicitante registra una solicitud con origen, waypoints y destino, el sistema planifica la ruta asignando un drone distinto a cada tramo y retorna el código de misión con los tramos asignados.
  - **AP-16 Reasignación ante fallo en waypoint:** si el drone de un tramo está en FALLO o con batería menor al 35% cuando le toca salir, el sistema asigna automáticamente un reemplazo que no esté en la ruta y notifica la reasignación al Operador.
  - **AP-17 Cadena de custodia:** el sistema registra cada traspaso de la muestra entre drones en los waypoints (zona, drone que entrega, drone que recibe); si un drone falla con la muestra durante un tramo, la ruta queda en `CUSTODIA_INTERRUMPIDA`.
  - **AP-18 Coordinación entre zonas:** el sistema solo planifica tramos hacia zonas activas y con condiciones hídricas aceptables (turbidez ≤ 50 NTU, nivel ≥ 0.5 m), y prefiere el drone que ya está en la zona de origen de cada tramo.
  - **AP-19 Reporte de eficiencia por zona:** el Administrador ECI consulta, por zona destino, la proporción de misiones entregadas sobre el total.

  **Requisitos No Funcionales (métricas de producción):**
  - **RNF-08 (Disponibilidad / SLA):** el servicio de planificación debe estar disponible el **99.5%** del tiempo mensual (máximo ~3.6 h de caída al mes), medido con un monitor de salud cada minuto.
  - **RNF-09 (Tiempo de reasignación):** desde que se detecta un drone en FALLO en un waypoint hasta que el reemplazo queda asignado deben pasar menos de **2 s** con 60 drones, medido en una prueba de integración con `assertTimeout`.
  - **RNF-10 (Throughput):** el sistema debe soportar **20 misiones multi-etapa simultáneas** (hasta 4 tramos cada una) sin que la planificación de cada una pase de 500 ms (p95), medido con una prueba de carga.
  - **RNF-11 (Trazabilidad):** el **100%** de los traspasos de custodia queda registrado con zona, drones y hora, y se conserva durante 5 años (requisito de laboratorio para muestras ambientales).
  - **RNF-12 (Calidad):** JaCoCo ≥ 85% líneas y ≥ 75% ramas; SonarQube con 0 bugs, 0 vulnerabilidades, deuda < 15 min y duplicación < 3% (Quality Gate del reto 13).

  **Clasificación MoSCoW:**
  | Requisito | Categoría | Justificación |
  |---|---|---|
  | AP-15 Rutas multi-etapa | Must Have | Es la funcionalidad que define el nivel Enterprise. |
  | AP-16 Reasignación ante fallo | Must Have | Con 60 drones en 4 zonas los fallos son esperables; sin reasignación la muestra se pierde. |
  | AP-17 Cadena de custodia | Must Have | Una muestra ambiental sin custodia trazable no sirve para el laboratorio. |
  | AP-18 Coordinación entre zonas | Should Have | Mejora la calidad de la asignación; sin ella el sistema aún funciona con las reglas de batería y capacidad. |
  | AP-19 Reporte de eficiencia | Could Have | Útil para la gestión, pero no afecta la operación diaria. |
  | RNF-08 SLA 99.5% | Must Have | Las rutas multi-etapa dependen de que el servicio esté arriba durante toda la ruta. |
  | RNF-09 Reasignación < 2 s | Must Have | Un drone esperando en un waypoint consume batería. |
  | RNF-10 Throughput | Should Have | 20 simultáneas es el pico estimado; el día normal es menor. |
  | RNF-11 Trazabilidad | Must Have | Es requisito regulatorio del laboratorio. |
  | RNF-12 Calidad | Must Have | Es el Quality Gate del DoD Enterprise. |

  **Tensiones entre requisitos:**
  | Tensión | Conflicto | Resolución |
  |---|---|---|
  | AP-16 vs AP-17 | Reasignar rápido (AP-16) puede traer un drone de otra zona que no está en el waypoint para recibir la muestra (AP-17). | El reemplazo primero se desplaza al waypoint y luego se registra el traspaso (`EjecutorRuta.recorrer`); la custodia nunca se salta. |
  | AP-18 vs AP-15 | Preferir el drone de la zona de origen (AP-18) puede dejar sin drone a un tramo posterior que lo necesitaba (AP-15). | La preferencia es local por tramo y un drone no se reutiliza; si un tramo no tiene drone, la ruta completa no se planifica (`RutaNoPlanificableException`) en vez de quedar a medias. |
  | RNF-09 vs AP-18 | Validar condiciones hídricas con la API externa en cada reasignación puede romper los 2 s. | El adaptador es el único punto de acceso a la API; si se necesita, se agrega caché por zona en infraestructura sin tocar el dominio. |
  | RNF-11 vs RNF-10 | Registrar cada traspaso con 5 años de retención agrega escritura en cada waypoint. | La custodia se escribe en la base de misiones de forma asíncrona; el registro en memoria (`CadenaCustodia`) garantiza que no se pierda durante la ruta. |
<img width="621" height="501" alt="imagen" src="https://github.com/user-attachments/assets/93ec8a0c-6454-4361-8b2d-3d5eeb7602f6" />

Reto 7 — Plantilla DOSW (RF AP-15)
- [x] Misión multi-etapa con waypoints, 8+ pasos
- Evidencia:

Proyecto: AquaPort Enterprise | DOSW 2026 | Página 1
AQUAPORT ENTERPRISE
Desarrollo y Operaciones de Software
ANÁLISIS DE REQUERIMIENTOS

# FUNCIONALIDAD

Código: AP-15
Nombre: Ejecutar misión multi-etapa con waypoints
Descripción: El sistema planifica y ejecuta una misión que recorre varias zonas hídricas, asignando un drone distinto a cada tramo, coordinando los traspasos de la muestra en los waypoints y reasignando drones ante fallos en mitad de la ruta.
Cómo se ejecutará: Automáticamente cuando el Solicitante registra una solicitud multi-etapa; el Operador Hídrico supervisa.
Actor principal: Sistema AquaPort (disparado por el Solicitante); Operador Hídrico supervisa; Técnico de Mantenimiento atiende fallos.
Precondiciones: Existen al menos tantos drones operativos como tramos tiene la ruta. Las zonas de la ruta están registradas en la red. La API de Condiciones Hídricas está disponible.

# DATOS DE ENTRADA

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|---|---|---|---|---|
| solicitud | Pedido multi-etapa | — | — | Sí |
| solicitud.id | Identificador de la solicitud | String | Formato S-XXX | Sí |
| solicitud.cargaGramos | Peso de la muestra | Integer | Entre 1 y 1500 g | Sí |
| solicitud.paradas | Origen, waypoints y destino, en orden | List<Enum(EMBALSE_INVESTIGACION, RED_CANALES, LAGUNA_RESERVA, LAB_HIDRICO_CENTRAL)> | Mínimo 2 elementos (origen y destino) | Sí |

# DATOS DE SALIDA

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|---|---|---|---|---|
| ruta | Tramos planificados | List<Tramo(origen:Enum, destino:Enum, drone:Drone(id:String, tipo:Enum, bateria:Integer, zona:Enum))> | Un drone distinto por tramo | Sí |
| resultado.estado | Cómo terminó la ruta | Enum(COMPLETADA, CUSTODIA_INTERRUMPIDA, FALLIDA) | — | Sí |
| resultado.custodia | Registro de traspasos | List<String> | "Traspaso en ZONA: AR-XX -> AR-YY" o "INTERRUMPIDA: motivo" | Sí |

# FLUJO BÁSICO

| Paso | Actor | Descripción | Excepciones |
|---|---|---|---|
| 1 | Solicitante | Registra la solicitud con carga, origen, waypoints y destino. | — |
| 2 | Sistema | Divide la ruta en tramos consecutivos (origen → waypoint 1 → … → destino). | — |
| 3 | Sistema | Para cada tramo, consulta las condiciones hídricas de la zona destino del tramo vía el adaptador de la API. | FA-02 |
| 4 | Sistema | Valida a cada candidato con la cadena: estado operativo → batería ≥ 35% → capacidad del tipo → zona activa → condiciones hídricas. | FA-03 |
| 5 | Sistema | Asigna a cada tramo el drone apto que ya está en la zona de origen (o el mejor de fuera); un drone no se repite en la ruta. | FA-03 |
| 6 | Sistema | Antes de cada tramo, revalida al drone asignado (fallo o batería crítica). | FA-01 |
| 7 | Sistema | En cada waypoint registra el traspaso de custodia entre el drone que entrega y el que recibe. | FA-04 |
| 8 | Sistema | El drone recorre el tramo; el módulo de telemetría registra inicio y fin. | FA-04 |
| 9 | Sistema | Notifica al Operador que se alcanzó el waypoint. | — |
| 10 | Sistema | Al llegar al destino final cierra la ruta como COMPLETADA y retorna el registro de custodia. | — |

# FLUJO ALTERNO

| Paso | Actor | Descripción | Excepciones |
|---|---|---|---|
| FA-01 | Sistema | **Fallo de drone en waypoint intermedio** (o batería < 35% al salir): se busca un reemplazo que no esté en la ruta, primero en la zona del waypoint; si viene de otra zona, se desplaza al waypoint. Se notifica la reasignación. Si no hay reemplazo, la ruta termina FALLIDA. | Continúa en paso 7 |
| FA-02 | Sistema | **Condiciones hídricas adversas en un tramo específico** (turbidez > 50 NTU o nivel < 0.5 m): ningún drone pasa la cadena para ese tramo y la ruta no se planifica; se informa el tramo afectado para que el Operador defina un desvío por otra zona. | Fin del caso |
| FA-03 | Sistema | **Zona de destino temporalmente inactiva**: la cadena rechaza el tramo hacia esa zona y la ruta no se planifica. | Fin del caso |
| FA-04 | Sistema | **Fallo del drone durante el tramo con la muestra**: la custodia se marca INTERRUMPIDA, no se notifica el waypoint no alcanzado y se alerta al Técnico de Mantenimiento. | Fin del caso |

Proyecto: AquaPort Enterprise | DOSW 2026 | Página 2

**Notas y comentarios:**
Implementado en `PlanificadorRuta`, `EjecutorRuta`, `CadenaCustodia` y la cadena `ValidadorMision` (`Empoleon/src/main/java/com/eci/aquaport/ejercicio12/`), con una prueba por cada flujo alterno (reto 12).

# REGLAS DE NEGOCIO

| No. | Descripción |
|---|---|
| RN-01 | Un drone con batería menor a 35% no puede iniciar un tramo; si baja de ese umbral en mitad de la ruta, su tramo se reasigna. |
| RN-02 | Cadena de custodia: toda muestra tiene en cada momento un único drone responsable; cada cambio de responsable ocurre en un waypoint y queda registrado. |
| RN-03 | El drone que recibe la muestra debe estar físicamente en el waypoint; si viene de otra zona, primero se desplaza. |
| RN-04 | Umbrales de reasignación automática: estado FALLO o batería < 35% antes de iniciar el tramo. |
| RN-05 | Un drone no puede tener dos tramos de la misma ruta. |
| RN-06 | No se planifican tramos hacia zonas inactivas ni con turbidez > 50 NTU o nivel de agua < 0.5 m. |

# HISTORIAL DE REVISIÓN

| Elaborado por | Aprobado por | Fecha | Descripción y Justificación de Cambios |
|---|---|---|---|
| Equipo Piplup | | 10/10/2026 | Versión inicial del documento para el nivel Enterprise. |
<img width="529" height="424" alt="imagen" src="https://github.com/user-attachments/assets/36ced7db-a060-4d3d-a738-3dbd241a9612" />

Reto 8 — Design system y mapa de flujos
- [x] Tokens, componentes, 6 pantallas, WCAG AA
- Evidencia:

  **Tokens de diseño** (evolución del manual de Piplup y del sistema de Prinplup):
  | Categoría | Token | Valor |
  |---|---|---|
  | Color | `color.fondo` | `#0B1622` |
  | Color | `color.superficie` | `#12202E` |
  | Color | `color.primario` | `#0B3C5D` (solo superficies y botones con texto claro; nunca como texto) |
  | Color | `color.acento` | `#1D9A9F` |
  | Color | `color.texto` | `#E6EDF3` |
  | Estado | `estado.disponible` | `#4CD9B0` |
  | Estado | `estado.en-mision` | `#2F80ED` |
  | Estado | `estado.recargando` | `#F0A830` |
  | Estado | `estado.mantenimiento` | `#8A94A6` |
  | Estado | `estado.fallo` | `#E25C5C` |
  | Estado | `estado.sumergido` | `#7B8CDE` (antes `#1B2A6B`, ajustado por WCAG) |
  | Tipografía | `fuente.interfaz` / `fuente.datos` | Inter / JetBrains Mono (IDs `AR-XX`, códigos `M-XXX`) |
  | Tipografía | `texto.xs / sm / md / lg / xl` | 12 / 14 / 16 / 20 / 28 px |
  | Espaciado | `espacio.1 … 6` | 4 / 8 / 12 / 16 / 24 / 32 px (base 4) |
  | Radio | `radio.sm / md / lg` | 4 / 8 / 12 px |

  **Verificación de contraste WCAG AA** sobre `color.fondo` `#0B1622`, calculada con la fórmula de luminancia relativa de WCAG 2.1. El mínimo AA para texto normal es 4.5:1:
  | Color | Contraste | Resultado |
  |---|---|---|
  | Texto `#E6EDF3` | 15.43:1 | ✅ AA |
  | Disponible `#4CD9B0` | 10.29:1 | ✅ AA |
  | Recargando `#F0A830` | 8.98:1 | ✅ AA |
  | Mantenimiento `#8A94A6` | 5.96:1 | ✅ AA |
  | Acento `#1D9A9F` | 5.36:1 | ✅ AA |
  | Fallo `#E25C5C` | 5.14:1 | ✅ AA |
  | En misión `#2F80ED` | 4.71:1 | ✅ AA |
  | Sumergido original `#1B2A6B` | **1.38:1** | ❌ No cumple; se reemplazó |
  | Sumergido ajustado `#7B8CDE` | 5.77:1 | ✅ AA |
  | Primario `#0B3C5D` como texto | 1.58:1 | ❌ Por eso solo se usa como superficie |
<img width="1258" height="864" alt="Screenshot 2026-10-10 193307" src="https://github.com/user-attachments/assets/24ddec61-b218-4365-90fe-ca415a68b2af" />
<img width="1261" height="878" alt="Screenshot 2026-10-10 193642" src="https://github.com/user-attachments/assets/5c146f31-090f-40df-b806-d62e1cc0631a" />
<img width="1253" height="733" alt="Screenshot 2026-10-10 193946" src="https://github.com/user-attachments/assets/834c28b9-1fe0-4665-a20e-b021cdbdefe5" />
<img width="1261" height="746" alt="Screenshot 2026-10-10 194121" src="https://github.com/user-attachments/assets/e07a7cda-cd46-48e9-8e58-ce32864567d3" />
<img width="1265" height="780" alt="Screenshot 2026-10-10 194208" src="https://github.com/user-attachments/assets/d81049f5-2be2-4693-9dd4-c48fc7d846f5" />
<img width="1259" height="525" alt="Screenshot 2026-10-10 194231" src="https://github.com/user-attachments/assets/50277a45-5847-410b-8213-a71b85c60f65" />

  Además, el estado nunca se comunica solo con color: cada tarjeta lleva la etiqueta escrita (DISPONIBLE, FALLO…) y un ícono, para personas con daltonismo.

  **Componentes con estados:** tarjeta de drone (6 estados), botón de acción (default, hover, procesando, éxito, deshabilitado), indicador de batería (≥ 60% verde, 35-59% ámbar, < 35% rojo con ícono), chip de zona (activa / inactiva / condiciones adversas), barra de progreso de waypoints (pendiente, en curso, alcanzado, reasignado, interrumpido) y banner de alerta.

  **Mapa de flujos del Enterprise (6 pantallas) y leyes UX:**
  | # | Pantalla | Ley UX aplicada |
  |---|---|---|
  | 1 | Mapa de las 4 zonas | **Proximidad (Gestalt):** los drones de cada zona se agrupan visualmente dentro de su zona. |
  | 2 | Selección de ruta (origen → waypoints → destino) | **Hick:** solo se ofrecen las 4 zonas y se marcan las inactivas, en vez de una lista libre. |
  | 3 | Asignación por tramos | **Miller:** cada tramo muestra solo origen, destino, drone y motivo de la elección (≤ 7 datos). |
  | 4 | Monitoreo en tiempo real | **Doherty:** el progreso de cada waypoint se actualiza en < 400 ms para que el operador no pierda el hilo. |
  | 5 | Alerta de fallo y reasignación | **Fitts:** el botón "Confirmar reasignación" es grande y está junto a la alerta; **Von Restorff:** la alerta en rojo es lo único que resalta. |
  | 6 | Reporte de entrega y custodia | **Jakob:** se presenta como una línea de tiempo vertical, un formato que el usuario ya conoce de los rastreos de paquetes. |

  Los prompts para generar estas pantallas están en el reto 11.
<img width="581" height="461" alt="imagen" src="https://github.com/user-attachments/assets/db1af1fd-d384-4a43-9dc4-4dc972dea984" />

Reto 9 — Roadmap en Jira
- [x] 3 sprints, DoD Enterprise y retrospectiva
- Evidencia:

  **Link de Jira:** https://mail-team-nyjtgqcj.atlassian.net/jira/software/projects/AQ/boards/71/backlog

  **Épica AQ-27:** AquaPort Enterprise — Red multi-embalse con rutas multi-etapa (el roadmap y el DoD están en su descripción).

  **Capacidad del equipo:** 28 SP por sprint, que es la velocity real del sprint v2 (28 SP comprometidos y 28 entregados).

  | Sprint | Fechas | Goal | Issues (SP) | Total |
  |---|---|---|---|---|
  | **AQ Ent S1 Arquitectura** | 26/10 – 06/11 | Arquitectura por capas con cadena de validación, decorator de telemetría y adapter de la API hídrica, auditada con ArchUnit | AQ-28 Validar misión con cadena de reglas (8), AQ-29 Telemetría por tramo (5), AQ-30 Condiciones reales del agua (5), AQ-31 Auditoría ArchUnit (3) | 21 |
  | **AQ Ent S2 Rutas** | 09/11 – 20/11 | Rutas multi-etapa con un drone por tramo, reasignación automática ante fallo y cadena de custodia trazable | AQ-32 Misión multi-etapa (8), AQ-33 Reasignación en waypoint (8), AQ-34 Cadena de custodia (5), AQ-35 Eficiencia por zona (3) | 24 |
  | **AQ Ent S3 Calidad** | 23/11 – 04/12 | JaCoCo ≥ 85% / ≥ 75%, SonarQube con 0 bugs, deuda < 15 min y duplicación < 3% | AQ-36 Gate JaCoCo (3), AQ-37 SonarQube Enterprise (5), AQ-38 Estándares de tamaño (2) | 10 |

  Ningún sprint supera la capacidad de 28 SP. S3 queda con holgura a propósito: la retrospectiva mostró que la calidad se subestimó en el sprint v2.

  **DoD Enterprise:**
  - Compila sin warnings y todas las pruebas pasan (unitarias e integración).
  - JaCoCo ≥ 85% de líneas y ≥ 75% de ramas (quality gate del pom).
  - SonarQube: 0 bugs, 0 vulnerabilidades, deuda < 15 min y duplicación < 3%.
  - ArchUnit en verde: el dominio no depende de infraestructura ni de librerías externas.
  - Métodos de 15 líneas o menos y clases de 150 líneas o menos.
  - Commits con Conventional Commits y PR hacia `Develop` (rama protegida).
  - C4 nivel 2, plantilla DOSW y CHANGELOG actualizados.

  **Retrospectiva real del sprint v2 (Prinplup)**, registrada como comentario en la épica AQ-18:
  | | |
  |---|---|
  | **Qué funcionó** | TDD con commits separados (Red → Green) que demuestran que las pruebas fueron primero; una rama y un PR pequeño por patrón (#7 a #12); el quality gate de JaCoCo desde el inicio, que mostró 9 clases sin cubrir antes de cerrar el sprint. |
  | **Qué no funcionó** | Se editó el README directo en `main` y en `Develop`, las ramas divergieron y hubo que sincronizarlas con PRs (#18, #19); SonarQube se corrió al final (17 smells, 160 min de deuda, 16.3% de duplicación); la duplicación entre ejercicios no se planeó. |
  | **Qué cambiaremos** | Ramas protegidas (aplicado en el reto 02); hook de Conventional Commits y CHANGELOG automático (aplicado); SonarQube al cierre de cada sprint (tarea AQ-37 desde el roadmap); reglas de capas verificadas en cada build con ArchUnit (reto 14). |
<img width="495" height="439" alt="imagen" src="https://github.com/user-attachments/assets/56710400-7254-4b0d-8446-d71870945270" />

Reto 10 — Diagrama de CU con flujos de fallo
- [x] 5 extends con su condición
- Evidencia:
<img width="1621" height="446" alt="Screenshot 2026-10-10 194433" src="https://github.com/user-attachments/assets/a0d119f8-94fa-4490-80c0-7dd560bf75e3" />
  
  **Decisiones del diagrama:**
  - `<<include>>`: planificar siempre valida condiciones hídricas y siempre ejecuta tramos con traspaso de custodia.
  - Los 5 flujos alternos del Enterprise son `<<extend>>`, cada uno con su condición escrita en la flecha. Corresponden uno a uno a las pruebas del reto 12.
<img width="488" height="553" alt="imagen" src="https://github.com/user-attachments/assets/a2eba31f-f97f-4e53-b59b-8a666e2c12c2" />

Reto 11 — Mocks con IA
- [x] 5 pantallas y 5 prompts
- Evidencia:

  **Herramienta:** Gemini

  **Bloque de estilo:**
  ```
  Actúa como diseñador UX/UI senior de sistemas de monitoreo ambiental en producción.
  SISTEMA: AquaPort Enterprise — red de 60 drones acuáticos en 4 zonas hídricas de la Escuela Colombiana de Ingeniería:
  Embalse de Investigación, Red de Canales, Laguna de Reserva y Laboratorio Hídrico Central.
  ESTILO: dashboard técnico oscuro. Fondo #0B1622, superficies #12202E, primario #0B3C5D (solo superficies), acento #1D9A9F, texto #E6EDF3.
  Tipografía Inter para la interfaz y JetBrains Mono para IDs (AR-01…AR-60), solicitudes (S-XXX) y misiones (M-XXX).
  ESTADOS: Disponible #4CD9B0, En misión #2F80ED, Recargando #F0A830, Mantenimiento #8A94A6, Fallo #E25C5C, Sumergido #7B8CDE.
  Cada estado lleva etiqueta escrita e ícono además del color (accesibilidad WCAG AA). Espaciado base 4 px, radios 4/8/12 px.
  Textos en español. Sin ilustraciones decorativas.
  ```

  **Prompt 1 — Mapa de las 4 zonas en tiempo real:**
  ```
  PANTALLA: Mapa de la red hídrica. Muestra las 4 zonas como regiones conectadas por rutas:
  Embalse de Investigación (15 drones buceadores), Red de Canales (15 superficiales), Laguna de Reserva (15 semisumergidos)
  y Laboratorio Hídrico Central (destino común). Cada drone es un punto con el color de su estado; al pasar el cursor
  muestra ID, tipo y batería. Cada zona tiene un chip de condiciones: turbidez y nivel de agua (Laguna: 64 NTU, "condiciones adversas").
  Panel lateral con totales por estado y 2 rutas multi-etapa activas dibujadas sobre el mapa.
  ```
  <img width="1258" height="864" alt="Screenshot 2026-10-10 193307" src="https://github.com/user-attachments/assets/005c603b-420e-44d4-8ef1-e4074108a300" />


  **Prompt 2 — Configuración de ruta multi-etapa con waypoints:**
  ```
  PANTALLA: Nueva solicitud multi-etapa S-900. Formulario con: peso de la carga (450 g) y una lista ordenable de paradas:
  origen Embalse de Investigación → waypoint Red de Canales → destino Laboratorio Hídrico Central.
  Botón "+ Agregar waypoint". Las zonas inactivas aparecen deshabilitadas con el motivo. Debajo, la vista previa de la ruta
  como una línea con un nodo por parada. Botones "Planificar ruta" (primario) y "Cancelar".
  ```
 <img width="1261" height="878" alt="Screenshot 2026-10-10 193642" src="https://github.com/user-attachments/assets/fbf4a007-0dfd-4ed2-bf0d-ee563c450da8" />
 

  **Prompt 3 — Monitoreo del progreso por tramo:**
  ```
  PANTALLA: Monitoreo de la solicitud S-900. Barra de progreso horizontal con 3 nodos (Embalse → Canales → Laboratorio).
  Tramo 1 alcanzado por AR-01 (verde), tramo 2 en curso con AR-16 (azul, 60%). Para cada tramo: drone, batería, hora de salida
  y hora estimada de llegada. Panel inferior de telemetría con los últimos eventos: "Inicio tramo EMBALSE -> CANALES con 85%",
  "Fin tramo en RED_CANALES con 75%", "Traspaso en RED_CANALES: AR-01 -> AR-16".
  ```
<img width="1253" height="733" alt="Screenshot 2026-10-10 193946" src="https://github.com/user-attachments/assets/fa309664-2bc6-4c71-aa19-8484456d50e1" />


  **Prompt 4 — Alerta de fallo en waypoint con reasignación automática:**
  ```
  PANTALLA: Monitoreo de S-901 en estado de ALERTA. Banner rojo fijo: "AR-16 en FALLO en el waypoint Red de Canales —
  reasignado automáticamente a AR-17". La barra de progreso marca el nodo de Red de Canales con el ícono de reasignación.
  Tarjeta comparativa: drone saliente AR-16 (Fallo, rojo) → drone entrante AR-17 (Disponible, 80%, misma zona).
  Botones: "Ver detalle" y "Notificar al técnico" (grande, junto a la alerta — Ley de Fitts). AR-16 muestra "Técnico notificado".
  ```
<img width="1261" height="746" alt="Screenshot 2026-10-10 194121" src="https://github.com/user-attachments/assets/bea6c40a-bfd9-45d1-a9a3-cf6c5fee117d" />


  **Prompt 5 — Reporte final de la cadena de custodia:**
  ```
  PANTALLA: Reporte de entrega de la solicitud S-900, estado COMPLETADA.
  Línea de tiempo vertical de custodia: 08:00 AR-01 recibe la muestra en Embalse de Investigación → 08:24 traspaso en
  Red de Canales AR-01 → AR-16 → 08:51 AR-16 entrega en Laboratorio Hídrico Central. Cada evento con hora, zona y drones
  en JetBrains Mono. Resumen arriba: 2 tramos, 0 reasignaciones, carga 450 g, duración 51 min.
  Variante: si la custodia fue interrumpida, el evento final es rojo con "INTERRUMPIDA: AR-02 falló entre EMBALSE y CANALES".
  Botón "Exportar PDF".
  ```
<img width="1265" height="780" alt="Screenshot 2026-10-10 194208" src="https://github.com/user-attachments/assets/4fa53542-58e5-4797-91fb-97667e332c80" />


  **Prompt 6 — Asignación por tramos (pantalla 3 del mapa de flujos del reto 08):**
  ```
  PANTALLA: Ruta planificada de S-900. Tabla con una fila por tramo: Embalse → Canales asignado a AR-01 (Semisumergido, 85%,
  "ya está en la zona de origen"); Canales → Laboratorio asignado a AR-16 (Superficial, 75%, "ya está en la zona de origen").
  Por tramo, un chip con las condiciones de la zona destino (turbidez y nivel). Máximo 4 datos por fila (Ley de Miller).
  Botones "Iniciar ruta" (primario) y "Cambiar drone" (secundario) por tramo.
  ```
<img width="1259" height="525" alt="Screenshot 2026-10-10 194231" src="https://github.com/user-attachments/assets/93fc29cf-3e5a-41f3-a5c5-a11f6a6c40a1" />

<img width="514" height="363" alt="imagen" src="https://github.com/user-attachments/assets/1827d9ae-73d9-4756-82af-9ff64837e05f" />

Reto 12 — TDD de las 3 capas
- [x] Unitarias, integración, JaCoCo ≥ 85% y los 5 flujos alternos probados
- Evidencia:

  **Ubicación del código:** `Empoleon/src/main/java/com/eci/aquaport/ejercicio12/`
  **Pruebas:** `Empoleon/src/test/java/com/eci/aquaport/ejercicio12/`

  **Ciclo TDD (orden de commits):**
  1. `chore:` se copió el modelo por capas de `ejercicio3`.
  2. `test: ... (Red)`: se escribieron las pruebas de `PlanificadorRuta`, `EjecutorRuta` e integración. No compilaba: 154 símbolos sin definir.
  3. `feat: ... (Green)`: se implementó lo mínimo para que pasaran: `SolicitudMultiEtapa`, `Tramo`, `CadenaCustodia`, `ValidadorEstadoOperativo`, el puerto `NotificadorWaypoints`, `PlanificadorRuta` y `EjecutorRuta`.

  **Las 3 capas de pruebas:**
  | Capa de prueba | Clase | Qué cubre |
  |---|---|---|
  | Unitarias del planificador | `PlanificadorRutaTest` (5) | Un drone por tramo, preferencia por la zona de origen, no reutiliza drones, solicitud inválida. |
  | Unitarias del ejecutor | `EjecutorRutaTest` (6) | Recorrido tramo a tramo, notificación en orden (`InOrder`), traspasos de custodia, reasignación y reposicionamiento. |
  | Reglas por tramo | `ReglasTramoTest` (4) | Capacidad, nivel de agua, lecturas inválidas y API caída. |
  | Integración | `FlujoMultiEtapaIntegracionTest` (2) | Solicitud → planificación → asignación por tramos → notificación de waypoints, con el adaptador de la API hídrica real (simulado), la cadena real y el planificador y ejecutor reales. Solo el notificador es mock. |

  **Los 5 flujos alternos del Enterprise:**
  | Flujo alterno | Prueba | Comportamiento |
  |---|---|---|
  | Fallo de drone en waypoint intermedio | `falloEnWaypointReasigna` | El drone en FALLO no pasa la cadena (`ValidadorEstadoOperativo`); se asigna un reemplazo y se notifica `droneReasignado`. |
  | Zona destino inactiva | `zonaDestinoInactiva` | `RutaNoPlanificableException` que indica el tramo afectado. |
  | Batería crítica en mitad de ruta | `bateriaCriticaEnRutaReasigna` | Antes de cada tramo se revalida al drone; si bajó de 35%, su tramo se reasigna. |
  | Cadena de custodia interrumpida | `custodiaInterrumpida` | Si el drone falla durante el tramo con la muestra, la ruta termina en `CUSTODIA_INTERRUMPIDA` y no se notifica un waypoint que no se alcanzó. |
  | Condición hídrica adversa en un tramo | `condicionAdversaEnTramo` (+ `condicionesRealesDeLaApiBloqueanTramo`) | El tramo con turbidez por encima de 50 NTU no se puede planificar; en integración, la Laguna (64 NTU según la API) bloquea el tramo. |
<img width="560" height="545" alt="imagen" src="https://github.com/user-attachments/assets/32a8ba2a-f357-459b-8d83-9068a9f7b2e6" />

Reto 13 — JaCoCo + SonarQube Enterprise
- [ ] 85% líneas, 75% ramas, 0 bugs, deuda < 15 min, duplicación < 3%
- Evidencia:

  **Quality Gate de JaCoCo (`pom.xml`):**
  | Regla | Mínimo | Resultado |
  |---|---|---|
  | `BUNDLE` / `LINE` (todo el proyecto) | 85% | **99.7%** |
  | `BUNDLE` / `BRANCH` (todo el proyecto) | 75% | **100%** |
  | `CLASS` / `LINE` (cada clase) | 80% | ✅ todas |

  **Issues encontrados y cómo se resolvieron:** al subir el gate, 3 clases de `ejercicio12` quedaron por debajo del 80% (`ValidadorCapacidadCarga` 66%, `NivelAgua` 75%, `Turbidez` 75%). Tenían ramas de rechazo heredadas de `ejercicio3` que la ruta multi-etapa no recorría. Se corrigió con pruebas (nunca suprimiendo el issue) en `ReglasTramoTest`: capacidad excedida en un tramo, nivel de agua bajo el mínimo, lecturas negativas o `NaN`, y API caída. Commit: `build: quality gate Enterprise de JaCoCo...`.

  **Estándares de código:** todos los métodos tienen 15 líneas o menos y la clase más larga tiene 77 líneas (`EjecutorRuta`), dentro del límite de 150. Los `Main` se dividieron en métodos pequeños para cumplirlo (commit `refactor: dividir los Main...`).

  **SonarQube:** pendiente de correr el análisis del nivel Empoleon.

Reto 14 — Arquitectura por capas
- [x] Auditoría de dependencias entre capas
- Evidencia:

  **Herramienta:** ArchUnit (`ArquitecturaCapasTest`). Las reglas corren con `mvn test`, así que el build falla si alguien rompe la arquitectura.

  | Regla ArchUnit | Qué garantiza |
  |---|---|
  | `capasRespetanLaDireccionDeDependencias` | `layeredArchitecture()`: infraestructura → aplicación → dominio; nadie depende de infraestructura y el dominio solo es usado por las capas superiores. |
  | `dominioSoloJavaPuro` | El dominio solo depende de `..dominio..` y `java..`: ninguna librería externa. |
  | `dominioNoCreaInfraestructura` | Ninguna clase del dominio usa ni crea clases de infraestructura. |
  | `serviciosConInyeccionPorConstructor` | Todo campo de la capa de aplicación es `final`, es decir, inyectado por constructor. |
  | `adaptadoresImplementanPuertosDelDominio` | Cada `Adaptador*` de infraestructura depende de un puerto del dominio. |

  **Resultado:** 5/5 reglas en verde.

  **Prueba de que las reglas funcionan:** se agregó a propósito un `new ClienteApiHidricaSimulado()` dentro de `dominio/Mision` y ArchUnit lo detectó en 3 reglas:
  ```
  Tests run: 5, Failures: 3
  Rule 'classes that reside in a package '..dominio..' should only depend on classes that reside in any package
  ['..dominio..', 'java..'] ...' was violated (1 times)
  Rule 'no classes that reside in a package '..dominio..' should depend on classes that reside in a package
  '..infraestructura..' ...' was violated (1 times)
  ```
  El cambio se revirtió. La tabla de imports por capa está en el reto 04.
  <img width="498" height="377" alt="imagen" src="https://github.com/user-attachments/assets/8823b173-0271-4009-a538-b3cba52a3f1e" />

