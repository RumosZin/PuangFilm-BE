package gdsc.cau.puangbe.photo.repository;

import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PhotoRequestRepository extends JpaRepository<PhotoRequest, Long> {
    Optional<PhotoRequest> findById(Long photoRequestId);
    
    // 특정 유저의 최근에 만들어진 PhotoRequest 조회
    Optional<PhotoRequest> findTopByUserIdOrderByCreateDateDesc(Long photoRequestId);
}
