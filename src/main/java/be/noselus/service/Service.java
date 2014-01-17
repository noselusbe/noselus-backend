package be.noselus.service;

/**
 * Services need to be started during initialization of the application.
 */
public interface Service {
    /**
     * Starts the service. This method blocks until the service has completely started.
     */
    void start() throws Exception;

    /**
     * Stops the service. This method blocks until the service has completely shut down.
     */
    void stop();
}