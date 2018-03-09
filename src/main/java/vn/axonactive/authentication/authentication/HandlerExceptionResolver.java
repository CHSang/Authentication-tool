package vn.axonactive.authentication.authentication;

import java.util.Iterator;

import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;

public class HandlerExceptionResolver extends ExceptionHandlerWrapper {

	private ExceptionHandler wrappedException;

	public HandlerExceptionResolver(ExceptionHandler wrappedException) {
		this.wrappedException = wrappedException;
	}

	@Override
	public void handle() {
		for (Iterator<ExceptionQueuedEvent> iter = getUnhandledExceptionQueuedEvents().iterator(); iter.hasNext();) {
			Throwable exception = iter.next().getContext().getException();

			// handle ViewExpiredException, refresh project 
			if (exception instanceof ViewExpiredException) {
				iter.remove();
			}
		}

		getWrapped().handle();
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrappedException;
	}

}