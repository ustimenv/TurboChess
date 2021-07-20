
# turbochess

## contenido

~~~~
turbochess/pom.xml
turbochess/README.html
turbochess/src
turbochess/src/main
turbochess/src/main/java
turbochess/src/main/java/turbochess
turbochess/src/main/java/turbochess/service
turbochess/src/main/java/turbochess/service/participant
turbochess/src/main/java/turbochess/service/participant/ParticipantException.java
turbochess/src/main/java/turbochess/service/participant/ParticipantServiceImp.java
turbochess/src/main/java/turbochess/service/participant/ParticipantService.java
turbochess/src/main/java/turbochess/service/friendship
turbochess/src/main/java/turbochess/service/friendship/FriendshipServiceImp.java
turbochess/src/main/java/turbochess/service/friendship/FriendshipException.java
turbochess/src/main/java/turbochess/service/friendship/FriendshipService.java
turbochess/src/main/java/turbochess/service/game
turbochess/src/main/java/turbochess/service/game/GameServiceImp.java
turbochess/src/main/java/turbochess/service/game/GameService.java
turbochess/src/main/java/turbochess/service/game/GameException.java
turbochess/src/main/java/turbochess/service/room
turbochess/src/main/java/turbochess/service/room/RoomServiceImp.java
turbochess/src/main/java/turbochess/service/room/RoomService.java
turbochess/src/main/java/turbochess/service/room/RoomException.java
turbochess/src/main/java/turbochess/service/bet
turbochess/src/main/java/turbochess/service/bet/BetServiceImp.java
turbochess/src/main/java/turbochess/service/bet/BetException.java
turbochess/src/main/java/turbochess/service/bet/BetService.java
turbochess/src/main/java/turbochess/AppConfig.java
turbochess/src/main/java/turbochess/repository
turbochess/src/main/java/turbochess/repository/ParticipantRepository.java
turbochess/src/main/java/turbochess/repository/BetRepository.java
turbochess/src/main/java/turbochess/repository/FriendshipRepository.java
turbochess/src/main/java/turbochess/repository/RoomRepository.java
turbochess/src/main/java/turbochess/repository/GameRepository.java
turbochess/src/main/java/turbochess/configurations
turbochess/src/main/java/turbochess/configurations/WebConfiguration.java
turbochess/src/main/java/turbochess/configurations/IwUserDetailsService.java
turbochess/src/main/java/turbochess/configurations/WebSocketConfig.java
turbochess/src/main/java/turbochess/configurations/SecurityConfig.java
turbochess/src/main/java/turbochess/configurations/LoginSuccessHandler.java
turbochess/src/main/java/turbochess/configurations/WebSocketSecurityConfig.java
turbochess/src/main/java/turbochess/configurations/StartupConfig.java
turbochess/src/main/java/turbochess/control
turbochess/src/main/java/turbochess/control/AdminController.java
turbochess/src/main/java/turbochess/control/UserController.java
turbochess/src/main/java/turbochess/control/RootController.java
turbochess/src/main/java/turbochess/control/WebSocketEventListener.java
turbochess/src/main/java/turbochess/control/JsonConverter.java
turbochess/src/main/java/turbochess/control/room
turbochess/src/main/java/turbochess/control/room/RoomMessagingController.java
turbochess/src/main/java/turbochess/control/room/RoomAPIController.java
turbochess/src/main/java/turbochess/control/room/RoomController.java
turbochess/src/main/java/turbochess/LocalData.java
turbochess/src/main/java/turbochess/Application.java
turbochess/src/main/java/turbochess/model
turbochess/src/main/java/turbochess/model/AdminMessage.java
turbochess/src/main/java/turbochess/model/Transferable.java
turbochess/src/main/java/turbochess/model/User.java
turbochess/src/main/java/turbochess/model/Friendship.java
turbochess/src/main/java/turbochess/model/room
turbochess/src/main/java/turbochess/model/room/Participant.java
turbochess/src/main/java/turbochess/model/room/Room.java
turbochess/src/main/java/turbochess/model/chess
turbochess/src/main/java/turbochess/model/chess/Game.java
turbochess/src/main/java/turbochess/model/chess/Move.java
turbochess/src/main/java/turbochess/model/chess/Player.java
turbochess/src/main/java/turbochess/model/chess/Bet.java
turbochess/src/main/java/turbochess/model/messaging
turbochess/src/main/java/turbochess/model/messaging/JoinRoomPacket.java
turbochess/src/main/java/turbochess/model/messaging/GameOverPacket.java
turbochess/src/main/java/turbochess/model/messaging/LeavePacket.java
turbochess/src/main/java/turbochess/model/messaging/ClientPacket.java
turbochess/src/main/java/turbochess/model/messaging/TextPacket.java
turbochess/src/main/java/turbochess/model/messaging/MovePacket.java
turbochess/src/main/java/turbochess/model/messaging/PacketType.java
turbochess/src/main/java/turbochess/model/messaging/BetPacket.java
turbochess/src/main/java/turbochess/model/messaging/GetGamePacket.java
turbochess/src/main/java/turbochess/model/messaging/CreateRoomPacket.java
turbochess/src/main/java/turbochess/model/messaging/EmptyPacket.java
turbochess/src/main/resources
turbochess/src/main/resources/application-externaldb.properties
turbochess/src/main/resources/img
turbochess/src/main/resources/img/chess.png
turbochess/src/main/resources/examples
turbochess/src/main/resources/examples/rankings.json
turbochess/src/main/resources/import.sql
turbochess/src/main/resources/application.properties
turbochess/src/main/resources/templates
turbochess/src/main/resources/templates/admin.html
turbochess/src/main/resources/templates/head.html
turbochess/src/main/resources/templates/room.html
turbochess/src/main/resources/templates/register_success.html
turbochess/src/main/resources/templates/favicon.ico
turbochess/src/main/resources/templates/rooms.html
turbochess/src/main/resources/templates/spect.html
turbochess/src/main/resources/templates/history.html
turbochess/src/main/resources/templates/game.html
turbochess/src/main/resources/templates/userlist.html
turbochess/src/main/resources/templates/frags
turbochess/src/main/resources/templates/frags/infoPerfil.html
turbochess/src/main/resources/templates/frags/chatPartida.html
turbochess/src/main/resources/templates/frags/historial.html
turbochess/src/main/resources/templates/frags/bloqueEspectadores.html
turbochess/src/main/resources/templates/frags/barraLateral.html
turbochess/src/main/resources/templates/frags/barraSuperior.html
turbochess/src/main/resources/templates/frags/footer.html
turbochess/src/main/resources/templates/frags/fotoPerfil.html
turbochess/src/main/resources/templates/frags/chat.html
turbochess/src/main/resources/templates/othersProfile.html
turbochess/src/main/resources/templates/ranks.html
turbochess/src/main/resources/templates/login.html
turbochess/src/main/resources/templates/error.html
turbochess/src/main/resources/templates/index.html
turbochess/src/main/resources/templates/chatroom.html
turbochess/src/main/resources/templates/adminAux.html
turbochess/src/main/resources/templates/user.html
turbochess/src/main/resources/templates/signup_form.html
turbochess/src/main/resources/static
turbochess/src/main/resources/static/js
turbochess/src/main/resources/static/js/chess.js
turbochess/src/main/resources/static/js/app.js
turbochess/src/main/resources/static/js/libs
turbochess/src/main/resources/static/js/libs/chessboard-1.0.0.min.js
turbochess/src/main/resources/static/js/libs/bootstrap.js.map
turbochess/src/main/resources/static/js/libs/bootstrap.bundle.js
turbochess/src/main/resources/static/js/libs/jquery-3.4.1.min.js
turbochess/src/main/resources/static/js/libs/bootstrap.bundle.js.map
turbochess/src/main/resources/static/js/libs/sockjs.min.js
turbochess/src/main/resources/static/js/libs/bootstrap.bundle.min.js
turbochess/src/main/resources/static/js/libs/bootstrap.js
turbochess/src/main/resources/static/js/libs/stomp.min.js
turbochess/src/main/resources/static/js/libs/bootstrap.min.js
turbochess/src/main/resources/static/js/libs/bootstrap.esm.js.map
turbochess/src/main/resources/static/js/libs/bootstrap.esm.min.js.map
turbochess/src/main/resources/static/js/libs/bootstrap.esm.js
turbochess/src/main/resources/static/js/libs/bootstrap.min.js.map
turbochess/src/main/resources/static/js/libs/bootstrap.esm.min.js
turbochess/src/main/resources/static/js/libs/bootstrap.bundle.min.js.map
turbochess/src/main/resources/static/js/history.js
turbochess/src/main/resources/static/js/others.js
turbochess/src/main/resources/static/js/tablero.js
turbochess/src/main/resources/static/img
turbochess/src/main/resources/static/img/cheer.jpg
turbochess/src/main/resources/static/img/partidaPlaceholder.png
turbochess/src/main/resources/static/img/tick.jpg
turbochess/src/main/resources/static/img/chesspieces
turbochess/src/main/resources/static/img/chesspieces/wikipedia
turbochess/src/main/resources/static/img/chesspieces/wikipedia/bQ.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/bR.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/wK.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/bK.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/wP.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/wQ.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/bB.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/wN.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/bP.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/wR.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/bN.png
turbochess/src/main/resources/static/img/chesspieces/wikipedia/wB.png
turbochess/src/main/resources/static/img/save.jpg
turbochess/src/main/resources/static/img/refresh.png
turbochess/src/main/resources/static/img/chess.png
turbochess/src/main/resources/static/img/surrender.jpg
turbochess/src/main/resources/static/img/kick.jpg
turbochess/src/main/resources/static/img/goodbye.png
turbochess/src/main/resources/static/css
turbochess/src/main/resources/static/css/bootstrap-reboot.css.map
turbochess/src/main/resources/static/css/bootstrap.rtl.css
turbochess/src/main/resources/static/css/bootstrap-utilities.min.css.map
turbochess/src/main/resources/static/css/bootstrap-utilities.css.map
turbochess/src/main/resources/static/css/chessboard-1.0.0.min.css
turbochess/src/main/resources/static/css/profile.css
turbochess/src/main/resources/static/css/bootstrap.rtl.css.map
turbochess/src/main/resources/static/css/index.css
turbochess/src/main/resources/static/css/bootstrap-grid.rtl.css
turbochess/src/main/resources/static/css/bootstrap-grid.rtl.min.css.map
turbochess/src/main/resources/static/css/room.css
turbochess/src/main/resources/static/css/bootstrap-grid.min.css.map
turbochess/src/main/resources/static/css/bootstrap.rtl.min.css
turbochess/src/main/resources/static/css/bootstrap-reboot.rtl.css
turbochess/src/main/resources/static/css/bootstrap-grid.css.map
turbochess/src/main/resources/static/css/estilos.css
turbochess/src/main/resources/static/css/bootstrap-utilities.min.css
turbochess/src/main/resources/static/css/bootstrap-reboot.min.css.map
turbochess/src/main/resources/static/css/bootstrap-utilities.rtl.min.css
turbochess/src/main/resources/static/css/bootstrap-reboot.min.css
turbochess/src/main/resources/static/css/bootstrap-utilities.rtl.min.css.map
turbochess/src/main/resources/static/css/bootstrap-grid.css
turbochess/src/main/resources/static/css/bootstrap-utilities.css
turbochess/src/main/resources/static/css/bootstrap.css
turbochess/src/main/resources/static/css/bootstrap-reboot.rtl.min.css
turbochess/src/main/resources/static/css/bootstrap.rtl.min.css.map
turbochess/src/main/resources/static/css/bootstrap-reboot.rtl.css.map
turbochess/src/main/resources/static/css/bootstrap-grid.min.css
turbochess/src/main/resources/static/css/bootstrap-utilities.rtl.css
turbochess/src/main/resources/static/css/bootstrap.css.map
turbochess/src/main/resources/static/css/bootstrap.min.css
turbochess/src/main/resources/static/css/bootstrap-grid.rtl.min.css
turbochess/src/main/resources/static/css/bootstrap-grid.rtl.css.map
turbochess/src/main/resources/static/css/bootstrap-reboot.rtl.min.css.map
turbochess/src/main/resources/static/css/bootstrap-utilities.rtl.css.map
turbochess/src/main/resources/static/css/bootstrap-reboot.css
turbochess/src/main/resources/static/css/bootstrap.min.css.map
turbochess/src/main/resources/application-default.properties
turbochess/src/test
turbochess/src/test/karate-ui
turbochess/src/test/karate-ui/login.feature.orig
turbochess/src/test/karate-ui/login.feature
turbochess/src/test/karate-ui/message.feature.orig
turbochess/src/test/karate-ui/message.feature
turbochess/src/test/java
turbochess/src/test/java/karate-config.js.orig
turbochess/src/test/java/karate
turbochess/src/test/java/karate/KarateTests.java
turbochess/src/test/java/karate/KarateTests.java.orig
turbochess/src/test/java/karate/login
turbochess/src/test/java/karate/login/login1.feature
turbochess/src/test/java/karate/login/LoginRunner.java
turbochess/src/test/java/karate/login/login1.feature.orig
turbochess/src/test/java/karate/login/login2.feature
turbochess/src/test/java/karate/register
turbochess/src/test/java/karate/register/register1.feature
turbochess/src/test/java/logback.xml
turbochess/src/test/java/logback.xml.orig
turbochess/src/test/java/karate-config.js
~~~~

