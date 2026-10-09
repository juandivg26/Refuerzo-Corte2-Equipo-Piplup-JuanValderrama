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
 <img width="825" height="396" alt="imagen" src="https://github.com/user-attachments/assets/2df91030-4501-4365-bad5-66f0b4705f15" />

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
  | RF-03 Consultar estado de mision | Should Have | Mejora la trazabilidad; es secundaria frente al registro y consulta de flota, que son las operaciones principales del MVP. |
  | RNF-01 Rendimiento < 200ms | Should Have | Importa para la experiencia de uso, pero no bloquea el funcionamiento con una flota pequena. |
  | RNF-02 Cobertura >= 80% | Must Have | Es un criterio de aceptacion obligatorio del curso DOSW para aprobar el codigo de negocio (`ValidadorMision`); garantiza la deteccion de regresiones y la validez del sistema desde el MVP, no es opcional. |
  | RNF-03 Sin bugs/vulnerabilidades en Sonar | Must Have | Un codigo con bugs o vulnerabilidades criticas/bloqueantes no puede considerarse aprobado ni apto para entrega; es un requisito de calidad obligatorio desde el MVP, no se pospone. |
<img width="940" height="396" alt="imagen" src="https://github.com/user-attachments/assets/a9cb8608-ace7-4484-a3d0-644ab0ca1abc" />

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
| 3 | Sistema | Valida que el drone tenga bateria >= 35%, que la zona de destino sea una de las 5 zonas fijas del campus y que el drone no tenga ya una mision activa (PENDIENTE o EN_TRANSITO). | FA-01, FA-02, FA-03 |
| 4 | Sistema | Construye la mision con estado PENDIENTE y le asigna un codigo unico. | FA-04 |
| 5 | Sistema | Confirma el registro y retorna el codigo de mision generado al operador. | — |

# FLUJO ALTERNO

| Paso | Actor | Descripción | Excepciones |
|---|---|---|---|
| FA-01 | Sistema | Drone sin bateria suficiente: si la bateria del drone es menor a 35%, el sistema rechaza la asignacion y muestra: "El drone [id] tiene bateria insuficiente ([bateria]%). Minimo requerido: 35%." | Retorna al paso 1 |
| FA-02 | Sistema | Zona de destino invalida: si puntoLlegada no es una de las zonas fijas (Embalse Norte, Canal Central, Laguna Sur, Punto Ribereño Este, Laboratorio Hídrico), el sistema rechaza la mision y muestra: "La zona [puntoLlegada] no es una de las zonas fijas del campus." | Retorna al paso 2 |
| FA-03 | Sistema | Si el drone seleccionado ya tiene una mision activa, el sistema rechaza la asignacion y muestra: "El drone [id] ya tiene una mision activa." | Retorna al paso 1 |
| FA-04 | Sistema | Si puntoLlegada esta vacio o es igual a puntoPartida, el sistema rechaza la mision y muestra: "El punto de llegada es obligatorio y no puede ser igual al punto de partida." | Retorna al paso 2 |

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
| RN-03 | El punto de llegada debe ser una de las 5 zonas fijas del MVP. |

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
| Equipo Piplup | | 2026-10-06 | RNF-02 y RNF-03 reclasificados de Could Have/Won't Have a Must Have tras revision tecnica. |
<img width="928" height="368" alt="imagen" src="https://github.com/user-attachments/assets/1d18f1b7-ebac-4625-92ae-d53f385770a6" />

