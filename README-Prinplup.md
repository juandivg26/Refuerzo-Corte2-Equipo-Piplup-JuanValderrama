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

  **Fuente PlantUML** (se renderiza en https://www.plantuml.com/plantuml o con la extensión PlantUML de VS Code):
  ```plantuml
  @startuml
  !include <C4/C4_Context>
  title AquaPort v2 - Diagrama de Contexto (C4 nivel 1)

  Person(solicitante, "Solicitante", "Pide el transporte de muestras, sensores o equipos")
  Person(operador, "Operador Hidrico", "Supervisa la asignacion automatica")
  Person(admin, "Administrador ECI", "Gestiona la flota y consulta reportes")
  Person(tecnico, "Tecnico de Mantenimiento", "Atiende los drones en FALLO")

  System(aquaport, "AquaPort v2", "Asigna automaticamente el drone acuatico mas adecuado a cada mision")

  System_Ext(apiHidrica, "API Condiciones Hidricas", "Estado del agua por zona")
  System_Ext(centro, "Centro de Control ECI", "Registra misiones y autoriza rutas")
  System_Ext(alertas, "Sistema de Alertas", "Distribuye alertas de fallo")

  Rel(solicitante, aquaport, "Solicitud de transporte (zona destino, tipo de carga, peso, prioridad)")
  Rel(aquaport, solicitante, "Codigo de mision y drone asignado")
  Rel(operador, aquaport, "Estrategia de seleccion activa, supervision")
  Rel(aquaport, operador, "Estado de la flota y de las misiones")
  Rel(admin, aquaport, "Altas/bajas de drones, consulta de reportes")
  Rel(aquaport, apiHidrica, "Consulta de condiciones de la zona destino")
  Rel(apiHidrica, aquaport, "Nivel de agitacion, profundidad, temperatura")
  Rel(aquaport, centro, "Registro de mision y solicitud de autorizacion de ruta")
  Rel(centro, aquaport, "Autorizacion de ruta acuatica")
  Rel(aquaport, alertas, "Evento: drone en FALLO / mision CRITICA sin drone")
  Rel(alertas, tecnico, "Notificacion del drone a revisar (id, tipo, zona)")
  @enduml
  ```

  **Comparación MVP (Piplup) vs v2 (Prinplup):**
  | Elemento | MVP | v2 | ¿Qué cambió? |
  |---|---|---|---|
  | Actores | Operador Hídrico, Solicitante, Administrador ECI | Los mismos + **Técnico de Mantenimiento** | Creció: aparece quien atiende los fallos. |
  | Rol del operador | Asigna drones manualmente | Supervisa y elige la estrategia | Cambió: el sistema decide solo y el operador supervisa. |
  | Sistemas externos | Ninguno | **API Condiciones Hídricas**, **Centro de Control ECI**, **Sistema de Alertas** | Creció: primera integración con sistemas reales. |
  | Sistema central | AquaPort MVP como caja negra | AquaPort v2 como caja negra | Se mantuvo: el nivel 1 sigue sin mostrar clases. |
  | Flujo con el solicitante | Solicitud → código de misión | Solicitud (con peso y prioridad) → código + drone asignado | Creció: la solicitud trae más datos. |

Reto 6 — RF y RNF
- [x] 4 RF + 4 RNF + MoSCoW + tensión AP-07 vs AP-08
- Evidencia:

  **Requisitos Funcionales (nuevas funcionalidades de la v2):**
  - **AP-07 Asignación automática:** cuando el Solicitante registra una solicitud de transporte, el sistema asigna automáticamente el drone disponible de mayor batería que sea apto (batería ≥ 35%, capacidad suficiente para el peso) y retorna el código de misión con el drone asignado.
  - **AP-09 Alertas de fallo:** cuando un drone seleccionado está en estado `FALLO`, el sistema notifica al Centro de Control y al Técnico de Mantenimiento (id, tipo y zona del drone) y asigna un drone alternativo.
  - **AP-10 Consulta de condiciones hídricas:** antes de asignar, el sistema consulta a la API de Condiciones Hídricas el nivel de agitación y la profundidad de la zona destino, y descarta los tipos de drone que no pueden operar en esas condiciones.
  - **AP-11 Gestión por técnico:** el Técnico de Mantenimiento puede cambiar el estado de un drone (`FALLO` → `MANTENIMIENTO` → `DISPONIBLE`); el sistema refleja el nuevo estado en la flota y lo vuelve a considerar en la siguiente asignación.

  **Requisitos No Funcionales:**
  - **RNF-04 (Rendimiento):** el algoritmo de asignación automática debe seleccionar el drone en menos de **400 ms** con una flota de hasta **30 drones**, medido con JUnit 5 `assertTimeout`.
  - **RNF-05 (Tiempo de alerta):** la notificación de un drone en `FALLO` debe llegar a todos los observadores registrados en menos de **1 s** desde que se detecta, medido en una prueba de integración.
  - **RNF-06 (Calidad):** el build debe fallar si alguna clase de dominio baja del **80%** de líneas cubiertas o el proyecto del **70%** de ramas (quality gate de JaCoCo, reto 13).
  - **RNF-07 (Disponibilidad):** si la API de Condiciones Hídricas no responde en **2 s**, el sistema usa las últimas condiciones conocidas de la zona (con máximo **15 min** de antigüedad) en lugar de bloquear la asignación.

  **Clasificación MoSCoW:**
  | Requisito | Categoría | Justificación |
  |---|---|---|
  | AP-07 Asignación automática | Must Have | Es la razón de ser de la v2: el sistema decide y el operador supervisa. |
  | AP-09 Alertas de fallo | Must Have | Con 15 drones el operador no puede vigilar todos; un fallo sin aviso deja muestras perdidas. |
  | AP-10 Condiciones hídricas | Should Have | Mejora la calidad de la asignación, pero la v2 puede operar con las reglas de batería y capacidad mientras se integra la API. |
  | AP-11 Gestión por técnico | Could Have | Útil para cerrar el ciclo del fallo, pero el estado puede ajustarse manualmente desde administración. |
  | RNF-04 Asignación < 400 ms | Must Have | La asignación es automática; si es lenta, las misiones CRÍTICAS esperan. |
  | RNF-05 Alerta < 1 s | Should Have | Importa para reaccionar rápido, pero un segundo extra no rompe la operación. |
  | RNF-06 Quality gate | Must Have | Es criterio del DoD de la v2: sin él no hay garantía sobre la lógica de asignación. |
  | RNF-07 Respaldo de la API | Could Have | Solo aplica cuando AP-10 esté integrado. |

  **Tensión entre AP-07 y AP-08:**
  - **AP-07:** "El sistema asigna automáticamente el drone de mayor batería disponible para cualquier misión hídrica."
  - **AP-08:** "Las misiones CRÍTICAS tienen prioridad absoluta: deben recibir el drone técnicamente más apto para la zona, independientemente de la batería."

  No se contradicen, pero chocan en las misiones CRÍTICAS: AP-07 elige por **batería** y AP-08 por **aptitud para la zona**. Si el drone de 95% está en otra zona y uno de 60% ya está en la zona destino, AP-07 elige el primero y AP-08 el segundo.

  **Resolución:** AP-08 prevalece sobre AP-07 **solo** para prioridad `CRITICA`; para `ALTA`, `NORMAL` y `BAJA` se aplica AP-07. En el diseño esto no requiere `if` dentro del asignador: el criterio vive en las estrategias del patrón Strategy (`MayorBateriaStrategy` para AP-07 y `ZonaCercanaStrategy` como base de AP-08), y la estrategia se elige según la prioridad de la misión. En ambos casos se mantiene el mínimo de 35% de batería, así que AP-08 nunca asigna un drone que no pueda terminar la misión.

Reto 7 — Plantilla DOSW (RF AP-07)
- [x] Plantilla completa con sub-objetos
- Evidencia:

Proyecto: AquaPort v2 | DOSW 2026 | Página 1
AQUAPORT V2
Desarrollo y Operaciones de Software
ANÁLISIS DE REQUERIMIENTOS

# FUNCIONALIDAD

Código: AP-07
Nombre: Asignar automáticamente drone a misión hídrica
Descripción: El sistema selecciona y asigna, sin intervención del operador, el drone acuático más adecuado para una solicitud de transporte según la zona destino, el tipo y peso de la carga, las condiciones del agua y la estrategia de selección activa.
Cómo se ejecutará: Automáticamente, cada vez que el Solicitante registra una solicitud de transporte.
Actor principal: Sistema AquaPort (disparado por el Solicitante); el Operador Hídrico supervisa.
Precondiciones: Debe existir al menos un drone registrado en la flota. Debe haber una estrategia de selección activa configurada.

# DATOS DE ENTRADA

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|---|---|---|---|---|
| carga | Lo que se transporta | — | — | Sí |
| carga.peso | Peso de la carga | Integer | Entre 1 y 1500 gramos | Sí |
| carga.tipo | Clase de carga | Enum(MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO, EQUIPO_MEDICION) | — | Sí |
| carga.prioridad | Urgencia de la misión | Enum(CRITICA, ALTA, NORMAL, BAJA) | CRITICA tiene precedencia absoluta (AP-08) | Sí |
| zonaDestino | Zona de entrega | Enum(EMBALSE_NORTE, CANAL_CENTRAL, LAGUNA_SUR, RIBERA_ESTE, LAB_HIDRICO) | Debe ser una de las 5 zonas | Sí |

# DATOS DE SALIDA

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|---|---|---|---|---|
| codigoMision | Identificador de la misión | String | Generado por el sistema, formato M-XXX | Sí |
| droneAsignado | Drone elegido | — | Calculado por el sistema según la estrategia activa | No (salida) |
| droneAsignado.id | Identificador del drone | String | Formato AR-XX | No (salida) |
| droneAsignado.tipo | Tipo de drone | String | SUPERFICIAL / SEMISUMERGIDO / BUCEADOR | No (salida) |
| droneAsignado.bateria | Batería al asignar | Integer | Entre 35 y 100 | No (salida) |
| droneAsignado.estado | Estado tras la asignación | Enum(DISPONIBLE, EN_MISION, RECARGANDO, MANTENIMIENTO, SUMERGIDO, FALLO) | Queda en EN_MISION | No (salida) |

# FLUJO BÁSICO

| Paso | Actor | Descripción | Excepciones |
|---|---|---|---|
| 1 | Solicitante | Registra la solicitud con carga (peso, tipo, prioridad) y zona destino. | — |
| 2 | Sistema | Consulta a la API de Condiciones Hídricas la agitación y profundidad de la zona destino. | FA-02 |
| 3 | Sistema | Filtra los drones aptos para la zona: tipo compatible con las condiciones, estado no ocupado. | FA-01 |
| 4 | Sistema | Aplica la estrategia de selección activa sobre los drones aptos. | FA-01 |
| 5 | Sistema | Valida el drone elegido: batería ≥ 35%, capacidad de carga del tipo y que no esté en FALLO. | FA-03, FA-04 |
| 6 | Sistema | Asigna el drone, lo cambia a EN_MISION y genera el código de misión. | — |
| 7 | Sistema | Notifica a los observadores (Centro de Control) y retorna el código y el drone al Solicitante. | — |

# FLUJO ALTERNO

| Paso | Actor | Descripción | Excepciones |
|---|---|---|---|
| FA-01 | Sistema | Sin drones disponibles: no queda ningún drone apto. Si la misión es CRÍTICA, alerta al Centro de Control: "Misión CRÍTICA [id] sin drone disponible". La solicitud queda PENDIENTE. | Fin del caso |
| FA-02 | Sistema | Condiciones hídricas adversas: la zona supera el nivel de agitación permitido. Solo se consideran drones SEMISUMERGIDO; si no hay, se aplica FA-01. | Continúa en paso 3 |
| FA-03 | Sistema | La carga supera la capacidad del tipo (ej. 400 g para un BUCEADOR de 300 g): se descarta ese drone y se vuelve a aplicar la estrategia. | Retorna al paso 4 |
| FA-04 | Sistema | El drone elegido está en FALLO: se notifica al Centro de Control y al Técnico de Mantenimiento, se descarta y se busca un alternativo. | Retorna al paso 4 |

Proyecto: AquaPort v2 | DOSW 2026 | Página 2

**Notas y comentarios:**
Implementado en `AsignadorAutomatico`, `ValidadorMision`, `EstrategiaSeleccion` y `ObservadorMision` (`Prinplup/src/main/java/com/eci/aquaport/ejercicio3/` y `ejercicio12/`).

# REGLAS DE NEGOCIO

| No. | Descripción |
|---|---|
| RN-01 | Ningún drone con batería menor a 35% puede ser asignado. |
| RN-02 | El DroneBuceador no se asigna para cargas mayores a 300 g (Superficial: hasta 500 g; Semisumergido: hasta 1500 g). |
| RN-03 | Las misiones CRÍTICAS reciben el drone técnicamente más apto para la zona, aunque no sea el de mayor batería (AP-08). |
| RN-04 | Un drone EN_MISION, RECARGANDO, MANTENIMIENTO o SUMERGIDO no puede recibir otra misión. |

# ABREVIATURAS

| Abreviatura | Significado |
|---|---|
| RF | Requisito Funcional |
| RN | Regla de Negocio |
| FA | Flujo Alterno |

# HISTORIAL DE REVISIÓN

| Elaborado por | Aprobado por | Fecha | Descripción y Justificación de Cambios |
|---|---|---|---|
| Equipo Piplup | | 09/10/2026 | Versión inicial del documento para la v2. |

Reto 8 — Identidad y UX
- [ ] Tarjeta de drone con 6 estados, flujo de 3 pantallas, Fitts y Hick
- Evidencia:

Reto 9 — Agilismo y Jira
- [ ] Sprint con Story Points y DoD
- Evidencia:

Reto 10 — Diagrama de Casos de Uso v2
- [ ] Nuevo actor, include y extend
- Evidencia:

  **Fuente PlantUML:**
  ```plantuml
  @startuml
  left to right direction
  skinparam packageStyle rectangle

  actor "Usuario ECI" as base
  actor "Operador Hidrico" as operador
  actor "Administrador ECI" as admin
  actor "Solicitante" as solicitante
  actor "Tecnico de Mantenimiento" as tecnico
  actor "Centro de Control" as centro <<sistema>>

  operador --|> base
  admin --|> base

  rectangle "AquaPort v2" {
    usecase "Solicitar transporte" as UC1
    usecase "Asignar mision automaticamente" as UC2
    usecase "Validar condiciones hidricas" as UC3
    usecase "Notificar fallo al tecnico" as UC4
    usecase "Consultar estado de la flota" as UC5
    usecase "Cambiar estrategia de seleccion" as UC6
    usecase "Gestionar drones de la flota" as UC7
    usecase "Atender drone en FALLO" as UC8
  }

  solicitante --> UC1
  UC1 ..> UC2 : <<include>>
  UC2 ..> UC3 : <<include>>
  UC4 ..> UC2 : <<extend>>
  note right of UC4
    Condicion: hay un drone en
    estado FALLO durante la asignacion
  end note
  UC2 --> centro
  base --> UC5
  operador --> UC6
  admin --> UC7
  tecnico --> UC8
  UC4 --> tecnico
  @enduml
  ```

  **Decisiones del diagrama:**
  - `<<include>>` "Validar condiciones hídricas": siempre ocurre; sin validar el agua no se puede asignar.
  - `<<extend>>` "Notificar fallo al técnico": solo ocurre si aparece un drone en `FALLO` durante la asignación.
  - Generalización: Operador Hídrico y Administrador ECI heredan del actor base "Usuario ECI" el CU "Consultar estado de la flota"; cada uno conserva sus CU propios.

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
