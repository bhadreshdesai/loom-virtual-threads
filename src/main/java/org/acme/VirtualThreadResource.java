package org.acme;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.client.ExampleClient;
import org.acme.concurrency.ConcurrencyTracker;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/virtual")
@ApplicationScoped
public class VirtualThreadResource {

    @Inject
    ConcurrencyTracker concurrencyTracker;

    @RestClient
    ExampleClient exampleClient;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RunOnVirtualThread
    public String call() {
        concurrencyTracker.inc();
        try {
            System.out.println("Calling example API on " + Thread.currentThread());

            return exampleClient.blockingGet().toUpperCase();
        } finally {
            concurrencyTracker.dec();
        }
    }

    @GET
    @Path("/sleep")
    @Produces(MediaType.TEXT_PLAIN)
    @RunOnVirtualThread
    public String callSleep() {
        concurrencyTracker.inc();
        try {
            System.out.println("Calling example API on " + Thread.currentThread());

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "OK";
        } finally {
            concurrencyTracker.dec();
        }
    }

    @GET
    @Path("/thread")
    @Produces(MediaType.TEXT_PLAIN)
    @RunOnVirtualThread
    public String thread() {
        return "Running on " + Thread.currentThread().getName();
    }

}
