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
<img width="784" height="485" alt="imagen" src="https://github.com/user-attachments/assets/6196105a-b0e6-4916-a9bc-017339062b2d" />

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
<img width="895" height="424" alt="imagen" src="https://github.com/user-attachments/assets/ee876807-54d1-4a3e-ab60-9187814897db" />

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
<img width="959" height="497" alt="imagen" src="https://github.com/user-attachments/assets/93f24a71-5e3b-4a9d-af63-540018f4605a" />

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
<img width="845" height="542" alt="imagen" src="https://github.com/user-attachments/assets/4020c1c4-961f-44ee-be52-f0536ae27af2" />

Reto 5 — Diagrama de Contexto C4 v2
- [x] Nuevo actor (Técnico de Mantenimiento) y 3 sistemas externos con flujos etiquetados
- Evidencia:

 <img width="1579" height="996" alt="imagen" src="https://github.com/user-attachments/assets/56907216-1795-460c-8f2b-687bdc5ea590" />


  **Comparación MVP (Piplup) vs v2 (Prinplup):**
  | Elemento | MVP | v2 | ¿Qué cambió? |
  |---|---|---|---|
  | Actores | Operador Hídrico, Solicitante, Administrador ECI | Los mismos + **Técnico de Mantenimiento** | Creció: aparece quien atiende los fallos. |
  | Rol del operador | Asigna drones manualmente | Supervisa y elige la estrategia | Cambió: el sistema decide solo y el operador supervisa. |
  | Sistemas externos | Ninguno | **API Condiciones Hídricas**, **Centro de Control ECI**, **Sistema de Alertas** | Creció: primera integración con sistemas reales. |
  | Sistema central | AquaPort MVP como caja negra | AquaPort v2 como caja negra | Se mantuvo: el nivel 1 sigue sin mostrar clases. |
  | Flujo con el solicitante | Solicitud → código de misión | Solicitud (con peso y prioridad) → código + drone asignado | Creció: la solicitud trae más datos. |
<img width="845" height="515" alt="imagen" src="https://github.com/user-attachments/assets/28b44108-8c84-405c-ba43-0e8e37ed4b93" />

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
- [x] Tarjeta de drone con 6 estados, flujo de 3 pantallas, Fitts y Hick
- Evidencia:

  **Sistema de diseño v2** (extiende el manual de identidad de Piplup, reto 08):
  | Token | Valor |
  |---|---|
  | Primario | Azul profundo `#0B3C5D` |
  | Acento | Celeste `#1D9A9F` |
  | Disponible | Verde menta `#4CD9B0` |
  | En misión | Azul `#2F80ED` |
  | Recargando | Ámbar `#F0A830` |
  | Mantenimiento | Gris `#8A94A6` |
  | Fallo | Rojo coral `#E25C5C` |
  | Sumergido (nuevo en v2) | Azul oscuro `#1B2A6B` |
  | Tipografía | Inter (interfaz) + JetBrains Mono (IDs `AR-XX`, códigos `M-XXX`) |

  | Componente | Estados |
  |---|---|
  | Tarjeta de drone acuático | Disponible, En misión, Recargando, Mantenimiento, Fallo, Sumergido |
  | Botón "Asignar misión" | Default, hover, procesando (spinner), éxito, deshabilitado |
  | Indicador de batería | ≥ 60% verde, 35-59% ámbar, < 35% rojo con ícono de advertencia |

  **Leyes UX aplicadas:**
  - **Fitts:** el botón de emergencia "Detener misión" es el más grande de la pantalla (mínimo 48 px de alto), en rojo y anclado en la esquina inferior derecha, la zona más fácil de alcanzar. Los botones destructivos quedan lejos de "Asignar" para evitar clics por error.
  - **Hick:** los 15 drones no se muestran como una sola lista; se agrupan en 3 columnas por tipo (Superficial, Semisumergido, Buceador) con 5 tarjetas cada una. El operador primero elige entre 3 grupos y luego entre 5 drones, en vez de decidir entre 15 opciones a la vez.
  - **Miller:** cada tarjeta muestra solo 4 datos (ID, tipo, batería, estado), dentro del límite de 7 ± 2 elementos.

  Los prompts usados para generar la tarjeta y el flujo de 3 pantallas están en el reto 11.
<img width="1500" height="573" alt="Screenshot 2026-10-09 182834" src="https://github.com/user-attachments/assets/a533a48b-b5ed-4857-a59c-94bf0a861c81" />
<img width="1486" height="787" alt="Screenshot 2026-10-09 182850" src="https://github.com/user-attachments/assets/32d0e091-0db6-4d4f-b6da-7e14ed2b6e03" />
<img width="1496" height="657" alt="Screenshot 2026-10-09 182916" src="https://github.com/user-attachments/assets/2e252691-f29d-4c11-9480-85065d06b49a" />
<img width="1181" height="763" alt="Screenshot 2026-10-09 183212" src="https://github.com/user-attachments/assets/9cb96846-8e74-4b3a-938c-04ec03df98a5" />