## Corrección

Esta corrección no forma parte del enunciado del examen - pero te puede ser útil a la hora de realizarlo, y sirve de justificación a la nota final (último apartado de la corrección).

### entrega

* Seguís teniendo muchos recursos externos. Cuento 17 ocurrencias de "cdnjs". Os pedí hacerlos locales - y describirlos, y sus licencias, en el leeme.html. Si no conseguís que algunos sean locales, podríais haber preguntado cómo hacerlo (pista: en el .css de fontawesome hay URLs que podrían hacerse también locales...)

* Vuestro leeme.html se llama `README.html`, y no menciona ningún recurso externo. Aunque me consta que usais al menos 4 librerías JS que no son vuestras: `chess`, `chessboard`, y luego `socks`, `stomp`, `jquery`, y alguna más.

* Seguís sin carpeta `data/`

### funcionalidad

* Vuestra aplicación no parece tener en cuenta seguridad:

    + es posible para un jugador enviar una petición a `/api/create_room` ó `/api/join_room` como cualquier otro jugador - basta con modificar los valores del formulario antes de enviarlos.

    + también puedo llamar a `/api/game_over` en cualquier momento, con cualquier usuario y habitación. Y puedo decir que he ganado o perdido y no lo comprobáis.

    + en general, todos vuestros `clientPacket.getFrom()` son falsificables por un cliente. ¿Porqué no usais la información de la sesión, que *no* es fácilmente falsificable?

    + es posible apostar como cualquier jugador, en calquier habitación, cualquier cantidad positiva que quepa en un int. 

    + no comprobáis en el servidor que los movimientos sean válidos, por lo que hacer trampas debería resultar sencillo. 

