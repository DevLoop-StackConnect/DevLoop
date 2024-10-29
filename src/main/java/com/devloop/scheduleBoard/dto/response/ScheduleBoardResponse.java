package com.devloop.scheduleBoard.dto.response;

import com.devloop.scheduleTodo.dto.response.ScheduleTodoSimpleResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleBoardResponse {
    private final Long id;
    private final Long pwtId;
    private final String managerTutorName;

    private ScheduleBoardResponse(Long id, Long pwtId, String managerTutorName) {
        this.id = id;
        this.pwtId = pwtId;
        this.managerTutorName = managerTutorName;
    }

    public static ScheduleBoardResponse of(Long id, Long pwtId, String managerTutorName){
        return new ScheduleBoardResponse(
                id,
                pwtId,
                managerTutorName
        );
    }
}
