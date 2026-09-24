# AquaPort — Nivel Piplup (MVP)

Reto 1 — Streams & Lambdas: `ConsultorFlota`
- [x] 5 consultas implementadas solo con Streams
- Evidencia:

  **Ubicación del código:** `Piplup/src/main/java/com/eci/aquaport/ejercicio1/`
  (`DroneAcuatico.java`, `ConsultorFlota.java`, `Main.java`)

  **Paso a paso de la implementación:**
  1. Se creó el record `DroneAcuatico(id, modelo, bateria, disponible, zona)` tal cual lo pide el enunciado, incluyendo la flota de ejemplo de 4 drones dentro de `Main`.
  2. Se creó la clase `ConsultorFlota` con un método por cada una de las 5 consultas pedidas, cada uno recibiendo `List<DroneAcuatico>` como parámetro:
     - `dronesDisponiblesOrdenadosPorBateria`: `filter` (disponible) + `filter` (batería ≥35%) + `sorted` (descendente) + `toList`.
     - `idsDronesDisponibles`: `filter` (disponible) + `map` (a `id`) + `toList`.
     - `existeDroneDisponibleConBateriaSuficiente`: `anyMatch` con ambas condiciones.
     - `contarDronesDisponibles`: `filter` (disponible) + `count`.
     - `droneConMayorBateria`: `max` con `Comparator.comparingInt`, retorna `Optional<DroneAcuatico>`.
  3. Se escribió `Main` con la flota de ejemplo del enunciado y se imprimieron los resultados de las 5 consultas.
  4. Se compiló y ejecutó para verificar:
     ```
     mvn clean compile
     java -cp target\classes com.eci.aquaport.ejercicio1.Main
     ```

  **Salida obtenida (evidencia de ejecución):**
  ```
  Disponibles con batería >= 35% ordenados: [AR-01 (92), AR-04 (73), AR-02 (45)]
  IDs disponibles: [AR-01, AR-02, AR-04]
  ¿Existe disponible con batería suficiente?: true
  Cantidad de disponibles: 3
  Drone con mayor batería: Optional[AR-01 (92)]
  ```
<img width="1825" height="783" alt="aprobadoejericico1" src="https://github.com/user-attachments/assets/c9b6f9b5-157b-4022-93e0-ba0313093468" />

Reto 2 — GitHub y GitFlow
- [x] Rama develop y feature creadas, PR mergeado
- Evidencia:

  **Paso a paso:**
  1. Se creó el repositorio `Refuerzo-Corte2-Equipo-Piplup-JuanValderrama`.
  2. Se configuró el `.gitignore` para Java/Maven, excluyendo `target/`, `*.class`, `.idea/`.
  3. Se creó la rama `develop` a partir del commit inicial y se subió con `git push -u origin develop`.
  4. Desde `develop` se creó la rama `feature/streams-consultor-flota`.
  5. Se hicieron commits en esa rama, cada uno con una sola responsabilidad:
     - `feat: implementar ConsultorFlota con 5 consultas Stream (reto 01)`
     - `refactor: mover pom.xml y READMEs a la raiz, organizar Piplup por ejercicio1`
     - `docs: agregar evidencia del reto 01 en README-Piplup`
  6. Se abrió el Pull Request de `feature/streams-consultor-flota` hacia `develop` y se hizo el merge.

  **Pull Request:** https://github.com/juandivg26/Refuerzo-Corte2-Equipo-Piplup-JuanValderrama/pull/1