* No hay ninguna garantía de que `nextRoomCode` vaya a producir códigos únicos. Hacer que sea `synchronized` no es especialmente útil en código de servidor y cuando puede reiniciarse el mismo. Lo único seguro sería persistir códigos en la BD y verificar-no-duplicado de forma transaccional antes de concluir que el código es válido.

* Si hay un error, devolver `null` no es especialmente útil. Si el error es del cliente, hay códigos para indicarlo (empiezan por 4). Si es del servidor, empiezan por 5. Seguir el estándar es mejor que inventarse estándares nuevos.

* Los nuevos jugadores empiezan con ELO 1200, no ELO 0. En juegos con ELO, la ELO también puede disminuir si pierdes. Hay un `updateScoreOnVictory`, pero no un `updateScoreOnDefeat`. Y además, entiendo que también se puede actualizar la ELO con tablas (normalmente vale +1/2 punto). 

* Ésto falla por problemas de sintaxis SQL:
~~~
    ERROR 322597 --- [io-8080-exec-10] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.springframework.dao.InvalidDataAccessResourceUsageException: could not prepare statement; SQL [delete from bet cross join participant participan1_ where room_code=?]; nested exception is org.hibernate.exception.SQLGrammarException: could not prepare statement] with root cause

org.h2.jdbc.JdbcSQLSyntaxErrorException: Syntax error in SQL statement "DELETE FROM BET CROSS[*] JOIN PARTICIPANT PARTICIPAN1_ WHERE ROOM_CODE=?"; SQL statement:
delete from bet cross join participant participan1_ where room_code=? [42000-200]

 	at turbochess.service.bet.BetServiceImp.deleteRoomBets(BetServiceImp.java:31) ~[classes/:na]
