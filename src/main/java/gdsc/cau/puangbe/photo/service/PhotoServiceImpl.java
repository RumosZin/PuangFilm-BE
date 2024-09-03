package gdsc.cau.puangbe.photo.service;

import gdsc.cau.puangbe.common.enums.RequestStatus;
import gdsc.cau.puangbe.common.exception.BaseException;
import gdsc.cau.puangbe.common.util.ConstantUtil;
import gdsc.cau.puangbe.common.util.ResponseCode;
import gdsc.cau.puangbe.photo.dto.request.EmailInfo;
import gdsc.cau.puangbe.photo.entity.PhotoRequest;
import gdsc.cau.puangbe.photo.entity.PhotoResult;
import gdsc.cau.puangbe.photo.repository.PhotoResultRepository;
import gdsc.cau.puangbe.photo.repository.PhotoRequestRepository;
import gdsc.cau.puangbe.user.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private final PhotoResultRepository photoResultRepository;
    private final PhotoRequestRepository photoRequestRepository;
    private final RedisTemplate<String, Long> redisTemplate;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // 완성된 요청 id 및 imageUrl을 받아 저장
    @Override
    @Transactional
    public void uploadPhoto(Long photoResultId, String imageUrl) {
        PhotoResult photoResult = photoResultRepository.findById(photoResultId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTO_REQUEST_NOT_FOUND));
        if (photoResult.getStatus() == RequestStatus.FINISHED) {
            throw new BaseException(ResponseCode.URL_ALREADY_UPLOADED);
        }
        User user = photoResult.getUser();

        photoResult.finishStatus();
        photoResult.update(imageUrl);
        photoResultRepository.save(photoResult);
        photoResultRepository.save(photoResult);
        log.info("결과 이미지 URL 업로드 완료: {}", imageUrl);

        // 이메일 발송
        EmailInfo emailInfo = EmailInfo.builder()
                .email(photoRequest.getEmail())
                .photoUrl(imageUrl)
                .name(user.getUserName())
                .framePageUrl("https://www.google.com/") // TODO : 프론트 분들 링크 관련 답변 오면 프레임 페이지 링크 관련 수정
                .build();

        sendEmail(emailInfo);
    }

    // 특정 요청의 imageUrl 조회
    @Override
    @Transactional(readOnly = true)
    public String getPhotoUrl(Long photoRequestId) {
        PhotoResult photoResult = getPhotoResult(photoRequestId);
        if(photoResult.getImageUrl() == null){
            throw new BaseException(ResponseCode.IMAGE_ON_PROCESS);
        }

        log.info("결과 이미지 URL 조회 완료: {}", photoResult.getImageUrl());
        return photoResult.getImageUrl();
    }

    // 유저에게 이메일 전송
    private void sendEmail(EmailInfo emailInfo) {
        // 이메일 템플릿 내용 설정
        Context context = new Context();
        context.setVariable("userName", emailInfo.getName());
        context.setVariable("photoUrl", emailInfo.getPhotoUrl());
        context.setVariable("framePageUrl", emailInfo.getFramePageUrl());
        String body = templateEngine.process("email-template", context);

        try {
            // 메일 정보 설정
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            messageHelper.setFrom(ConstantUtil.GDSC_CAU_EMAIL);
            messageHelper.setTo(emailInfo.getEmail());
            messageHelper.setSubject("[푸앙이 사진관] AI 프로필 사진 생성 완료");
            messageHelper.setText(body, true);

            // 메일 전송
            mailSender.send(mimeMessage);
            log.info("이메일 전송 완료: {}", emailInfo.getEmail());
        } catch (Exception e){
            e.printStackTrace();
            throw new BaseException(ResponseCode.EMAIL_SEND_ERROR);
        }

    }

    private PhotoResult getPhotoResult(Long photoResultId){
        return photoResultRepository.findById(photoResultId)
                .orElseThrow(() -> new BaseException(ResponseCode.PHOTO_RESULT_NOT_FOUND));
    }
}
