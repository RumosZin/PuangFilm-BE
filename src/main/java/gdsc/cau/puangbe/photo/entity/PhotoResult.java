package gdsc.cau.puangbe.photo.entity;

import gdsc.cau.puangbe.common.enums.Gender;
import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private RequestStatus status;

    private String email;

    private Gender gender;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "request", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<PhotoOrigin> photoUrls = new ArrayList<>();

    @Nullable
    private String imageUrl; //s3에 저장된 AI결과 이미지 url

    @Builder
    public PhotoResult(User user, Gender gender, List<String> urls, String email){
        this.user = user;
        this.gender = gender;
        this.status = RequestStatus.WAITING;
        this.email = email;
        this.photoUrls = urls.stream().map(PhotoOrigin::new).toList();
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    public void update(String imageUrl){
        this.imageUrl = imageUrl;
        this.updateDate = LocalDateTime.now();
    }

    public void finishStatus() {
        this.status = RequestStatus.FINISHED;
        this.updateDate = LocalDateTime.now();
    }
}