<img width="908" height="546" alt="imagen" src="https://github.com/user-attachments/assets/fb9f81a4-dac4-46b6-94c2-1844452ba647" />

Reto 9 — Agilismo y Jira
- [x] Sprint con Story Points y DoD
- Evidencia:

  **Link de Jira:** https://mail-team-nyjtgqcj.atlassian.net/jira/software/projects/AQ/boards/71

  **Jerarquía de la v2:**
  - **Épica AQ-18:** AquaPort v2 — Flota autónoma con asignación automática y alertas.
  - **Feature AQ-19:** Asignación automática de drones por zona.
  - **Feature AQ-20:** Alertas al centro de control.

  **Sprint activo:** `AQ Sprint v2 — Prinplup` (09/10/2026 – 23/10/2026)
  **Sprint Goal:** "Completar el asignador automático con Strategy y las alertas con Observer"

  | HU | Historia de usuario | Feature | Story Points |
  |---|---|---|---|
  | AQ-21 | Como administrador ECI, quiero registrar drones de los 3 tipos con su capacidad de carga, para que el sistema sepa qué carga puede llevar cada uno. | AQ-19 | 3 |
  | AQ-22 | Como operador hídrico, quiero que el sistema asigne automáticamente el drone más adecuado según zona, carga y batería, para no tener que elegirlo manualmente entre 15 drones. | AQ-19 | 8 |
  | AQ-23 | Como operador hídrico, quiero elegir la estrategia de selección, para adaptar la asignación a la operación del día sin modificar el asignador. | AQ-19 | 5 |
  | AQ-24 | Como solicitante, quiero que mis misiones CRÍTICAS tengan prioridad absoluta, para que las muestras urgentes no esperen. | AQ-19 | 5 |
  | AQ-25 | Como centro de control, quiero recibir una alerta cuando un drone entra en FALLO, para saberlo de inmediato y que la misión no se pierda. | AQ-20 | 5 |
  | AQ-26 | Como técnico de mantenimiento, quiero ser notificado del drone en FALLO con su tipo y zona, para ir a revisarlo sin consultar el panel. | AQ-20 | 2 |
  | | | **Total** | **28** |

  **Estimación (Fibonacci):** AQ-22 vale 8 porque concentra la lógica central: filtrar candidatos, delegar a la estrategia y cambiar de estado, con más casos borde. AQ-26 vale 2 porque reutiliza el Observer de AQ-25 y solo agrega un observador.

  **Definition of Done de la v2** (también registrado en la descripción de la épica AQ-18):
  - El código compila sin warnings.
  - Las pruebas unitarias pasan (JUnit 5).
  - JaCoCo reporta ≥ 80% de line coverage para la feature.
  - El PR tiene revisión aprobada por al menos otro miembro.
  - El diagrama C4 y la plantilla DOSW están actualizados.
<img width="848" height="497" alt="imagen" src="https://github.com/user-attachments/assets/8c0ed31c-dbc9-4d4f-b67f-3c12915f2730" />

Reto 10 — Diagrama de Casos de Uso v2
- [x] Nuevo actor, include y extend
- Evidencia:

<img width="1055" height="433" alt="imagen" src="https://github.com/user-attachments/assets/e91753a3-acfb-4b32-b314-6658ed91de4d" />

<img width="831" height="644" alt="imagen" src="https://github.com/user-attachments/assets/f3f995c2-9bf5-4f7b-8e99-b8e9170f4017" />

  **Decisiones del diagrama:**
  - `<<include>>` "Validar condiciones hídricas": siempre ocurre; sin validar el agua no se puede asignar.
  - `<<extend>>` "Notificar fallo al técnico": solo ocurre si aparece un drone en `FALLO` durante la asignación.
  - Generalización: Operador Hídrico y Administrador ECI heredan del actor base "Usuario ECI" el CU "Consultar estado de la flota"; cada uno conserva sus CU propios.

Reto 11 — Mocks con IA
- [x] 3 pantallas + alerta, 4 prompts
- Evidencia:
<img width="1500" height="573" alt="Screenshot 2026-10-09 182834" src="https://github.com/user-attachments/assets/8078cb2d-7847-4dff-9dfe-0e9c2a9e2c95" />
<img width="1486" height="787" alt="Screenshot 2026-10-09 182850" src="https://github.com/user-attachments/assets/c55fb59d-bb80-4baa-8d20-680720e0487c" />
<img width="1496" height="657" alt="Screenshot 2026-10-09 182916" src="https://github.com/user-attachments/assets/ca622271-579c-4a74-bb03-580ee10c5791" />
<img width="1181" height="763" alt="Screenshot 2026-10-09 183212" src="https://github.com/user-attachments/assets/6919d747-6141-4adc-a0dd-43a99e463b6f" />
<img width="1180" height="881" alt="Screenshot 2026-10-09 183352" src="https://github.com/user-attachments/assets/277b72e8-20ed-4dc1-ad92-0489811cc6cd" />

