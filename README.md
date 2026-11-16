# El Torneo de los Inmortales

Trabajo Práctico N° 2 – Paradigmas de Programación

Este proyecto implementa en Java el torneo eliminatorio por parejas planteado en la consigna. En este documento explicamos cómo organizamos el código, qué decisiones de diseño tomamos y por qué, qué valores elegimos para el combate y cómo se relacionan las pruebas con las reglas del enunciado.

El proyecto está escrito en Java 17 y las pruebas usan JUnit 5. La clase principal es `inmortales.TorneoDeLosInmortales`.

## Organización del código

Separamos el sistema en paquetes según la responsabilidad de cada parte:

| Paquete | Contenido |
|---|---|
| `combatientes` | `Combatiente` (abstracta), los tres tipos de la consigna, `CombatienteReactivo` (bonus), `CreadorCombatientes` y la interfaz `FabricaCombatientes` |
| `tecnicas` | `Tecnica` (abstracta), `TipoTecnica` y las cinco técnicas |
| `equipamiento` | `Equipamiento` (abstracta), `CategoriaEquipamiento` y los cuatro objetos |
| `escuelas` | `Escuela` |
| `torneo` | `Torneo`, `Combate`, `Pareja`, `Cruce` y `ReglasCombate` |
| `acontecimientos` | `Acontecimiento`, `TipoAcontecimiento` y la interfaz `ObservadorTorneo` |
| `presentacion` | `HeraldoImperial`, `CronistaDeLaSecta` y `RegistroPosicion` |
| `excepciones` | `TorneoInmortalesException` y las cuatro excepciones que derivan de ella |

## Decisiones de diseño

### Los tipos de combatiente

`Combatiente` es una clase abstracta que concentra todo lo común: identificador, nombre, vida y energía, técnicas, equipamiento y el registro de usos. Las diferencias entre tipos las resolvimos con tres métodos que cada subclase puede redefinir: `calcularDanioCausado`, `calcularDanioRecibido` y `calcularCostoEnergia`. En la clase base devuelven el valor sin cambios, y cada tipo redefine solo el que le corresponde. El Cultivador aumenta el daño que causa, el Monje reduce el que recibe y el Hechicero paga menos energía.

Las técnicas nunca calculan el daño por su cuenta. Cuando una técnica ofensiva se ejecuta, le pide al ejecutor que ataque (`ejecutor.atacar(objetivo, danioBase)`), y es el ejecutor quien aplica sus modificadores. Así, una misma técnica produce distinto daño según quién la usa, sin que la técnica conozca el tipo del combatiente y sin ningún `if` ni `instanceof` sobre el tipo. Por la misma razón, agregar un tipo nuevo no obliga a modificar las técnicas ni el torneo.

Dos combatientes son el mismo si tienen el mismo identificador, sin importar el nombre, por lo que `equals` y `hashCode` usan solo el `id`. Esto además nos permite usar combatientes dentro de un `Set` para detectar inscripciones repetidas.

### Las técnicas

Una técnica es solo su definición: nombre, costo de energía, tipo (ofensiva, defensiva o recuperación) y efecto. Los usos no se guardan en la técnica sino en cada combatiente, porque una misma técnica puede estar en varios combatientes a la vez. Dos técnicas son iguales si tienen el mismo nombre. Esto es importante para la regla del compañero: aunque dos escuelas creen cada una su propia instancia de `CorteDeLosMilAmaneceres`, el sistema las reconoce como la misma técnica.

### Escuelas y creación de combatientes

Una escuela es un objeto de configuración con nombre, una lista de técnicas y una lista de equipamiento. No tiene subclases: agregar una escuela nueva es crear otro objeto `Escuela` con otros valores. La escuela no determina el tipo del combatiente; cualquier tipo puede salir de cualquier escuela.

La creación está centralizada en `CreadorCombatientes`. Quien pide un combatiente solo indica el nombre del tipo, el nombre del combatiente y la escuela. El creador guarda un `Map` que asocia cada nombre de tipo con la fábrica que sabe construirlo, de modo que no hace falta un `switch` sobre el texto. Para agregar un tipo nuevo alcanza con registrar su fábrica con `registrarTipo`. El creador también asigna los identificadores en orden de creación y le pide a la escuela que prepare al combatiente.

Decidimos que el nombre del tipo se reconozca con o sin tildes y sin importar mayúsculas, porque el main de ejemplo de la consigna escribe "Hechicero del Vacío" con tilde y nos pareció razonable aceptar las dos formas.

