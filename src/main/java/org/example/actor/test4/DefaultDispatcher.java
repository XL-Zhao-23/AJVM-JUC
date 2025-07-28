package org.example.actor.test4;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultDispatcher implements Dispatcher {
    private final ExecutorService pool = Executors.newFixedThreadPool(8);

    @Override
    public void dispatch(Actor actor) {
        pool.submit(actor);
    }
}
