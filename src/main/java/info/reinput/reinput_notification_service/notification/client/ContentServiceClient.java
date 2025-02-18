package info.reinput.reinput_notification_service.notification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "content-service")
public interface ContentServiceClient {

    @GetMapping("/ids/{memberId}")
    List<Long> getInsightIdsByMemberId(@PathVariable("memberId") Long memberId);
} 