### Equipamiento

Cada combatiente guarda su equipamiento en un `EnumMap` cuya clave es la categoría. Como un mapa no admite claves repetidas, la regla de "no dos de la misma categoría" se verifica de forma directa. Si se intenta equipar un cuarto objeto o una categoría repetida, se lanza `EquipamientoInvalidoException` sin modificar nada.

Los máximos de vida y energía se recalculan al equipar o quitar. Al equipar, el valor actual no sube; al quitar, se recorta al nuevo máximo si lo supera. Durante un combate no se puede cambiar el equipamiento.

Para que el equipamiento se pueda cambiar entre combates, `Torneo` ofrece dos formas de jugar: `iniciar()` juega todo lo que falte, y `jugarSiguienteRonda()` juega una sola ronda y devuelve el control. Entre una ronda y otra se puede modificar el equipamiento de las parejas que siguen en carrera, y el escenario 1 del main lo muestra.

### Parejas, combate y torneo

La pareja guarda a sus dos integrantes ordenados por identificador, porque dentro de la pareja actúa primero el de menor id. También lleva el registro de la regla del compañero: un `Map` que asocia cada técnica con el integrante que la usó primero en el combate actual. Ese registro se borra al comenzar cada combate.

`Combate` desarrolla un cruce asalto por asalto. Al comenzar cada asalto los combatientes en pie recuperan energía, juega primero la pareja de menor id, y después de cada acción se verifica si alguna pareja quedó sin integrantes en pie para terminar el combate en ese momento. Si se llega al límite de asaltos, gana la pareja con más vida total y, en empate, la de menor id. No usamos ningún valor aleatorio, así que el mismo estado inicial siempre produce el mismo resultado.

`Torneo` controla las inscripciones (rechaza parejas y combatientes repetidos con `InscripcionInvalidaException`) y solo permite comenzar con 4 u 8 parejas. Los cruces de la primera ronda se arman por orden de inscripción, y los de las siguientes, por el orden de los cruces anteriores. Cada ronda se guarda como una lista de `Cruce`, de modo que el orden de rondas y cruces queda registrado.

### Acontecimientos

El combate y el torneo comunican lo que pasa mediante acontecimientos: inicio de combate, acción realizada, eliminación, resultado del combate y proclamación. Cada acontecimiento guarda solo datos simples (números y textos) del momento en que ocurrió, nunca referencias a objetos que después cambian.

Los interesados implementan `ObservadorTorneo` y se suman con `agregarObservador`, sin modificar la lógica del torneo. El Heraldo Imperial es la única clase que escribe en pantalla, incluido lo que muestra el main. El Cronista de la Secta guarda el historial y arma la tabla de posiciones a partir de los resultados.

Elegimos representar los acontecimientos con una sola clase y un enum de tipos, con los datos de cada uno en un mapa. Esto nos permitió crearlos con métodos de fábrica (`Acontecimiento.eliminacion(...)`, etc.) y compararlos fácilmente para verificar que dos torneos iguales producen el mismo historial. La contrapartida es que los datos se leen por clave, por lo que fuimos cuidadosos en usar siempre las mismas claves.

## Patrones de diseño aplicados

- **Observer:** `Torneo` y `Combate` notifican los acontecimientos a todos los `ObservadorTorneo` registrados (el Heraldo, el Cronista y los propios combatientes). Así se pueden sumar interesados nuevos sin tocar el torneo.
- **Factory:** `CreadorCombatientes` y la interfaz `FabricaCombatientes` centralizan la creación. El código que pide un combatiente no conoce su clase concreta.
- **Polimorfismo por redefinición de métodos:** los tipos de combatiente, las técnicas y los equipamientos redefinen métodos de su clase base en lugar de preguntar por el tipo.

## Colecciones utilizadas

| Dónde | Colección | Por qué |
|---|---|---|
| Técnicas del combatiente | `List` | Conserva el orden de la escuela, que se usa para desempatar |
| Usos por técnica | `Map<Tecnica, Integer>` | Registra cuántas veces cada combatiente usó cada técnica |
| Equipamiento | `EnumMap<CategoriaEquipamiento, Equipamiento>` | La clave impide repetir categoría |
| Regla del compañero | `Map<Tecnica, Combatiente>` en `Pareja` | Registra quién usó cada técnica en el combate actual |
| Parejas inscriptas | `List` | Conserva el orden de inscripción para la primera ronda |
| Parejas y combatientes inscriptos | `Set` | Evita inscripciones duplicadas |
| Rondas | `List<List<Cruce>>` | Conserva el orden de las rondas y de los cruces dentro de cada una |
| Historial del Cronista | `List` | Conserva el orden en que ocurrieron los acontecimientos |
| Tabla de posiciones | `Map` y luego `List` ordenada con un `Comparator` | Una fila por pareja, ordenada por ronda, vida total e identificador |

