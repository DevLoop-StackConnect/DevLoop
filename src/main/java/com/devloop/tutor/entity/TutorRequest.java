package com.devloop.tutor.entity;

import com.devloop.common.Timestamped;
import com.devloop.tutor.enums.TutorRequestStatus;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RestController;

@Entity
@Getter
@Setter
@RestController
@NoArgsConstructor
@Table
public class TutorRequest extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorRequestId;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 255)
    private String subUrl;

    @Column(nullable = false, length = 20)
    private String accoutNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TutorRequestStatus status;

    @OneToOne
    @JoinColumn(nullable = false, referencedColumnName = "id")  // 단방향 참조
    private User userId;

    private TutorRequest(String name, String subUrl, String accoutNum, TutorRequestStatus status, User userId) {
        this.name = name;
        this.subUrl = subUrl;
        this.accoutNum = accoutNum;
        this.status = status;
        this.userId = userId;
    }

    public static TutorRequest from(String name, String subUrl, String accoutNum, TutorRequestStatus status, User userId){
        return new TutorRequest(
                name,
                subUrl,
                accoutNum,
                status,
                userId
        );
    }




}
