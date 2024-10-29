package com.devloop.scheduleBoard.dto.response;

import com.devloop.scheduleTodo.dto.response.ScheduleTodoSimpleResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleBoardResponse {
    private final Long id;
    private final Long pwtId;
    private final String managerTutorName;
    private final List<ScheduleTodoSimpleResponse> todos;

    private ScheduleBoardResponse(Long id, Long pwtId, String managerTutorName, List<ScheduleTodoSimpleResponse> todos) {
        this.id = id;
        this.pwtId = pwtId;
        this.managerTutorName = managerTutorName;
        this.todos=todos;
    }

    public static ScheduleBoardResponse of(Long id, Long pwtId, String managerTutorName,List<ScheduleTodoSimpleResponse> todos){
        return new ScheduleBoardResponse(
                id,
                pwtId,
                managerTutorName,
                todos
        );
    }
}
