package com.ssafy.los.backend.controller.play;


import com.ssafy.los.backend.domain.entity.BattleRoom;
import com.ssafy.los.backend.dto.play.request.BattleRoomRegisterDto;
import com.ssafy.los.backend.service.play.BattleRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/battle-rooms")
public class BattleRoomController {

    private final BattleRoomService battleRoomService;

    private final RedisTemplate redisTemplate;

    // 대결방을 생성하기 위해 필요한 정보를 form에서 받아와 객체를 생성하였다.
    @PostMapping
    public ResponseEntity<?> createAndEnterBattleRoom(
            @RequestBody BattleRoomRegisterDto battleRoomRegisterDto) {
        log.info("대결방 등록 요청을 한 DTO = {}", battleRoomRegisterDto.toString());

        // 방을 생성 후 입장
        Map<String, Object> battleRoom = battleRoomService.createAndEnterBattleRoom(
                battleRoomRegisterDto);

        return new ResponseEntity<>(battleRoom, HttpStatus.CREATED);
    }

    // 대결방에 유저가 들어왔을 때, 인원 정보를 추가.
    @PutMapping("/{room_id}")
    public ResponseEntity<?> enterBattleRoom(@PathVariable("room_id") Long roomId) {

        log.info("대결방 입장 = {}", roomId);

        // roomid를 가지고 방 인원을 체크하여, 방 인원이 2명 미만이면 로직을 실행하도록 한다.
        String redisKey = "battleRoom:" + roomId;
        ListOperations<String, Long> listOperations = redisTemplate.opsForList();
        if (listOperations.size(redisKey) < 2) {
            BattleRoom battleRoom = battleRoomService.selectAndEnterBattleRoom(roomId);
            return new ResponseEntity<>(battleRoom, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

    }


    // 대결방에서 유저가 나갔을 때 인원 정보를 삭제
    // 만약 유저가 나간 이후, 인원이 0명이라면 스키마 삭제
    @DeleteMapping("/{room_id}")
    public ResponseEntity<?> deleteBattleRoom(@PathVariable("room_id") Long roomId) {
        log.info("유저가 방을 나갔습니다. : {} ", roomId);
        battleRoomService.selectAndExitBattleRoom(roomId);

        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }

    // 전체 대결방 목록 가져오기
    @GetMapping
    public ResponseEntity<?> getAllBattleRooms() {
        log.info("전체 대결방 목록을 가져왔습니다.");
        List<Map<String, Object>> results = battleRoomService.selectAllBattleRooms();
        return new ResponseEntity<>(results, HttpStatus.OK);
    }


    // 대결방 목록에서 유저가 특정 대결방을 검색
    @GetMapping("/{keyword}")
    public ResponseEntity<?> getBattleRoomById(@PathVariable("keyword") String keyword) {
        List<Map<String, Object>> battleRooms = battleRoomService.selectAllBattleRoomsByTitle(
                keyword);
        return new ResponseEntity<>(battleRooms, HttpStatus.OK);
    }


}
