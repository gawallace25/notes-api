package com.gitlab.rurouniwallace.notes.command;

import rx.Observer;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.yammer.tenacity.core.TenacityCommand;

/**
 * Facilitates running a Tenacity command
 */
public class CommandRunner {

	/**
	 * Run the command asynchronously
	 * 
	 * @param <T> response type
	 * @param response the asynchronous API response
	 * @param command the Tenacity command
	 * @param logger the event logger
	 */
	public static <T> void run(final AsyncResponse response, final TenacityCommand<T> command, final Logger logger) {
		command.observe().subscribe(new Observer<T>() {

			/**
			 * Actions to take upon completion
			 */
			@Override
			public void onCompleted() {
				// empty
			}

			/**
			 * Actions to take upon an error being thrown
			 */
			@Override
			public void onError(final Throwable e) {
				response.resume(e);
				
			}

			/**
			 * Actions to take upon moving on to the next observable. In this case
			 * there is only one observable, the command, so the transaction is completed here.
			 * 
			 * @param value the value emitted by the command
			 */
			@Override
			public void onNext(final T value) {
				logger.info(String.format("%s %s returned value: %s", command.getClass().getSimpleName(), command.getCommandKey(), value));
				response.resume(value);
			}
		});
	}
}
