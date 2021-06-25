package turbochess.control.room;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import turbochess.model.User;
import turbochess.model.messaging.client.ClientPacket;
import turbochess.model.messaging.client.CreateRoomPacket;
import turbochess.model.messaging.client.JoinRoomPacket;
import turbochess.model.messaging.server.CreateRoomResponse;
import turbochess.model.messaging.server.JoinRoomResponse;
import turbochess.model.messaging.server.Response;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.participant.ParticipantException;
import turbochess.service.room.RoomException;

import static java.text.MessageFormat.format;

@Controller
public class RoomAPIController extends RoomController{
    private static Logger log = LogManager.getLogger(RoomAPIController.class);

    @RequestMapping(value = "/api/create_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public Response createRoom(@RequestBody CreateRoomPacket packet) throws ParticipantException{
        log.info(format("[create room]: received packet{0}", packet));

        Room createdRoom = null;
        User userFrom = null;
        try{
            userFrom = getUserByUsername(packet.getFrom());
            // code generation guarantees room code is unique, if implementation changes will have to check for room's existence
            createdRoom = roomService.createRoom(nextRoomCode(), Integer.parseInt(packet.getCapacity()));
            Participant p = participantService.createParticipant(createdRoom, userFrom);
            p.setRole(createdRoom.assignRole(p));
            p.setColour(Participant.Colour.WHITE);
            createdRoom.addParticipant(p);
            entityManager.persist(p);
            entityManager.persist(createdRoom);
            log.info(format("Room {0} created successfully by {1}", createdRoom.getCode(), p.getUser().getUsername()));
            return new CreateRoomResponse(createdRoom.getCode(), p.getColourString());
        } catch(RoomException e){
            log.error(format("[create room]: failed to create room {0}", (Object) e.getMessage()));
            return null;		                    // TODO change to smth like 505?
        } catch(ParticipantException e){
            return retrieveRoomStateForUser(createdRoom, userFrom);
        }
    }

    @RequestMapping(value = "/api/join_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public Response joinRoom(@RequestBody JoinRoomPacket packet) throws ParticipantException{
        log.info(format("[join room]: received packet{0}", packet));
        User userFrom = null;
        Room room = null;
        try{
            userFrom = getUserByUsername(packet.getFrom());
            room = roomService.getRoomByCode(packet.getRoomToJoin());
            if(participantService.isUserInRoom(room, userFrom)){
                return retrieveRoomStateForUser(room, userFrom);
            }

            if(!room.isBelowCapacity())	throw new RoomException(format("Capacity exceeded for room {0)", room.getCode()));

            Participant p = participantService.createParticipant(room, userFrom);
            p.setRole(room.assignRole(p));

            if(p.getRole() == Participant.Role.OBSERVER){
                p.setColour(Participant.Colour.NONE);
            } else{
                p.setColour(Participant.Colour.BLACK);
            }
            room.addParticipant(p);
            entityManager.persist(p);
            entityManager.persist(room);
            log.info(format("User {0} joined room {1} successfully", userFrom.getUsername(), room.getCode()));
            return new JoinRoomResponse(p.getColourString(), "", "0");
        } catch(RoomException e){
            log.error(format("[join room]: User failed to join room. Packet:\n {0}\n" +
                                                                    "Error:\n{1}", packet, e.getMessage()));
            return null;
        } catch(ParticipantException e){
            return retrieveRoomStateForUser(room, userFrom);
        }
    }

    private Response retrieveRoomStateForUser(Room room, User user) throws ParticipantException{
        // the only way we end here is if user is already present in the room, so disregard the exception
        log.info(format("[get room]: Retrieving room state for {0} for user {1}", room.getCode(), user.getUsername()));
        Participant p = participantService.getParticipantByUsernameAndRoom(room, user);
        return new JoinRoomResponse(p.getColourString(), room.getFen(), String.valueOf(p.getCurrentBet()));
    }

//    @RequestMapping(value = "/api/save_room", method=RequestMethod.POST, produces = "application/json")
//    @ResponseBody
//    @Transactional
//    public ResponsePacket saveRoom(@RequestBody MessagePacket packet){
//        log.info(format("[save room]: received packet{0}", packet));
//        try{
//            String boardState = packet.getPayload();
//            Room contextRoom = roomService.getRoomByCode(packet.getContext());
//            User userFrom = getUserByUsername(packet.getFrom());
//            Partic
//            Participant.Role role = getParticipantRole(userFrom, contextRoom);
//            if(role != Participant.Role.PLAYER1)    // ie must be the owner of the room
//                throw new RoomException(format("User {0} doesn't have have the permission to save room {1}",
//                                                userFrom.getUsername(), contextRoom.getCode()));
//
//            List<String> participantJSONs=new ArrayList<>();
//            for(Participant p :getRoomParticipants(contextRoom)){
//                participantJSONs.add(p.toJSON());
//            }
////            for(String s : participantJSONs){
////                System.out.println(s);
////            }
//            roomService.prepareAndSave(contextRoom.getCode(), boardState,
//                                        participantJSONs.stream().map(Object::toString).collect(Collectors.joining(",")));
//            log.info(format("Room {0} saved successfully by {1}", contextRoom.getCode(), userFrom.getUsername()));
//            return new ResponsePacket("", "oki");
//        } catch(RoomException e){
//            log.error(format("[save room]: error--> {0}", e.getMessage()));
//            return null;
//        }
//    }



}
