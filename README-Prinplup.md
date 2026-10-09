# AquaPort — Nivel Prinplup (v2)

15 drones de 3 tipos, asignación automática por zona y alertas al centro de control.

> **Nivel activo en el `pom.xml`:** `Prinplup` (`sourceDirectory` y `testSourceDirectory` apuntan a `Prinplup/src`).
> Para compilar y probar todo el nivel:
> ```
> mvn clean test
> ```

**Estructura del nivel** (una carpeta por ejercicio con código; cada ejercicio es autocontenido):
```
Prinplup/src/
├── main/java/com/eci/aquaport/
│   ├── ejercicio1/   → Reto 01: ConsultorFlota v2 (Streams)
│   ├── ejercicio3/   → Reto 03: FabricaDrones, Strategy, Observer, AsignadorAutomatico
│   └── ejercicio12/  → Reto 12: AsignadorAutomatico construido con TDD
└── test/java/com/eci/aquaport/
    ├── ejercicio1/   → ConsultorFlotaTest
    ├── ejercicio3/   → AsignadorAutomaticoTest (Mockito), FabricaDronesTest, EstrategiaSeleccionTest, ObservadoresTest
    └── ejercicio12/  → AsignadorAutomaticoTest (TDD + Mockito), ModeloDronesTest
```

Reto 1 — Streams & Lambdas: `ConsultorFlota` v2
- [x] groupingBy, partitioningBy, averagingInt, distinct sobre la flota de 15 drones
- Evidencia:

  **Ubicación del código:** `Prinplup/src/main/java/com/eci/aquaport/ejercicio1/`
  (`TipoDrone.java`, `Prioridad.java`, `DroneAcuatico.java`, `Mision.java`, `ConsultorFlota.java`, `Main.java`)
  **Pruebas:** `Prinplup/src/test/java/com/eci/aquaport/ejercicio1/ConsultorFlotaTest.java` (6 pruebas)

  **Paso a paso de la implementación:**
  1. Se amplió el record `DroneAcuatico` con el campo `tipo` (`TipoDrone`: `SUPERFICIAL`, `SEMISUMERGIDO`, `BUCEADOR`) y se creó el record `Mision(id, zonaDestino, prioridad)` con el enum `Prioridad` (`CRITICA`, `ALTA`, `NORMAL`, `BAJA`).
  2. `ConsultorFlota` implementa las 5 consultas, una cadena de stream por método:
     - `agruparDisponiblesPorTipo`: `filter` + `Collectors.groupingBy(DroneAcuatico::tipo)`.
     - `droneOptimoParaZona`: `filter` (disponible, batería ≥ 35%, tipo, zona) + `max` por batería → `Optional`.
     - `promedioBateriaPorTipo`: `groupingBy` + `averagingInt`.
     - `separarCriticas`: `partitioningBy(prioridad == CRITICA)`.
     - `zonasCubiertasPorFlotaActiva`: `filter` + `map` + `distinct` + `sorted`.
  3. `Main` arma la flota de 15 drones (5 por tipo) y 5 misiones, e imprime las 5 consultas.
     ```
     mvn clean compile
     mvn exec:java "-Dexec.mainClass=com.eci.aquaport.ejercicio1.Main"
     ```

  **Salida obtenida (resumida):**
  ```
  2) Drone optimo para Embalse Norte (BUCEADOR): Optional[DroneAcuatico[id=AR-11, tipo=BUCEADOR, bateria=79, ...]]
  3) Promedio de bateria por tipo: {BUCEADOR=58.0, SEMISUMERGIDO=67.0, SUPERFICIAL=57.6}
  4) Misiones criticas (true) vs demas (false): {false=[M-202, M-203, M-205], true=[M-201, M-204]}
  5) Zonas cubiertas por la flota activa: [Canal Central, Embalse Norte, Laboratorio Hídrico, Laguna Sur, Punto Ribereño Este]
  ```

