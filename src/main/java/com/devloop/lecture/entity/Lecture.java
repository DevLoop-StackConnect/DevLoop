package com.devloop.lecture.entity;

import com.devloop.common.enums.Approval;
import com.devloop.common.enums.BoardType;
import com.devloop.common.enums.Category;
import com.devloop.lecture.request.SaveLectureRequest;
import com.devloop.lecture.request.UpdateLectureRequest;
import com.devloop.lecturereview.entity.LectureReview;
import com.devloop.product.entity.Product;
import com.devloop.pwt.enums.Level;
import com.devloop.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Document(indexName = "lecture")
@Setting(settingPath = "/elasticsearch/setting.json")
@Mapping(mappingPath = "/elasticsearch/lecture-mapping.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Field(type = FieldType.Keyword, name = "board_type")
    private BoardType boardType = BoardType.LECTURE;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 20, nullable = false)
    private String recommend;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category = Category.ETC;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Approval approval = Approval.WAITE;

    @ManyToOne(fetch = FetchType.LAZY)
    @Field(type = FieldType.Object, includeInParent = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LectureVideo> lectureVideos;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "lecture", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<LectureReview> lectureReviews;

    private Lecture(String title, String description, String recommend, Category category, Level level, BigDecimal price, User user) {
        super(title, price);
        this.description = description;
        this.recommend = recommend;
        this.category = category != null ? category : Category.ETC;
        this.level = level;
        this.user = user;
        this.boardType = BoardType.LECTURE;
        this.approval = Approval.WAITE;

    }

    public static Lecture from(SaveLectureRequest request, User user) {
        return new Lecture(
                request.getTitle(),
                request.getDescription(),
                request.getRecommend(),
                request.getCategory(),
                request.getLevel(),
                request.getPrice(),
                user
        );
    }

    private static BoardType $default$boardType() {
        return BoardType.LECTURE;
    }

    private static Category $default$category() {
        return Category.ETC;
    }

    private static Approval $default$approval() {
        return Approval.WAITE;
    }

    public static LectureBuilder builder() {
        return new LectureBuilder();
    }

    public void update(UpdateLectureRequest request) {
        update(request.getTitle(), request.getPrice());
        this.description = request.getDescription();
        this.recommend = request.getRecommend();
        this.category = request.getCategory();
        this.level = request.getLevel();
    }

    public void changeApproval(Approval approval) {
        this.approval = approval;
    }

    public static class LectureBuilder {
        private Long id;
        private BoardType boardType$value;
        private boolean boardType$set;
        private String description;
        private String recommend;
        private Category category$value;
        private boolean category$set;
        private Level level;
        private Approval approval$value;
        private boolean approval$set;
        private User user;
        private List<LectureVideo> lectureVideos;
        private List<LectureReview> lectureReviews;

        LectureBuilder() {
        }

        public LectureBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LectureBuilder boardType(BoardType boardType) {
            this.boardType$value = boardType;
            this.boardType$set = true;
            return this;
        }

        public LectureBuilder description(String description) {
            this.description = description;
            return this;
        }

        public LectureBuilder recommend(String recommend) {
            this.recommend = recommend;
            return this;
        }

        public LectureBuilder category(Category category) {
            this.category$value = category;
            this.category$set = true;
            return this;
        }

        public LectureBuilder level(Level level) {
            this.level = level;
            return this;
        }

        public LectureBuilder approval(Approval approval) {
            this.approval$value = approval;
            this.approval$set = true;
            return this;
        }

        public LectureBuilder user(User user) {
            this.user = user;
            return this;
        }

        public LectureBuilder lectureVideos(List<LectureVideo> lectureVideos) {
            this.lectureVideos = lectureVideos;
            return this;
        }

        public LectureBuilder lectureReviews(List<LectureReview> lectureReviews) {
            this.lectureReviews = lectureReviews;
            return this;
        }

        public Lecture build() {
            BoardType boardType$value = this.boardType$value;
            if (!this.boardType$set) {
                boardType$value = Lecture.$default$boardType();
            }
            Category category$value = this.category$value;
            if (!this.category$set) {
                category$value = Lecture.$default$category();
            }
            Approval approval$value = this.approval$value;
            if (!this.approval$set) {
                approval$value = Lecture.$default$approval();
            }
            return new Lecture(this.id, boardType$value, this.description, this.recommend, category$value, this.level, approval$value, this.user, this.lectureVideos, this.lectureReviews);
        }

        public String toString() {
            return "Lecture.LectureBuilder(id=" + this.id + ", boardType$value=" + this.boardType$value + ", description=" + this.description + ", recommend=" + this.recommend + ", category$value=" + this.category$value + ", level=" + this.level + ", approval$value=" + this.approval$value + ", user=" + this.user + ", lectureVideos=" + this.lectureVideos + ", lectureReviews=" + this.lectureReviews + ")";
        }
    }
}
