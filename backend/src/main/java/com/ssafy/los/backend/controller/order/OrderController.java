package com.ssafy.los.backend.controller.order;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.dto.order.response.OrderSheetDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.order.OrderService;
import com.ssafy.los.backend.service.sheet.SheetService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.ssafy.los.backend.controller.order fileName       : OrderController author :
 * moongi date           : 8/5/24 description    :
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final AuthService authService;
    private final OrderService orderService;
    private final SheetService sheetService;
    private final SheetRepository sheetRepository;

    // 주문 내역 저장
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderSheetDto orderSheetDto) {
        User loginUser = authService.getLoginUser();

        log.info("orderSheetDto ====== {}", orderSheetDto.getSheetIds());

        List<Sheet> sheets = sheetRepository.findAllById(orderSheetDto.getSheetIds());

        for (Sheet sheet : sheets) {
            System.out.println("sheet = " + sheet.getTitle());
        }

//        List<Sheet> sheets = orderService.searchSheetByIds(orderSheetDto.getSheetIds());
//        System.out.println("sheets = " + sheets.size());

//        for (Sheet sheet : sheets) {
//            System.out.println("sheet = " + sheet.getTitle());
//        }
        //TODO: 주문 내역 저장하기
        return new ResponseEntity<>(sheets, HttpStatus.CREATED);
    }

    // 주문 내역 가져오기
    @GetMapping
    public ResponseEntity<?> getOrder() {
        User loginUser = authService.getLoginUser();

        // TODO: 로그인한 유저의 주문 내역 가져오기

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
