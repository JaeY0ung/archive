package com.ssafy.los.backend.service.play;


import com.ssafy.los.backend.domain.entity.BattleRoom;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.play.BattleRoomRepository;
import com.ssafy.los.backend.dto.play.request.BattleRoomRegisterDto;
import com.ssafy.los.backend.service.auth.AuthService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BattleRoomService {


    private final BattleRoomRepository battleRoomRepository;

    private final AuthService authService;
    private final RedisTemplate redisTemplate;

    // 배틀룸을 생성하는 메서드
    public Map<String, Object> createAndEnterBattleRoom(
            BattleRoomRegisterDto battleRoomRegisterDto) {
        BattleRoom battleRoom = BattleRoom.builder()
                .title(battleRoomRegisterDto.getTitle())
                .build();

        battleRoomRepository.save(battleRoom);

        User loginUser = authService.getLoginUser();

        // Redis에 방 ID를 키로 하여 유저 리스트에 추가
        String redisKey = "battleRoom:" + battleRoom.getId();

        // Redis에 value로 넣을 List 생성,
        ListOperations<String, Long> listOperations = redisTemplate.opsForList();

        // redisKey를 key값으로 하는 list에 유저의 id를 오른쪽에 추가한다.
        listOperations.rightPush(redisKey, loginUser.getId());

        // 방과 해당 방에 참여한 인원을 묶어서 보내줄 것이다.
        Map<String, Object> result = new HashMap<>();

        result.put("info", battleRoom);
        result.put("occupancy", 1);

        return result;
    }

    // 방 조회 및 입장
    public BattleRoom selectAndEnterBattleRoom(Long roomId) {
        // 방 조회
        BattleRoom battleRoom = selectBattleRoomById(roomId);

        // 유저 조회
        User loginUser = authService.getLoginUser();

        // Redis에서 유저 리스트 조회
        String redisKey = "battleRoom:" + roomId;
        ListOperations<String, Long> listOperations = redisTemplate.opsForList();
        List<Long> userIds = listOperations.range(redisKey, 0, -1);


        if (userIds != null) {
            // 유저 아이디 추가
            userIds.add(loginUser.getId());

            // redisKey를 key값으로 하는 곳에 userId 목록을 다시 추가한다.
            listOperations.rightPush(redisKey, loginUser.getId());
        }

        return battleRoom;
    }

    // 방 조회 및 퇴장
    public void selectAndExitBattleRoom(Long roomId) {
        // 방 조회
        BattleRoom battleRoom = selectBattleRoomById(roomId);
        // 유저 조회
        User loginUser = authService.getLoginUser();

        // Redis에서 유저 리스트 조회
        String redisKey = "battleRoom:" + roomId;
        ListOperations<String, Long> listOperations = redisTemplate.opsForList();

        long cnt = listOperations.remove(redisKey, 1, loginUser.getId());

        // 조건문을 통해, 방의 인원이 0명이라면 방을 삭제한다.
        if (listOperations.size(redisKey) == 0) {
            // redis의 키값쌍을 삭제
            redisTemplate.delete(redisKey);

            // mysql에서의 방 정보 삭제
            deleteBattleRoomById(roomId);
        }

    }

    // room id를 매개변수로 room 정보를 가져오는 메서드
    // enter room 메서드에서 방정보를 가져오기 위해 사용한다.
    public BattleRoom selectBattleRoomById(Long id) {
        return battleRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
    }

    // 방에 인원이 0명이어서 방을 삭제할 때 호출되는 메서드
    public void deleteBattleRoomById(Long id) {
        battleRoomRepository.deleteById(id);
    }

    // 전체 방 목록 가져오는 메서드
    public List<Map<String, Object>> selectAllBattleRooms() {
        List<BattleRoom> battleRooms = battleRoomRepository.findAll();

        List<Map<String, Object>> results = new ArrayList<>();

        for (BattleRoom room : battleRooms) {

            // Redis에서 유저 리스트 조회
            String redisKey = "battleRoom:" + room.getId();
            ListOperations<String, Long> listOperations = redisTemplate.opsForList();

            Map<String, Object> result = new HashMap<>();
            result.put("info", room);
            result.put("occupancy", listOperations.size(redisKey));

            results.add(result);
        }

        return results;
    }

    // 특정 검색어를 포함하는 방 목록을 가져오는 메서드
    public List<Map<String, Object>> selectAllBattleRoomsByTitle(String keyword) {
        List<BattleRoom> battleRooms = battleRoomRepository.findByTitleContaining(keyword);

        List<Map<String, Object>> results = new ArrayList<>();

        for (BattleRoom room : battleRooms) {

            // Redis에서 유저 리스트 조회
            String redisKey = "battleRoom:" + room.getId();
            ListOperations<String, Long> listOperations = redisTemplate.opsForList();

            Map<String, Object> result = new HashMap<>();
            result.put("info", room);
            result.put("occupancy", listOperations.size(redisKey));

            results.add(result);
        }

        return results;

    }
}