package com.github.tand0.anshogio.log;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.classic.spi.LoggingEvent;
public class StackAppender<E> extends UnsynchronizedAppenderBase<E>  {
	public static LinkedList<LoggingEvent> linkedList = new LinkedList<>();
	@Override
	protected void append(E eventObject) {
		if (!(eventObject instanceof LoggingEvent)) {
			return;
		}
		LoggingEvent logEvent = (LoggingEvent)eventObject;
		linkedList.addLast(logEvent);
		if (1000 < linkedList.size()) {
			linkedList.removeFirst();
		}
	}
	/** Webから読みだすときに使う */
	public static String getResult() {
		StringBuilder b = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		for (LoggingEvent logEvent :linkedList) {
			Timestamp timestamp = new Timestamp(logEvent.getTimeStamp());
	        String formattedDate = sdf.format(timestamp);
			String threadName = logEvent.getThreadName();
			b.append(formattedDate);
			b.append("[");
			b.append(threadName);
			b.append("]");
			b.append(logEvent.toString());
			b.append("\n");
		}
		return b.toString();
	}
}
