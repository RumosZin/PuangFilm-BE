package gdsc.cau.puangbe.photorequest.service;

import gdsc.cau.puangbe.photo.service.PhotoService;
import gdsc.cau.puangbe.photo.service.PhotoServiceImpl;
import gdsc.cau.puangbe.photorequest.dto.CreateImageDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class PhotoConcurrencyTeset {

    @Autowired
    private PhotoServiceImpl photoService;

    @Autowired
    private PhotoRequestServiceImpl photoRequestService;

    @Test
    void 수정_조회가_동시에_발생하는_경우_테스트() throws InterruptedException {

        Long userId = 1000L; // 테스트할 사용자 ID
        String email = "newEmail@gmail.com"; // 변경할 이메일

        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // uploadPhoto 호출
        executor.submit(() -> {
            try {
                photoService.uploadPhoto(998L, "http://example.com/image.jpg");
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });

        // modifyEmail 호출
        executor.submit(() -> {
            try {
                Thread.sleep(100);
                photoRequestService.updateEmail(userId, email);
                successCount.incrementAndGet();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });

        latch.await(); // 두 개의 쓰레드가 완료될 때까지 대기
        executor.shutdown();

        System.out.println("success count: " + successCount.get());
        System.out.println("fail count: " + failCount.get());

    }
}