<img width="997" height="690" alt="imagen" src="https://github.com/user-attachments/assets/f9b0198a-f27f-400d-998f-dc97d37ec5eb" />


  **Bloque de estilo (va al inicio de cada prompt):**
  ```
  Actúa como diseñador UX/UI senior de sistemas de monitoreo ambiental.
  SISTEMA: AquaPort v2 — panel de control de una flota de 15 drones acuáticos de la Escuela Colombiana de Ingeniería.
  ESTILO: dashboard técnico, fondo oscuro #0B1622, superficies #12202E, primario #0B3C5D, acento #1D9A9F.
  Tipografía Inter para la interfaz y JetBrains Mono para IDs de drones (AR-01…AR-15) y códigos de misión (M-301).
  COLORES DE ESTADO: Disponible #4CD9B0, En misión #2F80ED, Recargando #F0A830, Mantenimiento #8A94A6, Fallo #E25C5C, Sumergido #1B2A6B.
  BATERÍA: ≥60% verde, 35-59% ámbar, <35% rojo con ícono de advertencia.
  Diseño minimalista, flat, sin ilustraciones decorativas. Textos en español.
  ```

  **Prompt 0 — Componente "Tarjeta de drone acuático" (reto 08):**
  ```
  [bloque de estilo]
  Diseña el componente "Tarjeta de drone acuático" en sus 6 estados, uno al lado del otro:
  Disponible, En misión, Recargando, Mantenimiento, Fallo y Sumergido.
  Cada tarjeta muestra solo: ID en JetBrains Mono (ej. AR-07), tipo (Superficial / Semisumergido / Buceador),
  indicador de batería en % con su color, zona actual y una etiqueta del estado con su color.
  El estado Fallo tiene borde rojo de 2 px y un ícono de alerta. Agrega debajo el botón "Asignar misión"
  en sus 5 estados: default, hover, procesando (spinner), éxito y deshabilitado.
  ```

  **Prompt 1 — Pantalla 1: Panel de flota:**
  ```
  [bloque de estilo]
  PANTALLA: Panel de flota (vista principal del Operador Hídrico).
  Muestra los 15 drones agrupados en 3 columnas por tipo: Superficial (AR-01 a AR-05), Semisumergido (AR-06 a AR-10)
  y Buceador (AR-11 a AR-15), usando la tarjeta de drone. Arriba, 4 indicadores: drones disponibles, en misión,
  en fallo y misiones pendientes. A la derecha, la cola de misiones ordenada por prioridad (CRÍTICA arriba en rojo).
  Muestra un selector de estrategia activa: "Mayor batería" / "Zona cercana".
  Botón de emergencia "Detener misión" grande, rojo, anclado abajo a la derecha (Ley de Fitts).
  ```

  **Prompt 2 — Pantalla 2: Detalle de misión con drone recomendado:**
  ```
  [bloque de estilo]
  PANTALLA: Detalle de la misión M-301.
  Datos de la misión: zona destino Laguna Sur, carga EQUIPO_MEDICION de 1200 g, prioridad ALTA.
  Panel "Drone recomendado por el sistema": AR-09, Semisumergido, batería 83%, zona Laboratorio Hídrico.
  Bloque "¿Por qué este drone?" con 3 viñetas: capacidad 1500 g ≥ 1200 g; batería 83% ≥ 35%;
  estrategia activa "Zona cercana".
  Bloque "Descartados": AR-07 (en FALLO), Buceadores (capacidad 300 g insuficiente).
  Botones: "Confirmar asignación" (primario) y "Cancelar" (secundario, separado).
  ```

  **Prompt 3 — Pantalla 3: Confirmación de asignación:**
  ```
  [bloque de estilo]
  PANTALLA: Confirmación de asignación.
  Mensaje de éxito: "Misión M-301 asignada a AR-09" con código de misión en JetBrains Mono.
  Debajo, la flota actualizada agrupada por tipo, donde AR-09 ahora aparece en estado En misión (azul)
  y AR-07 sigue en Fallo (rojo). Línea de tiempo corta: Solicitud recibida → Drone seleccionado →
  Centro de Control notificado. Botón "Volver al panel de flota".
  ```

  **Prompt 4 — Estado de alerta: misión CRÍTICA sin drone:**
  ```
  [bloque de estilo]
  PANTALLA: Panel de flota en estado de ALERTA.
  Banner rojo superior fijo: "Misión CRÍTICA M-303 sin drone disponible — carga de 2000 g supera la capacidad
  de toda la flota". Botones en el banner: "Notificar al Centro de Control" y "Ver misión".
  La misión M-303 aparece primera en la cola, resaltada en rojo. En la flota, AR-07 en Fallo con borde rojo
  y la etiqueta "Técnico notificado". El resto de la pantalla igual al panel de flota.
  ```

  **Heurísticas de Nielsen que deben cumplir las pantallas:**
  | # | Heurística | Dónde se ve |
  |---|---|---|
  | 1 | Visibilidad del estado del sistema | Colores de estado en cada tarjeta y banner de alerta siempre visible. |
  | 3 | Control y libertad del usuario | "Cancelar" en el detalle de misión y "Detener misión" de emergencia. |
  | 5 | Prevención de errores | Drones con batería < 35% y tipos sin capacidad aparecen como descartados antes de confirmar. |
  | 8 | Diseño minimalista | Cada tarjeta solo muestra ID, tipo, batería y estado. |
  | 10 | Ayuda y documentación | El bloque "¿Por qué este drone?" explica la decisión del sistema. |

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
<img width="910" height="514" alt="imagen" src="https://github.com/user-attachments/assets/c2c5652a-adc5-4a58-bddc-cc1af3812f93" />

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
<img width="870" height="499" alt="imagen" src="https://github.com/user-attachments/assets/44f18f40-dbdb-4c49-862b-ec2ab1b784a6" />