## Valores elegidos y justificación

La consigna deja estos valores a criterio del grupo. Los definimos buscando que las técnicas valgan la pena frente al ataque básico, que ningún tipo domine claramente a los otros y que la mayoría de los combates terminen por eliminación.

### Reglas generales

| Valor | Elegido | Justificación |
|---|---|---|
| Vida inicial | 100 | Es el valor que sugiere la consigna y facilita leer los porcentajes de daño. |
| Energía inicial | 50 | La consigna sugiere 40, pero con ese valor un combatiente podía usar una sola técnica cara al comienzo y después pasaba varios asaltos con ataques básicos. Con 50 puede usar dos técnicas caras o tres baratas en los primeros asaltos, y el combate arranca más activo. |
| Energía por asalto | 5 | Permite volver a usar una técnica cada dos o tres asaltos. Con valores mayores la energía dejaba de ser un recurso a administrar. |
| Límite de asaltos | 20 | En los diez combates del main, siete terminan por eliminación entre los asaltos 6 y 18. Los tres que llegan al límite son entre parejas con monjes que se curan, que de otro modo se alargarían demasiado. |
| Ataque básico | 10 de daño | Es la mitad de una técnica común: sirve cuando no hay energía, pero conviene usar técnicas cuando se puede. |
| Usos por técnica | 5 por torneo | Lo fija la consigna. |
| Daño recibido mínimo | 1 | Evita que la suma de armadura, tipo y defensa anule por completo un golpe. |

### Tipos de combatiente

| Tipo | Efecto | Justificación |
|---|---|---|
| Cultivador de la Espada | +25% de daño causado | Es el tipo ofensivo; le dimos un porcentaje algo mayor porque su ventaja solo cuenta cuando ataca. |
| Monje del Cuerpo Celestial | −20% de daño recibido | Su ventaja actúa en todos los golpes que recibe, incluidos los ataques básicos, por eso usamos un porcentaje menor que el del Cultivador. |
| Hechicero del Vacío | −20% del costo de energía | Con este descuento puede usar aproximadamente una técnica más por combate que los otros tipos, sin volverse el más fuerte. |

### Técnicas

| Técnica | Tipo | Costo | Efecto | Justificación |
|---|---|---|---|---|
| Corte de los Mil Amaneceres | Ofensiva | 10 | 20 de daño | Ofensiva económica: duplica el ataque básico por poca energía.|
| Rugido de la Bestia Primordial | Ofensiva | 25 | 35 de daño | Es el ataque más poderoso, pero con la energía inicial solo se puede usar dos veces seguidas. |
| Dominio de la Nada | Ofensiva | 15 | 15 de daño y −10 de energía al rival | Hace menos daño que el Corte, pero debilita al rival quitándole energía para sus técnicas. |
| Muro del Cuerpo Dorado | Defensiva | 12 | −30% de daño recibido durante el resto del combate | Barata porque el criterio de acción la usa una sola vez por combate. |
| Meditación del Inmortal | Recuperación | 15 | +25 de vida, sin superar el máximo | Recupera un poco más de lo que quita un Corte, así que puede sostener a un combatiente pero no lo hace invencible. |

### Equipamiento

| Objeto | Categoría | Efecto | Justificación |
|---|---|---|---|
| Espada Celestial | Arma | +5 de daño por golpe | Se nota más en los golpes débiles (un 50% en el ataque básico) que en los fuertes. |
| Armadura Dorada | Armadura | −4 de daño por golpe recibido | Es simétrica a la espada pero un poco menor, porque se combina con la reducción del Monje y con el Muro. |
| Anillo Espiritual | Anillo | +5 de energía máxima | Un asalto extra de recuperación al comienzo del combate, sin cambiar demasiado el ritmo. |
| Sello del Fénix | Sello | +30 de vida máxima | Equivale a resistir un golpe fuerte más; es el objeto más defensivo. |

El orden en que se aplican los modificadores es fijo para que el resultado sea siempre el mismo. Para el daño causado se suma primero el equipamiento y después se aplica el tipo. Para el daño recibido se resta primero el equipamiento, después se aplica el tipo y por último la defensa activa (Muro).

