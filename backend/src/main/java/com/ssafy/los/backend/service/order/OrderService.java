package com.ssafy.los.backend.service.order;

import com.ssafy.los.backend.domain.entity.Sheet;
import java.util.List;

public interface OrderService {

    List<Sheet> searchSheetByIds(List<Long> ids);

}