Reto 8 — Manual de Identidad y UX/UI
- [x] Identidad + mock con IA (3 estados)
- Evidencia:

  **Manual de Identidad:**

  **Paleta de colores:**
  - Azul profundo `#0B3C5D` — color primario (agua, confiabilidad)
  - Celeste `#1D9A9F` — color secundario/acento (tecnología, drones)
  - Verde menta `#4CD9B0` — éxito/disponibilidad
  - Ámbar `#F0A830` — advertencia (batería baja, misión pendiente)
  - Rojo coral `#E25C5C` — error/no disponible

  **Colores de estado del drone:**
  | Estado | Color | HEX |
  |---|---|---|
  | Disponible | Verde menta | `#4CD9B0` |
  | En misión | Azul | `#2F80ED` |
  | Recargando | Ámbar | `#F0A830` |
  | Mantenimiento | Gris | `#8A94A6` |
  | Fallo | Rojo coral | `#E25C5C` |

  **Tipografía:** Inter (bold/semibold para encabezados, regular para cuerpo). Monoespaciada **JetBrains Mono** para IDs de drones (`AR-01`) y códigos de misión (`M-101`).

  **Logo (concepto):** ícono de gota de agua combinado con hélice de dron, en azul profundo, acompañando el nombre "AquaPort" en Inter bold.

  **Tono de voz:** directo, técnico, orientado a la acción (ej. "El drone AR-01 ya tiene una misión activa"), sin jerga innecesaria.
  LINK DE MANUAL DE IDENTIDAD: https://canva.link/r2niavkec9cnzzu 
  **Panel de monitoreo de la flota acuática
  <img width="1866" height="882" alt="imagen" src="https://github.com/user-attachments/assets/9af67555-cdad-4658-86ae-c44ed94e6cf9" />


  **Heurísticas de Nielsen que cumple el mock:**
  | # | Heurística | Cómo se cumple en el panel |
  |---|---|---|
  | 1 | Visibilidad del estado del sistema | Cada drone muestra su estado con color y etiqueta, y su batería en %, sin clics adicionales. |
  | 2 | Coincidencia con el mundo real | Se usan los nombres reales de las zonas (Embalse Norte, Laguna Sur…) y términos del operador (misión, carga, batería). |
  | 4 | Consistencia y estándares | Los mismos colores de estado y la misma tarjeta se repiten para los 4 drones. |
  | 6 | Reconocer antes que recordar | ID, zona, batería y estado están visibles en la tarjeta; el operador no necesita recordarlos. |
  | 8 | Diseño estético y minimalista | El panel solo muestra lo necesario para decidir: ID, batería, estado y zona. |

<img width="922" height="375" alt="imagen" src="https://github.com/user-attachments/assets/f455d7f8-9686-4d67-909e-39f3a3f3aa48" />

Reto 9 — Agilismo y Jira
- [x] Épica, feature, 3 HU, subtareas
- Evidencia:
Link de Jira: https://mail-team-nyjtgqcj.atlassian.net/jira/software/projects/AQ/boards/71/backlog?atlOrigin=eyJpIjoiNzk3YzJiMTIwY2M0NGY2MzgxNDFlOWM3ZGExZDA4NWIiLCJwIjoiaiJ9
<img width="938" height="517" alt="imagen" src="https://github.com/user-attachments/assets/edee31f6-0736-4245-99e8-70d9d2e2ed35" />


Reto 10 — Diagrama de Casos de Uso
- [x] Diagrama en docs/diagrama-cu-piplup.png
- Evidencia:
<img width="700" height="274" alt="imagen" src="https://github.com/user-attachments/assets/8312f897-cc4e-4fc4-a385-aa215f022087" />

<img width="961" height="471" alt="imagen" src="https://github.com/user-attachments/assets/7b4b751c-236a-4ae8-9e93-c4d83f15a82e" />


Reto 11 — Mocks con IA
- [x] Proceso de 4 pasos documentado
- Evidencia:

  **Proceso de 4 pasos para generar mocks con IA:**

  **Paso 1 — Definir el contexto y los datos reales antes de escribir el prompt.**
  Antes de pedirle algo a la IA, se recopilaron los datos exactos que debía mostrar el mock: la paleta de colores y tipografía ya definidas en el Manual de Identidad (reto 08), y los datos reales de la flota de ejemplo (AR-01 a AR-04, con sus baterías y zonas, tomados del reto 01). Esto evita que la IA invente datos genéricos que no coincidan con el resto del proyecto.

  **Paso 2 — Redactar un prompt detallado y estructurado.**
  Se escribió un prompt especificando: identidad visual exacta (colores en HEX, tipografía), estructura de la pantalla (header, tarjetas resumen, lista de drones), datos exactos a mostrar por cada drone, y el estilo general (flat design, minimalista, sin decoraciones). Un prompt vago ("hazme un dashboard de drones") habría dado un resultado genérico sin coherencia con la identidad del proyecto.

  **Paso 3 — Generar con la herramienta de IA.**
  Se usó figma para generar el manual de identidad y el mock del panel de monitoreo, a partir del prompt del paso 2.

  **Paso 4 — Validar el resultado contra un criterio objetivo y ajustar.**
  El mock generado se contrastó contra 5 heurísticas de usabilidad de Nielsen (visibilidad del estado del sistema, coincidencia con el mundo real, consistencia, reconocer antes que recordar, diseño minimalista), documentado en el reto 08. Esto permitió verificar que el resultado de la IA no solo se viera bien, sino que fuera realmente usable, y detectar ajustes necesarios (ej. que los colores de estado coincidieran exactamente con los ya definidos en el código de `ConsultorFlota`).

  **Conclusión:** la IA acelera la generación visual, pero sin un prompt detallado con datos reales (paso 1-2) y sin una validación contra un criterio de usabilidad (paso 4), el resultado queda desconectado del resto del proyecto.