Reto 3 — Patrones de Diseño: `Mision.Builder`
- [x] Builder implementado con validación en build()
- Evidencia:

  **Ubicación del código:** `Piplup/src/main/java/com/eci/aquaport/ejercicio3/`
  (`DroneAcuatico.java`, `TipoCarga.java`, `EstadoMision.java`, `Mision.java`, `Main.java`)

  **Paso a paso de la implementación:**
  1. Se creó el modelo inmutable `Mision` utilizando el patrón de diseño Builder mediante la clase estática interna `Mision.Builder`.
  2. Se configuraron métodos fluidos para asignar cada atributo (`id`, `drone`, `puntoPartida`, `puntoLlegada`, `tipoCarga`, `estado`). Por defecto, el `estado` se inicializa en `EstadoMision.PENDIENTE`.
  3. En el método `build()`, se agregaron las validaciones de reglas de negocio antes de retornar la instancia de `Mision`:
     - `id` no nulo y no vacío (lanzando `IllegalStateException`).
     - `drone` no nulo y con disponibilidad verificada (`drone.disponible() == true`).
     - `puntoPartida` y `puntoLlegada` no nulos y no vacíos.
     - `tipoCarga` obligatorio.
  4. Se creó la clase `Main` en donde se probaron 4 escenarios: 1 caso exitoso y 3 casos de falla (drone ocupado, punto de llegada vacío e id nulo) usando bloques `try-catch` para verificar el lanzamiento correcto de excepciones.
  5. Se compiló y ejecutó desde la raíz del repositorio usando Maven:
     ```bash
     mvn clean compile
     mvn exec:java "-Dexec.mainClass=com.eci.aquaport.ejercicio3.Main"
     ```

  **Salida obtenida (evidencia de ejecución):**
  ```text
  === PRUEBA DE BUILDER DE MISION (RETO 03) ===

  1. Prueba con Caso Valido:
  Mision creada exitosamente: Mision{id='M-101', drone=AR-01 (Aqua-Ranger 100), puntoPartida='Embalse Norte', puntoLlegada='Laboratorio Hidrico', tipoCarga=MUESTRA_AGUA, estado=PENDIENTE}

  2. Prueba de Error: Drone no disponible
  Excepcion capturada esperada: El drone asignado (AR-03) no esta disponible para la mision.

  3. Prueba de Error: Punto de llegada vacio
  Excepcion capturada esperada: El punto de llegada es obligatorio y no puede estar vacio.

  4. Prueba de Error: ID nulo
  Excepcion capturada esperada: El ID de la mision es obligatorio.
<img width="1028" height="453" alt="Aprobadoejercicio3" src="https://github.com/user-attachments/assets/c3c6562d-6072-4d8e-966d-964ab5ca2893" />

Reto 4 — Principios SOLID (SRP y DIP)
- [x] RegistradorMisiones, ValidadorMision, NotificadorOperador, RepositorioMisiones
- Evidencia:

  **Ubicación del código:** `Piplup/src/main/java/com/eci/aquaport/ejercicio4/`
  (`DroneAcuatico.java`, `TipoCarga.java`, `EstadoMision.java`, `Mision.java`, `RepositorioMisiones.java`, `RepositorioMisionesEnMemoria.java`, `RegistradorMisiones.java`, `ValidadorMision.java`, `NotificadorOperador.java`, `Main.java`)

  **Diagrama de clases:** 

 <img width="762" height="598" alt="Diagramaejercicico4" src="https://github.com/user-attachments/assets/aee5c771-aa6f-4ba3-aa0f-35b2472246c2" />


  **Paso a paso de la implementación:**
  1. Se copió el modelo base (`DroneAcuatico`, `TipoCarga`, `EstadoMision`, `Mision`) al paquete `ejercicio4` para que cada ejercicio quede autocontenido.
  2. SRP: se separó la responsabilidad en tres clases:
     - `RegistradorMisiones`: solo registra y consulta misiones (`registrar`, `buscarPorId`, `listarMisiones`).
     - `ValidadorMision`: solo aplica reglas de negocio.
     - `NotificadorOperador`: solo muestra mensajes al operador (`mostrar`).
  3. DIP: se definió la interfaz `RepositorioMisiones` (`guardar`, `buscarPorId`, `listarTodas`). `RegistradorMisiones` la recibe por constructor (constructor injection) y no conoce la implementación concreta. La implementación `RepositorioMisionesEnMemoria` (guarda en un `Map`) se crea únicamente en `Main`.
  4. Regla de negocio de `ValidadorMision`: un drone no puede tener más de 1 misión activa simultánea (activa = `PENDIENTE` o `EN_TRANSITO`). Se valida con `filter` + `anyMatch` sobre las misiones ya registradas y lanza `IllegalStateException` si el drone está ocupado.
  5. `Main` coordina el flujo validar → registrar → notificar, con 3 misiones (la tercera reutiliza `AR-01` para demostrar el rechazo) y dos consultas por id (una existente y una inexistente).
  6. Se compiló y ejecutó para verificar:
```
     mvn clean compile
     java -cp target\classes com.eci.aquaport.ejercicio4.Main
```

  **Decisión de diseño:** `ValidadorMision` queda con una sola regla a propósito. Las reglas de batería, punto de llegada, drone no disponible y zona se desarrollan en el reto 12 con TDD (pruebas antes que código).

  **Salida obtenida (evidencia de ejecución):**
```
  [OPERADOR] Mision registrada: M-001
  [OPERADOR] Mision registrada: M-002
  [OPERADOR] Mision rechazada (M-003): El drone AR-01 ya tiene una mision activa. Un drone no puede tener mas de 1 mision activa simultanea.
  [OPERADOR] Total de misiones: 2
  [OPERADOR] Encontrada: Mision{id='M-002', drone=AR-02 (Aqua-Ranger 100), puntoPartida='Canal Central', puntoLlegada='Laguna Sur', tipoCarga=SENSOR, estado=PENDIENTE}
  [OPERADOR] No existe la mision M-999.