~~~

* El final de la partida no se detecta de forma automática.

### diseño

* En `User.java`, usais atributos con `_`. En Java, se usaría `matchesLost` en lugar de `matches_lost` - y de hecho, hibernate/JPA te crearía una tabla `matches_lost` para ese atributo. 

* Seguís sin incluir pruebas de Karate-UI que demuestren que se puede realizar funcionalidad básica.

### nota

Partís de una nota base de 5.0: es posible hacer lo mínimo, pero la experiencia de usuario deja mucho que desear, y la funcionalidad es muy escasa. Abriré una post-entrega para subir nota que se cerrará el 21 de julio; las actas se cierran el 28 de julio.

Mejorad la funcionalidad para subir la nota:

- tapad los agujeros de seguridad para subir del 6.0; en particular, no podéis fiaros de que las cosas vienen de quien dicen que vienen, tenéis que comprobarlo. Cualquier usuario puede apretar f12 en su navegador, abrir la consola, y reemplazar/editar su JS para enviar mensajes arbitrarios al servidor. Añadid también validación de jugadas en servidor.

- añadid relojes a los jugadores, y configuración de los relojes (tiempo máximo, tiempo extra por jugada) cuando se configura la partida. Si alguien se queda sin tiempo, y todavía no se ha acabado la partida, pierde automáticamente. Añadid tambén pruebas karate que demuestren que los tiempos funcionan. Con esto pasaríais al notable/sobresaliente.

