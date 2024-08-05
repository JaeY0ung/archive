package com.ssafy.los.backend.domain.repository.play;

import com.ssafy.los.backend.domain.entity.BattleRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BattleRoomRepository extends JpaRepository<BattleRoom, Long> {

    List<BattleRoom> findByTitleContaining(String keyword);
}
