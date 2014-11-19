package org.uberfire.client.mvp;

import static org.uberfire.debug.Debug.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.workbench.widgets.notifications.NotificationManager;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.NotificationEvent.NotificationType;

@ApplicationScoped
public class ActivityLifecycleErrorHandler {

    @Inject
    private Logger logger;

    @Inject
    private Event<ActivityLifecycleError> lifecycleErrorEvent;

    @Inject
    private NotificationManager notificationManager;

    /**
     * Becomes true when the error handling procedure is happening; reverts back to false when error handling is
     * complete. This is used to prevent multiple cascading failures when shutting down a broken activity.
     */
    private boolean errorHandlingInProgress;

    /**
     * Handles the failure of an activity's lifecycle method. This should only normally be called by the
     * {@link ActivityManager} or {@link PlaceManager} implementation.
     *
     * @param failedActivity
     *            the activity instance that was in error. Not null.
     * @param failedCall
     *            The lifecycle call that was in error. Not null.
     * @param exception
     *            The exception thrown by the lifecycle method, if the error was caused by an exception. Can be null.
     */
    public void handle( Activity failedActivity, LifecyclePhase failedCall, Throwable exception ) {

        if ( errorHandlingInProgress ) {
            return;
        }

        try {
            errorHandlingInProgress = true;
            ActivityLifecycleError event = new ActivityLifecycleError( failedActivity,
                                                                       failedCall,
                                                                       exception );

            try {
                lifecycleErrorEvent.fire( event );
            } catch ( Exception ex ) {
                logger.warn( "A lifecycle error observer threw an exception", ex );
            }

            if ( !event.isErrorMessageSuppressed() ) {
                StringBuilder message = new StringBuilder();
                message.append( shortName( failedActivity.getClass() ) + " failed in " ).append( failedCall );
                if ( exception != null) {
                    message.append( ": " ).append( exception.toString() );
                }
                notificationManager.addNotification( new NotificationEvent( message.toString(),
                                                                            NotificationType.ERROR ) );
            }
        } finally {
            errorHandlingInProgress = false;
        }
    }

}