```
<img width="836" height="505" alt="Aprobadoejercicio4" src="https://github.com/user-attachments/assets/592906ab-454f-4757-ab69-4fee37acff46" />


Reto 5 — Diagrama de Contexto C4
- [x] Diagrama en docs/c4-contexto-piplup.png
- Evidencia:

  **Ubicación del diagrama:** `docs/c4-contexto-piplup.png` (fuente PlantUML en `docs/c4-contexto-piplup.puml`)

  <img width="370" height="471" alt="Screenshot 2026-09-22 203458" src="https://github.com/user-attachments/assets/7a6ce289-0c20-45bc-9e78-9f8c5d4ba162" />


  **Paso a paso de la implementación:**
  1. Se identificaron los 3 actores del MVP: `Operador Hidrico` (registra solicitudes y asigna drones manualmente), `Solicitante` (pide el transporte de muestras o sensores) y `Administrador ECI` (gestiona la flota y consulta reportes).
  2. Se modeló `AquaPort MVP` como un único sistema (`System`), sin sistemas externos, ya que el MVP opera sin conexiones externas.
  3. Se definieron los flujos de información en ambos sentidos entre `Operador Hidrico` y `AquaPort MVP` (solicitud/asignación de mision hacia el sistema, confirmación/estado de mision hacia el operador), y entre `Solicitante` y `AquaPort MVP` (solicitud de transporte hacia el sistema, código de mision generado hacia el solicitante). `Administrador ECI` solo tiene flujo hacia el sistema (gestión de flota y consulta de reportes).
 
Reto 6 — RF y RNF
- [x] 3 RF + 3 RNF + MoSCoW
- Evidencia:

  **Requisitos Funcionales:**
  - **RF-01:** El Operador Hidrico puede registrar una mision de transporte especificando drone, origen, destino y tipo de carga; el sistema confirma la creacion con un codigo de mision y estado `PENDIENTE`.
  - **RF-02:** El Operador Hidrico puede consultar la flota de drones disponibles ordenados por bateria; el sistema retorna la lista filtrada (disponibles con bateria >= 35%).
  - **RF-03:** El Administrador ECI puede consultar el estado de una mision por su ID; el sistema retorna los datos de la mision o indica que no existe.

  **Requisitos No Funcionales:**
  - **RNF-01 (Rendimiento):** La consulta de drones disponibles debe retornar resultados en menos de 200ms con una flota de hasta 10 drones, medido con JUnit 5 `assertTimeout`.
  - **RNF-02 (Cobertura de pruebas):** `ValidadorMision` debe tener una cobertura de pruebas >= 80%, medida con JaCoCo.
  - **RNF-03 (Mantenibilidad):** El codigo debe compilar sin advertencias de `javac` y sin issues de tipo bug o vulnerability reportados por SonarQube.

  **Clasificacion MoSCoW:**

  | Requisito | Categoria | Justificacion |
  |---|---|---|
  | RF-01 Registrar mision | Must Have | Es la funcion central del MVP; sin ella no hay sistema. |
  | RF-02 Consultar flota disponible | Must Have | El operador necesita esta informacion antes de poder asignar cualquier drone. |
  | RF-03 Consultar estado de mision | Should Have | Mejora la trazabilidad, pero el MVP funciona sin ella si la mision ya se confirmo al crearla. |
  | RNF-01 Rendimiento < 200ms | Should Have | Importa para la experiencia de uso, pero no bloquea el funcionamiento con una flota pequena. |
  | RNF-02 Cobertura >= 80% | Could Have | Es una meta de calidad interna, no algo que el usuario final perciba directamente. |
  | RNF-03 Sin bugs/vulnerabilidades en Sonar | Won't Have (por ahora) | Es una meta de mantenimiento a largo plazo; se prioriza la funcionalidad del MVP primero. |

Reto 7 — Plantilla DOSW (RF AP-01)
- [x] Plantilla completa
- Evidencia:
Proyecto: AquaPort MVP | DOSW 2026 | Página 1
AQUAPORT MVP
Desarrollo y Operaciones de Software
ANÁLISIS DE REQUERIMIENTOS
Fecha: 23/09/2026
Página: 1 de 2

# FUNCIONALIDAD

Código: AP-01
Nombre: Registrar mision de transporte de muestra
Descripción: Permite al Operador Hidrico registrar una nueva mision de transporte, asignando un drone acuatico disponible para trasladar una muestra, un sensor o un paquete ligero entre dos puntos.
Cómo se ejecutará: El operador selecciona un drone disponible desde el panel de flota e ingresa los datos de la mision (punto de partida, punto de llegada y tipo de carga).
Actor principal: Operador Hidrico
Precondiciones: Debe existir al menos un drone disponible con bateria >= 35%. El operador debe conocer el punto de partida y el punto de llegada de la mision.

# DATOS DE ENTRADA

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|---|---|---|---|---|
| drone | Drone acuatico asignado a la mision | DroneAcuatico(id:String, modelo:String, bateria:int, disponible:boolean, zona:String) | Debe tener disponible == true y bateria >= 35% | Si |
| puntoPartida | Lugar donde inicia la mision | String | No puede estar vacio | Si |
| puntoLlegada | Lugar donde finaliza la mision | String | No puede estar vacio ni ser igual a puntoPartida | Si |
| tipoCarga | Tipo de carga que transporta el drone | Enum(MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO) | Debe ser uno de los 3 valores definidos | Si |

# DATOS DE SALIDA

| Nombre | Descripción | Tipo de campo | Reglas / Aplicación | Obligatorio |
|---|---|---|---|---|
| codigoMision | Identificador unico de la mision creada | String | Generado automaticamente por el sistema, ej. "M-101" | Si |
| estadoInicial | Estado con el que nace la mision | Enum(PENDIENTE, EN_TRANSITO, ENTREGADA, FALLIDA) | Siempre inicia en PENDIENTE | Si |

# FLUJO BÁSICO

| Paso | Actor | Descripción | Excepciones |
|---|---|---|---|
| 1 | Operador Hidrico | Selecciona un drone disponible de la flota. | — |
| 2 | Operador Hidrico | Ingresa el punto de partida, el punto de llegada y el tipo de carga. | — |
| 3 | Sistema | Valida que el drone no tenga ya una mision activa (PENDIENTE o EN_TRANSITO). | FA-01 |
| 4 | Sistema | Construye la mision con estado PENDIENTE y le asigna un codigo unico. | FA-02 |
| 5 | Sistema | Confirma el registro y retorna el codigo de mision generado al operador. | — |

# FLUJO ALTERNO

| Paso | Actor | Descripción | Excepciones |
|---|---|---|---|
| FA-01 | Sistema | Si el drone seleccionado ya tiene una mision activa, el sistema rechaza la asignacion y muestra: "El drone [id] ya tiene una mision activa." | Retorna al paso 1 |
| FA-02 | Sistema | Si puntoLlegada esta vacio o es igual a puntoPartida, el sistema rechaza la mision y muestra: "El punto de llegada es obligatorio y no puede ser igual al punto de partida." | Retorna al paso 2 |

Proyecto: AquaPort MVP | DOSW 2026 | Página 2

**Notas y comentarios:**
Esta funcionalidad corresponde al modelo `Mision.Builder` (reto 03) y a `ValidadorMision` (reto 04) implementados en `Piplup/src/main/java/com/eci/aquaport/`.

# ANEXOS
- Prototipos: pendiente (reto 08, mock del panel de monitoreo de flota).

# REGLAS DE NEGOCIO

| No. | Descripción |
|---|---|
| RN-01 | Un drone no puede tener mas de 1 mision activa simultanea. |
| RN-02 | Solo se pueden asignar drones con disponible == true y bateria >= 35%. |

# ABREVIATURAS

| Abreviatura | Significado |
|---|---|
| RF | Requisito Funcional |
| RN | Regla de Negocio |
| FA | Flujo Alterno |

# HISTORIAL DE REVISIÓN

| Elaborado por | Aprobado por | Fecha | Descripción y Justificación de Cambios |
|---|---|---|---|
| Equipo Piplup | | 23/09/2026 | Versión inicial del documento. |

Reto 8 — Manual de Identidad y UX/UI
- [ ] Identidad + mock con IA (3 estados)
- Evidencia:

Reto 9 — Agilismo y Jira
- [ ] Épica, feature, 3 HU, subtareas
- Evidencia:

Reto 10 — Diagrama de Casos de Uso
- [ ] Diagrama en docs/diagrama-cu-piplup.png
- Evidencia:

Reto 11 — Mocks con IA
- [ ] Proceso de 4 pasos documentado
- Evidencia:

Reto 12 — TDD: `ValidadorMision`
- [ ] Pruebas antes que código, ciclo Red-Green-Refactor
- Evidencia:

Reto 13 — JaCoCo
- [ ] Cobertura ≥80% en ValidadorMision
- Evidencia:

Reto 14 — SonarQube
- [ ] 0 bugs, 0 vulnerabilidades, deuda técnica 0
- Evidencia:
