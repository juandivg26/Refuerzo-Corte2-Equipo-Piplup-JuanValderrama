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
│   └── ejercicio3/   → Reto 03: patrones Enterprise por capas
│       ├── dominio/          (entidades, reglas, puertos — solo Java puro)
│       ├── aplicacion/       (casos de uso)
│       ├── infraestructura/  (adaptadores de API, decoradores técnicos)
│       └── Main.java         (raíz de composición)
└── test/java/com/eci/aquaport/
    ├── ejercicio1/   → AnalizadorRedHidricaTest
    └── ejercicio3/   → CadenaValidacionTest, DroneConMonitoreoTest, AdaptadorAPIHidricaTest (Mockito)
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
- [ ] Hook de Conventional Commits, protección de ramas, CHANGELOG automático y 10+ commits del Enterprise
- Evidencia:

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
- [ ] Capas, constructor injection, dominio sin dependencias externas
- Evidencia:

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
- [ ] Unitarias, integración, JaCoCo ≥ 85% y los 5 flujos alternos probados
- Evidencia:

Reto 13 — JaCoCo + SonarQube Enterprise
- [ ] 85% líneas, 75% ramas, 0 bugs, deuda < 15 min, duplicación < 3%
- Evidencia:

Reto 14 — Arquitectura por capas
- [ ] Auditoría de dependencias entre capas
- Evidencia:
