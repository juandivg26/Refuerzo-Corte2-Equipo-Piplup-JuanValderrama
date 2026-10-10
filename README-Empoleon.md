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

Reto 5 — C4 nivel 2: contenedores
- [ ] Diagrama de contenedores en docs/c4-contenedores-empoleon.png
- Evidencia:

Reto 6 — RF y RNF Enterprise
- [ ] 5 RF + 5 RNF con métricas de producción + MoSCoW + tensiones
- Evidencia:

Reto 7 — Plantilla DOSW (RF AP-15)
- [ ] Misión multi-etapa con waypoints, 8+ pasos
- Evidencia:

Reto 8 — Design system y mapa de flujos
- [ ] Tokens, componentes, 6 pantallas, WCAG AA
- Evidencia:

Reto 9 — Roadmap en Jira
- [ ] 3 sprints, DoD Enterprise y retrospectiva
- Evidencia:

Reto 10 — Diagrama de CU con flujos de fallo
- [ ] 5 extends con su condición
- Evidencia:

Reto 11 — Mocks con IA
- [ ] 5 pantallas y 5 prompts
- Evidencia:

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
