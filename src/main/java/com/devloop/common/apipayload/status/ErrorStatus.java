package com.devloop.common.apipayload.status;


import com.devloop.common.apipayload.BaseCode;
import com.devloop.common.apipayload.dto.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    // common
    _INVALID_REQUEST(HttpStatus.NOT_FOUND, "404", "잘못된 요청입니다."),
    _PERMISSION_DENIED(HttpStatus.BAD_REQUEST, "404", "권한이 없습니다."),

    // user
    _NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404", "존재하지 않은 유저입니다"),
    _NOT_FOUND_(HttpStatus.NOT_FOUND, "404", "존재하지 않은 유저입니다"),

    // attachment
    _FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "400", "파일 크기가 5MB를 초과합니다"),
    _UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "400", "지원되지 않는 파일 형식입니다"),
    _ATTACHMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "첨부 파일을 찾을 수 없습니다"),
    _FILE_ISNOT_ONE(HttpStatus.BAD_REQUEST,"400", "파일 업로드는 하나만 가능합니다"),

    // board
    _NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "404", "존재하지 않은 보드입니다"),
    _INVALID_TITLE_REQUEST(HttpStatus.BAD_REQUEST, "400", "제목이 비어있습니다. 제목을 입력해주세요."),
    // lists
    _NOT_FOUND_LISTS(HttpStatus.NOT_FOUND, "404", "존재하지 않은 리스트입니다"),

    // comment
    _NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "404", "존재하지 않은 댓글입니다."),
    _NOT_INCLUDE_COMMENT(HttpStatus.NOT_FOUND, "404", "해당 댓글은 이 게시글에 속해있지 않습니다."),
    _INVALID_COMMENTUSER(HttpStatus.BAD_REQUEST, "400", "본인 댓글만 수정 및 삭제가 가능합니다."),
    // 동시성 제어
    _CONCURRENT_UPDATE(HttpStatus.CONFLICT, "409", "동시성 업데이트 충돌이 발생했습니다."),


    // tutor
    _HAS_NOT_ACCESS_PERMISSION(HttpStatus.CONFLICT, "409", "해당 서비스 사용 권한이 없습니다."),
    _TUTOR_REQUEST_ALREADY_EXIST(HttpStatus.CONFLICT, "409", "이미 튜터 요청 신청 내역이 존재합니다."),
    _TUTOR_REQUEST_NOT_EXIST(HttpStatus.NOT_FOUND, "404", "튜터 요청 내역이 존재하지 않습니다."),

    // Project With Tutor
    _NOT_FOUND_PROJECT_WITH_TUTOR(HttpStatus.NOT_FOUND, "404", "튜터랑 함께하는 협업 프로젝트 게시글이 존재하지 않습니다."),
    _ACCESS_PERMISSION_DENIED(HttpStatus.BAD_REQUEST, "400", "잘못된 접근입니다."),
    _LEVEL_NOT_EXIST(HttpStatus.BAD_REQUEST, "400", "존재하지 않는 난이도 입니다."),

    // Cart
    _NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "404", "존재하지 않는 상품 입니다."),
    _NOT_FOUND_CART_ITEM(HttpStatus.NOT_FOUND, "404", "장바구니에 상품이 존재하지 않습니다."),


    //Auth
    _NOT_AUTHENTICATIONPRINCIPAL_USER(HttpStatus.UNAUTHORIZED, "401", "인증되지 않은 유저입니다."),
    _INVALID_LOGIN_TYPE(HttpStatus.BAD_REQUEST, "400", "로그인 타입이 올바르지 않습니다."),
    _DUPLICATE_EMAIL(HttpStatus.CONFLICT, "409", "이미 존재하는 이메일입니다."),

    //Party
    _NOT_FOUND_PARTY(HttpStatus.NOT_FOUND, "404", "존재하지 않는 스터디 파티입니다"),

    //Search
    _BAD_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "400", "검색 오류"),

    //Community
    _NOT_FOUND_COMMUNITY(HttpStatus.NOT_FOUND, "404", "존재하지 않는 게시글입니다"),

    //S3
    _UNSUPPORTED_OBJECT_TYPE(HttpStatus.BAD_REQUEST,"400","지원하지 않는 객체입니다."),
    _STATUS_NOT_EXSIST(HttpStatus.BAD_REQUEST, "400", "잘못된 해결 상태 입력값입니다. 오타 및 해결상태 종류를 확인하세요"),
    _CATEGORY_NOT_EXSIST(HttpStatus.BAD_REQUEST, "400", "잘못된 카테고리 입력값입니다. 오타 및 카테고리 종류를 확인하세요");



    private HttpStatus httpStatus;
    private String statusCode;
    private String message;


    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .statusCode(statusCode)
                .message(message)
                .httpStatus(httpStatus)
                .success(false)
                .build();
    }
}