- mejorad la estética y usabilidad en general (mejor administración, más fácil entrar en una partida, etcétera) para más nota.

## examen

Estas son las instrucciones del examen. Léelas con atención antes de nada, y pregunta cualquier duda sobre ellas cuanto antes. Después, verás las 6 preguntas de las que consta: A, B, C, D, E y F. No dejes ninguna pregunta sin contestar, aunque no consigas solucionarlas.

* Tienes acceso a Internet, pero *solamente* para consultar páginas o recursos que existían antes de comenzar el examen. Está *estrictamente prohibido* comunicarte con tus compañeros o con terceros. Puedes, por ejemplo, buscar documentación o ver preguntas y respuestas en StackOverflow. No puedes, por ejemplo, entrar en cualquier aplicación de mensajería o chat para comunicarte con terceros.

* Para tu entrega, usa exclusivamente los fuentes entregados con este enunciado. *No* uses fuentes descargados de GitHub, ni de ningún otro sitio sin consultarlo antes con el profesor.

* Recuerda que el objetivo de este examen no es mejorar la entrega, sino *demostrar que sabes bien cómo funciona*, y que te desenvuelves bien con los conceptos, herramientas y tecnologías vistas en la asignatura. 

* Si resuelves un ejercicio correctamente, no hace falta describir mucho cómo lo has conseguido: el código cambiado hablará por sí solo. Aun así, escribe por favor una frase en el fichero "markdown" (_tuproyecto.md_) del enunciado describiendo la idea de la solución.