Reto 14 — SonarQube
- [x] 0 bugs, 0 vulnerabilidades, deuda < 30 min, duplicación < 5%
- Evidencia:

  **Primer análisis (v2.0.0):** 0 bugs, 0 vulnerabilidades, 17 code smells (160 min de deuda), cobertura 80.4% y duplicación 16.3%. Quality Gate en verde, pero la deuda y la duplicación estaban por encima de lo que pide el reto.

  **Issues resueltos** (cada commit documenta causa raíz, corrección y por qué es la adecuada):
  | Regla | Cantidad | Causa raíz | Corrección | Commit |
  |---|---|---|---|---|
  | `java:S1135` TODO pendiente | 2 | La palabra "todo" en el comentario de `ObservadorMision` ("no todo observador…") se interpretaba como TODO. | Se reformuló el comentario; no había ninguna tarea pendiente. | `refactor: reescribir comentario de ObservadorMision…` |
  | `java:S1192` literales duplicados | 3 | Los nombres de zona se repetían hasta 7 veces en `ejercicio1/Main`. | Constantes privadas para las 5 zonas: un solo punto de cambio. | `refactor: constantes para los nombres de zona…` |
  | `java:S106` uso de `System.out` | 12 | Los `Main` y los observadores escribían directo en consola. | Los observadores reciben su canal de salida por constructor (`Consumer<String>`, DIP). Los `Main` usan `java.util.logging.Logger` con suppliers. Las pruebas verifican los mensajes con una lista en lugar de secuestrar `System.out`. | `refactor: reemplazar System.out por Logger…` |

  **Duplicación:** `ejercicio12` copia a propósito el modelo de `ejercicio3`, porque cada ejercicio es autocontenido (reto 12: TDD desde cero sobre el mismo dominio). Esa copia es una decisión de organización del repositorio, no código duplicado dentro del sistema, así que se excluyó del cálculo con `sonar.cpd.exclusions=**/ejercicio12/**` en el `pom.xml`. El código de `ejercicio12` **sí** se sigue analizando para bugs, vulnerabilidades y code smells; solo se excluye de la métrica de duplicación.

  **Cobertura:** se excluyeron los `Main` (`sonar.coverage.exclusions=**/Main.java`) con el mismo criterio del quality gate de JaCoCo (reto 13): solo imprimen la demo de cada reto.

  **Resultado final (Overall Code, versión 2.0.0, 653 líneas):**
  | Indicador | Meta del reto | Resultado |
  |---|---|---|
  | Bugs (Reliability) | 0 | **0**  |
  | Vulnerabilidades (Security) | 0 | **0**  |
  | Deuda técnica (Maintainability) | < 30 min | **0 issues, 0 min**  |
  | Duplicación | < 5% | **0.0%**  |
  | Cobertura | — | **98.8%** |
  | Quality Gate | Passed | **Passed**  |
  <img width="1690" height="599" alt="imagen" src="https://github.com/user-attachments/assets/b77824ff-0384-4167-b964-9d95fe4ba25e" />
  <img width="862" height="477" alt="imagen" src="https://github.com/user-attachments/assets/e37b8640-bd59-4187-93ba-1bf0d92ba10d" />