## Criterio de acción

Usamos el criterio por defecto de la consigna, en este orden:

1. Una técnica de recuperación, si al combatiente le falta vida.
2. Una técnica defensiva, si no tiene una activa.
3. La técnica ofensiva con más usos restantes.
4. Si ninguna es posible (por energía, por usos o por la regla del compañero), un ataque básico.

En empate se elige la primera según el orden de la escuela. Para ataques y técnicas ofensivas, el objetivo es el rival en pie con menos vida actual y, en empate, el de menor identificador. Las técnicas defensivas y de recuperación se aplican al propio ejecutor.

## Bonus: combatiente reactivo

Implementamos el combatiente reactivo como un tipo más (`CombatienteReactivo`), que se crea igual que los demás desde `CreadorCombatientes`.

Todos los combatientes son observadores del torneo: al inscribir una pareja, el torneo registra a sus dos integrantes. En la clase base el método `actualizar` no hace nada, y el reactivo lo redefine. Sus reglas son:

- **Activación:** cuando recibe la eliminación de su compañero, siempre que él siga en pie.
- **Efecto:** su próximo ataque (básico o técnica ofensiva) hace un 50% más de daño.
- **Finalización:** la potencia se consume con ese ataque o se pierde al terminar el combate. Si en su turno usa una técnica defensiva o de recuperación, la conserva para el siguiente ataque.

## Pruebas

El proyecto tiene 122 pruebas JUnit 5 en la carpeta `test`. Las pruebas de combate y de torneo verifican los resultados mediante el estado de los objetos o el historial del Cronista, nunca leyendo la pantalla. La única excepción es `HeraldoTest`, que prueba justamente lo que muestra el Heraldo.

En las pruebas usamos un combatiente auxiliar (`Utiles.CombatienteDePrueba`) con vida, energía y daño configurables. Nos permite armar situaciones exactas, como un combatiente que cae de un solo golpe, sin depender de los modificadores de los tipos reales.

### Qué prueba cubre cada regla

**Sección 4: Equipamiento**

| Regla | Prueba |
|---|---|
| Hasta tres equipamientos, consultables | `CombatienteTest.hastaTresYConsultables`, `CombatienteTest.equipamientosSoloLectura` |
| Un cuarto objeto se rechaza sin cambios | `CombatienteTest.cuartoObjetoRechazado`, `EscuelaTest.maximoTresEquipamientos` |
| Categoría repetida se rechaza sin cambios | `CombatienteTest.categoriaRepetidaRechazada`, `EscuelaTest.categoriaRepetida` |
| Los efectos se acumulan | `CombatienteTest.efectosSeAcumulan` |
| Equipar y quitar antes del torneo y entre combates, no durante | `CombatienteTest.equipoSoloFueraDeCombate` |
| Quitar algo que no se lleva se rechaza | `CombatienteTest.quitarInexistente` |
| Equipar no sube el valor actual | `CombatienteTest.equiparNoSubeValorActual` |
| Al quitar, el valor actual se recorta | `CombatienteTest.quitarSelloRecortaVida`, `CombatienteTest.quitarAnilloRecortaEnergia` |

**Sección 5: Parejas y torneo**

| Regla | Prueba |
|---|---|
| Ids de combatientes en orden de creación | `EscuelaTest.idsEnOrdenDeCreacion` |
| Pareja con identificador y dos integrantes distintos | `ParejaTest.integrantesOrdenados`, `ParejaTest.dosDistintos`, `ParejaTest.igualdadPorId` |
| Pareja registrada dos veces | `TorneoTest.parejaDuplicada` |
| Combatiente inscripto dos veces | `TorneoTest.combatienteDuplicado` |
| Solo 4 u 8 parejas | `TorneoTest.cantidadInvalida`, `TorneoTest.masDeOcho`, `TorneoTest.cuatroParejas` |
| Cruces de la primera ronda por inscripción | `TorneoTest.crucesPrimeraRonda` |
| Rondas siguientes por orden de cruces | `TorneoTest.crucesRondasSiguientes` |
| Una pareja pierde cuando caen sus dos integrantes | `ParejaTest.enPie`, `CombateTest.terminaCuandoUnaParejaCae` |
| El torneo termina con una sola pareja | `TorneoTest.terminaConUnaSolaPareja`, `TorneoTest.proclamacion` |
| Tabla de posiciones ordenada | `CronistaTest.tablaPorRondaYVida`, `CronistaTest.tablaDesempateIdentificador`, `CronistaTest.tablaUsaUltimoCombate`, `TorneoTest.tablaDelTorneoOrdenada` |