* Si *no* te funciona algo, comenta, en el fichero "markdown" del enunciado (_tuproyecto.md_) qué has intentado, porqué crees que falla, y cómo lo intentarías solucionar si tuvieses más tiempo. Aquí sí es importante describir tu solución, ya que será la única forma en que podré valorar tus conocimientos sobre el tema.

### pregunta A

¿En qué partes de este proyecto has trabajado más? ¿En cuáles menos? Indica tu participación describiendo las 3 ó 4 partes más importantes de tu aplicación, y el porcentaje del total de cada parte al que consideras que has contribuído. Contesta directamente en el `.md`, debajo de este texto y antes de la siguiente pregunta.

Me he concentrado en todas cosas relacionadas a las salas:
He adaptado el código del ajedrez encontrado por un otro miembro.
He escrito el código JS (stomp para apuestas y mensajes, ajax
para manejar salas, animaciones de partidas pasadas en /user),
el los handlers en Room*Controller correspondientes.
He hecho las tablas Bet, Participant, Room y Game para conseguir persistence
de apuestas, jugadores, salas actualmente activas y partidas terminadas.

### pregunta B

Añadido linia messageToShow = message.text; para enseñar los contenidos de mensajes text. 

Añadi un botón "hacer trampas", que cuando sea pulsado por un jugador envíe un mensaje al servidor como si fuera *el otro jugador* y que se rinde. Puedes meter el nombre del otro jugador directamente en el código JS - sólo te pido demostrar que es posible, sin más que cambiar JS+html (=sin tocar nada del controlador), hacer trampas.
Tambien arreglé que las apuestas causaban en error sql. He cambiado endGame para que las apuestas, los participantes y la sala se borren correctamente.

### pregunta C

Usa un argumento `java.security.Principal principal` (ver https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#supported-method-arguments) en el método `sendMove` de tu `RoomMessagingController` para verificar que la jugada viene de quien dice venir. El nombre de usuario *real* está en `principal.getName()`; cógelo de ahí en lugar de fiarte de `clientPackage.getFrom()`.
Hecho. En total tuve que cambiar tres linias: el import de Principal, argumento "Principal principal" y String usernameFrom = prinicipal.getName(). Claro que es la solución mejor que clientPacket.getFrom(). No lo he cambiado en otras funciones del controlador porque se hace de exactamente misma manera y no tengo el tiempo. 

### pregunta D

Genera los IDs de habitación usando 6 letras aleatorias (en lugar de que el primer ID generado sea siempre `AAAAAA`). Comprueba en la BD que no existe antes de asignarlo a nadie. Usa un bucle.
Hecho, todo el código responsable está en createRoom(). En app.js añadi una linia para alert el codigo generado para enseñarlo.
No tuve que hacer ninguna modificación en sql porque el query para contar salas con dado código se ha quedado de la version anterior, (en la cual intenté generar los códigos al azar).
La longitud inicial de los códigos es 3, y se prueba 2*permutations(longitud).
Esta solución no garantiza que todos los codigo de longitud L se hubiesan probados antes de aumentar la longitud hasta L+1,
es una aproximación 
### pregunta E

Pon el tablero de ajedrez centrado en la habitación, ocupando como poco el 50% del ancho de la ventana. Coloca los mensajes en el lado derecho.
he hecho el tablero mas grande, iba a poner un div vacio a su izquerida para centrarlo, pero no tuve el tiempo.
El tablero iba a hacer un flex-item, para enseñar el div vacío, el tablero y el chat en una fila.
### pregunta F

Indica, usando 1 línea por fichero, y justo detrás de esta pregunta en el .md, qué ficheros tendrías que cambiar o añadir para implementar un sistema de tiempos como el descrito en la sección de nota; y qué cambios o contenidos (muy por encima) tendrías que tocar/añadir en cada uno.
las modiciciones mencionados arriba.