package com.cenimarket.backend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class BackendApplication {

    /**
     * 🎯 JVM 전역 타임존을 Asia/Seoul(KST)로 강제 고정합니다.
     * AWS EC2, RDS 등 배포 인프라가 UTC(세계 표준시) 환경이더라도
     * LocalDateTime.now() 호출 시 한국 시간이 정확히 보장됩니다.
     */
    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}