package com.cointr.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
public class CoinScheduledTask {

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;

    public void scheduleTask() {
        // 주기적으로 실행될 메소드
        Runnable runnableTask = () -> {
            // 실행할 메소드 로직
            System.out.println("Scheduled task executed!");
        };

        // Trigger 설정
        Trigger trigger = new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                Calendar nextExecutionTime = Calendar.getInstance();
                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());

                // 매시 정각으로 설정
                nextExecutionTime.set(Calendar.SECOND, 0);
                nextExecutionTime.set(Calendar.MILLISECOND, 0);

                // 15분과 3초를 더하여 다음 실행 시간 계산
                nextExecutionTime.add(Calendar.MINUTE, 1);
                nextExecutionTime.add(Calendar.SECOND, 3);

                return nextExecutionTime.getTime();
            }
        };

        // 실행 스케줄링
        scheduledFuture = taskScheduler.schedule(runnableTask, trigger);
    }

    public void stopTask() {
        scheduledFuture.cancel(false);
    }
}