Reto 12 — TDD: `ValidadorMision`
- [x] Pruebas antes que código, ciclo Red-Green-Refactor
- Evidencia:

  **Ubicación del código:** `Piplup/src/main/java/com/eci/aquaport/ejercicio12/`
  (`DroneAcuatico.java`, `Mision.java`, `EstadoMision.java`, `TipoCarga.java`, `ValidadorMision.java`)
  **Ubicación de las pruebas:** `Piplup/src/test/java/com/eci/aquaport/ejercicio12/ValidadorMisionTest.java`

  **Paso a paso del ciclo TDD:**
  1. **Red:** se copió el modelo base (`DroneAcuatico`, `Mision`, `EstadoMision`, `TipoCarga`) desde `ejercicio4` al paquete `ejercicio12`, y se escribió `ValidadorMisionTest.java` con 8 pruebas que referencian métodos que aún no existían en `ValidadorMision` (`tieneBateriaSuficiente`, `validarPuntoLlegada`, `validarDroneDisponible`, `validarZona`) — el proyecto no compilaba, confirmando el estado Red.
  2. **Green:** se implementó cada método con la lógica mínima necesaria para que su prueba correspondiente pasara.
  3. **Refactor:** se integraron todas las reglas dentro del método `validar(Mision, List<Mision>)`, que ahora valida disponibilidad del drone, punto de llegada, zona y batería suficiente, además de la regla ya existente de "drone sin misión activa" (heredada de `ejercicio4`). Las 8 pruebas se mantuvieron pasando durante el refactor, confirmando que no se rompió el comportamiento.
  4. **Segundo ciclo (zona inválida como caso edge):** **Red** — se agregaron 7 pruebas (zona fuera de las 5 zonas fijas, zona fija válida, drone nulo, misión nula, zona de destino inválida dentro de `validar`, drone sin batería dentro de `validar`, drone con misión `ENTREGADA` que sí puede reasignarse); 2 fallaron porque `validarZona` solo revisaba nulo/vacío. **Green** — `validarZona` ahora valida contra el conjunto `ZONAS_VALIDAS` y `validar` revisa la zona del punto de llegada. Total: 15 pruebas en verde. El commit de pruebas (`test: ... (Red)`) es anterior al de código (`feat: ... (Green)`).

<img width="881" height="402" alt="imagen" src="https://github.com/user-attachments/assets/41afcb3b-8a9c-41df-aa35-44c396b3e18c" />

<img width="867" height="551" alt="imagen" src="https://github.com/user-attachments/assets/8a8fda4d-50b8-4062-9f50-1b4a047d6d7a" />


Reto 13 — JaCoCo
- [x] Cobertura ≥80% en ValidadorMision
- Evidencia:

  **Paso a paso:**
  1. El plugin `jacoco-maven-plugin` ya estaba configurado en el `pom.xml` (goals `prepare-agent` y `report`, atados a la fase `test`).
  2. Se corrió `mvn clean test`, lo que ejecuta las 15 pruebas de `ValidadorMisionTest` y genera automáticamente el reporte HTML en `target/site/jacoco/index.html`.
  3. Se abrió el reporte y se navegó hasta el paquete `com.eci.aquaport.ejercicio12` → clase `ValidadorMision`.

  **Resultado obtenido:**
  | Métrica | Primer ciclo (8 pruebas) | Segundo ciclo (15 pruebas) |
  |---|---|---|
  | Líneas | 92% | **100%** |
  | Instrucciones | 84% | **100%** |
  | Ramas (branches) | 70% | **91%** |

  La cobertura de líneas de `ValidadorMision` es del **100%**, superando el mínimo de 80% exigido por el reto. Las 15 pruebas (ver reto 12) cubren: batería suficiente/insuficiente, punto de llegada nulo/vacío, drone nulo/no disponible, zona vacía/no registrada/válida, misión nula, zona de destino inválida, drone sin batería, caso feliz, drone con misión activa y drone con misión entregada.

 
<img width="882" height="561" alt="imagen" src="https://github.com/user-attachments/assets/6cfed7cf-ddfb-4581-ac79-fb6e13617638" />
<img width="846" height="371" alt="imagen" src="https://github.com/user-attachments/assets/5324fe57-b932-4aab-8dfd-6a945b8fca94" />


Reto 14 — SonarQube
- [x] 0 bugs, 0 vulnerabilidades, deuda técnica 0
- Evidencia:

<img width="653" height="635" alt="imagen" src="https://github.com/user-attachments/assets/2066b18c-26da-47df-be0a-e0aabbb17324" />
<img width="868" height="401" alt="imagen" src="https://github.com/user-attachments/assets/a00c01a2-76c2-4cb6-8e35-c6fec8a184d7" />