Reto 2 — GitHub y GitFlow (release y tag)
- [x] Features con PR a Develop, release/v2.0, merge a main con tag v2.0.0
- Evidencia:

  **Paso a paso:**
  1. Se marcó la v1.0 (MVP Piplup) en `main` con el tag `v1.0.0`.
  2. Desde `Develop` se trabajó cada funcionalidad de la v2 en su propia rama `feature/`, con commits atómicos (`feat:`, `test:`, `build:`, `docs:`).
  3. Cada feature se integró a `Develop` con su Pull Request:

  | PR | Rama | Contenido |
  |---|---|---|
  | [#7](https://github.com/juandivg26/Refuerzo-Corte2-Equipo-Piplup-JuanValderrama/pull/7) | `feature/streams-flota-v2` | Reto 01 + activar Prinplup en el pom |
  | [#8](https://github.com/juandivg26/Refuerzo-Corte2-Equipo-Piplup-JuanValderrama/pull/8) | `feature/factory-drones` | Reto 03: Factory Method |
  | [#9](https://github.com/juandivg26/Refuerzo-Corte2-Equipo-Piplup-JuanValderrama/pull/9) | `feature/asignacion-automatica` | Reto 03: Strategy |
  | [#10](https://github.com/juandivg26/Refuerzo-Corte2-Equipo-Piplup-JuanValderrama/pull/10) | `feature/alertas-centro-control` | Reto 03: Observer + Mockito |
  | [#11](https://github.com/juandivg26/Refuerzo-Corte2-Equipo-Piplup-JuanValderrama/pull/11) | `feature/tdd-asignador` | Reto 12: TDD |
  | [#12](https://github.com/juandivg26/Refuerzo-Corte2-Equipo-Piplup-JuanValderrama/pull/12) | `feature/quality-gate-jacoco` | Reto 13: quality gate |

  4. Desde `Develop` se creó `release/v2.0` con un commit de ajuste (versión `2.0.0` en el `pom.xml` y esta documentación). En una rama release solo entran ajustes y bugfixes, nada de funcionalidades nuevas.
  5. `release/v2.0` se integró a `main` con Pull Request, se creó el tag `v2.0.0` sobre `main` y `main` se volvió a integrar a `Develop` para que ambas ramas queden alineadas.

  **Ramas de la v2:**
  ```
  main ─────●─────────────────────────────────────────────●── v2.0.0
            v1.0.0                                        ↑
  Develop ──●──●──────●──────●──────●──────●──────●───────┤
               ↑      ↑      ↑      ↑      ↑      ↑       │
               │      │      │      │      │      │   release/v2.0
               │      │      │      │      │      └─ feature/quality-gate-jacoco
               │      │      │      │      └─ feature/tdd-asignador
               │      │      │      └─ feature/alertas-centro-control
               │      │      └─ feature/asignacion-automatica
               │      └─ feature/factory-drones
               └─ feature/streams-flota-v2
  ```

Reto 3 — Patrones de Diseño: Strategy + Observer + Factory Method
- [x] FabricaDrones, AsignadorAutomatico con Strategy y alertas por Observer; prueba con Mockito
- Evidencia:

  **Ubicación del código:** `Prinplup/src/main/java/com/eci/aquaport/ejercicio3/`
  **Pruebas:** `Prinplup/src/test/java/com/eci/aquaport/ejercicio3/` (`AsignadorAutomaticoTest`, `FabricaDronesTest`, `EstrategiaSeleccionTest`, `ObservadoresTest`)

  | Patrón | Clases | Rol en AquaPort |
  |---|---|---|
  | Factory Method | `FabricaDrones`, `DroneAcuatico` (abstracta), `DroneSuperficial` (500 g), `DroneSemisumergido` (1500 g), `DroneBuceador` (300 g) | Decide qué subclase crear según el `TipoDrone`. El asignador nunca hace `new` de un drone concreto. |
  | Strategy | `EstrategiaSeleccion`, `MayorBateriaStrategy`, `ZonaCercanaStrategy` | `AsignadorAutomatico` delega la elección del drone a la estrategia activa (recibida por constructor). No hay ningún `instanceof`. |
  | Observer | `ObservadorMision`, `CentroControlObserver`, `TecnicoMantenimientoObserver` | Cuando la estrategia elige un drone en `FALLO`, el asignador avisa a todos los observadores, lo descarta y busca otro. Si una misión `CRITICA` queda sin drone, alerta a los observadores. |

  **Paso a paso de la implementación:**
  1. Se agregó `EstadoDrone` (`DISPONIBLE`, `EN_MISION`, `RECARGANDO`, `MANTENIMIENTO`, `SUMERGIDO`, `FALLO`). `FALLO` no está en el enum del enunciado, pero el flujo de alertas lo necesita.
  2. `ValidadorMision.esApto` aplica las reglas de negocio: batería ≥ 35%, la carga no supera la capacidad del tipo (el buceador no lleva más de 300 g) y el drone no está ocupado.
  3. `AsignadorAutomatico.asignar(mision, flota)`: filtra los candidatos aptos → la estrategia elige → si el elegido está en `FALLO`, notifica a los observadores y vuelve a elegir sin él → marca el drone `EN_MISION`.
  4. Prueba con Mockito (`AsignadorAutomaticoTest`): con 2 observadores mock, una misión cuyo primer drone está en `FALLO` verifica `times(1)` en cada observador y que se asigna el drone alternativo.
     ```
     mvn exec:java "-Dexec.mainClass=com.eci.aquaport.ejercicio3.Main"
     ```

  **Salida obtenida (evidencia de ejecución):**
  ```
  === ASIGNACION AUTOMATICA (ZonaCercanaStrategy) ===
  [CENTRO DE CONTROL] Drone AR-07 en FALLO durante la mision M-301
  [TECNICO] Revisar drone AR-07 (SEMISUMERGIDO) en Laguna Sur
  M-301 -> AR-09 (SEMISUMERGIDO, 83%, Laboratorio Hídrico, EN_MISION)
  M-302 -> AR-15 (BUCEADOR, 95%, Embalse Norte, EN_MISION)
  [CENTRO DE CONTROL] ALERTA: mision CRITICA M-303 sin drone disponible
  M-303 -> sin drone
  ```

Reto 4 — Principios SOLID
- [x] Auditoría de las clases de la v2
- Evidencia:

  | Principio | Dónde se respeta | Por qué importa en AquaPort |
  |---|---|---|
  | **S** — Responsabilidad única | `ValidadorMision` solo decide si un drone es apto; `AsignadorAutomatico` solo coordina la asignación; `FabricaDrones` solo crea drones; cada observador solo reacciona a su alerta. | Cambiar la regla de batería no toca el asignador ni las alertas. |
  | **O** — Abierto/cerrado | Nueva estrategia = nueva clase que implementa `EstrategiaSeleccion`. Nuevo tipo de drone (ej. `DroneSumergibleProfundo`) = nueva subclase + un `case` en `FabricaDrones`; `AsignadorAutomatico` y `ValidadorMision` no cambian porque usan `getCapacidadMaximaGramos()` y `puedeCargar()`. | La flota crece por tipos sin reabrir la lógica de asignación ya probada. |
  | **L** — Sustitución de Liskov | Las 3 subclases cumplen el contrato de `DroneAcuatico`: ninguna lanza excepciones ni retorna `null`; solo cambian tipo y capacidad. `ModeloDronesTest` recorre las 3 como `DroneAcuatico`. | El asignador trata a cualquier drone igual, sin preguntar qué tipo es. |
  | **I** — Segregación de interfaces | `ObservadorMision` tiene un solo método obligatorio (`notificarFalloDrone`); `notificarFalloAsignacion` es `default`, así el técnico no implementa una alerta que no le corresponde. `EstrategiaSeleccion` tiene un solo método. | Cada observador implementa solo lo que atiende. |
  | **D** — Inversión de dependencias | `AsignadorAutomatico` recibe `EstrategiaSeleccion` y `ValidadorMision` por constructor y conoce a los observadores solo por la interfaz. Los concretos se crean en `Main`. | Permite inyectar mocks en las pruebas (reto 12) y cambiar la estrategia sin tocar el asignador. |

Reto 5 — Diagrama de Contexto C4 v2
- [ ] Nuevo actor (Técnico de Mantenimiento) y 3 sistemas externos con flujos etiquetados
- Evidencia:

Reto 6 — RF y RNF
- [ ] 4 RF + 4 RNF + MoSCoW + tensión AP-07 vs AP-08
- Evidencia:

Reto 7 — Plantilla DOSW (RF AP-07)
- [ ] Plantilla completa con sub-objetos
- Evidencia:

Reto 8 — Identidad y UX
- [ ] Tarjeta de drone con 6 estados, flujo de 3 pantallas, Fitts y Hick
- Evidencia:

Reto 9 — Agilismo y Jira
- [ ] Sprint con Story Points y DoD
- Evidencia:

Reto 10 — Diagrama de Casos de Uso v2
- [ ] Nuevo actor, include y extend
- Evidencia:

Reto 11 — Mocks con IA
- [ ] 3 pantallas + alerta, 4 prompts
- Evidencia:

Reto 12 — TDD con Mockito: `AsignadorAutomatico`
- [x] Pruebas antes que código, mocks de dependencias, casos edge
- Evidencia:

  **Ubicación del código:** `Prinplup/src/main/java/com/eci/aquaport/ejercicio12/`
  **Ubicación de las pruebas:** `Prinplup/src/test/java/com/eci/aquaport/ejercicio12/AsignadorAutomaticoTest.java`

  **Paso a paso del ciclo TDD:**
  1. **Preparación:** se copió el modelo de la v2 (drones, enums, `Mision`, `EstrategiaSeleccion`, `ObservadorMision`, `ValidadorMision`) a `ejercicio12` en un commit `chore:` aparte.
  2. **Red:** se escribió `AsignadorAutomaticoTest` con `@ExtendWith(MockitoExtension.class)`, `@Mock EstrategiaSeleccion` y `@Mock ObservadorMision`. `AsignadorAutomatico` no existía, así que el proyecto no compilaba (`cannot find symbol`). Commit `test: ... (Red)`.
  3. **Green:** se implementó `AsignadorAutomatico` hasta que las 8 pruebas pasaron. Commit `feat: ... (Green)`, posterior al de las pruebas.

  | Prueba obligatoria | Test | Qué verifica |
  |---|---|---|
  | Asignación exitosa (happy path) | `asignacionExitosa` | El drone elegido queda `EN_MISION`. |
  | Misión CRÍTICA sin drones | `misionCritica_sinDrones_notificaObservadores` | `verify(observador, times(1)).notificarFalloAsignacion(mision)`. |
  | — (complemento) | `misionNormal_sinDrones_noNotifica` | Una misión NORMAL sin drone no dispara la alerta crítica (`never()`). |
  | Drone en FALLO durante la asignación | `droneEnFallo_notificaYBuscaAlternativo` | Notifica una vez y asigna el alternativo. |
  | Lista de drones vacía | `flotaVacia_retornaVacio` | Retorna `Optional.empty()`. |
  | Batería exactamente en el umbral | `bateriaEnUmbral_esCandidata` | 35% sí llega a la estrategia. |
  | — (complemento) | `bateriaBajoUmbral_noEsCandidata` | 34% no llega a la estrategia. |
  | Regla del buceador | `buceador_cargaMayorA300_noEsCandidato` | Con 301 g el buceador queda fuera. |

  **Cobertura de `AsignadorAutomatico` (ejercicio12):** 100% de líneas y 100% de ramas.

Reto 13 — JaCoCo con Quality Gate
- [x] El build falla si la cobertura baja del 80%
- Evidencia:

  **Configuración (`pom.xml`):** se agregó al `jacoco-maven-plugin` una ejecución `check` en la fase `test` con dos reglas:
  - `CLASS` / `LINE` / `COVEREDRATIO` ≥ **0.80**: cada clase debe tener al menos 80% de líneas cubiertas.
  - `BUNDLE` / `BRANCH` / `COVEREDRATIO` ≥ **0.70**: estándar de ramas de la v2.
  - Se excluyen los `Main` (`**/Main.class`) porque solo imprimen la demo de cada reto; no son dominio.

  **Paso a paso:**
  1. Con el gate activo, `mvn clean test` terminó en **BUILD FAILURE** porque 9 clases estaban por debajo del 80%: los observadores (0%), `ObservadorMision` (método `default` sin ejecutar), los drones de `ejercicio12` (`getTipo()` sin probar, 75%), y había ramas amarillas en `ValidadorMision` y `AsignadorAutomatico` de `ejercicio3`.
  2. Para cada rama amarilla se escribió la prueba faltante:

  | Rama amarilla | Prueba agregada | Por qué es importante probarla |
  |---|---|---|
  | `ValidadorMision.esApto`: batería < 35% | `validadorDescartaBateriaBaja` | Es la regla de negocio principal de la v2; si falla, se asignan drones que no terminan la misión. |
  | `ValidadorMision.esApto`: carga > capacidad | `validadorDescartaCargaExcesiva` | Protege la regla "el DroneBuceador no se asigna para cargas > 300 g". |
  | `ValidadorMision.esApto`: drone ocupado | `droneOcupado_noEsApto` | Evita asignar dos misiones al mismo drone. |
  | `AsignadorAutomatico`: sin drone y misión CRÍTICA | `misionCriticaSinDrone_alertaCentroControl` | Una misión crítica sin drone debe llegar al centro de control; si la rama falla, la alerta se pierde. |
  | `AsignadorAutomatico`: sin drone y misión no crítica | `misionNormalSinDrone_sinAlerta` | Evita falsas alarmas al centro de control. |

  3. Se volvió a correr `mvn clean test`: **38 pruebas, BUILD SUCCESS, "All coverage checks have been met."**

  **Resultado obtenido (sin `Main`):**
  | Métrica | Cobertura |
  |---|---|
  | Líneas (total) | **98.5%** |
  | Ramas (total) | **100%** |
  | Clase con menor cobertura | `ejercicio12.DroneAcuatico` — 86% de líneas |

Reto 14 — SonarQube
- [ ] 0 bugs, 0 vulnerabilidades, deuda < 30 min, duplicación < 5%
- Evidencia:
