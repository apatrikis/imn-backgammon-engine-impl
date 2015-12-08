/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.notification.impl;

import java.util.ArrayList;
import java.util.List;

import net.ichmags.backgammon.l10n.LocalizationManager;
import net.ichmags.backgammon.notification.IGameStatusChangedNotificationConsumer;
import net.ichmags.backgammon.notification.INotification;
import net.ichmags.backgammon.notification.INotificationConsumer;
import net.ichmags.backgammon.notification.INotificationEmitter;
import net.ichmags.backgammon.notification.pojo.BoardChangedNotification;
import net.ichmags.backgammon.notification.pojo.DicesChangedNotification;
import net.ichmags.backgammon.notification.pojo.StringNotification;

/**
 * The {@code StatusEmitter} provides convenience methods for sending {@link INotification}
 * messages to the registered {@link INotificationConsumer} instances.
 * {@link String} messages will be localized before transmission. 
 * 
 * @author Anastasios Patrikis
 */
public class StatusEmitter implements INotificationEmitter {

	private static StatusEmitter instance = new StatusEmitter();

	private List<IGameStatusChangedNotificationConsumer> consumerList;
	
	/**
	 * Default constructor.
	 * {@code private} for supporting the {@code singleton} pattern.
	 */
	private StatusEmitter() {
		consumerList = new ArrayList<>(4);
	}
	
	/**
	 * Get the {@code singleton} {@code StatusEmitter} instance.
	 * 
	 * @return the sole instance of this class ({@code singleton} pattern).
	 */
	public static StatusEmitter get() {
		return instance;
	}
	
	/**
	 * Send a message with {@link INotification.Level#INFO} to the registered clients.
	 * 
	 * @param key the Message to send; if possible, the message will be localized.
	 * @return the incoming message, or the localized message.
	 */
	public String info(String key) {
		return emit(INotification.Level.INFO, key);
	}
	
	/**
	 * Send a message with {@link INotification.Level#INFO} to the registered clients.
	 * 
	 * @param key the Message to send; if possible, the message will be localized.
	 * @param args the values to place into the localized message.
	 * @return the incoming message, or the localized message.
	 */
	public String info(String key, Object ... args) {
		return emit(INotification.Level.INFO, key, args);
	}
	
	/**
	 * Send a message with {@link INotification.Level#DEBUG} to the registered clients.
	 * 
	 * @param key the Message to send; if possible, the message will be localized.
	 * @return the incoming message, or the localized message.
	 */
	public String debug(String key) {
		return emit(INotification.Level.DEBUG, key);
	}
	
	/**
	 * Send a message with {@link INotification.Level#DEBUG} to the registered clients.
	 * 
	 * @param key the Message to send; if possible, the message will be localized.
	 * @param args the values to place into the localized message.
	 * @return the incoming message, or the localized message.
	 */
	public String debug(String key, Object ... args) {
		return emit(INotification.Level.DEBUG, key, args);
	}
	
	/**
	 * Send a message with {@link INotification.Level#TRACE} to the registered clients.
	 * 
	 * @param key the Message to send; if possible, the message will be localized.
	 * @return the incoming message, or the localized message.
	 */
	public String trace(String key) {
		return emit(INotification.Level.TRACE, key);
	}
	
	/**
	 * Send a message with {@link INotification.Level#TRACE} to the registered clients.
	 * 
	 * @param key the Message to send; if possible, the message will be localized.
	 * @param args the values to place into the localized message.
	 * @return the incoming message, or the localized message.
	 */
	public String trace(String key, Object ... args) {
		return emit(INotification.Level.TRACE, key, args);
	}
	
	/**
	 * Send a message to the registered clients.
	 * 
	 * @param level the {@link INotification.Level} of the message to send.
	 * @param key the Message to send; if possible, the message will be localized.
	 * @return the incoming message, or the localized message.
	 */
	public String emit(INotification.Level level, String key) {
		String msg = LocalizationManager.get().get(key);
		emitNotification(new StringNotification(level, msg));
		return msg;
	}
	
	/**
	 * Send a message to the registered clients.
	 * 
	 * @param level the {@link INotification.Level} of the message to send.
	 * @param key the Message to send; if possible, the message will be localized.
	 * @param args the values to place into the localized message.
	 * @return the incoming message, or the localized message.
	 */
	public String emit(INotification.Level level, String key, Object ... args) {
		String msg = LocalizationManager.get().get(key, args);
		emitNotification(new StringNotification(level, msg));
		return msg;
	}

	
	@Override
	public INotificationEmitter addConsumer(INotificationConsumer consumer) {
		assert (consumer instanceof IGameStatusChangedNotificationConsumer);
		consumerList.add((IGameStatusChangedNotificationConsumer) consumer);
		return this;
	}

	@Override
	public INotificationEmitter removeConsumer(INotificationConsumer consumer) {
		consumerList.remove(consumer);
		return this;
	}

	@Override
	public INotificationEmitter emitNotification(INotification notification) {
		for(IGameStatusChangedNotificationConsumer consumer : consumerList) {
			if(notification instanceof StringNotification) {
				consumer.message(notification);
			} else if(notification instanceof BoardChangedNotification) {
				consumer.boardChanged(notification);
			} else if(notification instanceof DicesChangedNotification) {
				consumer.dicesChanged(notification);
			} else {
				throw new RuntimeException("Unexpected INotification type: " + notification.getClass().getName());
			}
		}
		return this;
	}
}
