package ru.dlamanche;

import com.google.common.base.Stopwatch;
import com.hazelcast.core.Member;
import ru.dlamanche.storage.HazelcastStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final int MAX_ELEMENTS = 1000;

    HazelcastStorage hazelcastStorage = new HazelcastStorage();

    public static void main( String[] args )
    {
        new App().run();
    }

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void insertIntoHazelcast(List<Long> visitors){
        Member m = hazelcastStorage.getInstance().getCluster().getLocalMember();


        System.out.println(m.getUuid());

        for (Long visitor : visitors) {
            hazelcastStorage.addToMap(visitor, visitor);
        }
    }


    private <E> List<E> generate(){
        Stopwatch stopwatch = Stopwatch.createStarted();
        List result = new ArrayList<>(MAX_ELEMENTS);
        Long k = new Date().getTime();

        for (int i = 0; i < MAX_ELEMENTS; i++) {
             result.add(i+k);
        }

        stopwatch.elapsed(TimeUnit.MILLISECONDS);
        stopwatch.stop();
        return result;
    }

    public void run() {

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                Stopwatch stopwatch = Stopwatch.createStarted();
                List<Long> visitors = generate();
                String vistTime = stopwatch.toString();
                insertIntoHazelcast(visitors);
                String hazTime = stopwatch.toString();
                System.out.println(String.format("Generated: %s, hazelcast inserted: %s, size: %d, owned: %d ",
                        vistTime.toString(), hazTime.toString(), hazelcastStorage.getMap().size(),
                        hazelcastStorage.getMap().getLocalMapStats().getOwnedEntryCount()
                        )
                );
            } catch (Exception e) {
                System.out.println(e.getMessage());            }

        }, 0, 1, TimeUnit.SECONDS);
    }
}
