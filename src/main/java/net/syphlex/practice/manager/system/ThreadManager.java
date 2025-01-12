package net.syphlex.practice.manager.system;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
public class ThreadManager {

    private final ExecutorService service;

    public ThreadManager(){
        service = Executors.newFixedThreadPool(4);
    }

    public void onDisable(){
        try {
            service.shutdown();
            if (!service.awaitTermination(60, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
    }

}