**Sección 6: Reglas del combate**

| Regla | Prueba |
|---|---|
| Empieza la pareja de menor id y dentro de ella el de menor id | `CombateTest.ordenDeTurnos` |
| Cada integrante en pie realiza una acción por asalto | `CombateTest.cadaIntegranteUnaAccionPorAsalto` |
| El combate termina si una pareja queda sin integrantes | `CombateTest.terminaCuandoUnaParejaCae` |
| Ataque básico sin costo, sin límite y fuera de la regla del compañero | `CombatienteTest.ataqueBasico`, `ParejaTest.ataqueBasicoNoReserva` |
| La técnica requiere energía y usos | `CombatienteTest.sinEnergiaNoEjecuta`, `CombatienteTest.sinUsosNoEjecuta` |
| Criterio de acción y desempate | `CriterioDeAccionTest.recuperacionPrimero`, `sinFaltaDeVidaNoRecupera`, `conDefensaActivaPasaAOfensiva`, `ofensivaConMasUsos`, `empateOrdenDeEscuela`, `saltaLoQueNoPuedePagar`, `ataqueBasicoCuandoNadaEsPosible` |
| Elección de objetivo | `CriterioDeAccionTest.objetivoConMenosVida`, `CriterioDeAccionTest.objetivoDesempateMenorId` |
| Un caído no actúa ni es objetivo | `CombateTest.caidoNoActuaNiEsObjetivo`, `CombatienteTest.caidoNoActua`, `CriterioDeAccionTest.caidoNoEsObjetivo` |
| Energía por asalto sin superar el máximo | `CombateTest.energiaPorAsalto` |
| Vida y energía al máximo al comenzar cada combate, incluidos los caídos | `CombateTest.entreCombates`, `CombateTest.caidosVuelvenAlSiguienteCombate`, `CombatienteTest.caidosSeRecuperanAlIniciarCombate` |
| Regla del compañero | `ParejaTest.companeroBloqueado`, `ParejaTest.reglaConInstanciasDistintas`, `ParejaTest.companeroLibreSiAgotaUsos`, `ParejaTest.companeroLibreSiCae`, `ParejaTest.restriccionTerminaConElCombate`, `CriterioDeAccionTest.reglaDelCompaneroAtaqueBasico` |
| Cinco usos por torneo que se conservan entre combates | `CombatienteTest.usosSeConservanVidaYEnergiaSeRecuperan`, `CombatienteTest.sinUsosNoEjecuta`, `CombateTest.entreCombates` |
| Límite de asaltos: gana la de más vida total | `CombateTest.limiteGanaMasVidaTotal`, `ParejaTest.vidaTotal` |
| Límite de asaltos: en empate gana la de menor id | `CombateTest.limiteEmpateGanaMenorId` |
| El combate es determinista | `CombateTest.determinista`, `TorneoTest.reproducible` |

**Otras pruebas pedidas por la consigna**

| Pedido | Prueba |
|---|---|
| Una misma técnica daña distinto según el tipo del ejecutor | `CombatienteTest.mismaTecnicaDaniaDistintoSegunTipo` |
| Tipos y técnicas nuevos sin modificar lo existente | `CombatienteTest.tipoNuevoSinModificarTecnicas`, `EscuelaTest.registrarTipoNuevo`, `TecnicaTest.tecnicaNuevaSinModificarExistentes` |
| Nuevos interesados sin modificar el torneo | `TorneoTest.nuevosObservadores`, `CombateTest.variosObservadores` |
| Bonus: combatiente reactivo | `CombatienteReactivoTest` (todas) |

## Escenarios del programa principal

El main ejecuta tres escenarios, y todo lo que se muestra pasa por el Heraldo:

1. Un torneo de 4 parejas con el detalle de cada acción. Incluye un combatiente reactivo y un cambio de equipamiento entre rondas.
2. Un torneo de 8 parejas en versión resumida, que muestra solo inicios, eliminaciones y resultados.
3. Una serie de operaciones inválidas que el sistema rechaza: categoría de equipamiento repetida, cuarto objeto, pareja inscripta dos veces, combatiente en dos parejas y torneo con una cantidad de parejas no permitida.

Al final de cada torneo, el main le pide la tabla de posiciones al Cronista y el Heraldo la presenta. En `docs/salida_ejemplo.txt` se puede ver una ejecución completa.