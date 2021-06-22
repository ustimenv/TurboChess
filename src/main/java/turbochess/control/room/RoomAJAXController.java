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
import turbochess.model.messaging.MessagePacket;
import turbochess.model.messaging.ResponsePacket;
import turbochess.model.room.Participant;
import turbochess.model.room.Room;
import turbochess.service.room.RoomException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Controller
public class RoomAJAXController extends RoomController{
    private static Logger log = LogManager.getLogger(RoomAJAXController.class);

    @RequestMapping(value = "/api/create_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public ResponsePacket createRoom(@RequestBody MessagePacket packet){
        log.info(format("[create room]: received packet{0}", packet));

        try{
            User userFrom = getUserByUsername(packet.getFrom());
            // code generation guarantees room code is unique, if implementation changes will have to check for room's existence
            Room createdRoom = roomService.createRoom(nextRoomCode(), Integer.parseInt(packet.getPayload()));
            Participant p = new Participant(createdRoom, userFrom);
            p.setRole(roomService.assignRole(createdRoom.getCode(), p));
            p.setColour(Participant.Colour.WHITE);
            roomService.joinRoom(createdRoom.getCode(), p);
            entityManager.persist(p);
            log.info(format("Room {0} created successfully by {1}", createdRoom.getCode(), p.getUser().getUsername()));
            return new ResponsePacket(p.getColourString(), createdRoom.getCode());
        } catch(RoomException e){
            log.error(format("[create room]: failed to create room {0}", (Object) e.getStackTrace()));
            return null;		                    // TODO change to smth like 505?
        }
    }

    @RequestMapping(value = "/api/join_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public ResponsePacket joinRoom(@RequestBody MessagePacket packet){
        log.info(format("[join room]: received packet{0}", packet));
        try{
            User userFrom = getUserByUsername(packet.getFrom());
            Room room = roomService.getRoomByCode(packet.getPayload());

            if(!room.isBelowCapacity())	throw new RoomException(format("Capacity exceeded for room {0)", room.getCode()));

            Participant p = new Participant(room, userFrom);
            p.setRole(roomService.assignRole(room.getCode(), p));

            if(p.getRole() == Participant.Role.OBSERVER){
                p.setColour(Participant.Colour.NONE);
            } else{
                p.setColour(Participant.Colour.BLACK);
            }
            roomService.joinRoom(room.getCode(), p);
            entityManager.persist(p);
            log.info(format("User {0} joined room {1} successfully", userFrom.getUsername(), room.getCode()));
            return new ResponsePacket(p.getColourString(), "oki");
        } catch(RoomException e){
            log.error(format("[join room]: User failed to join room. Packet:\n {0}\n" +
                                                                    "Error:\n{1}", packet, e.getStackTrace()));
            return null;
        }
    }
    @RequestMapping(value = "/api/save_room", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    @Transactional
    public ResponsePacket saveRoom(@RequestBody MessagePacket packet){
        log.info(format("[save room]: received packet{0}", packet));
        try{
            String boardState = packet.getPayload();
            Room contextRoom = roomService.getRoomByCode(packet.getContext());
            User userFrom = getUserByUsername(packet.getFrom());
            Participant.Role role = getParticipantRole(userFrom, contextRoom);
            if(role != Participant.Role.PLAYER1)    // ie must be the owner of the room
                throw new RoomException(format("User {0} doesn't have have the permission to save room {1}",
                                                userFrom.getUsername(), contextRoom.getCode()));

            List<String> participantJSONs=new ArrayList<>();
            for(Participant p :getRoomParticipants(contextRoom)){
                participantJSONs.add(p.toJSON());
            }
            roomService.prepareAndSave(contextRoom.getCode(), boardState,
                                        participantJSONs.stream().map(Object::toString).collect(Collectors.joining(",")));
            log.info(format("Room {0} saved successfully by {1}", contextRoom.getCode(), userFrom.getUsername()));
            return new ResponsePacket("", "oki");
        } catch(RoomException e){
            log.error(format("[save room]: error--> {0}", (Object) e.getStackTrace()));
            return null;
        }
    }


//	@RequestMapping(value = "/api/place_bet", method=RequestMethod.POST, produces = "application/json")
//	@ResponseBody
//	@Transactional
//	public ResponsePacket placeBet(@RequestBody MessagePacket packet){
//		log.info(format("[place bet]: received packet{0}", packet));
//		String from =null;
//		int betAmount=-1;
//		try{
//			from = packet.getFrom();
//			betAmount = Integer.parseInt(packet.getPayload());
//			if(betAmount<=0)	throw new NumberFormatException("Invalid bet value: " + betAmount);
//			User userFrom = getUserByUsername(from);
//			Integer currentUserBalance = entityManager.createNamedQuery("User.getBalanceByUsername", Integer.class)
//					.setParameter("username", userFrom.getUsername())
//					.getSingleResult();
//
//			Room room = roomService.getRoomByCode(packet.getContext());
//			Participant p = getParticipantByUsernameAndRoom(userFrom, room);
//			// all the relevant variables initialised at this point
//			if(currentUserBalance == null || currentUserBalance < betAmount){
//				throw new RoomException(format("Insufficient ({0}) balance ({1}) for player {2}", currentUserBalance, betAmount, userFrom.getUsername()));
//			}
//			entityManager.createNamedQuery("User.removeCoins")
//			 			 .setParameter("username", userFrom.getUsername())
//						 .setParameter("amount", betAmount).executeUpdate();
//
//			log.info(format("Bet of {0} coins has been placed successfully by {1}", betAmount, p.getUser().getUsername()));
//			return new ResponsePacket("OKI");
//		} catch(RoomException | NumberFormatException e){
//			log.error(format("[place bet]: failed to place bet for participant {0} --> {1}", from, e.getMessage()));
//			return null;		// TODO change to 505 or smth
//		}
//	}
}
